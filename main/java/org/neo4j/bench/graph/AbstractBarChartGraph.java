package org.neo4j.bench.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;

public abstract class AbstractBarChartGraph extends AbstractJFreeChartGraph
{
    @Override
    protected AbstractDataset instantiateDataset()
    {
        return new DefaultCategoryDataset();
    }

    @Override
    protected JFreeChart createChart( AbstractDataset dataset )
    {
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Performance chart",                                                                                // title
            "Bench case", // domain axis label
            "Time(s)", // range axis label
            ( DefaultCategoryDataset ) dataset, // data
            PlotOrientation.VERTICAL, // orientation
            true, // include legend
            true, // tooltips?
            false // URLs?
            );

//        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
//
//        // set the background color for the chart...
//        chart.setBackgroundPaint( Color.white );
//
//        // get a reference to the plot for further customisation...
//        final CategoryPlot plot = chart.getCategoryPlot();
//        plot.setBackgroundPaint( Color.lightGray );
//        plot.setDomainGridlinePaint( Color.white );
//        plot.setRangeGridlinePaint( Color.white );
//
//        // set the range axis to display integers only...
//        final NumberAxis rangeAxis = ( NumberAxis ) plot.getRangeAxis();
//        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
//
//        // disable bar outlines...
//        final BarRenderer renderer = ( BarRenderer ) plot.getRenderer();
//        renderer.setDrawBarOutline( false );
//
//        // set up gradient paints for series...
//        final GradientPaint gp0 = new GradientPaint( 0.0f, 0.0f, Color.blue,
//            0.0f, 0.0f, Color.lightGray );
//        final GradientPaint gp1 = new GradientPaint( 0.0f, 0.0f, Color.green,
//            0.0f, 0.0f, Color.lightGray );
//        final GradientPaint gp2 = new GradientPaint( 0.0f, 0.0f, Color.red,
//            0.0f, 0.0f, Color.lightGray );
//        renderer.setSeriesPaint( 0, gp0 );
//        renderer.setSeriesPaint( 1, gp1 );
//        renderer.setSeriesPaint( 2, gp2 );
//
//        final CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setCategoryLabelPositions( CategoryLabelPositions
//            .createUpRotationLabelPositions( Math.PI / 6.0 ) );
//        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }
}
