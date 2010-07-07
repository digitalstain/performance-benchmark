package org.neo4j.bench.remote;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.neo4j.helpers.Args;

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
        Args arguments = new Args( args );
        int port = arguments.getNumber( "port", 8080 ).intValue();
        RemoteController server = new RemoteController( port );
        server.start();
        System.out.println( "Remote controller started, you can reach it at " +
        	"http://<host>:" + port + "/run" );
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
