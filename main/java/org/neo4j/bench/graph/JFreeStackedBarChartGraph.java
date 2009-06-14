package org.neo4j.bench.graph;

import java.util.Map;
import java.util.regex.Pattern;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.neo4j.bench.RunUtil;
import org.neo4j.bench.chart.ChartData;
import org.neo4j.bench.chart.ChartDestination;

public class JFreeStackedBarChartGraph extends
    AbstractJFreeChartGraph<ChartData> implements ChartDestination<JFreeChart>
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
        return new ChartData( "Performance chart", "Bench case", "Time(s)" );
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
