package org.neo4j.bench.cases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.neo4j.api.core.Direction;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.api.core.ReturnableEvaluator;
import org.neo4j.api.core.StopEvaluator;
import org.neo4j.api.core.Transaction;
import org.neo4j.api.core.TraversalPosition;
import org.neo4j.api.core.Traverser;
import org.neo4j.api.core.Traverser.Order;

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

    public void run( NeoService neo )
    {
        List<Long> entityIds = new ArrayList<Long>();
        Random random = new Random();
        Node subref = null;
        
        beginTransaction( CREATE_TIMER );
        Transaction tx = neo.beginTx();
        try
        {
            subref = neo.createNode();
            neo.getReferenceNode().createRelationshipTo( subref,
                RelTypes.SUBREF );
            for ( int i = 0; i < this.getNumberOfIterations(); )
            {
                // Create one entity
                Node entity = neo.createNode();
                subref.createRelationshipTo( entity, RelTypes.ENTITY );
                entityIds.add( entity.getId() );
                String name = "http://neo4j.org#" + entity.getId();
                entity.setProperty( "name", name );
                
                // Create a property node (neo-rdf-quad-store-like)
                int numberOfProperties = random.nextInt( 10 );
                for ( int p = 0; p < numberOfProperties; p++ )
                {
                    Node property = neo.createNode();
                    entity.createRelationshipTo( property,
                        RelTypes.ENTITY_HAS_PROPERTY );
                    property.setProperty( "value", random.nextLong() );
                    tx = checkCommit( neo, tx, ++i );
                }
                
                // Maybe connect it to other entities
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
                        
                        Node otherEntity = neo.getNodeById( otherEntityId );
                        entity.createRelationshipTo( otherEntity,
                            RelTypes.ENTITY_HAS_ENTITY );
                        tx = checkCommit( neo, tx, ++i );
                    }
                }
                tx = checkCommit( neo, tx, ++i );
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, CREATE_TIMER );
        }
        
        // Traverse the structure
        for ( int i = 0; i < 30; i++ )
        {
            beginTransaction( TRAVERSE_TIMER );
            tx = neo.beginTx();
            try
            {
                AtomicInteger counter = new AtomicInteger();
                int max = getNumberOfIterations();
                while ( counter.get() < max )
                {
                    Node startEntity = neo.getNodeById( entityIds.get(
                        random.nextInt( entityIds.size() ) ) );
                    Node goalEntity = neo.getNodeById( entityIds.get(
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
        tx = neo.beginTx();
        try
        {
            int counter = 0;
            for ( long nodeId : entityIds )
            {
                Node node = neo.getNodeById( nodeId );
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
                tx = checkCommit( neo, tx, counter++ );
            }
            neo.getReferenceNode().getSingleRelationship(
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
    
    private Transaction checkCommit( NeoService neo, Transaction tx, int i )
    {
        if ( i % 1000 == 0 )
        {
            tx.success();
            tx.finish();
            tx = neo.beginTx();
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
