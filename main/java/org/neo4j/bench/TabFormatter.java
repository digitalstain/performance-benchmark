package org.neo4j.bench;

import java.io.PrintStream;

import org.neo4j.bench.BenchCaseResult.ResultData;

public class TabFormatter implements Formatter
{
    public void format( BenchCaseResult result, PrintStream out )
    {
        for ( String timer : result.getTimers() )
        {
            ResultData data = result.getData( timer );
            long nanoTime = data.getTime();
            long asMillis = nanoTime / 1000000;
            out.println( result.getName() + "\t" + timer + "\t" +
                data.getNumberOfIterations() + "\t" + asMillis );
        }
    }
}
