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
package com.lyndir.lhunath.lib.network.db;

import java.util.Map;

/**
 * <i>DBQuery - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * 
 * @author lhunath
 */
public class DBQuery {

    /**
     * The type of operation this query will execute on its rows.
     */
    public enum Type {
        /**
         * Retrieve table data.
         */
        SELECT,
        /**
         * Append table data.
         */
        INSERT,
        /**
         * Update table data.
         */
        UPDATE,
        /**
         * Delete table data.
         */
        DELETE,
        /**
         * Append or update table data depending on whether the given primary key already exists.
         */
        REPLACE,
    }

    private Type                type;
    private String              table;
    private DBWhere             where;
    private Integer             limit;
    private Map<String, Object> values;
    private String              sql;

    /**
     * Create a new DBQuery instance.
     * 
     * @param type
     *        The type of operation this query will perform.
     * @param table
     *        The table to query for data.
     * @param values
     *        The new values for the row that will be added or modified in this query.
     * @param where
     *        The condition that should be fulfilled for each row.
     * @param limit
     *        The maximum amount of rows that will be processed.
     */
    public DBQuery(Type type, String table, Map<String, Object> values, DBWhere where, Integer limit) {

        if (type.equals( Type.SELECT ) || type.equals( Type.DELETE ))
            throw new IllegalArgumentException( "Cannot create a select-type query with this constructor." );

        this.type = type;
        this.table = table;
        this.values = values;
        this.where = where;
        this.limit = limit;
    }

    /**
     * Create a new DBQuery instance.
     * 
     * @param type
     *        The type of operation this query will perform.
     * @param table
     *        The table to query for data.
     * @param where
     *        The condition that should be fulfilled for each row.
     * @param limit
     *        The maximum amount of rows that will be processed.
     */
    public DBQuery(Type type, String table, DBWhere where, Integer limit) {

        if (!(type.equals( Type.SELECT ) || type.equals( Type.DELETE )))
            throw new IllegalArgumentException( "Can only create a select-type query with this constructor." );

        this.type = type;
        this.table = table;
        this.where = where;
        this.limit = limit;
    }

    /**
     * Retrieve the table of this DBQuery.
     * 
     * @return Guess.
     */
    public String getTable() {

        return table;
    }

    /**
     * Retrieve the where of this DBQuery.
     * 
     * @return Guess.
     */
    public DBWhere getWhere() {

        return where;
    }

    /**
     * Retrieve the values of this DBQuery.
     * 
     * @return Guess.
     */
    public Map<String, Object> getValues() {

        return values;
    }

    /**
     * Retrieve the limit of this DBQuery.
     * 
     * @return Guess.
     */
    public Integer getLimit() {

        return limit;
    }

    /**
     * Retrieve the type of this DBQuery.
     * 
     * @return Guess.
     */
    public Type getType() {

        return type;
    }

    /**
     * Generate an SQL query for this query object.<br>
     * TODO: Escape values.
     * 
     * @return Guess.
     */
    public String toSql() {

        if (sql == null || sql.length() == 0) {
            StringBuffer valueString = new StringBuffer();
            if (values != null)
                for (String column : values.keySet())
                    valueString.append( String.format( "%s = '%s', ", column, values.get( column ) ) );
            valueString.substring( 0, Math.max( 0, valueString.length() - 2 ) );

            switch (type) {
                case SELECT:
                    sql = String.format( "SELECT * FROM %s", table );
                break;

                case INSERT:
                    sql = String.format( "INSERT INTO %s SET %s", table, valueString );
                break;

                case UPDATE:
                    sql = String.format( "UPDATE %s SET %s", table, valueString );
                break;

                case REPLACE:
                    sql = String.format( "REPLACE %s SET %s", table, valueString );
                break;

                case DELETE:
                    sql = String.format( "DELETE FROM %s", table );
                break;

                default:
                    throw new IllegalStateException( "Query type " + type + " has no SQL generation implemented." );
            }

            if (where != null && !where.getNodes().isEmpty())
                sql = String.format( "%s WHERE %s", sql, where );
            if (limit != null)
                sql = String.format( "%s LIMIT %d", sql, limit );
        }

        return sql;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        if (obj instanceof DBQuery)
            return ((DBQuery) obj).toSql().equals( toSql() );

        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {

        return toSql().hashCode();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {

        return toSql();
    }
}
