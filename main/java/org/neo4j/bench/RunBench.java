package org.neo4j.bench;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.neo4j.bench.cases.AddRemoveNodePropsCase;
import org.neo4j.bench.cases.AddRemoveRelPropsCase;
import org.neo4j.bench.cases.ComplexStructureCase;
import org.neo4j.bench.cases.CreateDeleteNodeCase;
import org.neo4j.bench.cases.CreateDeleteRelsCase;
import org.neo4j.bench.cases.MinReadTxCase;
import org.neo4j.bench.cases.MinWriteTxCase;
import org.neo4j.bench.cases.SetSameNodePropCase;
import org.neo4j.bench.cases.SetSameRelPropCase;
import org.neo4j.bench.cases.ValueGenerator;

public class RunBench extends RunUtil
{
    public static void main( String[] args ) throws Exception
    {
        Map<String, String> arguments = parseArguments( args );
        BenchCaseRunner runner = new BenchCaseRunner();
        Collection<BenchCase> cases = instantiateAllCases( arguments );
        cases = filterCases( cases, arguments );
        runner.run( cases.toArray( new BenchCase[ 0 ] ) );
        
        PrintStream out = new PrintStream( new FileOutputStream(
            getResultsFile( arguments ), true ) );
        Map<String, String> header = new HashMap<String, String>();
        header.put( KEY_NEO_VERSION,
            arguments.get( KEY_NEO_VERSION ) );
        header.put( KEY_DATE,
            new SimpleDateFormat( DATE_FORMAT ).format( new Date() ) );
        runner.displayResult( header, new TabFormatter(), out );
    }
    
    private static Collection<BenchCase> filterCases(
        Collection<BenchCase> cases, Map<String, String> arguments )
        throws IOException
    {
        WeightedPattern[] filters = loadFilters( arguments );
        if ( filters == null )
        {
            return cases;
        }
        
        Collection<BenchCase> result = new ArrayList<BenchCase>();
        for ( BenchCase benchCase : cases )
        {
            if ( matches( filters, benchCase.toString() ) )
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
            arguments.get( KEY_ITERATIONS_FILE );
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
        
        // Add the cases to run (filters are applied later)
        cases.add( new CreateDeleteNodeCase( iterationCounts ) );
        cases.add( new CreateDeleteRelsCase( iterationCounts ) );
        cases.add( new MinWriteTxCase( iterationCounts ) );
        cases.add( new MinReadTxCase( iterationCounts ) );
        cases.add( new ComplexStructureCase( iterationCounts ) );
        for ( Object propertyValue : propertyValues )
        {
            cases.add( new AddRemoveNodePropsCase( iterationCounts,
                propertyValue ) );
        }
        for ( Object propertyValue : propertyValues )
        {
            cases.add( new AddRemoveRelPropsCase( iterationCounts,
                propertyValue ) );
        }
        for ( Object propertyValue : propertyValues )
        {
            cases.add( new SetSameNodePropCase( iterationCounts,
                propertyValue ) );
        }
        for ( Object propertyValue : propertyValues )
        {
            cases.add( new SetSameRelPropCase( iterationCounts,
                propertyValue ) );
        }
        return cases;
    }
}
