package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class AddRemoveRelPropsCase extends AddRemovePropsCase
{
    public AddRemoveRelPropsCase( Properties iterationCountConfig,
        Object value )
    {
        super( iterationCountConfig, value );
    }

    @Override
    protected PropertyContainer[] createContainers(
            GraphDatabaseService graphDb, int count )
    {
        Relationship[] result = new Relationship[ count ];
        Node startNode = graphDb.createNode();
        RelationshipType type = DynamicRelationshipType.withName(
            "TEST_RELATIONSHIP" );
        for ( int i = 0; i < result.length; i++ )
        {
            Node endNode = graphDb.createNode();
            result[ i ] = startNode.createRelationshipTo( endNode, type );
            startNode = endNode;
        }
        return result;
    }
}
