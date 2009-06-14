package org.neo4j.bench.graph;

import java.util.Map;

public interface ResultHandler
{
    void newResult( Map<String, String> header );
    
    void value( Map<String, String> header, double value,
        int numberOfIterations, String benchCase, String timer );
}
