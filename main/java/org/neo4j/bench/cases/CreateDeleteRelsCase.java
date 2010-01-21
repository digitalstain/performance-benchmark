package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

/**
 * Create/delete many relationships in one tx.
 * 
 * Three timers
 * o Creates many in a tx
 * o {@link Node#getRelationships()} in another tx
 * o Deletes them in another tx
 */
public class CreateDeleteRelsCase extends AbstractBenchCase
{
    public static final String CREATE_TIMER = "c";
    public static final String GET_TIMER = "g";
    public static final String DELETE_TIMER = "d";
    
    public CreateDeleteRelsCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
    }

    public void run( GraphDatabaseService graphDb )
    {
        int max = getNumberOfIterations();
        Transaction tx = graphDb.beginTx();
        Node node1 = null;
        Node node2 = null;
        RelationshipType type = DynamicRelationshipType.withName(
            "TEST_RELATIONSHIP" );
        try
        {
            timerOff( MAIN_TIMER );
            node1 = graphDb.createNode();
            node2 = graphDb.createNode();
            timerOn( MAIN_TIMER );
            
            beginTransaction( CREATE_TIMER );
            for ( int i = 0; i < max; i++ )
            {
                node1.createRelationshipTo( node2, type );
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, CREATE_TIMER );
        }
        
        timerOn( GET_TIMER );
        tx = graphDb.beginTx();
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
        
        beginTransaction( DELETE_TIMER );
        tx = graphDb.beginTx();
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
            finishTransaction( tx, DELETE_TIMER );
        }
    }
}
