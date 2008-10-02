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

import static com.lyndir.lhunath.lib.db.DBQuery.Type.INSERT;
import static com.lyndir.lhunath.lib.db.DBQuery.Type.SELECT;
import static com.lyndir.lhunath.lib.db.DBQuery.Type.UPDATE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * TODO: Database<br>
 * 
 * @author lhunath
 */
public class DBTable {

    private static final Map<DBQuery, DBCache> sqlCache    = Collections.synchronizedMap( new HashMap<DBQuery, DBCache>() );
    private static final List<DBTable>         objectCache = Collections.synchronizedList( new ArrayList<DBTable>() );
    protected static DBLink                    db          = new DBMysql();

    protected Map<String, Object>              data;


    protected DBTable() {

        data = new HashMap<String, Object>();
    }

    /**
     * Create a new Database instance.
     * 
     * @param row
     *            The data in the database object.
     */
    protected DBTable(Map<String, Object> row) {

        this();

        data = row;
    }

    /**
     * Get the database object of the given type with the given data.
     * 
     * @param <T>
     *            Same as type.
     * @param type
     *            The type of database object to generate using the database data.
     * @param data
     *            The data that should be contained in the database object.
     * @return The database object that contains the given data.
     */
    protected static <T extends DBTable> T getInstance(Class<T> type, Map<String, Object> data) {

        for (DBTable object : objectCache)
            if (object.getClass().equals( type ) && object.getData().equals( data ))
                return type.cast( object );

        synchronized (objectCache) {
            try {
                T instance = type.getConstructor( Map.class ).newInstance( data );
                objectCache.add( instance );

                return instance;
            } catch (Exception e) {
                Logger.error( e, "BUG: Couldn't instantiate database class." );
                return null;
            }
        }
    }

    /**
     * Retrieve the data of this database object.
     * 
     * @return Guess.
     */
    public Map<String, Object> getData() {

        return data;
    }

    /**
     * Retrieve a specific column of the data from this database object and try to guess the data type. Currently, we
     * check the following: - Non-decimal numbers -> Integer - Decimal numbers -> Double - The rest -> the Object that
     * they were.
     * 
     * @param column
     *            The name of the column to read.
     * @return Guess.
     */
    public Object getData(String column) {

        Object value = data.get( column );
        if (value == null)
            return null;

        try {
            return value instanceof Integer ? value : Integer.valueOf( value.toString() );
        } catch (NumberFormatException e) {
            /* Shh. */
        }

        try {
            return value instanceof Double ? value : Double.valueOf( value.toString() );
        } catch (NumberFormatException e) {
            /* Shh. */
        }

        return value;
    }

    /**
     * Retrieve a specific column of the data from this database object.
     * 
     * @param <D>
     *            Same as type.
     * @param column
     *            The name of the column to read.
     * @param dataType
     *            The data type of column value.
     * @return Guess.
     */
    public <D> D getData(String column, Class<D> dataType) {

        Object value = getData( column );
        return value == null ? null : dataType.cast( value );
    }

    /**
     * Get the ID value for this table's row.
     * 
     * @return Guess.
     */
    public Integer getId() {

        return getData( getId( getClass() ), Integer.class );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return data.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return data.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;
        if (obj == null || obj.getClass() != getClass())
            return false;
        if (obj instanceof DBTable)
            return data.equals( ((DBTable) obj).data );

        return false;
    }

    /**
     * Notify this object that it's data has become outdated and needs to be refreshed.
     */
    public void invalidate() {

        invalidate( true );
    }

    private void invalidate(boolean allowUncache) {

        synchronized (objectCache) {
            objectCache.remove( this );

            /* Remove all SQL cache for this table so we can make a cache-less query. */
            if (allowUncache)
                DBTable.uncacheTable( getClass(), false );

            DBTable valid = conjure( getClass(), getId() );

            /* Attempt to refresh this database object's content. */
            if (valid != null) {
                objectCache.remove( valid );
                data = valid.getData();
                objectCache.add( this );
            }
        }
    }

    /**
     * Create a database object using on the primary key value to choose the row to instantiate.
     * 
     * @param <T>
     *            Same as type.
     * @param type
     *            The type of database object to generate using the database data.
     * @param id
     *            The value for the primary key column.
     * @return Guess.
     */
    public static <T extends DBTable> T conjure(Class<T> type, Integer id) {

        return conjure( type, new DBWhere( getId( type ), id ) );
    }

    /**
     * Create a database object. Only the first matching row will be instantiated.
     * 
     * @param <T>
     *            Same as type.
     * @param type
     *            The type of database object to generate using the database data.
     * @param where
     *            The expression that should be used to select the row to instantiate.
     * @return Guess.
     */
    public static <T extends DBTable> T conjure(Class<T> type, DBWhere where) {

        List<Map<String, Object>> conjuredRows = conjureAll( type, where, 1 );
        if (conjuredRows.isEmpty())
            return null;

        return getInstance( type, conjuredRows.get( 0 ) );
    }

