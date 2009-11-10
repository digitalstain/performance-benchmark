package org.neo4j.bench;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class RunUtil
{
    public static final String KEY_NEO_VERSION = "neo-version";
    public static final String KEY_RESULTS_FILE = "results-file";
    public static final String KEY_ITERATIONS_FILE = "iterations-file";
    public static final String KEY_BENCH_FILTER_FILE = "bench-filter-file";
    public static final String KEY_TIMER_FILTER = "timer-filter";
    public static final String KEY_AGGREGATIONS_FILE = "aggregations-file";
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
    
    public static Map<Boolean, Collection<String>> loadFilters(
        Map<String, String> arguments ) throws IOException
    {
        String filterFile = arguments.get( KEY_BENCH_FILTER_FILE );
        if ( filterFile == null || !new File( filterFile ).exists() )
        {
            return null;
        }
        
        Map<Boolean, Collection<String>> result =
            new HashMap<Boolean, Collection<String>>();
        BufferedReader reader = new BufferedReader(
            new FileReader( filterFile ) );
        String line = null;
        while ( ( line = reader.readLine() ) != null )
        {
            line = line.trim();
            if ( line.length() == 0 || line.startsWith( "#" ) )
            {
                continue;
            }
            
            char firstChar = line.charAt( 0 );
            Boolean type = firstChar != '-';
            Collection<String> lines = result.get( type );
            if ( lines == null )
            {
                lines = new ArrayList<String>();
                result.put( type, lines );
            }
            if ( line.startsWith( "+" ) ||
                line.startsWith( "-" ) )
            {
                line = line.substring( 1 );
            }
            lines.add( line );
        }
        return result;
    }
    
    public static Map<String, Collection<String>> loadAggregations(
        Map<String, String> arguments ) throws IOException
    {
        String file = arguments.get( KEY_AGGREGATIONS_FILE );
        if ( file == null )
        {
            return null;
        }
        
        Map<String, Collection<String>> result =
            new HashMap<String, Collection<String>>();
        Properties properties = new Properties();
        properties.load( new FileInputStream( new File( file ) ) );
        for ( Object pattern : properties.keySet() )
        {
            String name = properties.getProperty( ( String ) pattern );
            Collection<String> patterns = result.get( name );
            if ( patterns == null )
            {
                patterns = new ArrayList<String>();
                result.put( ( String ) name, patterns );
            }
            patterns.add( ( String ) pattern );
        }
        return result;
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
    
    public static boolean matches( Map<Boolean, Collection<String>> patterns,
        String toMatch )
    {
        if ( patterns == null )
        {
            return true;
        }
        
        String[] inclusionsPatters = patterns.get( true ) != null ?
            patterns.get( true ).toArray( new String[ 0 ] ) : null;
        String[] exclusionPatters = patterns.get( false ) != null ?
            patterns.get( false ).toArray( new String[ 0 ] ) : null;
        if ( exclusionPatters != null )
        {
            if ( matchesAny( exclusionPatters, toMatch ) )
            {
                return false;
            }
        }
        
        if ( inclusionsPatters != null )
        {
            if ( matchesAny( inclusionsPatters, toMatch ) )
            {
                return true;
            }
        }
        return false;
    }

    public static File getResultsFile( Map<String, String> arguments )
    {
        String fileName = arguments.get( KEY_RESULTS_FILE );
        fileName = fileName != null ? fileName : "results";
        return new File( fileName );
    }
    
    public static String shortenCount( int count )
    {
        int shortCount = count;
        String postFix = "";
        if ( count >= 1000000 )
        {
            shortCount /= 1000000;
            postFix = "M";
        }
        else if ( count >= 1000 )
        {
            shortCount /= 1000;
            postFix = "k";
        }
        return "" + shortCount + postFix;
        
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
            result += " (" + shortenCount( numberOfIterations ) + ")";
        }
        return result;
    }
}
