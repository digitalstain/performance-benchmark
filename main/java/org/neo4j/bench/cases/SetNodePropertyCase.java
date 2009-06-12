package org.neo4j.bench.cases;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;
import org.neo4j.api.core.Transaction;

public class SetNodePropertyCase extends WriteOneBigTxCase
{
    private final Object value;
    private PropertyContainer container;
    
    public SetNodePropertyCase( int numberOfIterations, Object value )
    {
        super( numberOfIterations );
        this.value = value;
    }

    @Override
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
        
        super.run( neo );
    }
    
    protected PropertyContainer createContainer( NeoService neo )
    {
        return neo.createNode();
    }

    @Override
    protected void doOneWriteOperation( NeoService neo )
    {
        container.setProperty( "my_key", this.value );
    }
    
    @Override
    public String toString()
    {
        String valueString = null;
        Class<?> valueClass = this.value.getClass();
        if ( valueClass.isArray() )
        {
            Object[] values = ( Object[] ) this.value;
            valueString = values[ 0 ].getClass().getSimpleName() + "[]";
        }
        else
        {
            valueString = this.value.getClass().getSimpleName();
        }
        return super.toString() + "(" + valueString + ")";
    }
}
