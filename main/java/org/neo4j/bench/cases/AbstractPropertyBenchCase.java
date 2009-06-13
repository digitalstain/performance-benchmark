package org.neo4j.bench.cases;

public abstract class AbstractPropertyBenchCase extends AbstractBenchCase
{
    private final Object propertyValue;
    
    public AbstractPropertyBenchCase( String name, int numberOfIterations,
        Object propertyValue )
    {
        super( name, numberOfIterations );
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
            valueString = values[ 0 ].getClass().getSimpleName() + "[]";
        }
        else
        {
            valueString = this.propertyValue.getClass().getSimpleName();
        }
        return super.toString() + "(" + valueString + ")";
    }
}
