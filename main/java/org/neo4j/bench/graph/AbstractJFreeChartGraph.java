package org.neo4j.bench.graph;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

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
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String line = null;
        AbstractDataset dataset = instantiateDataset();
        Map<String, String> header = null;
        while ( ( line = reader.readLine() ) != null )
        {
            if ( BenchCaseResult.isHeader( line ) )
            {
                header = BenchCaseResult.parseHeader( line );
                continue;
            }

            String[] tokens = line.split( Pattern.quote( "\t" ) );
            String columnKey = tokens[ 0 ] + "-" + tokens[ 1 ];
            double value = Double.parseDouble( tokens[ 2 ] );
            addValue( dataset, value,
                header.get( BenchCaseResult.HEADER_KEY_NEO_VERSION ),
                columnKey );
        }

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
        String rowKey, String columnKey );
    
    public void close()
    {
        setVisible( false );
    }

    protected abstract JFreeChart createChart( AbstractDataset dataset );
}
