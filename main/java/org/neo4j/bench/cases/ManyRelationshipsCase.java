package org.neo4j.bench.cases;

import org.neo4j.api.core.DynamicRelationshipType;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;
import org.neo4j.api.core.Transaction;

public class ManyRelationshipsCase extends AbstractBenchCase
{
    public static final String CREATE_TIMER = "create";
    public static final String GET_TIMER = "get";
    public static final String DELETE_TIMER = "delete";
    
    public ManyRelationshipsCase( int numberOfIterations )
    {
        super( numberOfIterations );
    }

    public void run( NeoService neo )
    {
        int max = getNumberOfIterations();
        Transaction tx = neo.beginTx();
        Node node1 = null;
        Node node2 = null;
        RelationshipType type = DynamicRelationshipType.withName(
            "TEST_RELATIONSHIP" );
        try
        {
            timerOff( MAIN_TIMER );
            node1 = neo.createNode();
            node2 = neo.createNode();
            timerOn( MAIN_TIMER );
            
            timerOn( CREATE_TIMER );
            for ( int i = 0; i < max; i++ )
            {
                node1.createRelationshipTo( node2, type );
            }
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOff( CREATE_TIMER );
        
        timerOn( GET_TIMER );
        tx = neo.beginTx();
        try
        {
            for ( int i = 0; i < 50; i++ )
            {
                for ( Relationship rel : node1.getRelationships() )
                {
                    rel.getId();
                }
            }
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOff( GET_TIMER );
        
        timerOn( DELETE_TIMER );
        tx = neo.beginTx();
        try
        {
            for ( Relationship rel : node1.getRelationships() )
            {
                rel.delete();
            }
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOff( DELETE_TIMER );
    }
}
