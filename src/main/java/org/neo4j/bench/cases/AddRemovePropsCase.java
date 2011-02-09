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
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;

/**
 * o Add many new properties (on many different containers) in one tx
 * o Remove those properties in another tx
 */
public abstract class AddRemovePropsCase
    extends PropertyBenchCase
{
    public static final String SET_TIMER = "s";
    public static final String REMOVE_TIMER = "r";
    protected static final int PROPERTY_COUNT = 100;
    
    public AddRemovePropsCase( Properties iterationCountConfig, Object value )
    {
        super( iterationCountConfig, value );
    }

    public void run( GraphDatabaseService graphDb )
    {
        timerOff( MAIN_TIMER );
        PropertyContainer[] containers = null;
        Transaction tx = graphDb.beginTx();
        try
        {
            containers = createContainers( graphDb, getNumberOfContainers() );
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOn( MAIN_TIMER );
        
        beginTransaction( SET_TIMER );
        tx = graphDb.beginTx();
        try
        {
            for ( PropertyContainer container : containers )
            {
                for ( int i = 0; i < PROPERTY_COUNT; i++ )
                {
                    container.setProperty( "key" + i, getPropertyValue() );
                }
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, SET_TIMER );
        }

        Iterable<Integer> randomInts = generateRandomInts();
        beginTransaction( REMOVE_TIMER );
        tx = graphDb.beginTx();
        try
        {
            for ( PropertyContainer container : containers )
            {
                for ( int index : randomInts )
                {
                    container.removeProperty( "key" + index );
                }
            }
            tx.success();
        }
        finally
        {
            finishTransaction( tx, REMOVE_TIMER );
        }
    }
    
    private int getNumberOfContainers()
    {
        return getNumberOfIterations() / PROPERTY_COUNT;
    }
    
    private Iterable<Integer> generateRandomInts()
    {
        List<Integer> list = new ArrayList<Integer>();
        int max = getNumberOfContainers();
        for ( int i = 0; i < max; i++ )
        {
            list.add( i );
        }
        Collections.shuffle( list );
        return list;
    }

    protected abstract PropertyContainer[] createContainers(
        GraphDatabaseService graphDb, int count );
}
