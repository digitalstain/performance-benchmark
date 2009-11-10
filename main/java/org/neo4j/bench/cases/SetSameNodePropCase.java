package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;

public class SetSameNodePropCase extends SetSamePropCase
{
    public SetSameNodePropCase( Properties iterationCountConfig,
        Object value )
    {
        super( iterationCountConfig, value );
        // "Set the same node property many times in one tx"
    }

    @Override
    protected PropertyContainer createContainer( NeoService neo )
    {
        return neo.createNode();
    }
}
