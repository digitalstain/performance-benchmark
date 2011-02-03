package org.neo4j.bench.remote;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.neo4j.bench.RunUtil;
import org.neo4j.bench.chart.Chart;
import org.neo4j.bench.chart.JFreeBarChart;
import org.neo4j.bench.chart.JFreeStackedBarChart;
import org.neo4j.helpers.Args;

public class RemoteClient extends RunUtil
{
    private final String server;
    private final int port;
    private final String root;

    public RemoteClient( String server, int port, String root )
    {
        this.server = server;
        this.port = port;
        if ( root != null && root.length() > 0 )
        {
            if ( root.endsWith( "/" ) )
            {
                this.root = root.substring( 0, root.length() - 1 );
            }
            else
            {
                this.root = root;
            }
        }
        else
        {
            this.root = "";
        }
    }

    public Reader run( String version ) throws IOException, URISyntaxException
    {
        if ( version != null )
        {
            return makeRequest( "/run", "neo-version=" + version );
        }
        else
        {
            return makeRequest( "/run" );
        }
    }

    public static void main( String[] args ) throws Exception
    {
        Args arguments = new Args( args );

        Properties config = new Properties();
        config.load( new FileInputStream( "remote-config.properties" ) );

        RemoteClient client = new RemoteClient( config.getProperty( "server",
            "localhost" ), Integer.parseInt( config
            .getProperty( "port", "8080" ) ), config.getProperty( "root", "/" ) );

        String version = arguments.get( KEY_NEO_VERSION, "1.0" );
        Reader result = client.run( version );

        String layout = arguments.get( KEY_LAYOUT, "bar" );
        Chart graph = null;
        if ( layout.equals( "bar" ) )
        {
            graph = new JFreeBarChart();
        }
        else if ( layout.equals( "stacked-bar" ) )
        {
            graph = new JFreeStackedBarChart();
        }

        graph.open( result, arguments );
    }

    private Reader makeRequest( String path, String... query )
        throws IOException, URISyntaxException
    {
        StringBuilder queryBuilder = new StringBuilder();
        String sep = "";
        for ( String part : query )
        {
            queryBuilder.append( sep );
            sep = "&";
            queryBuilder.append( part );
        }
        URI uri = new URI( "http", null, server, port, getPath( path ),
            queryBuilder.toString(), null );
        return new InputStreamReader( uri.toURL().openConnection()
            .getInputStream() );
    }

    private String getPath( String path )
    {
        if ( path.startsWith( "/" ) )
        {
            return root + path;
        }
        else
        {
            return root + "/" + path;
        }
    }
}
