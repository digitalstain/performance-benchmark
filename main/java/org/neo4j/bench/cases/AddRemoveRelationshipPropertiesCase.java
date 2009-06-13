package org.neo4j.bench.cases;

import org.neo4j.api.core.DynamicRelationshipType;
import org.neo4j.api.core.NeoService;
import org.neo4j.api.core.Node;
import org.neo4j.api.core.PropertyContainer;
import org.neo4j.api.core.Relationship;
import org.neo4j.api.core.RelationshipType;

public class AddRemoveRelationshipPropertiesCase extends
    AbstractAddRemovePropertiesCase
{
    public AddRemoveRelationshipPropertiesCase( int numberOfIterations,
        Object value )
    {
        super( "Add/remove relationship properties in one tx",
            numberOfIterations, value );
    }

    @Override
    protected PropertyContainer[] createContainers( NeoService neo, int count )
    {
        Relationship[] result = new Relationship[ count ];
        Node startNode = neo.createNode();
        RelationshipType type = DynamicRelationshipType.withName(
            "TEST_RELATIONSHIP" );
        for ( int i = 0; i < result.length; i++ )
        {
            Node endNode = neo.createNode();
            result[ i ] = startNode.createRelationshipTo( endNode, type );
            startNode = endNode;
        }
        return result;
    }
}
