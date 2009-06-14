package org.neo4j.bench.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.ui.TextAnchor;
import org.neo4j.bench.RunUtil;

public class JFreeBarChartGraph extends AbstractJFreeChartGraph<AbstractDataset>
{
    private List<Integer> numberOfIterationsList;
    
    @Override
    protected AbstractDataset instantiateDataset()
    {
        this.numberOfIterationsList = new ArrayList<Integer>();
        return new DefaultCategoryDataset();
    }

    @Override
    protected JFreeChart createChart( AbstractDataset dataset )
    {
        JFreeChart chart = ChartFactory.createBarChart(
            "Performance chart", "Bench case", "Time(s)",
            ( DefaultCategoryDataset ) dataset, PlotOrientation.VERTICAL,
            true, true, false );
        
        rotateCategoryAxis( chart, Math.PI / 4.0 );
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = ( BarRenderer ) plot.getRenderer();
        renderer.setItemLabelGenerator( new CategoryItemLabelGenerator()
        {
            public String generateColumnLabel( CategoryDataset dataset,
                int column )
            {
                return "col" + column;
            }

            public String generateLabel( CategoryDataset dataset, int row,
                int column )
            {
                return "" + dataset.getValue( row, column ).intValue();
            }

            public String generateRowLabel( CategoryDataset dataset, int row )
            {
                return "row" + row;
            }
        } );
        renderer.setBaseItemLabelPaint( Color.black );
        renderer.setBasePositiveItemLabelPosition( new ItemLabelPosition(
            ItemLabelAnchor.INSIDE12, TextAnchor.CENTER_RIGHT,
            TextAnchor.CENTER_RIGHT, -Math.PI / 2.0 ) );
        renderer.setItemLabelsVisible( true );
        
        return chart;
    }

    @Override
    protected void addValue( AbstractDataset dataset,
        Map<String, String> header, double value, int numberOfIterations,
        String benchCase, String timer )
    {
        ( ( DefaultCategoryDataset ) dataset ).addValue(
            value, header.get( RunUtil.KEY_NEO_VERSION ),
            RunUtil.getNiceBenchCaseName( benchCase, timer,
                numberOfIterations ) );
        this.numberOfIterationsList.add( numberOfIterations );
    }
}
