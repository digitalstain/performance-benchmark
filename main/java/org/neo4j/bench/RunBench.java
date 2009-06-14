package org.neo4j.bench;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.neo4j.bench.cases.AddRemoveNodePropertiesCase;
import org.neo4j.bench.cases.AddRemoveRelationshipPropertiesCase;
import org.neo4j.bench.cases.CreateDeleteNodeCase;
import org.neo4j.bench.cases.CreateDeleteRelationshipsCase;
import org.neo4j.bench.cases.MinReadTxCase;
import org.neo4j.bench.cases.MinWriteTxCase;
import org.neo4j.bench.cases.SetNodePropertyCase;
import org.neo4j.bench.cases.SetRelationshipPropertyCase;
import org.neo4j.bench.cases.ValueGenerator;

public class RunBench
{
    public static void main( String[] args ) throws Exception
    {
        Map<String, String> arguments = RunUtil.parseArguments( args );
        BenchCaseRunner runner = new BenchCaseRunner();
        Collection<BenchCase> cases = instantiateAllCases( arguments );
        cases = filterCases( cases, arguments );
        runner.run( cases.toArray( new BenchCase[ 0 ] ) );
        
        PrintStream out = new PrintStream( new FileOutputStream(
            RunUtil.getResultsFile( arguments ), true ) );
        Map<String, String> header = new HashMap<String, String>();
        header.put( BenchCaseResult.HEADER_KEY_NEO_VERSION,
            arguments.get( BenchCaseResult.HEADER_KEY_NEO_VERSION ) );
        runner.displayResult( header, new TabFormatter(), out );
    }
    
    private static Collection<BenchCase> filterCases(
        Collection<BenchCase> cases, Map<String, String> arguments )
        throws IOException
    {
        String[] filters = RunUtil.loadBenchFilters( arguments );
        if ( filters == null )
        {
            return cases;
        }
        
        Collection<BenchCase> result = new ArrayList<BenchCase>();
        for ( BenchCase benchCase : cases )
        {
            if ( RunUtil.matchesAny( filters, benchCase.toString() ) )
            {
                result.add( benchCase );
            }
        }
        return result;
    }

    private static Properties loadIterationsConfig(
        Map<String, String> arguments ) throws IOException
    {
        String iterationCountsConfigFileName =
            arguments.get( "iterations-file" );
        iterationCountsConfigFileName = iterationCountsConfigFileName != null ?
            iterationCountsConfigFileName : "iterations.properties";
        Properties iterationCounts = null;
        iterationCounts = new Properties();
        iterationCounts.load( new FileInputStream(
            new File( iterationCountsConfigFileName ) ) );
        return iterationCounts;
    }
    
    private static Collection<BenchCase> instantiateAllCases(
        Map<String, String> arguments ) throws IOException
    {
        Collection<BenchCase> cases = new ArrayList<BenchCase>();
        Object[] propertyValues = new Object[] {
            ValueGenerator.BOOLEAN,
            ValueGenerator.BOOLEAN_ARRAY,
            ValueGenerator.BYTE,
            ValueGenerator.BYTE_ARRAY,
            ValueGenerator.CHARACTER,
            ValueGenerator.CHARACTER_ARRAY,
            ValueGenerator.SHORT,
            ValueGenerator.SHORT_ARRAY,
            ValueGenerator.INTEGER,
            ValueGenerator.INTEGER_ARRAY,
            ValueGenerator.LONG,
            ValueGenerator.LONG_ARRAY,
            ValueGenerator.FLOAT,
            ValueGenerator.FLOAT_ARRAY,
            ValueGenerator.DOUBLE,
            ValueGenerator.DOUBLE_ARRAY,
            ValueGenerator.STRING,
            ValueGenerator.STRING_ARRAY,
        };
      
        Properties iterationCounts = loadIterationsConfig( arguments );
        for ( Object propertyValue : propertyValues )
        {
            cases.add( new AddRemoveNodePropertiesCase( iterationCounts,
                propertyValue ) );
            cases.add( new AddRemoveRelationshipPropertiesCase( iterationCounts,
                propertyValue ) );
            cases.add( new SetNodePropertyCase( iterationCounts,
                propertyValue ) );
            cases.add( new SetRelationshipPropertyCase( iterationCounts,
                propertyValue ) );
        }
  
        cases.add( new CreateDeleteNodeCase( iterationCounts ) );
        cases.add( new CreateDeleteRelationshipsCase( iterationCounts ) );
        cases.add( new MinWriteTxCase( iterationCounts ) );
        cases.add( new MinReadTxCase( iterationCounts ) );
        return cases;
    }
}
