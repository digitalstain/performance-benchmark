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
package org.neo4j.bench.chart;

import java.util.HashSet;
import java.util.Set;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.DefaultCategoryDataset;

public class ChartData
{
    private final String title;
    private final DefaultCategoryDataset dataset;
    private final SubCategoryAxis domainAxis;
    private final ValueAxis rangeAxis;
    private KeyToGroupMap groupmap;
    private final Set<String> groups = new HashSet<String>();

    public ChartData( String title, String domainAxis, String rangeAxis )
    {
        this.title = title;
        this.dataset = new DefaultCategoryDataset();
        this.domainAxis = new SubCategoryAxis( domainAxis );
        this.rangeAxis = new NumberAxis( rangeAxis );
    }

    public <T> T render( ChartDestination<T> chart )
    {
        if ( groupmap == null )
        {
            throw new IllegalStateException(
                "Cannot render a chart without any data." );
        }
        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
        renderer.setSeriesToGroupMap( groupmap );
        CategoryPlot plot = new CategoryPlot( dataset, domainAxis, rangeAxis, renderer );
        return chart.render( title, plot, renderer );
    }

    public void add( String columnKey, String group, String shard, double value )
    {
        dataset.addValue( value, domainKey( group, shard ), columnKey );
    }

    // Implementation internals

    private DomainKey domainKey( String group, String shard )
    {
        final DomainKey key = new DomainKey( group, shard );
        if ( groups.add( group ) )
        {
            if ( groupmap == null )
            {
                groupmap = new KeyToGroupMap( group );
            }
            domainAxis.addSubCategory( group );
        }
        groupmap.mapKeyToGroup( key, group );
        return key;
    }

    private static class DomainKey implements Comparable<DomainKey>
    {
        public static final DomainKey NULL = new DomainKey( "", "" )
        {
            @Override
            public int compareTo( DomainKey other )
            {
                if ( this.equals( other ) )
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }
        };
        private final String group;
        private final String shard;
        private final int hash;

        public DomainKey( String group, String shard )
        {
            this.group = group;
            this.shard = shard;
            this.hash = ( group + shard ).hashCode();
        }

        public int compareTo( DomainKey other )
        {
            if ( NULL.equals( other ) )
            {
                return -NULL.compareTo( this );
            }
            int cmp = this.group.compareTo( other.group );
            if ( cmp == 0 )
            {
                cmp = this.shard.compareTo( other.shard );
            }
            return cmp;
        }

        @Override
        public boolean equals( Object obj )
        {
            return ( obj instanceof DomainKey ) && equalTo( ( DomainKey ) obj );
        }

        private boolean equalTo( DomainKey other )
        {
            return this == other
                || ( this.group.equals( other.group ) && this.shard
                    .equals( other.shard ) );
        }

        @Override
        public String toString()
        {
            return group + " " + shard;
        }

        @Override
        public int hashCode()
        {
            return hash;
        }
    }
}
