package org.neo4j.bench.cases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;
import org.neo4j.api.core.Transaction;

public class SetAndRemoveNodePropertiesCase extends AbstractPropertyBenchCase
{
    public static final String SET_TIMER = "set";
    public static final String REMOVE_TIMER = "remove";
    protected static final int PROPERTY_COUNT = 100;
    
    public SetAndRemoveNodePropertiesCase( int numberOfIterations,
        Object value )
    {
        super( numberOfIterations, value );
    }

    public void run( NeoService neo )
    {
        timerOff( MAIN_TIMER );
        PropertyContainer[] containers = null;
        Transaction tx = neo.beginTx();
        try
        {
            containers = createContainers( neo );
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOn( MAIN_TIMER );
        
        beginTransaction( SET_TIMER );
        tx = neo.beginTx();
        try
        {
            for ( PropertyContainer container : containers )
            {
                for ( int i = 0; i < PROPERTY_COUNT; i++ )
                {
                    container.setProperty( "key" + i, getPropertyValue() );
                }
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, SET_TIMER );
        }

        Iterable<Integer> randomInts = generateRandomInts();
        beginTransaction( REMOVE_TIMER );
        tx = neo.beginTx();
        try
        {
            for ( PropertyContainer container : containers )
            {
                for ( int index : randomInts )
                {
                    container.removeProperty( "key" + index );
                }
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, REMOVE_TIMER );
        }
    }
    
    private Iterable<Integer> generateRandomInts()
    {
        List<Integer> list = new ArrayList<Integer>();
        for ( int i = 0; i < PROPERTY_COUNT; i++ )
        {
            list.add( i );
        }
        Collections.shuffle( list );
        return list;
    }

    protected PropertyContainer[] createContainers( NeoService neo )
    {
        PropertyContainer[] result =
            new PropertyContainer[ getNumberOfIterations() ];
        for ( int i = 0; i < result.length; i++ )
        {
            result[ i ] = neo.createNode();
        }
        return result;
    }
}
