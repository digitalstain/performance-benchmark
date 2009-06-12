package org.neo4j.bench;

import org.neo4j.api.core.NeoService;

public interface BenchCase
{
    public static final String MAIN_TIMER = "whole";
    
    int getNumberOfIterations();
    
    void run( NeoService neo );
    
    void timerOn( String whichTimer );
    
    void timerOff( String whichTimer );
    
    Iterable<String> getTimers();
    
    long getTime( String whichTimer );
}
