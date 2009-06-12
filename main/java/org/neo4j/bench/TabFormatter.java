package org.neo4j.bench;

public class TabFormatter implements Formatter
{
    public String format( BenchCaseResult result )
    {
        StringBuffer buffer = new StringBuffer();
        int counter = 0;
        for ( String timer : result.getTimers() )
        {
            if ( counter++ > 0 )
            {
                buffer.append( "\n" );
            }
            buffer.append( result.getName() + "\t" + timer + "\t" +
                result.getTime( timer ) );
        }
        return buffer.toString();
    }
}
