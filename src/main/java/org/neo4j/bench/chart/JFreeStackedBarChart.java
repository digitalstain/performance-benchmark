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

import java.util.Map;
import java.util.regex.Pattern;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.neo4j.bench.RunUtil;

public class JFreeStackedBarChart extends
    AbstractJFreeChart<ChartData> implements ChartDestination<JFreeChart>
{
    @Override
    protected void addValue( ChartData dataset, Map<String, String> header,
        double value, int numberOfIterations, String benchCase, String timer )
    {
        String[] subTokens = timer.split( Pattern.quote( "." ) );
        dataset.add( benchCase, header.get( RunUtil.KEY_NEO_VERSION ),
            subTokens[ 0 ], value );
    }

    @Override
    protected ChartData instantiateDataset()
    {
        return new ChartData( "Performance chart", "Bench case", "Time(ms)" );
    }

    @Override
    protected JFreeChart createChart( ChartData dataset )
    {
        return dataset.render( this );
    }

    public JFreeChart render( String title, CategoryPlot plot,
        AbstractRenderer renderer )
    {
        plot.getDomainAxis().setCategoryLabelPositions(
            CategoryLabelPositions
                .createUpRotationLabelPositions( Math.PI / 4.0 ) );
        return new JFreeChart( title, plot );
    }
}
