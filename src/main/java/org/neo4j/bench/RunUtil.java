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

import org.neo4j.helpers.Args;

public class RunUtil
{
    public static final String KEY_NEO_VERSION = "neo-version";
    public static final String KEY_RESULTS_FILE = "results-file";
    public static final String KEY_ITERATIONS_FILE = "iterations-file";
    public static final String KEY_BENCH_FILTER_FILE = "bench-filter-file";
    public static final String KEY_TIMER_FILTER = "timer-filter";
    public static final String KEY_AGGREGATIONS_FILE = "aggregations-file";
    public static final String KEY_LAYOUT = "layout";
    public static final String KEY_DATE = "date";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";
    
    public static WeightedPattern[] loadFilters( Args arguments ) throws IOException
    {
        String filterFile = arguments.get( KEY_BENCH_FILTER_FILE, "default-bench-filter" );
        if ( filterFile == null || !new File( filterFile ).exists() )
        {
            return null;
        }
        
        Collection<WeightedPattern> result = new ArrayList<WeightedPattern>();
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
            boolean type = firstChar != '-';
            if ( line.startsWith( "+" ) ||
                line.startsWith( "-" ) )
            {
                line = line.substring( 1 );
            }
            result.add( new WeightedPattern( line, type ) );
        }
        return result.toArray( new WeightedPattern[ result.size() ] );
    }
    
    public static Map<String, Collection<String>> loadAggregations(
        Args arguments ) throws IOException
    {
        String file = arguments.get( KEY_AGGREGATIONS_FILE, null );
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
            if ( !Pattern.compile( patternOrNull ).matcher( toMatch ).matches() )
            {
                return false;
            }
        }
        return true;
    }
    
    public static boolean matches( WeightedPattern[] patterns, String toMatch )
    {
        if ( patterns == null )
        {
            return true;
        }
        
        for ( WeightedPattern pattern : patterns )
        {
            if ( matches( pattern.pattern, toMatch ) )
            {
                return pattern.trueForInclusive;
            }
        }
        return false;
    }

    public static File getResultsFile( Args arguments )
    {
        return new File( arguments.get( KEY_RESULTS_FILE, "results" ) );
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
    
    public static class WeightedPattern
    {
        private final String pattern;
        private final boolean trueForInclusive;
        
        WeightedPattern( String pattern, boolean trueForInclusive )
        {
            this.pattern = pattern; 
            this.trueForInclusive = trueForInclusive;
        }

        public String getPattern()
        {
            return pattern;
        }

        public boolean isTrueForInclusive()
        {
            return trueForInclusive;
        }
    }
}
