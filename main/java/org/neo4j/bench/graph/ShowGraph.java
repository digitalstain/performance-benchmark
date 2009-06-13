package org.neo4j.bench.graph;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ShowGraph
{
    public static void main( String[] args ) throws Exception
    {
        Map<String, String> arguments = parseArguments( args );
        String fileName = arguments.get( "file" );
        fileName = fileName != null ? fileName : "results";
        File file = new File( fileName );
        
        Graph graph = new JFreeBarChartGraph();
        graph.open( file, arguments );
    }

    private static Map<String, String> parseArguments( String[] args )
    {
        Map<String, String> result = new HashMap<String, String>();
        for ( String arg : args )
        {
            if ( arg.startsWith( "-" ) )
            {
                arg = arg.substring( 1 );
                String[] tokens = arg.split( Pattern.quote( "=" ) );
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
}
