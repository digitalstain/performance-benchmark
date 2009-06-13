package org.neo4j.bench.graph;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.AbstractDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.neo4j.bench.BenchCaseResult;

public abstract class AbstractJFreeChartGraph
    extends ApplicationFrame implements Graph
{
    public AbstractJFreeChartGraph()
    {
        super( "Neo performance graph" );
    }
    
    protected abstract AbstractDataset instantiateDataset();

    public void open( File file ) throws IOException
    {
        final AbstractDataset dataset = instantiateDataset();
        ResultParser parser = new ResultParser( new ResultHandler()
        {
            public void newResult( Map<String, String> header )
            {
            }

            public void value( Map<String, String> header, double value,
                String benchCase, String timerName )
            {
                addValue( dataset, value,
                    header.get( BenchCaseResult.HEADER_KEY_NEO_VERSION ),
                    benchCase, timerName );
            }
        } );
        parser.parse( file );

        JFreeChart chart = createChart( dataset );
        ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( getDimensions() );
        setContentPane( chartPanel );
        pack();
        RefineryUtilities.centerFrameOnScreen( this );
        setVisible( true );
    }
    
    protected Dimension getDimensions()
    {
        return new Dimension( 500, 270 );
    }
    
    protected abstract void addValue( AbstractDataset dataset, double value,
        String rowKey, String benchCase, String subCase );
    
    public void close()
    {
        setVisible( false );
    }

    protected abstract JFreeChart createChart( AbstractDataset dataset );
}
