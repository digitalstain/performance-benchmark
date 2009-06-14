package org.neo4j.bench.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.neo4j.bench.BenchCaseResult;
import org.neo4j.bench.RunUtil;

public class ResultParser
{
    private final ResultHandler handler;
    
    public ResultParser( ResultHandler handler )
    {
        this.handler = handler;
    }
    
    public void parse( File file, Map<String, String> options )
        throws IOException
    {
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String line = null;
        Map<String, String> header = null;
        
        String[] benchCaseFilters = RunUtil.loadBenchFilters( options );
        String timerFilterString = options.get( "timer-filter" );
        Pattern timerFilter = timerFilterString != null ?
            Pattern.compile( timerFilterString ) : null;
        
        while ( ( line = reader.readLine() ) != null )
        {
            if ( BenchCaseResult.isHeader( line ) )
            {
                header = BenchCaseResult.parseHeader( line );
                handler.newResult( header );
                continue;
            }

            String[] tokens = line.split( Pattern.quote( "\t" ) );
            String benchCase = tokens[ 0 ];
            String timer = tokens[ 1 ];
            int numberOfIterations = Integer.parseInt( tokens[ 2 ] );
            if ( !matchesBenchFilter( benchCaseFilters, benchCase ) ||
                !matches( timerFilter, timer ) )
            {
                continue;
            }
            
            double value = Double.parseDouble( tokens[ 3 ] );
            handler.value( header, value, numberOfIterations,
                benchCase, timer );
        }
    }
    
    private boolean matchesBenchFilter( String[] benchCaseFilters,
        String benchCase )
    {
        if ( benchCaseFilters == null )
        {
            return true;
        }
        
        for ( String filter : benchCaseFilters )
        {
            if ( matches( Pattern.compile( filter ), benchCase ) )
            {
                return true;
            }
        }
        return false;
    }

    private boolean matches( Pattern patternOrNull, String toMatch )
    {
        if ( patternOrNull != null )
        {
            if ( !patternOrNull.matcher( toMatch ).find() )
            {
                return false;
            }
        }
        return true;
    }
}
