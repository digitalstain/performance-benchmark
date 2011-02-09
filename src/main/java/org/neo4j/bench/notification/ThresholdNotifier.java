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
package org.neo4j.bench.notification;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.neo4j.bench.AggregatedResultHandler;
import org.neo4j.bench.ResultHandler;
import org.neo4j.bench.ResultParser;
import org.neo4j.bench.RunUtil;
import org.neo4j.helpers.Args;

/**
 * The following work pretty much in the same way as the report generator does,
 * but instead of creating statistics, it simply checks if any statistic is
 * higher than a specified threshold, over which it exits with an error code
 * (!=0). This is supposed to be used by the shell to take appropriate action.
 */
public class ThresholdNotifier extends RunUtil
{
    public static void main( String[] args ) throws Exception
    {
        Args arguments = new Args( args );
        final List<OneResultData> dataset = new ArrayList<OneResultData>();
        final WeightedPattern[] benchFilters = loadFilters( arguments );
        final String timeFilter = arguments.get( KEY_TIMER_FILTER, null );
        final OneResultData[] referenceData = new OneResultData[1];
        final String[] referenceVersion = new String[1];
        referenceVersion[0] = arguments.get( KEY_NEO_VERSION, null );
        ResultHandler handler = new ResultHandler()
        {
            OneResultData currentData = null;

            public void newResult( Map<String, String> header )
            {
                String version = header.get( KEY_NEO_VERSION );
                if ( referenceVersion[0] == null )
                {
                    referenceVersion[0] = version;
                }
                currentData = new OneResultData( header );
                dataset.add( currentData );
                if ( referenceVersion[0].equals( version ) )
                {
                    referenceData[0] = currentData;
                }
            }

            public void value( Map<String, String> header, double value,
                    int numberOfIterations, String benchCase, String timer )
            {
                if ( matches( benchFilters, benchCase )
                     && matches( timeFilter, timer ) )
                {
                    currentData.values.put(
                            getNiceBenchCaseName( benchCase, timer, null ),
                            (int) value );
                }
            }

            public void endResult()
            {
            }
        };

        Map<String, Collection<String>> aggregations = RunUtil.loadAggregations( arguments );
        if ( aggregations != null )
        {
            handler = new AggregatedResultHandler( handler, aggregations );
        }

        File file = getResultsFile( arguments );
        new ResultParser( handler ).parse( new FileReader( file ), arguments );

        if ( checkExceedsThreshold( dataset, referenceData[0] ) )
        {
            System.exit( 1 );
        }
        else
        {
            System.exit( 0 );
        }
    }

    private static boolean checkExceedsThreshold( List<OneResultData> dataset,
            OneResultData referenceData )
    {
        System.out.println( "Reference version "
                            + referenceData.header.get( RunUtil.KEY_NEO_VERSION ) );
        for ( OneResultData data : dataset )
        {
            if ( data == referenceData )
            {
                continue;
            }

            System.out.println();
            System.out.println( "" + data.header );
            for ( Map.Entry<String, Integer> entry : data.values.entrySet() )
            {
                String key = entry.getKey();
                int referenceValue = referenceData.values.get( key );
                int dataValue = entry.getValue();
                double percentage = (double) dataValue
                                    / (double) referenceValue;
                percentage *= 100.0;
                percentage = Math.round( percentage );
                if ( percentage > 110 )
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static class OneResultData
    {
        private Map<String, String> header;
        private Map<String, Integer> values = new TreeMap<String, Integer>();

        OneResultData( Map<String, String> header )
        {
            this.header = header;
        }
    }
}
