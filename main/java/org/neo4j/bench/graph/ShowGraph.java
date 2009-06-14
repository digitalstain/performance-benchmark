package org.neo4j.bench.graph;

import java.io.File;
import java.util.Map;

import org.neo4j.bench.RunUtil;

public class ShowGraph
{
    public static void main( String[] args ) throws Exception
    {
        Map<String, String> arguments = RunUtil.parseArguments( args );
        File file = RunUtil.getResultsFile( arguments );
        
        Graph graph = new JFreeBarChartGraph();
        graph.open( file, arguments );
    }
}
