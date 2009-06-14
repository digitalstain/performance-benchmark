package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.api.core.DynamicRelationshipType;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.PropertyContainer;

public class SetRelationshipPropertyCase extends AbstractSetPropertyCase
{
    public SetRelationshipPropertyCase( Properties iterationCountConfig,
        Object value )
    {
        super( "S_RP", iterationCountConfig, value );
        // "Set the same relationship property many times in one tx"
    }

    @Override
    protected PropertyContainer createContainer( NeoService neo )
    {
        Node node1 = neo.createNode();
        Node node2 = neo.createNode();
        return node1.createRelationshipTo( node2,
            DynamicRelationshipType.withName( "TEST_RELATIONSHIP" ) );
    }
}
