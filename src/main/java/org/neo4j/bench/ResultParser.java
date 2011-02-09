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
package org.neo4j.bench;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.regex.Pattern;

import org.neo4j.bench.RunUtil.WeightedPattern;
import org.neo4j.helpers.Args;

public class ResultParser
{
    private final ResultHandler handler;
    
    public ResultParser( ResultHandler handler )
    {
        this.handler = handler;
    }
    
    public void parse( Reader input, Args options ) throws IOException
    {
        BufferedReader reader = new BufferedReader( input );
        String line = null;
        Map<String, String> header = null;
        
        WeightedPattern[] benchCaseFilters = RunUtil.loadFilters( options );
        String timerFilterString = options.get( RunUtil.KEY_TIMER_FILTER, "w" );
        int lineCounter = 0;
        
        while ( ( line = reader.readLine() ) != null )
        {
            if ( BenchCaseResult.isHeader( line ) )
            {
                if ( lineCounter > 0 )
                {
                    handler.endResult();
                }
                header = BenchCaseResult.parseHeader( line );
                handler.newResult( header );
                continue;
            }

            String[] tokens = line.split( Pattern.quote( "\t" ) );
            String benchCase = tokens[ 0 ];
            String timer = tokens[ 1 ];
            int numberOfIterations = Integer.parseInt( tokens[ 2 ] );
            if ( !RunUtil.matches( benchCaseFilters, benchCase ) ||
                !RunUtil.matches( timerFilterString, timer ) )
            {
                continue;
            }
            
            double value = Double.parseDouble( tokens[ 3 ] );
            handler.value( header, value, numberOfIterations,
                benchCase, timer );
            lineCounter++;
        }
        
        if ( lineCounter > 0 )
        {
            handler.endResult();
        }
    }
}
