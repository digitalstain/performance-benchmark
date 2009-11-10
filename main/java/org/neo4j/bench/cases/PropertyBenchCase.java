package org.neo4j.bench.cases;

import java.util.Properties;

/**
 * Cases which deals with a certain property type throughout the test can
 * inherit from this class. It alters {@link #toString()} and
 * {@link #calculateIterationCount(Properties)}.
 */
public abstract class PropertyBenchCase extends AbstractBenchCase
{
    private final Object propertyValue;
    
    public PropertyBenchCase( Properties iterationCountConfig,
        Object propertyValue )
    {
        super( iterationCountConfig );
        this.propertyValue = propertyValue;
    }
    
    protected Object getPropertyValue()
    {
        return this.propertyValue;
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
