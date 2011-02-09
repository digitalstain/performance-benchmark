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
import java.util.Random;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

public class OverspillingLastTransactionCase extends AbstractBenchCase
{
    public OverspillingLastTransactionCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
    }

    private static final int TX_SIZE = 10000;
    private static final Random r = new Random( System.currentTimeMillis() );
    
    public void run( GraphDatabaseService graphDb )
    {
        int max = getNumberOfIterations()/TX_SIZE;
        final RelationshipType TESTING = DynamicRelationshipType.withName( "TESTING" );
        int count = 0;
        for ( int k = 0; k < max; k++ )
        {
            Transaction tx = graphDb.beginTx();
            try
            {
                for ( int i = 0; i < TX_SIZE; i++ )
                {
                    Node node = graphDb.createNode();
                    node.setProperty( "name", "A NAME WITH ID " + count );
                    count++;
                    if ( count < 100 )
                    {
                        continue;
                    }
                    for ( int j = 0; j < 5; j++ )
                    {
                        Node otherNode = graphDb.getNodeById( r.nextInt( count ) );
                        node.createRelationshipTo( otherNode, TESTING );
                    }
                }
                tx.success();
            }
            finally
            {
                tx.finish();
            }
        }
    }
}
