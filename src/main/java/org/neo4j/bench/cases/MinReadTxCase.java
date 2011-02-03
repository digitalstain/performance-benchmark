package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Many getNodeById in separate tx:s
 */
public class MinReadTxCase extends AbstractBenchCase
{
    public MinReadTxCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
    }

    private Node createANode( GraphDatabaseService graphDb )
    {
        Transaction tx = graphDb.beginTx();
        try
        {
            Node result = graphDb.createNode();
            tx.success();
            return result;
        }
        finally
        {
            tx.finish();
        }
    }

    public void run( GraphDatabaseService graphDb )
    {
        timerOff( MAIN_TIMER );
        Node theNode = createANode( graphDb );
        timerOn( MAIN_TIMER );
        
        int max = getNumberOfIterations();
        long theNodeId = theNode.getId();
        for ( int i = 0; i < max; i++ )
        {
            Transaction tx = graphDb.beginTx();
            try
            {
                graphDb.getNodeById( theNodeId );
                tx.success();
            }
            finally
            {
                tx.finish();
            }
        }
    }
}
