package org.neo4j.bench.graph;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.neo4j.bench.BenchCaseRunner;

public class JFreeChartGraph extends ApplicationFrame implements Graph
{
    public JFreeChartGraph()
    {
        super( "Neo performance graph" );
    }

    public void open( File file ) throws IOException
    {
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String line = null;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, String> header = null;
        while ( ( line = reader.readLine() ) != null )
        {
            if ( line.startsWith( BenchCaseRunner.MAGIC_HEADER_START )
                && line.endsWith( BenchCaseRunner.MAGIC_HEADER_END ) )
            {
                String headerLine = line.substring(
                    BenchCaseRunner.MAGIC_HEADER_START.length(), line.length()
                        - BenchCaseRunner.MAGIC_HEADER_END.length() );
                header = parseHeader( headerLine );
                continue;
            }

            String[] tokens = line.split( Pattern.quote( "\t" ) );
            String columnKey = tokens[ 0 ] + "-" + tokens[ 1 ];
            double value = Double.parseDouble( tokens[ 2 ] );
            dataset.addValue( value, header
                .get( BenchCaseRunner.HEADER_KEY_NEO_VERSION ), columnKey );
        }

        JFreeChart chart = createChart( dataset );
        ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( new Dimension( 500, 270 ) );
        setContentPane( chartPanel );
        pack();
        RefineryUtilities.centerFrameOnScreen( this );
        setVisible( true );
    }
    
    public void close()
    {
        setVisible( false );
    }

    private JFreeChart createChart( final CategoryDataset dataset )
    {
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            "Performance chart",                                                                                // title
            "Bench case", // domain axis label
            "Time(s)", // range axis label
            dataset, // data
            PlotOrientation.VERTICAL, // orientation
            true, // include legend
            true, // tooltips?
            false // URLs?
            );

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
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

    private Map<String, String> parseHeader( String header )
    {
        Map<String, String> result = new HashMap<String, String>();
        String[] pairs = header.split( Pattern.quote( "," ) );
        for ( String pair : pairs )
        {
            String[] tokens = pair.split( Pattern.quote( ":" ) );
            String key = tokens[ 0 ].trim();
            String value = tokens[ 1 ].trim();
            if ( value.startsWith( "\"" ) && value.endsWith( "\"" ) )
            {
                value = value.substring( 1, value.length() - 1 );
            }
            result.put( key, value );
        }
        return result;
    }
}
