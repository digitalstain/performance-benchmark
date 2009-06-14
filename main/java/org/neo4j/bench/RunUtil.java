package org.neo4j.bench;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RunUtil
{
    public static final String KEY_NEO_VERSION = "neo-version";
    public static final String KEY_RESULTS_FILE = "results-file";
    public static final String KEY_ITERATIONS_FILE = "iterations-file";
    public static final String KEY_BENCH_FILTER_FILE = "bench-filter-file";
    public static final String KEY_TIMER_FILTER = "timer-filter";
    public static final String KEY_LAYOUT = "layout";
    
    public static Map<String, String> parseArguments( String[] args )
    {
        Map<String, String> result = new HashMap<String, String>();
        for ( String arg : args )
        {
            if ( arg.startsWith( "-" ) )
            {
                arg = arg.substring( 1 );
                String[] tokens = arg.split( Pattern.quote( "=" ) );
                if ( tokens.length < 2 || tokens[ 1 ] == null ||
                    tokens[ 1 ].trim().length() == 0 )
                {
                    continue;
                }
                result.put( tokens[ 0 ], tokens[ 1 ] );
            }
            else
            {
                throw new RuntimeException( "Arguments must be of type:" +
                    "-<key>=<value>" );
            }
        }
        return result;
    }
    
    public static String[] loadBenchFilters( Map<String, String> arguments )
        throws IOException
    {
        String benchFilterFile = arguments.get( KEY_BENCH_FILTER_FILE );
        if ( benchFilterFile == null )
        {
            return null;
        }
        return loadFilters( new File( benchFilterFile ) );
    }

    public static String[] loadFilters( File file ) throws IOException
    {
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String line = null;
        Collection<String> lines = new ArrayList<String>();
        while ( ( line = reader.readLine() ) != null )
        {
            lines.add( line );
        }
        return lines.toArray( new String[ lines.size() ] );
    }

    public static boolean matchesAny( String[] patternsOrNull, String toMatch )
    {
        if ( patternsOrNull == null )
        {
            return true;
        }
        
        for ( String pattern : patternsOrNull )
        {
            if ( matches( pattern, toMatch ) )
            {
                return true;
            }
        }
        return false;
    }

    public static boolean matches( String patternOrNull, String toMatch )
    {
        if ( patternOrNull != null )
        {
            if ( !Pattern.compile( patternOrNull ).matcher( toMatch ).find() )
            {
                return false;
            }
        }
        return true;
    }

    public static File getResultsFile( Map<String, String> arguments )
    {
        String fileName = arguments.get( KEY_RESULTS_FILE );
        fileName = fileName != null ? fileName : "results";
        return new File( fileName );
    }

    public static String getNiceBenchCaseName( String benchCase, String timer,
        Integer numberOfIterations )
    {
        String result = null;
        if ( timer.equals( BenchCase.MAIN_TIMER ) )
        {
            result = benchCase;
        }
        else
        {
            result = benchCase + "-" + timer;
        }
        
        if ( numberOfIterations != null )
        {
            int shortNumber = numberOfIterations;
            String postFix = "";
            if ( numberOfIterations >= 1000000 )
            {
                shortNumber /= 1000000;
                postFix = "M";
            }
            else if ( numberOfIterations >= 1000 )
            {
                shortNumber /= 1000;
                postFix = "k";
            }
            result += " (" + shortNumber + postFix + ")";
        }
        return result;
    }
}
