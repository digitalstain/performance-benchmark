package org.neo4j.bench.cases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;
import org.neo4j.api.core.Transaction;

public abstract class AddRemovePropsCase
    extends PropertyBenchCase
{
    public static final String SET_TIMER = "s";
    public static final String REMOVE_TIMER = "r";
    protected static final int PROPERTY_COUNT = 100;
    
    public AddRemovePropsCase( Properties iterationCountConfig, Object value )
    {
        super( iterationCountConfig, value );
    }

    public void run( NeoService neo )
    {
        timerOff( MAIN_TIMER );
        PropertyContainer[] containers = null;
        Transaction tx = neo.beginTx();
        try
        {
            containers = createContainers( neo, getNumberOfContainers() );
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
    
    private int getNumberOfContainers()
    {
        return getNumberOfIterations() / PROPERTY_COUNT;
    }
    
    private Iterable<Integer> generateRandomInts()
    {
        List<Integer> list = new ArrayList<Integer>();
        int max = getNumberOfContainers();
        for ( int i = 0; i < max; i++ )
        {
            list.add( i );
        }
        Collections.shuffle( list );
        return list;
    }

    protected abstract PropertyContainer[] createContainers(
        NeoService neo, int count );
}
