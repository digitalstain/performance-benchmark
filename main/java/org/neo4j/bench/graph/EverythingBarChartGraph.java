package org.neo4j.bench.graph;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;

public class EverythingBarChartGraph extends AbstractBarChartGraph
{
    @Override
    protected void addValue( AbstractDataset dataset, double value,
        String rowKey, String columnKey )
    {
        ( ( DefaultCategoryDataset ) dataset ).addValue(
            value, rowKey, columnKey );
    }
}
