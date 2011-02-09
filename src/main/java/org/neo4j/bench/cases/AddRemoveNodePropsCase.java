/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
