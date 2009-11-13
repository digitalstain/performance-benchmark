package org.neo4j.bench.chart;

import java.io.File;
import java.util.Map;

import org.neo4j.bench.RunUtil;

public class GenerateChart extends RunUtil
{
    public static void main( String[] args ) throws Exception
    {
        Map<String, String> arguments = parseArguments( args );
        File file = RunUtil.getResultsFile( arguments );
        
        String layout = arguments.get( KEY_LAYOUT );
        layout = layout != null ? layout : "bar";
        Chart graph = null;
        if ( layout.equals( "bar" ) )
        {
            graph = new JFreeBarChart();
        }
        else if ( layout.equals( "stacked-bar" ) )
        {
            graph = new JFreeStackedBarChart();
        }
        
        graph.open( file, arguments );
    }
}
