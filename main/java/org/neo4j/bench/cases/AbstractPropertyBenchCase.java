package org.neo4j.bench.cases;

import java.util.Properties;

public abstract class AbstractPropertyBenchCase extends AbstractBenchCase
{
    private final Object propertyValue;
    
    public AbstractPropertyBenchCase( String name,
        Properties iterationCountConfig, Object propertyValue )
    {
        super( name, iterationCountConfig );
        this.propertyValue = propertyValue;
    }
    
    protected Object getPropertyValue()
    {
        return this.propertyValue;
    }
    
    @Override
    protected Integer calculateIterationCount( Properties iterationCountConfig )
    {
        String directMatch =
            iterationCountConfig.getProperty( toString(), null );
        if ( directMatch != null )
        {
            return Integer.parseInt( directMatch );
        }
        return super.calculateIterationCount( iterationCountConfig );
    }
    
    @Override
    public String toString()
    {
        String valueString = null;
        Class<?> valueClass = this.propertyValue.getClass();
        if ( valueClass.isArray() )
        {
            Object[] values = ( Object[] ) this.propertyValue;
            valueString =
                values[ 0 ].getClass().getSimpleName().substring( 0, 2 ) + "[]";
        }
        else
        {
            valueString =
                this.propertyValue.getClass().getSimpleName().substring( 0, 2 );
        }
        return super.toString() + "," + valueString;
    }
}
