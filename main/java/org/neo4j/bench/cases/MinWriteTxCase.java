package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Transaction;

public class MinWriteTxCase extends AbstractBenchCase
{
    public MinWriteTxCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
        // "Creates many nodes in separate tx:s"
    }

    public void run( NeoService neo )
    {
        int max = getNumberOfIterations();
        for ( int i = 0; i < max; i++ )
        {
            Transaction tx = neo.beginTx();
            try
            {
                neo.createNode();
                tx.success();
            }
            finally
            {
                tx.finish();
            }
        }
    }
}
