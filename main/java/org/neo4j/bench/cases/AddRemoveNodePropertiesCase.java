package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;

public class AddRemoveNodePropertiesCase extends
    AbstractAddRemovePropertiesCase
{
    public AddRemoveNodePropertiesCase( Properties iterationCountConfig,
        Object value )
    {
        super( "A/R_NP", iterationCountConfig, value );
        // "Add/remove node properties in one tx"
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
