package org.neo4j.bench;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;

public class BenchCaseRunner
{
    public static final String MAGIC_HEADER_START = ">>>>>";
    public static final String MAGIC_HEADER_END = "<<<<<";
    public static final String HEADER_KEY_NEO_VERSION = "neo-version";
    
    private NeoService neo;
    private final Map<String, BenchCaseResult> results =
        new TreeMap<String, BenchCaseResult>();
    
    protected NeoService instantiateNeoService()
    {
        return new EmbeddedNeo( getNeoPath() );
    }
    
    protected NeoService getNeoService()
    {
        return this.neo;
    }
    
    protected String getNeoPath()
    {
        return "neo";
    }
    
    protected void beforeCase( BenchCase benchCase )
    {
        File neoDir = new File( getNeoPath() );
        if ( neoDir.exists() )
        {
            for ( File neoFile : neoDir.listFiles() )
            {
                if ( neoFile.isFile() )
                {
                    neoFile.delete();
                }
            }
        }
        neo = instantiateNeoService();
    }
    
    protected void afterCase( BenchCase benchCase )
    {
        neo.shutdown();
    }

    public void run( BenchCase... benchCases )
    {
        for ( BenchCase benchCase : benchCases )
        {
            beforeCase( benchCase );
            System.out.println( "Running " + benchCase + "..." );
            benchCase.timerOn( BenchCase.MAIN_TIMER );
            benchCase.run( this.neo );
            benchCase.timerOff( BenchCase.MAIN_TIMER );
            afterCase( benchCase );
            
            String name = benchCase.toString();
            BenchCaseResult result = this.results.get( name );
            if ( result == null )
            {
                result = new BenchCaseResult( name );
                this.results.put( name, result );
            }
            
            for ( String timer : benchCase.getTimers() )
            {
                result.add( timer, benchCase.getTime( timer ) );
            }
        }
    }
    
    public Map<String, BenchCaseResult> getResult()
    {
        return this.results;
    }
    
    public void displayResult( Map<String, String> header, Formatter formatter,
        PrintStream out )
    {
        out.println( ">>>>>" + serializeHeader( header ) + "<<<<<" );
        Map<String, BenchCaseResult> result = getResult();
        for ( Map.Entry<String, BenchCaseResult> entry : result.entrySet() )
        {
            formatter.format( entry.getValue(), out );
        }
    }

    private String serializeHeader( Map<String, String> header )
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
        return buffer.toString();
    }
}
