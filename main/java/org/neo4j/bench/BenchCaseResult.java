package org.neo4j.bench;

import java.util.Map;
import java.util.TreeMap;

public class BenchCaseResult
{
    private final String name;
    private final Map<String, MutableLong> timers =
        new TreeMap<String, MutableLong>();
    
    public BenchCaseResult( String name )
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void add( String timer, long time )
    {
        MutableLong value = this.timers.get( timer );
        if ( value == null )
        {
            value = new MutableLong();
            this.timers.put( timer, value );
        }
        value.value += time;
    }
    
    public Iterable<String> getTimers()
    {
        return this.timers.keySet();
    }
    
    public long getTime( String timer )
    {
        return this.timers.get( timer ).value;
    }
    
    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append( name + ":" );
        for ( Map.Entry<String, MutableLong> entry : this.timers.entrySet() )
        {
            result.append( "\n\t" + entry.getKey() + ": " +
                formatTimeString( entry.getValue().value ) );
        }
        return result.toString();
    }
    
    private String formatTimeString( long time )
    {
        long asMillis = time / 1000000;
        long asSeconds = asMillis / 1000;
        return asSeconds + "s";
    }
    
    private static class MutableLong
    {
        private long value;
    }
}
