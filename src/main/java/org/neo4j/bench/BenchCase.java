package org.neo4j.bench;

import org.neo4j.graphdb.GraphDatabaseService;

public interface BenchCase
{
    public static final String MAIN_TIMER = "w";
    
    int getNumberOfIterations();
    
    void run( GraphDatabaseService graphDb );
    
    void timerOn( String whichTimer );
    
    void timerOff( String whichTimer );
    
    Iterable<String> getTimers();
    
    long getTime( String whichTimer );
    
    String getName();
}
