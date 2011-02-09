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

import java.lang.reflect.Array;
import java.util.Properties;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;

/**
 * A super class for setting a property on the same container with the same key
 * many times in the same tx.
 */
public abstract class SetSamePropCase extends PropertyBenchCase
{
    private PropertyContainer container;
    
    public SetSamePropCase( Properties iterationCountConfig,
        Object value )
    {
        super( iterationCountConfig, value );
    }

    @Override
    protected Integer calculateIterationCount()
    {
        Integer result = super.calculateIterationCount();
        if ( result != null )
        {
            if ( getPropertyValue().getClass().isArray() )
            {
                String division = getIterationCountConfig().getProperty(
                    "setArrayPropertyDivision", "1" );
                result /= Integer.parseInt( division );
            }
        }
        return result;
    }

    public void run( GraphDatabaseService graphDb )
    {
        timerOff( MAIN_TIMER );
        Transaction tx = graphDb.beginTx();
        try
        {
            this.container = createContainer( graphDb );
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        timerOn( MAIN_TIMER );
        
        tx = graphDb.beginTx();
        try
        {
            int max = getNumberOfIterations();
            Object propertyValue = getPropertyValue();
            if ( getPropertyValue().getClass().isArray() )
            {
                int arraySize = Array.getLength( getPropertyValue() );
                max /= arraySize;
            }
            for ( int i = 0; i < max; i++ )
            {
                container.setProperty( "my_key", propertyValue );
            }
            tx.success();
        }
        finally
        {
            tx.finish();
        }
        container = null;
    }
    
    protected abstract PropertyContainer createContainer(
            GraphDatabaseService graphDb );
}
