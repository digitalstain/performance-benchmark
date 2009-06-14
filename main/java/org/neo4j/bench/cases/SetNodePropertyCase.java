package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;

public class SetNodePropertyCase extends AbstractSetPropertyCase
{
    public SetNodePropertyCase( Properties iterationCountConfig, Object value )
    {
        super( "S_NP", iterationCountConfig, value );
        // "Set the same node property many times in one tx"
    }

    @Override
    protected PropertyContainer createContainer( NeoService neo )
    {
        return neo.createNode();
    }
}
