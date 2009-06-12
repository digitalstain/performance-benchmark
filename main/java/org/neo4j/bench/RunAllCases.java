package org.neo4j.bench;

import java.util.ArrayList;
import java.util.Collection;

import org.neo4j.bench.cases.CreateDeleteNodeCase;
import org.neo4j.bench.cases.ManyRelationshipsCase;
import org.neo4j.bench.cases.MinReadTxCase;
import org.neo4j.bench.cases.MinWriteTxCase;
import org.neo4j.bench.cases.SetAndRemoveNodePropertiesCase;
import org.neo4j.bench.cases.SetAndRemoveRelationshipPropertiesCase;
import org.neo4j.bench.cases.SetNodePropertyCase;
import org.neo4j.bench.cases.SetRelationshipPropertyCase;
import org.neo4j.bench.cases.ValueGenerator;

public class RunAllCases
{
    public static void main( String[] args )
    {
        int factor = 10;
        
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
      
        for ( Object propertyValue : propertyValues )
        {
            cases.add( new SetAndRemoveNodePropertiesCase( 100 * factor,
                propertyValue ) );
            cases.add( new SetAndRemoveRelationshipPropertiesCase( 100 * factor,
                propertyValue ) );
            cases.add( new SetNodePropertyCase( 100 * factor, propertyValue ) );
            cases.add( new SetRelationshipPropertyCase( 100 * factor,
                propertyValue ) );
        }
  
        cases.add( new CreateDeleteNodeCase( 10000 * factor ) );
        cases.add( new MinWriteTxCase( 1000 * factor ) );
        cases.add( new MinReadTxCase( 20000 * factor ) );
        cases.add( new ManyRelationshipsCase( 20000 * factor ) );
        
        runner.run( cases.toArray( new BenchCase[ 0 ] ) );
        runner.displayResult( new TabFormatter() );
    }
}
