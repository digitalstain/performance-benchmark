package org.neo4j.bench.cases;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;

public class AddRemoveNodePropertiesCase extends
    AbstractAddRemovePropertiesCase
{
    public AddRemoveNodePropertiesCase( int numberOfIterations, Object value )
    {
        super( "Add/remove node properties in one tx",
            numberOfIterations, value );
    }

    @Override
    protected PropertyContainer[] createContainers( NeoService neo, int count )
    {
        PropertyContainer[] result = new PropertyContainer[ count ];
        for ( int i = 0; i < result.length; i++ )
        {
            result[ i ] = neo.createNode();
        }
        return result;
    }
}
