package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;

public class AddRemoveNodePropsCase extends AddRemovePropsCase
{
    public AddRemoveNodePropsCase( Properties iterationCountConfig,
        Object value )
    {
        super( iterationCountConfig, value );
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
