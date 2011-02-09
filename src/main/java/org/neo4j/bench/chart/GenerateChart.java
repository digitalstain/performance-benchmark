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
package org.neo4j.bench.chart;

import java.io.File;
import java.io.FileReader;

import org.neo4j.bench.RunUtil;
import org.neo4j.helpers.Args;

public class GenerateChart extends RunUtil
{
    public static void main( String[] args ) throws Exception
    {
        Args arguments = new Args( args );
        File file = RunUtil.getResultsFile( arguments );
        
        String layout = arguments.get( KEY_LAYOUT, "bar" );
        Chart graph = null;
        if ( layout.equals( "bar" ) )
        {
            graph = new JFreeBarChart();
        }
        else if ( layout.equals( "stacked-bar" ) )
        {
            graph = new JFreeStackedBarChart();
        }
        
        graph.open( new FileReader( file ), arguments );
    }
}
