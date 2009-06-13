package org.neo4j.bench.graph;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface Graph
{
    void open( File file, Map<String, String> options ) throws IOException;
    
    void close();
}
