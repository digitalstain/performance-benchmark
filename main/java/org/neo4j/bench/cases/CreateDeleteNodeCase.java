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
    
    private Collection<Node> nodes = new ArrayList<Node>();
    
    public CreateDeleteNodeCase( int numberOfIterations )
    {
        super( numberOfIterations );
    }
    
    @Override
    protected String getTimerName()
    {
        return CREATE_TIMER;
    }

    @Override
    public void run( NeoService neo )
    {
        super.run( neo );
        
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
        
        beginTransaction( DELETE_TIMER );
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
            finishTransaction( tx, DELETE_TIMER );
        }
        nodes = null;
    }

    @Override
    protected void doOneWriteOperation( NeoService neo )
    {
        Node node = neo.createNode();
        nodes.add( node );
    }
}
