package org.neo4j.bench.cases;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

public class MinReadTxCase extends AbstractBenchCase
{
    public MinReadTxCase( int numberOfIterations )
    {
        super( "Many getNodeById in separate tx:s", numberOfIterations );
    }

    private Node createANode( NeoService neo )
    {
        Transaction tx = neo.beginTx();
        try
        {
            Node result = neo.createNode();
            tx.success();
            return result;
        }
        finally
        {
            tx.finish();
        }
    }

    public void run( NeoService neo )
    {
        timerOff( MAIN_TIMER );
        Node theNode = createANode( neo );
        timerOn( MAIN_TIMER );
        
        int max = getNumberOfIterations();
        long theNodeId = theNode.getId();
        for ( int i = 0; i < max; i++ )
        {
            Transaction tx = neo.beginTx();
            try
            {
                neo.getNodeById( theNodeId );
                tx.success();
            }
            finally
            {
                tx.finish();
            }
        }
    }
}