    /**
     * Create database objects.
     * 
     * @param <T>
     *            Same as type.
     * @param type
     *            The type of database object to generate using the database data.
     * @param where
     *            The expression that should be used to select the rows to instantiate.
     * @return Guess.
     */
    public static <T extends DBTable> List<T> conjureAll(Class<T> type, DBWhere where) {

        List<Map<String, Object>> conjuredRows = conjureAll( type, where, null );
        List<T> conjured = new ArrayList<T>();
        for (Map<String, Object> row : conjuredRows)
            conjured.add( getInstance( type, row ) );

        return conjured;
    }

    protected static <T extends DBTable> List<Map<String, Object>> conjureAll(Class<T> type, DBWhere where,
            Integer limit) {

        DBQuery query = new DBQuery( SELECT, getTable( type ), where, limit );

        /* Check whether the SQL is cached. */
        DBCache entry = sqlCache.get( query );
        if (entry != null) {
            entry.hit();

            Logger.fine( "Cache hit for: <%s>", entry );
            return entry.getResult();
        }

        List<Map<String, Object>> conjured = db.selectQuery( query );

        /* Update cache. */
        if (conjured != null)
            synchronized (sqlCache) {
                entry = new DBCache( query );
                entry.setResult( conjured );
                sqlCache.put( query, entry );
            }

        return conjured;
    }

    /**
     * Get the name of the primary key column of the table of the given type.
     * 
     * @param <T>
     *            Same as type.
     * @param type
     *            The type representing the database table.
     * @return Guess.
     */
    public static <T extends DBTable> String getId(Class<T> type) {

        try {
            return type.getField( "MY_ID" ).get( null ).toString();
        } catch (Exception e) {
            Logger.error( e, "BUG: Couldn't read ID field in database class." );
            return null;
        }
    }

    /**
     * Get the name of the table of the given type.
     * 
     * @param <T>
     *            Same as type.
     * @param type
     *            The type representing the database table.
     * @return Guess.
     */
    public static <T extends DBTable> String getTable(Class<T> type) {

        try {
            return type.getField( "MY_TABLE" ).get( null ).toString();
        } catch (Exception e) {
            Logger.error( e, "BUG: Couldn't read TABLE field in database class." );
            return null;
        }
    }

    /**
     * Remove a certain table from the SQL cache (because its cache has been invalidated).
     * 
     * @param <T>
     *            See type.
     * @param type
     *            The type of the entries that will be removed from the cache.
     */
    public static <T extends DBTable> void uncacheTable(Class<T> type) {

        uncacheTable( type, true );
    }

    private static <T extends DBTable> void uncacheTable(Class<T> type, boolean invalidate) {

        synchronized (sqlCache) {
            for (DBQuery query : new ArrayList<DBQuery>( sqlCache.keySet() ))
                if (query.getTable().equals( getTable( type ) ))
                    sqlCache.remove( query );
        }

        if (invalidate)
            synchronized (objectCache) {
                for (DBTable table : new ArrayList<DBTable>( objectCache ))
                    if (table.getClass().equals( type ))
                        table.invalidate( false );
            }
    }

    /**
     * Change a field in the given database table row instance.
     * 
     * @param <T>
     *            See type.
     * @param type
     *            The type that defines the table in which to add the new row.
     * @param field
     *            The name of the field to modify in the row.
     * @param newValue
     *            The new value for the field in the row.
     * @param where
     *            The where condition that defines which database objects to modify.
     */
    protected static <T extends DBTable> void modify(Class<T> type, final String field, final Object newValue,
            DBWhere where) {

        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put( field, newValue );

        db.modifyQuery( new DBQuery( UPDATE, getTable( type ), values, where, null ) );
        uncacheTable( type );
    }

    /**
     * Build a new row in the given table.
     * 
     * @param <T>
     *            See type.
     * @param type
     *            The type that defines the table in which to add the new row.
     * @param row
     *            The data to compose the new row of.
     * @return The new row as a table instance.
     */
    protected static <T extends DBTable> T construct(Class<T> type, Map<String, Object> row) {

        row.put( getId( type ), create( type, row ) );
        return getInstance( type, row );
    }

    /**
     * Add a field in the given database table row instance.
     * 
     * @param <T>
     *            See type.
     * @param type
     *            The type that will define the table the row will be added to.
     * @param row
     *            The data to compose the new row of.
     * @return The value of the ID field that was auto-generated for the new row.
     */
    public static <T extends DBTable> Integer create(Class<T> type, Map<String, Object> row) {

        Integer key = db.modifyQuery( new DBQuery( INSERT, getTable( type ), row, null, null ) );
        uncacheTable( type );

        return key;
    }
}
