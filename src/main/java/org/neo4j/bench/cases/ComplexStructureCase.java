/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.bench.cases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.neo4j.graphdb.Traverser.Order;

public class ComplexStructureCase extends AbstractBenchCase
{
    private static final String CREATE_TIMER = "c";
    private static final String TRAVERSE_TIMER = "t";
    
    private static enum RelTypes implements RelationshipType
    {
        SUBREF,
        ENTITY,
        ENTITY_HAS_PROPERTY,
        ENTITY_HAS_ENTITY
    }
    
    public ComplexStructureCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
    }

    public void run( GraphDatabaseService graphDb )
    {
        List<Long> entityIds = new ArrayList<Long>();
        Random random = new Random();
        Node subref = null;
        
        beginTransaction( CREATE_TIMER );
        Transaction tx = graphDb.beginTx();
        try
        {
            subref = graphDb.createNode();
            graphDb.getReferenceNode().createRelationshipTo( subref,
                RelTypes.SUBREF );
            for ( int i = 0; i < this.getNumberOfIterations(); )
            {
                // Create one entity
                Node entity = graphDb.createNode();
                subref.createRelationshipTo( entity, RelTypes.ENTITY );
                entityIds.add( entity.getId() );
                String name = "http://neo4j.org#" + entity.getId();
                entity.setProperty( "name", name );
                
                // Create a property node (neo-rdf-quad-store-like)
                int numberOfProperties = random.nextInt( 10 );
                for ( int p = 0; p < numberOfProperties; p++ )
                {
                    Node property = graphDb.createNode();
                    entity.createRelationshipTo( property,
                        RelTypes.ENTITY_HAS_PROPERTY );
                    property.setProperty( "value", random.nextLong() );
                    tx = checkCommit( graphDb, tx, ++i );
                }
                
                // Connect it to other entities
                if ( entityIds.size() > 10 )
                {
                    int numberOfEntities = random.nextInt( 3 );
                    Set<Long> connected = new HashSet<Long>();
                    for ( int e = 0; e < numberOfEntities; e++ )
                    {
                        long otherEntityId =
                            entityIds.get( random.nextInt( entityIds.size() ) );
                        if ( otherEntityId == entity.getId() ||
                            connected.contains( otherEntityId ) )
                        {
                            continue;
                        }
                        
                        Node otherEntity = graphDb.getNodeById( otherEntityId );
                        entity.createRelationshipTo( otherEntity,
                            RelTypes.ENTITY_HAS_ENTITY );
                        tx = checkCommit( graphDb, tx, ++i );
                    }
                }
                tx = checkCommit( graphDb, tx, ++i );
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, CREATE_TIMER );
        }
        
        // Traverse the structure
        for ( int i = 0; i < 5; i++ )
        {
            beginTransaction( TRAVERSE_TIMER );
            tx = graphDb.beginTx();
            try
            {
                AtomicInteger counter = new AtomicInteger();
                int max = getNumberOfIterations();
                while ( counter.get() < max )
                {
                    Node startEntity = graphDb.getNodeById( entityIds.get(
                        random.nextInt( entityIds.size() ) ) );
                    Node goalEntity = graphDb.getNodeById( entityIds.get(
                        random.nextInt( entityIds.size() ) ) );
                    TheEvaluator evaluator = new TheEvaluator( counter, max );
                    Traverser traverser = startEntity.traverse(
                        Order.DEPTH_FIRST, evaluator, evaluator,
                        RelTypes.ENTITY_HAS_ENTITY, Direction.BOTH );
                    for ( Node node : traverser )
                    {
                        if ( node.equals( goalEntity ) )
                        {
                            break;
                        }
                    }
                }
            }
            finally
            {
                finishTransaction( tx, TRAVERSE_TIMER );
            }
        }
        
        // Cleanup
        timerOff( MAIN_TIMER );
        tx = graphDb.beginTx();
        try
        {
            int counter = 0;
            for ( long nodeId : entityIds )
            {
                Node node = graphDb.getNodeById( nodeId );
                for ( Relationship rel : node.getRelationships(
                    RelTypes.ENTITY_HAS_PROPERTY, Direction.OUTGOING ) )
                {
                    rel.getEndNode().delete();
                    rel.delete();
                }
                for ( Relationship rel : node.getRelationships(
                    RelTypes.ENTITY_HAS_ENTITY ) )
                {
                    rel.delete();
                }
                node.getSingleRelationship( RelTypes.ENTITY,
                    Direction.INCOMING ).delete();
                node.delete();
                tx = checkCommit( graphDb, tx, counter++ );
            }
            graphDb.getReferenceNode().getSingleRelationship(
                RelTypes.SUBREF, Direction.OUTGOING ).delete();
            subref.delete();
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOn( MAIN_TIMER );
    }
    
    private Transaction checkCommit( GraphDatabaseService graphDb,
            Transaction tx, int i )
    {
        if ( i % 1000 == 0 )
        {
            tx.success();
            tx.finish();
            tx = graphDb.beginTx();
        }
        return tx;
    }

    private static class TheEvaluator
        implements StopEvaluator, ReturnableEvaluator
    {
        private final AtomicInteger counter;
        private final int max;
        
        public TheEvaluator( AtomicInteger counter, int max )
        {
            this.counter = counter;
            this.max = max;
        }
        
        public boolean isStopNode( TraversalPosition pos )
        {
            return this.counter.incrementAndGet() > this.max;
        }

        public boolean isReturnableNode( TraversalPosition pos )
        {
            return true;
        }
    }
}
