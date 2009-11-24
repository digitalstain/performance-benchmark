package org.neo4j.bench.chart;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public interface Chart
{
    void open( Reader input, Map<String, String> options ) throws IOException;
    
    void close();
}
