package org.neo4j.bench.graph;

import java.io.File;
import java.io.IOException;

public interface Graph
{
    void open( File file ) throws IOException;
    
    void close();
}
