/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
