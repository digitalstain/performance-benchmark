package org.neo4j.bench.cases;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Transaction;

public abstract class WriteOneBigTxCase extends AbstractBenchCase
{
    public WriteOneBigTxCase( int numberOfIterations )
    {
        super( numberOfIterations );
    }

    public void run( NeoService neo )
    {
        int max = getNumberOfIterations();
        Transaction tx = neo.beginTx();
        try
        {
            for ( int i = 0; i < max; i++ )
            {
                doOneWriteOperation( neo );
            }
            tx.success();
        }
        finally
        {
            tx.finish();
        }
    }

    protected abstract void doOneWriteOperation( NeoService neo );
}
