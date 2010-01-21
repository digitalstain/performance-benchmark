package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;

public class AddRemoveNodePropsCase extends AddRemovePropsCase
{
    public AddRemoveNodePropsCase( Properties iterationCountConfig,
        Object value )
    {
        super( iterationCountConfig, value );
    }

    @Override
    protected PropertyContainer[] createContainers(
            GraphDatabaseService graphDb, int count )
    {
        PropertyContainer[] result = new PropertyContainer[ count ];
        for ( int i = 0; i < result.length; i++ )
        {
            result[ i ] = graphDb.createNode();
        }
        return result;
    }
}
