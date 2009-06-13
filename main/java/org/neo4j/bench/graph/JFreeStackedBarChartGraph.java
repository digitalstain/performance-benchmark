package org.neo4j.bench.graph;

import java.util.Map;
import java.util.regex.Pattern;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.neo4j.bench.BenchCaseResult;

public class JFreeStackedBarChartGraph extends AbstractJFreeChartGraph
{
    private KeyToGroupMap groupMap;
    
    @Override
    protected JFreeChart createChart( AbstractDataset dataset )
    {
        JFreeChart chart = ChartFactory.createStackedBarChart(
            "Performance chart", "Bench case", "Time(s)",
            ( DefaultCategoryDataset ) dataset, PlotOrientation.VERTICAL,
            true, true, false );
        
        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
        CategoryPlot plot = ( CategoryPlot ) chart.getPlot();
        renderer.setSeriesToGroupMap( groupMap );
        plot.setRenderer( renderer );
        return chart;
    }

    @Override
    protected void addValue( AbstractDataset dataset,
        Map<String, String> header, double value,
        String benchCase, String subCase )
    {
        String[] subTokens = subCase.split( Pattern.quote( "." ) );
        String benchCaseWithSubCategory = benchCase + "-" + subTokens[ 0 ];
        groupMap.mapKeyToGroup( benchCase + "-" + subCase,
            benchCaseWithSubCategory );
        ( ( DefaultCategoryDataset ) dataset ).addValue( value,
            benchCase + "-" + subCase,
            header.get( BenchCaseResult.HEADER_KEY_NEO_VERSION ) );
    }

    @Override
    protected AbstractDataset instantiateDataset()
    {
        groupMap = new KeyToGroupMap( "whole" );
        return new DefaultCategoryDataset();
    }
}
