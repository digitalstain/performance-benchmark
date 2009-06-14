package org.neo4j.bench.reference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.neo4j.bench.RunUtil;
import org.neo4j.bench.graph.ResultHandler;
import org.neo4j.bench.graph.ResultParser;

public class ShowReference extends RunUtil
{
    public static void main( String[] args ) throws Exception
    {
        Map<String, String> arguments = parseArguments( args );
        final List<OneResultData> dataset = new ArrayList<OneResultData>();
        final String[] benchFilters = loadBenchFilters( arguments );
        final String timeFilter = arguments.get( KEY_TIMER_FILTER );
        final OneResultData[] referenceData = new OneResultData[ 1 ];
        final String[] referenceVersion = new String[ 1 ];
        referenceVersion[ 0 ] = arguments.get( KEY_NEO_VERSION );
        ResultHandler handler = new ResultHandler()
        {
            OneResultData currentData = null;
            
            public void newResult( Map<String, String> header )
            {
                String version = header.get( KEY_NEO_VERSION );
                if ( referenceVersion[ 0 ] == null )
                {
                    referenceVersion[ 0 ] = version;
                }
                currentData = new OneResultData();
                currentData.version = version;
                dataset.add( currentData );
                if ( referenceVersion[ 0 ].equals( version ) )
                {
                    referenceData[ 0 ] = currentData;
                }
            }

            public void value( Map<String, String> header, double value,
                int numberOfIterations, String benchCase, String timer )
            {
                if ( matchesAny( benchFilters, benchCase ) &&
                    matches( timeFilter, timer ) )
                {
                    currentData.values.put(
                        getNiceBenchCaseName( benchCase, timer, null ),
                        ( int ) value );
                }
            }
        };
        
        File file = getResultsFile( arguments );
        new ResultParser( handler ).parse( file, arguments );
        
        calculateResults( dataset, referenceData[ 0 ] );
    }
    
    private static void calculateResults( List<OneResultData> dataset,
        OneResultData referenceData )
    {
        System.out.println( "Reference version " + referenceData.version );
        for ( OneResultData data : dataset )
        {
            if ( data == referenceData )
            {
                continue;
            }
            
            for ( Map.Entry<String, Integer> entry : data.values.entrySet() )
            {
                String key = entry.getKey();
                int referenceValue = referenceData.values.get( key );
                int dataValue = entry.getValue();
                double percentage =
                    ( double ) dataValue / ( double ) referenceValue;
                percentage *= 100.0;
                percentage = Math.round( percentage );
                String alert = percentage >= 110 ? "  AAAAAAHHHHH" : "";
                System.out.println( key + "\t\t" + percentage + "%" +
                    alert );
            }
        }
    }

    private static class OneResultData
    {
        private String version;
        private Map<String, Integer> values = new TreeMap<String, Integer>();
    }
}
