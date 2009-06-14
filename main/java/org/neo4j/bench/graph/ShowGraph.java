package org.neo4j.bench.graph;

import java.io.File;
import java.util.Map;

import org.neo4j.bench.RunUtil;

public class ShowGraph extends RunUtil
{
    public static void main( String[] args ) throws Exception
    {
        Map<String, String> arguments = parseArguments( args );
        File file = RunUtil.getResultsFile( arguments );
        
        String layout = arguments.get( KEY_LAYOUT );
        layout = layout != null ? layout : "bar";
        Graph graph = null;
        if ( layout.equals( "bar" ) )
        {
            graph = new JFreeBarChartGraph();
        }
        else if ( layout.equals( "stacked-bar" ) )
        {
            graph = new JFreeStackedBarChartGraph();
        }
        
        graph.open( file, arguments );
    }
}
