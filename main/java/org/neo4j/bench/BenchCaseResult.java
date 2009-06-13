package org.neo4j.bench;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class BenchCaseResult
{
    public static final String MAGIC_HEADER_START = ">>>>>";
    public static final String MAGIC_HEADER_END = "<<<<<";
    public static final String HEADER_KEY_NEO_VERSION = "neo-version";
    
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
    
    public static String serializeHeaderString( Map<String, String> header )
    {
        StringBuffer buffer = new StringBuffer();
        for ( Map.Entry<String, String> entry : header.entrySet() )
        {
            if ( buffer.length() > 0 )
            {
                buffer.append( ", " );
            }
            buffer.append( entry.getKey() + ":" + entry.getValue() );
        }
        return MAGIC_HEADER_START + buffer.toString() + MAGIC_HEADER_END;
    }
    
    public static boolean isHeader( String line )
    {
        return line.startsWith( BenchCaseResult.MAGIC_HEADER_START )
            && line.endsWith( BenchCaseResult.MAGIC_HEADER_END );
    }
    
    public static Map<String, String> parseHeader( String header )
    {
        String headerLine = header.substring(
            BenchCaseResult.MAGIC_HEADER_START.length(), header.length() -
                BenchCaseResult.MAGIC_HEADER_END.length() );
        Map<String, String> result = new HashMap<String, String>();
        String[] pairs = headerLine.split( Pattern.quote( "," ) );
        for ( String pair : pairs )
        {
            String[] tokens = pair.split( Pattern.quote( ":" ) );
            String key = tokens[ 0 ].trim();
            String value = tokens[ 1 ].trim();
            if ( value.startsWith( "\"" ) && value.endsWith( "\"" ) )
            {
                value = value.substring( 1, value.length() - 1 );
            }
            result.put( key, value );
        }
        return result;
    }
    
    private static class MutableLong
    {
        private long value;
    }
}
