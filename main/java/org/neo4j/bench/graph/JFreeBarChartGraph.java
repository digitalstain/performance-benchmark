package org.neo4j.bench.graph;

import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.neo4j.bench.BenchCaseResult;

public class JFreeBarChartGraph extends AbstractJFreeChartGraph
{
    @Override
    protected AbstractDataset instantiateDataset()
    {
        return new DefaultCategoryDataset();
    }

    @Override
    protected JFreeChart createChart( AbstractDataset dataset )
    {
        return ChartFactory.createBarChart(
            "Performance chart", "Bench case", "Time(s)",
            ( DefaultCategoryDataset ) dataset, PlotOrientation.VERTICAL,
            true, true, false );
    }

    @Override
    protected void addValue( AbstractDataset dataset,
        Map<String, String> header, double value, int numberOfIterations,
        String benchCase, String timer )
    {
        ( ( DefaultCategoryDataset ) dataset ).addValue(
            value, header.get( BenchCaseResult.HEADER_KEY_NEO_VERSION ),
            getColumnName( benchCase, timer ) );
    }
}
