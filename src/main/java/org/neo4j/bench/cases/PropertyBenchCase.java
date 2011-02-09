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
package org.neo4j.bench.cases;

import java.util.Properties;

/**
 * Cases which deals with a certain property type throughout the test can
 * inherit from this class. It alters {@link #toString()} as well.
 */
public abstract class PropertyBenchCase extends AbstractBenchCase
{
    private final Object propertyValue;
    
    public PropertyBenchCase( Properties iterationCountConfig,
        Object propertyValue )
    {
        super( iterationCountConfig );
        this.propertyValue = propertyValue;
    }
    
    protected Object getPropertyValue()
    {
        return this.propertyValue;
    }
    
    @Override
    public String toString()
    {
        String valueString = null;
        Class<?> valueClass = this.propertyValue.getClass();
        if ( valueClass.isArray() )
        {
            Object[] values = ( Object[] ) this.propertyValue;
            valueString =
                values[ 0 ].getClass().getSimpleName().substring( 0, 2 ) + "[]";
        }
        else
        {
            valueString =
                this.propertyValue.getClass().getSimpleName().substring( 0, 2 );
        }
        return super.toString() + "," + valueString;
    }
}
