package org.neo4j.bench.remote;

import java.io.FileInputStream;
import java.util.Properties;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;

public class RemoteController
{
    private final Server server;
    
    public RemoteController( int port )
    {
        server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort( port );
        server.addConnector( connector );
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping( RemoteServlet.class, "/*" );
        server.addHandler( handler );
    }
    
    public void start() throws Exception
    {
        server.start();
    }
    
    public void stop() throws Exception
    {
        server.stop();
    }
    
    public static void main( String[] args ) throws Exception
    {
        Properties config = new Properties();
        config.load( new FileInputStream( "remote-config.properties" ) );
        RemoteController server = new RemoteController(
            Integer.parseInt( config.getProperty( "port", "8080" ) ) );
        server.start();
        System.out.println( "Remote controller started..." );
        try
        {
            while ( true )
            {
                Thread.sleep( 1000 );
            }
        }
        finally
        {
            server.stop();
        }
    }
}
