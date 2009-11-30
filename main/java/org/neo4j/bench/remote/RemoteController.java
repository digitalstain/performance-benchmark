package org.neo4j.bench.remote;

import java.util.Map;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.ServletHandler;
import org.neo4j.bench.RunUtil;

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
        Map<String, String> arguments = RunUtil.parseArguments( args );
        String portString = arguments.get( "port" );
        portString = portString != null ? portString : "8080";
        RemoteController server = new RemoteController(
            Integer.parseInt( portString ) );
        server.start();
        System.out.println( "Remote controller started, you can reach it at " +
        	"http://<host>:" + portString + "/run" );
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
