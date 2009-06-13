package org.neo4j.bench;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.bench.cases.CreateDeleteNodeCase;
import org.neo4j.bench.cases.CreateDeleteRelationshipsCase;
import org.neo4j.bench.cases.MinReadTxCase;
import org.neo4j.bench.cases.MinWriteTxCase;
import org.neo4j.bench.cases.ValueGenerator;

public class RunAllCases
{
    public static void main( String[] args ) throws Exception
    {
        String neoVersion = args[ 0 ];
        
        BenchCaseRunner runner = new BenchCaseRunner();
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
      
//        for ( Object propertyValue : propertyValues )
//        {
//            cases.add( new AddRemoveNodePropertiesCase( 10000,
//                propertyValue ) );
//            cases.add( new AddRemoveRelationshipPropertiesCase( 10000,
//                propertyValue ) );
//            cases.add( new SetNodePropertyCase( 10000, propertyValue ) );
//            cases.add( new SetRelationshipPropertyCase( 10000,
//                propertyValue ) );
//        }
  
        cases.add( new CreateDeleteNodeCase( 50000 ) );
        cases.add( new MinWriteTxCase( 10000 ) );
        cases.add( new MinReadTxCase( 20000 ) );
        cases.add( new CreateDeleteRelationshipsCase( 20000 ) );
        
        runner.run( cases.toArray( new BenchCase[ 0 ] ) );
        
        PrintStream out = new PrintStream( new FileOutputStream(
            new File( "results" ), true ) );
        Map<String, String> header = new HashMap<String, String>();
        header.put( BenchCaseResult.HEADER_KEY_NEO_VERSION, neoVersion );
        runner.displayResult( header, new TabFormatter(), out );
    }
}
