package org.neo4j.bench.chart;

import java.io.IOException;
import java.io.Reader;

import org.neo4j.helpers.Args;

public interface Chart
{
    void open( Reader input, Args options ) throws IOException;
    
    void close();
}
