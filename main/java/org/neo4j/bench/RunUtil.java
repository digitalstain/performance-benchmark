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
        String benchFilterFile = arguments.get( "bench-filter-file" );
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
        String fileName = arguments.get( "results-file" );
        fileName = fileName != null ? fileName : "results";
        return new File( fileName );
    }
}
