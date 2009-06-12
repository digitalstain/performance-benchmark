package org.neo4j.bench.cases;

import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

public class CreateDeleteNodeCase extends WriteOneBigTxCase
{
    public static final String CREATE_TIMER = "create";
    public static final String GET_TIMER = "get";
    public static final String DELETE_TIMER = "delete";
    
    private final Collection<Node> nodes = new ArrayList<Node>();
    
    public CreateDeleteNodeCase( int numberOfIterations )
    {
        super( numberOfIterations );
    }

    @Override
    public void run( NeoService neo )
    {
        timerOn( CREATE_TIMER );
        super.run( neo );
        timerOff( CREATE_TIMER );
        
        timerOn( GET_TIMER );
        Transaction tx = neo.beginTx();
        try
        {
            for ( Node node : nodes )
            {
                neo.getNodeById( node.getId() );
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
            for ( Node node : nodes )
            {
                node.delete();
            }
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOff( DELETE_TIMER );
    }

    @Override
    protected void doOneWriteOperation( NeoService neo )
    {
        Node node = neo.createNode();
        nodes.add( node );
    }
}
