package org.neo4j.bench;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class BenchCaseRunner
{
    private GraphDatabaseService graphDb;
    private final Map<String, BenchCaseResult> results =
        new TreeMap<String, BenchCaseResult>();
    
    protected GraphDatabaseService instantiateGraphDbService()
    {
        return new EmbeddedGraphDatabase( getGraphDbPath() );
    }
    
    protected GraphDatabaseService getGraphDbService()
    {
        return this.graphDb;
    }
    
    protected String getGraphDbPath()
    {
        return "neo4j";
    }
    
    protected void beforeCase( BenchCase benchCase )
    {
        File neoDir = new File( getGraphDbPath() );
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
        graphDb = instantiateGraphDbService();
    }
    
    protected void afterCase( BenchCase benchCase )
    {
        graphDb.shutdown();
    }

    public void run( BenchCase... benchCases )
    {
        int benchCaseCounter = 1;
        for ( BenchCase benchCase : benchCases )
        {
            beforeCase( benchCase );
            System.out.println( fillWithZeros( benchCaseCounter++, 2 ) + "/" +
                benchCases.length + " Running " + benchCase + " (" +
                benchCase.getClass().getSimpleName() + ")" +
                ", with size " + RunUtil.shortenCount(
                    benchCase.getNumberOfIterations() ) );
            benchCase.timerOn( BenchCase.MAIN_TIMER );
            benchCase.run( this.graphDb );
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
                result.add( timer, benchCase.getNumberOfIterations(),
                    benchCase.getTime( timer ) );
            }
        }
    }
    
    private static String fillWithZeros( int count, int length )
    {
        String result = String.valueOf( count );
        while ( result.length() < length )
        {
            result = "0" + result;
        }
        return result;
    }
    
    public Map<String, BenchCaseResult> getResult()
    {
        return this.results;
    }
    
    public void displayResult( Map<String, String> header, Formatter formatter,
        PrintStream out )
    {
        out.println( BenchCaseResult.serializeHeaderString( header ) );
        Map<String, BenchCaseResult> result = getResult();
        for ( Map.Entry<String, BenchCaseResult> entry : result.entrySet() )
        {
            formatter.format( entry.getValue(), out );
        }
    }
}
