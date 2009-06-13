package org.neo4j.bench.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import org.neo4j.bench.BenchCaseResult;

public class ResultParser
{
    private final ResultHandler handler;
    
    public ResultParser( ResultHandler handler )
    {
        this.handler = handler;
    }
    
    public void parse( File file ) throws IOException
    {
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String line = null;
        Map<String, String> header = null;
        while ( ( line = reader.readLine() ) != null )
        {
            if ( BenchCaseResult.isHeader( line ) )
            {
                header = BenchCaseResult.parseHeader( line );
                handler.newResult( header );
                continue;
            }

            String[] tokens = line.split( Pattern.quote( "\t" ) );
            double value = Double.parseDouble( tokens[ 2 ] );
            handler.value( header, value, tokens[ 0 ], tokens[ 1 ] );
        }
    }
}
