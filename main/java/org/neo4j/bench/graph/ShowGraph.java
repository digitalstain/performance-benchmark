package org.neo4j.bench.graph;

import java.io.File;

public class ShowGraph
{
    public static void main( String[] args ) throws Exception
    {
        File file = args.length > 0 ?
            new File( args[ 0 ] ) : new File( "results" );
            
        Graph graph = new JFreeChartGraph();
        graph.open( file );
    }
}
