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
import org.neo4j.graphdb.Transaction;

/**
 * Creates many nodes in separate tx:s, effectively testing the transaction
 * log performance.
 */
public class MinWriteTxCase extends AbstractBenchCase
{
    public MinWriteTxCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
    }

    public void run( GraphDatabaseService graphDb )
    {
        int max = getNumberOfIterations();
        for ( int i = 0; i < max; i++ )
        {
            Transaction tx = graphDb.beginTx();
            try
            {
                graphDb.createNode();
                tx.success();
            }
            finally
            {
                tx.finish();
            }
        }
    }
}
