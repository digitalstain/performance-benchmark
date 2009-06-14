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
import org.neo4j.bench.BenchCase;
import org.neo4j.bench.RunUtil;

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
        Map<String, String> header, double value, int numberOfIterations,
        String benchCase, String timer )
    {
        String[] subTokens = timer.split( Pattern.quote( "." ) );
        String benchCaseWithSubCategory = benchCase + "-" + subTokens[ 0 ];
        groupMap.mapKeyToGroup( benchCase + "-" + timer,
            benchCaseWithSubCategory );
        ( ( DefaultCategoryDataset ) dataset ).addValue( value,
            benchCase + "-" + timer,
            header.get( RunUtil.KEY_NEO_VERSION ) );
    }

    @Override
    protected AbstractDataset instantiateDataset()
    {
        groupMap = new KeyToGroupMap( BenchCase.MAIN_TIMER );
        return new DefaultCategoryDataset();
    }
}
