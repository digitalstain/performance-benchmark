package org.neo4j.bench.cases;

import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.PropertyContainer;

public class SetNodePropertyCase extends AbstractSetPropertyCase
{
    public SetNodePropertyCase( int numberOfIterations, Object value )
    {
        super( "Set the same node property many times in one tx",
            numberOfIterations, value );
    }

    @Override
    protected PropertyContainer createContainer( NeoService neo )
    {
        return neo.createNode();
    }
}
