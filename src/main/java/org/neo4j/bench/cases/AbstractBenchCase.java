/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.bench.cases;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.neo4j.bench.BenchCase;
import org.neo4j.graphdb.Transaction;

/**
 * The abstract super class of (probably) all the cases
 */
public abstract class AbstractBenchCase implements BenchCase
{
    public static final String POSTFIX_BEFORE_COMMIT = "b";
    public static final String POSTFIX_COMMIT = "c";
    
    private final Properties iterationCountConfig;
    private Integer numberOfIterations;
    private Map<String, Timer> timers = new HashMap<String, Timer>();
    
    public AbstractBenchCase( Properties iterationCountConfig )
    {
        this.iterationCountConfig = iterationCountConfig;
    }
    
    protected String getClassCamelCaseName()
    {
        String className = getClass().getSimpleName();
        StringBuilder camels = new StringBuilder();
        for ( int i = 0; i < className.length(); i++ )
        {
            char ch = className.charAt( i );
            if ( Character.isUpperCase( ch ) )
            {
                camels.append( ch );
            }
        }
        String result = camels.toString();
        if ( result.endsWith( "C" ) )
        {
            result = result.substring( 0, camels.length() - 1 );
        }
        return result;
    }
    
    protected Properties getIterationCountConfig()
    {
        return this.iterationCountConfig;
    }
    
    protected int getIterationBaseCount()
    {
        String baseCountString =
            iterationCountConfig.getProperty( "base", "100000" );
        return Integer.parseInt( baseCountString );
    }
    
    protected Integer getDirectMatchIterationCount()
    {
        int baseCount = getIterationBaseCount();
        String directMatch =
            iterationCountConfig.getProperty( toString(), null );
        directMatch = directMatch != null ? directMatch :
            iterationCountConfig.getProperty( getName(), null );
        Integer result = null;
        if ( directMatch != null )
        {
            if ( directMatch.endsWith( "%" ) )
            {
                double percent = Double.parseDouble( directMatch.substring( 0,
                    directMatch.length() - 1 ) );
                percent /= 100d;
                result = ( int ) ( baseCount * percent );
            }
            else
            {
                result = Integer.parseInt( directMatch );
            }
        }
        return result;
    }
    
    protected Integer calculateIterationCount()
    {
        Integer result = getDirectMatchIterationCount();
        result = result != null ? result : getIterationBaseCount();
        return result;
    }

    public int getNumberOfIterations()
    {
        if ( this.numberOfIterations == null )
        {
            this.numberOfIterations = calculateIterationCount();
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
        long time = System.currentTimeMillis();
        Timer timer = getTimer( whichTimer );
        timer.timeSpent += ( time - timer.timeMarker );
    }

    public void timerOn( String whichTimer )
    {
        getTimer( whichTimer ).timeMarker = System.currentTimeMillis();
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
        return getClassCamelCaseName();
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
