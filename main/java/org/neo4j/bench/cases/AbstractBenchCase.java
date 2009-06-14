package org.neo4j.bench.cases;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.neo4j.api.core.Transaction;
import org.neo4j.bench.BenchCase;

public abstract class AbstractBenchCase implements BenchCase
{
    public static final String POSTFIX_BEFORE_COMMIT = "b";
    public static final String POSTFIX_COMMIT = "c";
    
    private final String name;
    private final Properties iterationCountConfig;
    private Integer numberOfIterations;
    private Map<String, Timer> timers = new HashMap<String, Timer>();
    
    public AbstractBenchCase( String name, Properties iterationCountConfig )
    {
        this.name = name;
        this.iterationCountConfig = iterationCountConfig;
    }
    
    protected Integer calculateIterationCount( Properties iterationCountConfig )
    {
        String directMatch =
            iterationCountConfig.getProperty( getName(), null );
        if ( directMatch != null )
        {
            return Integer.parseInt( directMatch );
        }
        
        String defaultCount = iterationCountConfig.getProperty( "default",
            null );
        if ( defaultCount != null )
        {
            return Integer.parseInt( defaultCount );
        }
        return null;
    }

    public int getNumberOfIterations()
    {
        if ( this.numberOfIterations == null )
        {
            this.numberOfIterations =
                calculateIterationCount( iterationCountConfig );
        }
        return this.numberOfIterations;
    }
    
    private Timer getTimer( String name )
    {
        Timer result = this.timers.get( name );
        if ( result == null )
        {
            result = new Timer();
            this.timers.put( name, result );
        }
        return result;
    }

    public void timerOff( String whichTimer )
    {
        long time = System.nanoTime();
        Timer timer = getTimer( whichTimer );
        timer.timeSpent += ( time - timer.timeMarker );
    }

    public void timerOn( String whichTimer )
    {
        getTimer( whichTimer ).timeMarker = System.nanoTime();
    }

    public long getTime( String whichTimer )
    {
        return this.timers.get( whichTimer ).timeSpent;
    }
    
    protected void beginTransaction( String whichTimer )
    {
        timerOn( whichTimer );
        timerOn( whichTimer + POSTFIX_BEFORE_COMMIT );
    }
    
    protected void finishTransaction( Transaction tx, String whichTimer )
    {
        beforeCommit( whichTimer );
        tx.finish();
        afterCommit( whichTimer );
    }
    
    private void beforeCommit( String whichTimer )
    {
        timerOff( whichTimer + POSTFIX_BEFORE_COMMIT );
        timerOn( whichTimer + POSTFIX_COMMIT );
    }
    
    private void afterCommit( String whichTimer )
    {
        timerOff( whichTimer + POSTFIX_COMMIT );
        timerOff( whichTimer );
    }

    public Iterable<String> getTimers()
    {
        return this.timers.keySet();
    }

    public String getName()
    {
        return this.name;
    }
    
    @Override
    public String toString()
    {
        return getName();
    }
    
    private static class Timer
    {
        private long timeSpent;
        private long timeMarker;
    }
}
