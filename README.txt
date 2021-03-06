A little command line manual for using the performance benchmarks
-----------------------------------------------------------------

Using the benchmark components is comprised by calling one or more shell scripts
They really just forwards the calls to maven and the maven exec plugin so all
arguments passed to the scripts must be in the form -D<key>=<value>

Running the bench cases
	You can run the bench cases with the 'run-bench' script. It performs
	the cases you specify (or all if none are supplied) and concatenates the
	results to the 'results' file, if no specific results file is specified.
	
	Examples:
	
	./run-bench
	./run-bench -Dneo-version=1.0-b9-SNAPSHOT -Djvm-memory=1024M
	./run-bench -Dbench-filter-file=my-cases -Diterations-file=my-iterations
	
	Argument information:
	
	o neo-version:
		The version of neo that will run the bench cases
		(defaults to the one specified in the POM)
	o jvm-memory:
		The amount of memory the running JVM will use
		(there's a default value of 512M specified in the POM)
	o bench-filter-file:
		A plain-text file with a list of regex patterns. A bench case will
		be performed if any of those patterns matches its name
		(not the class name, the one returned from its #toString() method)
		If a line starts with a '-' then it's excluded. 
	o iterations-file:
		A plain-text file containing <key>=<value> pairs with configuration
		about the number of iterations each case will perform. The easiest way
		is to have a test's #toString() value as the <key>. There are also other
		ways, but that's implementation-specific. If none is specified the
		'iterations.properties' is used.
	o results-file:
		Which results file to concatenate the results to. If none is specified
		the file 'results' is used.

Generating charts
	With some bench data generated you can build charts with it.
	
	Examples:
	
	./generate-chart
	./generate-chart -Dbench-filter-file=my-cases -Dtimer-filter=^w$
	./generate-chart -Dlayout=bar
	
	Argument information:
	
	o bench-filter-file:
		See above
	o timer-filter:
		A regex pattern for matching which timers will be displayed.
		Each bench case can have one or more timers. All have the "w" timer
		which is short for "whole". Some have "c" for "create" or "d" for
		"delete" f.ex. They can in turn splitted into f.ex. "c.bc" and "c.c"
		which would mean "create (before commit)" and "create (commit)".
		We keep the names short since space in an issue in the charts.
	o layout:
		Which chart to use. Currently only "bar" is supported and is also the
		default value.
	o results-file:
		Which results file to read data from. If none is specified the file
		'results' is used.
	o aggregations-file:
		A .properties-style file with "pattern = alias" pairs. If a bench case
		matches a pattern it'll be included in that alias and each alias gets
		its own bar in the chart. All the values for an alias are combined and
		the mean value is used for that bar.

Generating references
	With that same generated bench data you can generate a simple and quick
	reference between the different result sets in the results file and see
	percentages between them. It can also alert you if f.ex. a later version
	of neo4j is slower than a previous version.
	
	Examples:
	
	./generate-reference
	./generate-reference -Dneo-version=1.0-b8 -Dbench-filter-file=my-cases
	./generate-reference -Dneo-version=1.0-b8 -Dtimer-filter=^c\.bc$
	
	Argument information:
	
	o neo-version:
		Which neo4j version (in the results file) should be the reference version
		with which the others are compared.
	o bench-filter-file:
		See above
	o timer-filter:
		See above
	o results-file:
		See above
	o aggregations-file:
		See above
