package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;
import org.neo4j.api.core.Transaction;

public abstract class AbstractSetPropertyCase extends AbstractPropertyBenchCase
{
    private PropertyContainer container;
    
    public AbstractSetPropertyCase( String name,
        Properties iterationCountConfig, Object value )
    {
        super( name, iterationCountConfig, value );
    }

    @Override
    protected Integer calculateIterationCount( Properties iterationCountConfig )
    {
        Integer result = super.calculateIterationCount( iterationCountConfig );
        if ( result != null )
        {
            if ( getPropertyValue().getClass().isArray() )
            {
                String division = iterationCountConfig.getProperty(
                    "setArrayPropertyDivision", null );
                result /= Integer.parseInt( division );
            }
        }
        return result;
    }

    public void run( NeoService neo )
    {
        timerOff( MAIN_TIMER );
        Transaction tx = neo.beginTx();
        try
        {
            this.container = createContainer( neo );
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOn( MAIN_TIMER );
        
        tx = neo.beginTx();
        try
        {
            int max = getNumberOfIterations();
            Object propertyValue = getPropertyValue();
            for ( int i = 0; i < max; i++ )
            {
                container.setProperty( "my_key", propertyValue );
            }
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        container = null;
    }
    
    protected abstract PropertyContainer createContainer( NeoService neo );
}
