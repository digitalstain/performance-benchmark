package org.neo4j.bench.cases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.Transaction;

public class CreateDeleteNodeCase extends AbstractBenchCase
{
    public static final String CREATE_TIMER = "c";
    public static final String GET_TIMER = "g";
    public static final String DELETE_TIMER = "d";
    
    public CreateDeleteNodeCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
        // "Create/delete many nodes in one tx"
    }
    
    public void run( NeoService neo )
    {
        beginTransaction( CREATE_TIMER );
        Collection<Node> nodes = new ArrayList<Node>();
        Transaction tx = neo.beginTx();
        try
        {
            int max = getNumberOfIterations();
            for ( int i = 0; i < max; i++ )
            {
                nodes.add( neo.createNode() );
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, CREATE_TIMER );
        }
        
        timerOn( GET_TIMER );
        tx = neo.beginTx();
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
    }
}
