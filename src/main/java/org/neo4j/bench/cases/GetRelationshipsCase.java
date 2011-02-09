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

import java.util.Properties;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

public class GetRelationshipsCase extends AbstractBenchCase
{
    public GetRelationshipsCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
    }

    public void run( GraphDatabaseService graphDb )
    {
        timerOff( MAIN_TIMER );
        Node[] nodes = setUp( graphDb );
        timerOn( MAIN_TIMER );
        doTheTests( graphDb, nodes );
        timerOff( MAIN_TIMER );
        cleanup( graphDb, nodes );
        timerOn( MAIN_TIMER );
    }
    
    private void cleanup( GraphDatabaseService graphDb, Node[] nodes )
    {
        Transaction tx = graphDb.beginTx();
        for ( Node node : nodes )
        {
            for ( Relationship rel :
                node.getRelationships( Direction.OUTGOING ) )
            {
                rel.delete();
            }
        }
        for ( Node node : nodes )
        {
            node.delete();
        }
        tx.success();
        tx.finish();
    }

    private void doTheTests( GraphDatabaseService graphDb, Node[] nodes )
    {
        Transaction tx = graphDb.beginTx();
        int max = getNumberOfIterations();
        int stepper = 0;
        for ( int i = 0; i < max; i++ )
        {
            Node node = nodes[ stepper ];
            stepper = ++stepper % 10;
            for ( Relationship rel : node.getRelationships() )
            {
                // Just iterate through them
            }
        }
        tx.finish();
    }

    private static RelationshipType getRelationshipType()
    {
        return DynamicRelationshipType.withName( "MY_TYPE" );
    }

    private Node[] setUp( GraphDatabaseService graphDb )
    {
        Transaction tx = graphDb.beginTx();
        try
        {
            int nodeCount = 10;
            Node[] nodes = new Node[ nodeCount ];
            for ( int i = 0; i < nodeCount; i++ )
            {
                nodes[ i ] = graphDb.createNode();
            }
            
            int stepper = 0;
            RelationshipType type = getRelationshipType();
            // This becomes like a piece of yarn, you roll and roll the
            // relationsips around the nodes.
            for ( int i = 0; i < 10000; i++ )
            {
                Node startNode = nodes[ stepper ];
                stepper = ++stepper % 10;
                Node endNode = nodes[ stepper ];
                startNode.createRelationshipTo( endNode, type );
            }
            tx.success();
            return nodes;
        }
        finally
        {
            tx.finish();
        }
    }
}
