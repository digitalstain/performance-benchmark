package org.neo4j.bench.chart;

import java.io.File;
import java.io.FileReader;

import org.neo4j.bench.RunUtil;
import org.neo4j.helpers.Args;

public class GenerateChart extends RunUtil
{
    public static void main( String[] args ) throws Exception
    {
        Args arguments = new Args( args );
        File file = RunUtil.getResultsFile( arguments );
        
        String layout = arguments.get( KEY_LAYOUT, "bar" );
        Chart graph = null;
        if ( layout.equals( "bar" ) )
        {
            graph = new JFreeBarChart();
        }
        else if ( layout.equals( "stacked-bar" ) )
        {
            graph = new JFreeStackedBarChart();
        }
        
        graph.open( new FileReader( file ), arguments );
    }
}
