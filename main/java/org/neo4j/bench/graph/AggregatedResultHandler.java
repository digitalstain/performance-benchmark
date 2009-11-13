package org.neo4j.bench.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.bench.ResultHandler;
import org.neo4j.bench.RunUtil;

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
