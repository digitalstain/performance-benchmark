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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.bench.RunUtil;

public class RemoteServlet extends HttpServlet
{
    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        verifySharedSecret( req );
        
        System.out.println( "got request:" + req.getPathInfo() );
        String command = req.getPathInfo();
        if ( command.equals( "/run" ) )
        {
            Map<String, String> requestArguments = composeArguments( req );
            runSvnUpProcess();
            runExternalBenchProcess( requestArguments );
            String results = requestArguments.get( RunUtil.KEY_RESULTS_FILE );
            File resultsFile = new File( results );
            if ( !resultsFile.exists() )
            {
                throw new ServletException( "Not successful, TODO why" );
            }
            
            PrintStream out = null;
            BufferedReader reader = null;
            try
            {
                out = new PrintStream( resp.getOutputStream() );
                reader = new BufferedReader( new FileReader( resultsFile ) );
                String line = null;
                while ( ( line = reader.readLine() ) != null )
                {
                    out.println( line );
                }
            }
            catch ( Exception e )
            {
                throw new ServletException( e );
            }
            finally
            {
                if ( reader != null )
                {
                    reader.close();
                }
                if ( out != null )
                {
                    out.close();
                }
            }
        }
    }

    private void verifySharedSecret( HttpServletRequest req )
        throws ServletException, IOException
    {
        File secretFile = new File( "secret" );
        if ( !secretFile.exists() )
        {
            // If no secret is specified then there's no security check.
            return;
        }
        
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader( new FileReader( secretFile ) );
            String secret = reader.readLine();
            String secretFromRequest = req.getParameter( "secret" );
            if ( secretFromRequest == null || !secret.equals( secretFromRequest ) )
            {
                throw new ServletException( "Invalid secret" );
            }
        }
        finally
        {
            reader.close();
        }
    }

    private void runSvnUpProcess() throws ServletException, IOException
    {
        try
        {
            Process process = Runtime.getRuntime().exec( new String[] {
                "svn",
                "up",
                new File( "." ).getAbsolutePath(),
            } );
            process.waitFor();
        }
        catch ( InterruptedException e )
        {
            throw new ServletException( e );
        }
    }

    private void runExternalBenchProcess( Map<String, String> requestArguments )
        throws ServletException, IOException
    {
        try
        {
            String[] processArgs = getProcessArgs( requestArguments );
            System.out.println( "running process " +
                Arrays.asList( processArgs ) );
            Process process = Runtime.getRuntime().exec( processArgs );
            process.waitFor();
        }
        catch ( InterruptedException e )
        {
            throw new ServletException( e );
        }
    }

    private String[] getProcessArgs( Map<String, String> requestArguments )
    {
        List<String> result = new ArrayList<String>();
        File path = new File( "." );
        result.add( new File( path, "run-bench" ).getAbsolutePath() );
//        result.add( "-cp" );
//        result.add( System.getProperty( "java.class.path" ) );
//        result.add( RunBench.class.getName() );
        for ( Map.Entry<String, String> entry : requestArguments.entrySet() )
        {
            result.add( "-D" + entry.getKey() + "=" + entry.getValue() );
        }
        return result.toArray( new String[ result.size() ] );
    }

    private Map<String, String> composeArguments( HttpServletRequest req )
    {
        Map<String, String> result = new HashMap<String, String>();
        result.put( RunUtil.KEY_RESULTS_FILE, "results-" +
            new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss" ).format(
                new Date() ) );
        Enumeration<?> names = req.getParameterNames();
        while ( names.hasMoreElements() )
        {
            String key = ( String ) names.nextElement();
            String value = req.getParameter( key );
            result.put( key, value );
        }
        return result;
    }
}
