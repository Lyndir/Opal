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

import java.util.List;
import java.util.Map;


/**
 * <i>DBLink - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * 
 * @author lhunath
 */
public interface DBLink {

    /**
     * Execute a query on the database that returns table data that matched the query requirements.
     * 
     * @param query
     *            The query to execute on the database.
     * @return The resulting table data.
     */
    public List<Map<String, Object>> selectQuery(DBQuery query);

    /**
     * Execute a query on the database that modifies table data.
     * 
     * @param query
     *            The query to execute on the database.
     * @return The value of the first primary ID of the row in the table that was modified.
     */
    public Integer modifyQuery(DBQuery query);
}
