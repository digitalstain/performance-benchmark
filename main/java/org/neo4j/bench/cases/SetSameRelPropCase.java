package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;

public class SetSameRelPropCase extends SetSamePropCase
{
    public SetSameRelPropCase( Properties iterationCountConfig,
        Object value )
    {
        super( iterationCountConfig, value );
        // "Set the same relationship property many times in one tx"
    }

    @Override
    protected PropertyContainer createContainer( GraphDatabaseService graphDb )
    {
        Node node1 = graphDb.createNode();
        Node node2 = graphDb.createNode();
        return node1.createRelationshipTo( node2,
            DynamicRelationshipType.withName( "TEST_RELATIONSHIP" ) );
    }
}
