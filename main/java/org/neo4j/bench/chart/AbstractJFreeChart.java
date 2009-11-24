package org.neo4j.bench.chart;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.neo4j.bench.AggregatedResultHandler;
import org.neo4j.bench.ResultHandler;
import org.neo4j.bench.ResultParser;
import org.neo4j.bench.RunUtil;

public abstract class AbstractJFreeChart<T> implements Chart
{
    public AbstractJFreeChart()
    {
    }
    
    protected abstract T instantiateDataset();
    
    public void open( Reader input, Map<String, String> options )
        throws IOException
    {
        final T dataset = instantiateDataset();
        ResultHandler handler = new ResultHandler()
        {
            public void newResult( Map<String, String> header )
            {
            }

            public void value( Map<String, String> header, double value,
                int numberOfIterations, String benchCase, String timer )
            {
                addValue( dataset, header, ( int ) value,
                    numberOfIterations, benchCase, timer );
            }
            
            public void endResult()
            {
            }
        };
        
        Map<String, Collection<String>> aggregations =
            RunUtil.loadAggregations( options );
        if ( aggregations != null )
        {
            handler = new AggregatedResultHandler( handler, aggregations );
        }
        
        ResultParser parser = new ResultParser( handler );
        parser.parse( input, options );

        JFreeChart chart = createChart( dataset );
        ChartPanel chartPanel = new ChartPanel( chart );
        Dimension dimensions = getDimensions();
        chartPanel.setPreferredSize( dimensions );
        File chartFile = new File( "chart-" + new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss" ).format( new Date() ) + ".jpg" );
        ChartUtilities.saveChartAsJPEG( chartFile, chart,
            ( int ) dimensions.getWidth(), ( int ) dimensions.getHeight() );
    }
    
    protected void rotateCategoryAxis( JFreeChart chart, double piAngle )
    {
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions( piAngle ) );
    }
    
    protected Dimension getDimensions()
    {
        return new Dimension( 1024, 600 );
    }
    
    protected abstract void addValue( T dataset,
        Map<String, String> header, double value, int numberOfIterations,
        String benchCase, String timer );
    
    public void close()
    {
    }

    protected abstract JFreeChart createChart( T dataset );
}
