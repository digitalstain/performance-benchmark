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

/**
 * Arrays are size of 5
 */
public abstract class ValueGenerator
{
    public static final Boolean BOOLEAN = true;

    public static final Boolean[] BOOLEAN_ARRAY =
        new Boolean[] { true, false, false, true, true };
    
    public static final Byte BYTE = ( byte ) 123;

    public static final Byte[] BYTE_ARRAY =
        new Byte[] { 1, 2, 3, 4, 5 };
    
    public static final Character CHARACTER = '1';
    
    public static final Character[] CHARACTER_ARRAY =
        new Character[] { '1', '2', '3', '4', '5' };
    
    public static final Short SHORT = ( short ) 12345;
    
    public static final Short[] SHORT_ARRAY =
        new Short[] { 1, 12, 123, 1234, 12345 };

    public static final Integer INTEGER = 123456;
    
    public static final Integer[] INTEGER_ARRAY =
        new Integer[] { 1, 12, 123, 1234, 12345 };

    public static final Long LONG = 1234567L;
    
    public static final Long[] LONG_ARRAY =
        new Long[] { 1L, 12L, 123L, 1234L, 12345L };
    
    public static final Float FLOAT = 1234.56F;

    public static final Float[] FLOAT_ARRAY =
        new Float[] { 1.2F, 12.3F, 123.45F, 1234.56F, 12345.67F };
    
    public static final Double DOUBLE = 1234.56;

    public static final Double[] DOUBLE_ARRAY =
        new Double[] { 1.2, 12.3, 123.45, 1234.56, 12345.67 };
    
    public static final String STRING = "Just some value";
    
    public static final String[] STRING_ARRAY =
        new String[] { "Shortest", "Shorter string",
        "Not so short at all string",
        "This string is starting to become quite long",
        "Oh my god, this string doesn't even fit in one record, or does it?"
    };
}
