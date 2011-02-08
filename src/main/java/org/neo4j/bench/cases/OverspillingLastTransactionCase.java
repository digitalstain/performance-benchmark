package org.neo4j.bench.cases;

import java.util.Properties;
import java.util.Random;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

public class OverspillingLastTransactionCase extends AbstractBenchCase
{
    public OverspillingLastTransactionCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
    }

    private static final int TX_SIZE = 10000;
    private static final Random r = new Random( System.currentTimeMillis() );
    
    public void run( GraphDatabaseService graphDb )
    {
        int max = getNumberOfIterations()/TX_SIZE;
        final RelationshipType TESTING = DynamicRelationshipType.withName( "TESTING" );
        int count = 0;
        for ( int k = 0; k < max; k++ )
        {
            Transaction tx = graphDb.beginTx();
            try
            {
                for ( int i = 0; i < TX_SIZE; i++ )
                {
                    Node node = graphDb.createNode();
                    node.setProperty( "name", "A NAME WITH ID " + count );
                    count++;
                    if ( count < 100 )
                    {
                        continue;
                    }
                    for ( int j = 0; j < 5; j++ )
                    {
                        Node otherNode = graphDb.getNodeById( r.nextInt( count ) );
                        node.createRelationshipTo( otherNode, TESTING );
                    }
                }
                tx.success();
            }
            finally
            {
                tx.finish();
            }
        }
    }
}
