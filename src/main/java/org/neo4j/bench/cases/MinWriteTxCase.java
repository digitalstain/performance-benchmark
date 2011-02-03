package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * Creates many nodes in separate tx:s, effectively testing the transaction
 * log performance.
 */
public class MinWriteTxCase extends AbstractBenchCase
{
    public MinWriteTxCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
    }

    public void run( GraphDatabaseService graphDb )
    {
        int max = getNumberOfIterations();
        for ( int i = 0; i < max; i++ )
        {
            Transaction tx = graphDb.beginTx();
            try
            {
                graphDb.createNode();
                tx.success();
            }
            finally
            {
                tx.finish();
            }
        }
    }
}
