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
 * TODO: StatementCache<br>
 * 
 * @author lhunath
 */
public class DBCache {

    private int                       hits;
    private DBQuery                   query;
    private List<Map<String, Object>> result;

    /**
     * Create a new DBCache instance.
     * 
     * @param query
     *        The SQL query for which these cache results apply.
     */
    public DBCache(DBQuery query) {

        hits = 0;
        setQuery( query );
    }

    /**
     * Retrieve the query of this cache entry.
     * 
     * @return Guess.
     */
    public DBQuery getQuery() {

        return query;
    }

    /**
     * Set the query of this cache entry.
     * 
     * @param query
     *        Guess.
     */
    public void setQuery(DBQuery query) {

        this.query = query;
    }

    /**
     * Record a cache hit.
     */
    public void hit() {

        hits++;
    }

    /**
     * Retrieve the result of this cache entry.
     * 
     * @return Guess.
     */
    public List<Map<String, Object>> getResult() {

        return result;
    }

    /**
     * Set the result of this cache entry.
     * 
     * @param newResult
     *        Guess.
     */
    public void setResult(List<Map<String, Object>> newResult) {

        result = newResult;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        if (obj instanceof DBCache)
            return ((DBCache) obj).query.equals( query );

        return false;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {

        return query.hashCode();
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {

        return String.format( "%s [hit: %d]", query, hits );
    }
}
