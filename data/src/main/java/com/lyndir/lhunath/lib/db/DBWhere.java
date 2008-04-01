/*
 *   Copyright 2005-2007 Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.lib.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO: WhereStatement<br>
 * 
 * @author lhunath
 */
public class DBWhere {

    private LinkedList<DBWhereNode> nodes;

    /**
     * Create a new WhereStatement instance.
     */
    public DBWhere() {

        nodes = new LinkedList<DBWhereNode>();
    }

    /**
     * Create a new WhereStatement instance by requiring all given nodes to resolve true for the expression to resolve
     * true.
     * 
     * @param where
     *        You must pass a sequence of String and Object objects. These represent the column name to check against,
     *        and the value to check that column for.
     */
    public DBWhere(Object... where) {

        this();

        String column = null;
        for (Object whereNode : where)
            if (column == null)
                if (whereNode instanceof String)
                    column = String.class.cast( whereNode );
                else
                    throw new IllegalArgumentException( "Arguments must be a sequence of String and Object objects." );
            else {
                and( column, whereNode );
                column = null;
            }
    }

    /**
     * A where statement that matches all rows.
     * 
     * @return Guess.
     */
    public static DBWhere matchAll() {

        return new DBWhere();
    }

    /**
     * A where statement that matches all rows.<br>
     * <br>
     * The key may also be a {@link DBTable}. In this case, its primary ID is used instead.
     * 
     * @param <T>
     *        Same as type.
     * @param type
     *        The type of database object to generate using the database data.
     * @param key
     *        The value of the primary key column.
     * @return Guess.
     */
    public static <T extends DBTable> DBWhere matchKey(Class<T> type, Object key) {

        if (key instanceof DBTable)
            key = ((DBTable) key).getId();

        return new DBWhere( DBTable.getId( type ), key );
    }

    /**
     * Add a new element to this where expression using the AND operation. The where expression will be true if the
     * original where expression would resolve to true, and the given value exists in the given column.<br>
     * <br>
     * The values may also be {@link DBTable}s. In this case, their primary ID is used instead.
     * 
     * @param column
     *        The name of the column to check against the given value.
     * @param value
     *        The value the given column must match for this element to resolve to true.
     * @return A reference to this where expression.
     */
    public DBWhere and(String column, Object value) {

        if (value instanceof DBTable)
            value = ((DBTable) value).getId();

        nodes.add( new DBWhereNode( DBWhereNodeType.AND, column, value ) );
        return this;
    }

    /**
     * Add a new element to this where expression using the AND operation. The where expression will be true if the
     * original where expression would resolve to true, and the given value exists in the primary key column of the
     * given type's table.<br>
     * <br>
     * The value may also be a {@link DBTable}. In this case, its primary ID is used instead.
     * 
     * @param <T>
     *        Same as type.
     * @param type
     *        The type of database object to generate using the database data.
     * @param value
     *        The value the given column must match for this element to resolve to true.
     * @return A reference to this where expression.
     */
    public <T extends DBTable> DBWhere andKey(Class<T> type, Object value) {

        return and( DBTable.getId( type ), value );
    }

    /**
     * Add a new element to this where expression using the OR operation. The where expression will be true if the
     * original where expression would resolve to true, or the given value exists in the given column.<br>
     * <br>
     * The values may also be {@link DBTable}s. In this case, their primary ID is used instead.
     * 
     * @param column
     *        The name of the column to check against the given value.
     * @param value
     *        The value the given column must match for this element to resolve to true.
     * @return A reference to this where expression.
     */
    public DBWhere or(String column, Object value) {

        if (value instanceof DBTable)
            value = ((DBTable) value).getId();

        nodes.add( new DBWhereNode( DBWhereNodeType.OR, column, value ) );
        return this;
    }

    /**
     * Add a new element to this where expression using the OR operation. The where expression will be true if the
     * original where expression would resolve to true, or the given value exists in the primary key column of the given
     * type's table.<br>
     * <br>
     * The value may also be a {@link DBTable}. In this case, its primary ID is used instead.
     * 
     * @param <T>
     *        Same as type.
     * @param type
     *        The type of database object to generate using the database data.
     * @param value
     *        The value the given column must match for this element to resolve to true.
     * @return A reference to this where expression.
     */
    public <T extends DBTable> DBWhere orKey(Class<T> type, Object value) {

        return or( DBTable.getId( type ), value );
    }

    /**
     * Invert the previous where node.
     * 
     * @return A reference to this where expression.
     */
    public DBWhere not() {

        nodes.getLast().invert();
        return this;
    }

    /**
     * Generate a string expression representing this where expression in an SQL statement.
     * 
     * @return Guess.
     */
    @Override
    public String toString() {

        StringBuffer sql = new StringBuffer();
        for (DBWhereNode node : nodes)
            sql.append( " " + node.toString() );

        /* Delete the first AND or OR and trim whitespace. */
        return sql.delete( 0, 4 ).toString().trim();
    }

    /**
     * Retrieve the nodes of this Where.
     * 
     * @return Guess.
     */
    public List<DBWhereNode> getNodes() {

        return nodes;
    }

    /**
     * Retrieve all the values for this where expression.
     * 
     * @return Guess.
     */
    public List<Object> values() {

        List<Object> values = new ArrayList<Object>();
        for (DBWhereNode node : nodes)
            values.add( node.getValue() );

        return values;
    }

    /**
     * The definition of a singular where condition.
     */
    public class DBWhereNode {

        private DBWhereNodeType test;
        private String          column;
        private Object          value;

        /**
         * Create a new WhereNodes instance.
         * 
         * @param test
         *        Either one of the test fields. See {@link DBWhere#or(String, Object)} and
         *        {@link DBWhere#and(String, Object)}.
         * @param column
         *        The name of the column to test.
         * @param value
         *        The value of the column for the test to succeed.
         */
        public DBWhereNode(DBWhereNodeType test, String column, Object value) {

            this.test = test;
            this.column = column;
            this.value = value;
        }

        /**
         * Invert the type for this node.
         */
        public void invert() {

            test = test.inverse();
        }

        /**
         * Retrieve the column of this Where.WhereNode.
         * 
         * @return Guess.
         */
        public String getColumn() {

            return column;
        }

        /**
         * Retrieve the match value.
         * 
         * @return Guess.
         */
        public Object getValue() {

            return value;
        }

        /**
         * Retrieve the type of this Where.WhereNode.
         * 
         * @return Guess.
         */
        public DBWhereNodeType getType() {

            return test;
        }

        /**
         * Convert this object into an expression that can be used in an SQL statement.
         * 
         * @return Guess.
         */
        @Override
        public String toString() {

            if (value == null)
                return String.format( "%s %s IS NULL", test, column );

            return String.format( "%s %s = %s", test, column, value );
        }
    }

    /**
     * The type of condition for a where node.
     */
    public enum DBWhereNodeType {

        /**
         * This node defines an optional check.
         */
        OR,

        /**
         * This node defines a required check.
         */
        AND,

        /**
         * This node defines an optional check.
         */
        OR_NOT,

        /**
         * This node defines a required check.
         */
        AND_NOT;

        /**
         * Get the inverse type of this type.
         * 
         * @return Guess.
         */
        public DBWhereNodeType inverse() {

            if (isInversed())
                return valueOf( name().replace( "_NOT", "" ) );

            return valueOf( name() + "_NOT" );
        }

        /**
         * Check whether this type is NOT-ed.
         * 
         * @return <code>true</code> if this type includes a NOT.
         */
        public boolean isInversed() {

            return name().contains( "NOT" );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {

            return name().replace( '_', ' ' );
        }
    }
}
