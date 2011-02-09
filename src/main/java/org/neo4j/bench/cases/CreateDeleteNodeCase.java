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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * Create/delete many nodes in one tx.
 * 
 * Three timers
 * o Creates many in a tx
 * o getNodeById in another tx
 * o Deletes them in another tx
 */
public class CreateDeleteNodeCase extends AbstractBenchCase
{
    public static final String CREATE_TIMER = "c";
    public static final String GET_TIMER = "g";
    public static final String DELETE_TIMER = "d";
    
    public CreateDeleteNodeCase( Properties iterationCountConfig )
    {
        super( iterationCountConfig );
    }
    
    public void run( GraphDatabaseService graphDb )
    {
        beginTransaction( CREATE_TIMER );
        Collection<Node> nodes = new ArrayList<Node>();
        Transaction tx = graphDb.beginTx();
        try
        {
            int max = getNumberOfIterations();
            for ( int i = 0; i < max; i++ )
            {
                nodes.add( graphDb.createNode() );
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, CREATE_TIMER );
        }
        
        timerOn( GET_TIMER );
        tx = graphDb.beginTx();
        try
        {
            for ( Node node : nodes )
            {
                graphDb.getNodeById( node.getId() );
            }
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOff( GET_TIMER );
        
        beginTransaction( DELETE_TIMER );
        tx = graphDb.beginTx();
        try
        {
            for ( Node node : nodes )
            {
                node.delete();
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, DELETE_TIMER );
        }
    }
}
