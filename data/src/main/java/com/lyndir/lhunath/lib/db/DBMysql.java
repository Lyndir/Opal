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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <i>DBLink - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * 
 * @author lhunath
 */
public class DBMysql implements DBLink {

    private static Connection db;


    /* Connect to the database. */
    private static Connection getDb() {

        try {
            Class.forName( "com.mysql.jdbc.Driver" );
            if (db == null || db.isClosed())
                db = DriverManager.getConnection( "jdbc:mysql://localhost/central", "monitor", "secumon" );
        } catch (ClassNotFoundException e) {
            Logger.error( e, "Couldn't load the database driver!" );
        } catch (SQLException e) {
            Logger.error( e, "Couldn't connect to the database server." );
        }

        return db;
    }

    protected PreparedStatement query(String sql) throws SQLException {

        PreparedStatement statement = getDb().prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
        Logger.fine( "Querying database: %s", statement );

        try {
            statement.execute();
            return statement;
        } catch (SQLException e) {
            throw new SQLException( e.getLocalizedMessage() + "\nResponsible SQL was: " + sql );
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Map<String, Object>> selectQuery(DBQuery query) {

        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

        try {
            ResultSet result = query( query.toString() ).getResultSet();
            while (result != null && result.next()) {
                /* Make a map of all columns in this row. */
                Map<String, Object> row = new HashMap<String, Object>();
                for (int i = 1; i <= result.getMetaData().getColumnCount(); ++i)
                    row.put( result.getMetaData().getColumnName( i ), result.getObject( i ) );

                /* Create a new object for this row. */
                rows.add( row );
            }
        } catch (SQLException e) {
            Logger.error( e, "Failed to read database information for object generation." );
        }

        return rows;
    }

    /**
     * {@inheritDoc}
     */
    public Integer modifyQuery(DBQuery query) {

        try {
            ResultSet rs = query( query.toString() ).getGeneratedKeys();
            if (rs.next())
                return rs.getInt( 1 );
        } catch (SQLException e) {
            Logger.error( e, "Failed to update database information for object creation." );
        }

        return null;
    }
}
