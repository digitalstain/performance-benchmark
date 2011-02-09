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
import org.neo4j.bench.cases.GetRelationshipsCase;
import org.neo4j.bench.cases.MinReadTxCase;
import org.neo4j.bench.cases.MinWriteTxCase;
import org.neo4j.bench.cases.OverspillingLastTransactionCase;
import org.neo4j.bench.cases.SetSameNodePropCase;
import org.neo4j.bench.cases.SetSameRelPropCase;
import org.neo4j.bench.cases.ValueGenerator;
import org.neo4j.helpers.Args;

public class RunBench extends RunUtil
{
    public static void main( String[] args ) throws Exception
    {
        Args arguments = new Args( args );
        BenchCaseRunner runner = run( arguments );
        PrintStream out = new PrintStream( new FileOutputStream(
            getResultsFile( arguments ), true ) );
        Map<String, String> header = getHeader( arguments );
        runner.displayResult( header, new TabFormatter(), out );
    }
    
    public static Map<String, String> getHeader( Args arguments )
    {
        Map<String, String> header = new HashMap<String, String>();
        header.put( KEY_NEO_VERSION, arguments.get( KEY_NEO_VERSION, null ) );
        header.put( KEY_DATE, new SimpleDateFormat( DATE_FORMAT ).format( new Date() ) );
        return header;
    }
    
    public static BenchCaseRunner run( Args arguments )
        throws Exception
    {
        BenchCaseRunner runner = new BenchCaseRunner();
        Collection<BenchCase> cases = instantiateAllCases( arguments );
        cases = filterCases( cases, arguments );
        runner.run( cases.toArray( new BenchCase[ 0 ] ) );
        return runner;
    }
    
    private static Collection<BenchCase> filterCases(
        Collection<BenchCase> cases, Args arguments ) throws IOException
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

    private static Properties loadIterationsConfig( Args arguments ) throws IOException
    {
        String iterationCountsConfigFileName =
            arguments.get( KEY_ITERATIONS_FILE, "iterations.properties" );
        System.out.println( "itr:" + iterationCountsConfigFileName );
        Properties iterationCounts = null;
        iterationCounts = new Properties();
        iterationCounts.load( new FileInputStream(
            new File( iterationCountsConfigFileName ) ) );
        return iterationCounts;
    }
    
    private static Collection<BenchCase> instantiateAllCases(
        Args arguments ) throws IOException
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
        cases.add( new GetRelationshipsCase( iterationCounts ) );
        cases.add(new OverspillingLastTransactionCase( iterationCounts ));
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
