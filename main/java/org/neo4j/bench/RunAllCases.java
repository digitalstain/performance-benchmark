package org.neo4j.bench;

import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.bench.cases.ManyRelationshipsCase;

public class RunAllCases
{
    public static void main( String[] args )
    {
        BenchCaseRunner runner = new BenchCaseRunner();
        Collection<BenchCase> cases = new ArrayList<BenchCase>();
        
//        cases.add( new MinWriteTxCase( 10000 ) );
//        cases.add( new MinReadTxCase( 200000 ) );
//        int propertyIterationCount = 1000;
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.BOOLEAN ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.BOOLEAN_ARRAY ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.BYTE ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.BYTE_ARRAY ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.CHARACTER ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.CHARACTER_ARRAY ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.SHORT ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.SHORT_ARRAY ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.INTEGER ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.INTEGER_ARRAY ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.LONG ) );
//        cases.add( new SetNodePropertyCase( propertyIterationCount,
//            ValueGenerator.LONG_ARRAY ) );
//
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.BOOLEAN ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.BOOLEAN_ARRAY ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.BYTE ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.BYTE_ARRAY ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.CHARACTER ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.CHARACTER_ARRAY ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.SHORT ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.SHORT_ARRAY ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.INTEGER ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.INTEGER_ARRAY ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.LONG ) );
//        cases.add( new SetRelationshipPropertyCase( propertyIterationCount,
//            ValueGenerator.LONG_ARRAY ) );
        
//        Object[] propertyValues = new Object[] {
//            ValueGenerator.BOOLEAN,
//            ValueGenerator.BOOLEAN_ARRAY,
//            ValueGenerator.BYTE,
//            ValueGenerator.BYTE_ARRAY,
//            ValueGenerator.CHARACTER,
//            ValueGenerator.CHARACTER_ARRAY,
//            ValueGenerator.INTEGER,
//            ValueGenerator.INTEGER_ARRAY,
//            ValueGenerator.LONG,
//            ValueGenerator.LONG_ARRAY,
//            ValueGenerator.SHORT,
//            ValueGenerator.SHORT_ARRAY,
//            ValueGenerator.STRING,
//            ValueGenerator.STRING_ARRAY,
//            ValueGenerator.FLOAT,
//            ValueGenerator.FLOAT_ARRAY,
//            ValueGenerator.DOUBLE,
//            ValueGenerator.DOUBLE_ARRAY,
//        };
//        
//        for ( Object propertyValue : propertyValues )
//        {
//            cases.add( new SetAndRemoveNodePropertiesCase( 100,
//                propertyValue ) );
//        }
//        for ( Object propertyValue : propertyValues )
//        {
//            cases.add( new SetAndRemoveRelationshipPropertiesCase( 100,
//                propertyValue ) );
//        }
        
//        cases.add( new CreateDeleteNodeCase( 100000 ) );
        
        cases.add( new ManyRelationshipsCase( 200000 ) );
        
        runner.run( cases.toArray( new BenchCase[ 0 ] ) );
        runner.displayResult();
    }
}
