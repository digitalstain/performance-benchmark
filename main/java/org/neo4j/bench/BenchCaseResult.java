package org.neo4j.bench;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class BenchCaseResult
{
    public static final String MAGIC_HEADER_START = ">>>>>";
    public static final String MAGIC_HEADER_END = "<<<<<";
    
    private final String name;
    private final Map<String, ResultData> data =
        new TreeMap<String, ResultData>();
    
    public BenchCaseResult( String name )
    {
        this.name = name;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public void add( String timer, int numberOfIterations, long time )
    {
        ResultData data = this.data.get( timer );
        if ( data == null )
        {
            data = new ResultData();
            this.data.put( timer, data );
        }
        data.time += time;
        data.numberOfIterations += numberOfIterations;
    }
    
    public Iterable<String> getTimers()
    {
        return this.data.keySet();
    }
    
    public ResultData getData( String timer )
    {
        return this.data.get( timer );
    }
    
    public static String serializeHeaderString( Map<String, String> header )
    {
        StringBuffer data = new StringBuffer();
        for ( Map.Entry<String, String> entry : header.entrySet() )
        {
            if ( data.length() > 0 )
            {
                data.append( ", " );
            }
            data.append( entry.getKey() + ":" + entry.getValue() );
        }
        return MAGIC_HEADER_START + data.toString() + MAGIC_HEADER_END;
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
    
    public static class ResultData
    {
        private long time;
        private int numberOfIterations;
        
        public long getTime()
        {
            return this.time;
        }
        
        public int getNumberOfIterations()
        {
            return this.numberOfIterations;
        }
    }
}
