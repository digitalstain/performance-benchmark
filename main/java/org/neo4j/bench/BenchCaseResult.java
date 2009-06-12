package org.neo4j.bench;

import java.util.Map;
import java.util.TreeMap;

public class BenchCaseResult
{
    private final String name;
    private final Map<String, MutableLong> times =
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
        MutableLong value = this.times.get( timer );
        if ( value == null )
        {
            value = new MutableLong();
            this.times.put( timer, value );
        }
        value.value += time;
    }
    
    @Override
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append( name + ": " );
        MutableLong mainTime = this.times.get( BenchCase.MAIN_TIMER );
        if ( mainTime != null )
        {
            result.append( formatTimeString( mainTime.value ) );
        }
        
        for ( Map.Entry<String, MutableLong> entry : this.times.entrySet() )
        {
            if ( !entry.getKey().equals( BenchCase.MAIN_TIMER ) )
            {
                result.append( "\n\t" + entry.getKey() + ": " +
                    formatTimeString( entry.getValue().value ) );
            }
        }
        return result.toString();
    }
    
    private String formatTimeString( long time )
    {
        long asMillis = time / 1000000;
        long asSeconds = asMillis / 1000;
        return time + "ns, " + asMillis + "ms, " +
            asSeconds + "s";
    }
    
    private static class MutableLong
    {
        private long value;
    }
}
