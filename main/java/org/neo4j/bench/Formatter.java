package org.neo4j.bench;

import java.io.PrintStream;

public interface Formatter
{
    void format( BenchCaseResult result, PrintStream out );
}
