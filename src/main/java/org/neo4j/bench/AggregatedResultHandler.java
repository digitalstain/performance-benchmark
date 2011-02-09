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
package org.neo4j.bench;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class AggregatedResultHandler implements ResultHandler
{
    private final ResultHandler handler;
    private final Map<String, Collection<String>> aggregations;
    
    private Map<String, String> currentHeader;
    private Map<String, AggregatedResult> results;
    
    public AggregatedResultHandler( ResultHandler innerHandler,
        Map<String, Collection<String>> aggregations )
    {
        this.handler = innerHandler;
        this.aggregations = aggregations;
    }
    
    public void newResult( Map<String, String> header )
    {
        this.currentHeader = header;
        this.results = new HashMap<String, AggregatedResult>();
    }
    
    private static String combineCaseAndTimer( String benchCase, String timer )
    {
        return benchCase + ":" + timer;
    }

    public void value( Map<String, String> header, double value,
        int numberOfIterations, String benchCase, String timer )
    {
        boolean aggregated = false;
        for ( String aggregationKey : aggregations.keySet() )
        {
            Collection<String> patterns = aggregations.get( aggregationKey );
            if ( RunUtil.matchesAny( patterns.toArray( new String[ 0 ] ),
                benchCase ) )
            {
                aggregated = true;
                String key = combineCaseAndTimer( aggregationKey, timer );
                AggregatedResult aggregatedResult = this.results.get( key );
                if ( aggregatedResult == null )
                {
                    aggregatedResult =
                        new AggregatedResult( numberOfIterations );
                    this.results.put( key, aggregatedResult );
                }
                aggregatedResult.add( value, numberOfIterations );
            }
        }
        
        if ( !aggregated )
        {
            this.handler.value( header, value, numberOfIterations, benchCase,
                timer );
        }
    }

    public void endResult()
    {
        for ( Map.Entry<String, AggregatedResult> entry :
            this.results.entrySet() )
        {
            AggregatedResult result = entry.getValue();
            String combinedKey = entry.getKey();
            String benchCase =
                combinedKey.substring( 0, combinedKey.lastIndexOf( ":" ) );
            String timer =
                combinedKey.substring( combinedKey.lastIndexOf( ":" ) + 1 );
            this.handler.value( this.currentHeader, result.getValue(),
                result.numberOfIterations, benchCase, timer );
        }
    }
    
    private static class AggregatedResult
    {
        private final int numberOfIterations;
        private double totalValue;
        private int counter;
        
        public AggregatedResult( int numberOfIterations )
        {
            this.numberOfIterations = numberOfIterations;
        }
        
        public void add( double value, int numberOfIterations )
        {
            if ( numberOfIterations != this.numberOfIterations )
            {
                throw new RuntimeException( "numberOfIterations differ " +
                    numberOfIterations + ", should be " +
                    this.numberOfIterations );
            }
            this.totalValue += value;
            this.counter++;
        }
        
        public double getValue()
        {
            return this.totalValue / this.counter;
        }
    }
}
