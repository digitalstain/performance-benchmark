package org.neo4j.bench;

import java.io.PrintStream;

public class TabFormatter implements Formatter
{
    public void format( BenchCaseResult result, PrintStream out )
    {
        for ( String timer : result.getTimers() )
        {
            long nanoTime = result.getTime( timer );
            long asMillis = nanoTime / 1000000;
            long asSeconds = asMillis / 1000;
            out.println( result.getName() + "\t" + timer + "\t" + asSeconds );
        }
    }
}
