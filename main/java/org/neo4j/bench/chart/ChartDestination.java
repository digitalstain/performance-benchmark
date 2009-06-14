package org.neo4j.bench.chart;

import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.AbstractRenderer;

public interface ChartDestination<T>
{
    T render( String title, CategoryPlot plot, AbstractRenderer renderer );
}
