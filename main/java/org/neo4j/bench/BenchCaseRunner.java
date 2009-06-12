package org.neo4j.bench;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.neo4j.api.core.EmbeddedNeo;
import org.neo4j.api.core.NeoService;

public class BenchCaseRunner
{
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
    
    public void displayResult()
    {
        System.out.println( "\nResults\n----------------------------" );
        Map<String, BenchCaseResult> result = getResult();
        for ( Map.Entry<String, BenchCaseResult> entry : result.entrySet() )
        {
            System.out.println( entry.getValue() );
        }
    }
}
