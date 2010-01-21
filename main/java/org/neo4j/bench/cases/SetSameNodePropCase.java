package org.neo4j.bench.cases;

import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;

public class SetSameNodePropCase extends SetSamePropCase
{
    public SetSameNodePropCase( Properties iterationCountConfig,
        Object value )
    {
        super( iterationCountConfig, value );
        // "Set the same node property many times in one tx"
    }

    @Override
    protected PropertyContainer createContainer( GraphDatabaseService graphDb )
    {
        return graphDb.createNode();
    }
}
