package org.neo4j.bench.chart;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface Chart
{
    void open( File file, Map<String, String> options ) throws IOException;
    
    void close();
}
