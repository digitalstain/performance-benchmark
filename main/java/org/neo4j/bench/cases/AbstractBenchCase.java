package org.neo4j.bench.cases;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.api.core.Transaction;
import org.neo4j.bench.BenchCase;

public abstract class AbstractBenchCase implements BenchCase
{
    private final String name;
    private final int numberOfIterations;
    private Map<String, Timer> timers = new HashMap<String, Timer>();
    
    public AbstractBenchCase( String name, int numberOfIterations )
    {
        this.name = name;
        this.numberOfIterations = numberOfIterations;
    }
    
    public int getNumberOfIterations()
    {
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
        timerOn( whichTimer + ".before_commit" );
    }
    
    protected void finishTransaction( Transaction tx, String whichTimer )
    {
        beforeCommit( whichTimer );
        tx.finish();
        afterCommit( whichTimer );
    }
    
    private void beforeCommit( String whichTimer )
    {
        timerOff( whichTimer + ".before_commit" );
        timerOn( whichTimer + ".commit" );
    }
    
    private void afterCommit( String whichTimer )
    {
        timerOff( whichTimer + ".commit" );
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
        return getName() + "(" + getNumberOfIterations() + ")";
    }
    
    private static class Timer
    {
        private long timeSpent;
        private long timeMarker;
    }
}
