package org.neo4j.bench.graph;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.AbstractDataset;
import org.neo4j.bench.BenchCase;

public abstract class AbstractJFreeChartGraph implements Graph
{
    public AbstractJFreeChartGraph()
    {
    }
    
    protected abstract AbstractDataset instantiateDataset();
    
    public void open( File file, Map<String, String> options )
        throws IOException
    {
        final AbstractDataset dataset = instantiateDataset();
        ResultParser parser = new ResultParser( new ResultHandler()
        {
            public void newResult( Map<String, String> header )
            {
            }

            public void value( Map<String, String> header, double value,
                int numberOfIterations, String benchCase, String timer )
            {
                addValue( dataset, header, value, numberOfIterations,
                    benchCase, timer );
            }
        } );
        parser.parse( file, options );

        JFreeChart chart = createChart( dataset );
        ChartPanel chartPanel = new ChartPanel( chart );
        Dimension dimensions = getDimensions();
        chartPanel.setPreferredSize( dimensions );
        File chartFile = new File( "chart-" + new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss" ).format( new Date() ) + ".jpg" );
        ChartUtilities.saveChartAsJPEG( chartFile, chart,
            ( int ) dimensions.getWidth(), ( int ) dimensions.getHeight() );
    }
    
    protected Dimension getDimensions()
    {
        return new Dimension( 1024, 600 );
    }
    
    protected abstract void addValue( AbstractDataset dataset,
        Map<String, String> header, double value, int numberOfIterations,
        String benchCase, String timer );
    
    protected String getColumnName( String benchCase, String timer )
    {
        if ( timer.equals( BenchCase.MAIN_TIMER ) )
        {
            return benchCase;
        }
        else
        {
            return benchCase + "-" + timer;
        }
    }
    
    public void close()
    {
    }

    protected abstract JFreeChart createChart( AbstractDataset dataset );
}
