package org.neo4j.bench;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.bench.cases.AddRemoveNodePropertiesCase;
import org.neo4j.bench.cases.AddRemoveRelationshipPropertiesCase;
import org.neo4j.bench.cases.CreateDeleteNodeCase;
import org.neo4j.bench.cases.CreateDeleteRelationshipsCase;
import org.neo4j.bench.cases.MinReadTxCase;
import org.neo4j.bench.cases.MinWriteTxCase;
import org.neo4j.bench.cases.SetNodePropertyCase;
import org.neo4j.bench.cases.SetRelationshipPropertyCase;
import org.neo4j.bench.cases.ValueGenerator;

public class RunAllCases
{
    public static void main( String[] args ) throws Exception
    {
        String neoVersion = args[ 0 ];
        
        BenchCaseRunner runner = new BenchCaseRunner();
        Collection<BenchCase> cases = new ArrayList<BenchCase>();
        
        Object[] propertyValues = new Object[] {
//            ValueGenerator.BOOLEAN,
//            ValueGenerator.BOOLEAN_ARRAY,
//            ValueGenerator.BYTE,
//            ValueGenerator.BYTE_ARRAY,
//            ValueGenerator.CHARACTER,
//            ValueGenerator.CHARACTER_ARRAY,
//            ValueGenerator.SHORT,
//            ValueGenerator.SHORT_ARRAY,
            ValueGenerator.INTEGER,
            ValueGenerator.INTEGER_ARRAY,
//            ValueGenerator.LONG,
//            ValueGenerator.LONG_ARRAY,
//            ValueGenerator.FLOAT,
//            ValueGenerator.FLOAT_ARRAY,
//            ValueGenerator.DOUBLE,
//            ValueGenerator.DOUBLE_ARRAY,
//            ValueGenerator.STRING,
//            ValueGenerator.STRING_ARRAY,
        };
      
        for ( Object propertyValue : propertyValues )
        {
            int addRemoveCount = 100000;
            int setCount = 500000;
            if ( propertyValue.getClass().isArray() )
            {
                addRemoveCount /= 3;
                setCount /= 50;
            }
            
            cases.add( new AddRemoveNodePropertiesCase( addRemoveCount,
                propertyValue ) );
            cases.add( new AddRemoveRelationshipPropertiesCase( addRemoveCount,
                propertyValue ) );
            cases.add( new SetNodePropertyCase( setCount, propertyValue ) );
            cases.add( new SetRelationshipPropertyCase( setCount,
                propertyValue ) );
        }
  
        cases.add( new CreateDeleteNodeCase( 200000 ) );
        cases.add( new CreateDeleteRelationshipsCase( 75000 ) );
        cases.add( new MinWriteTxCase( 10000 ) );
        cases.add( new MinReadTxCase( 200000 ) );
        
        runner.run( cases.toArray( new BenchCase[ 0 ] ) );
        
        PrintStream out = new PrintStream( new FileOutputStream(
            new File( "results" ), true ) );
        Map<String, String> header = new HashMap<String, String>();
        header.put( BenchCaseResult.HEADER_KEY_NEO_VERSION, neoVersion );
        runner.displayResult( header, new TabFormatter(), out );
    }
}
