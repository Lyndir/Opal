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
package com.lyndir.lhunath.lib.math;

import java.io.Serializable;

import com.lyndir.lhunath.lib.system.Utils;


/**
 * <i>{@link Version} - A class that represents version numbers in a comparable structure.</i><br>
 * <br>
 * Versions delimited by dots can be represented by this class. It assumes that the earlier the number in the version
 * string, the higher its importance is.<br>
 * You can use this version class to compare versions painlessly.<br>
 * <br>
 * 
 * @author lhunath
 */
public class Version implements Comparable<Version>, Serializable {

    private static final long serialVersionUID = 1L;

    private String            version;
    private String[]          tags;


    /**
     * Create a new {@link Version} instance.
     * 
     * @param version
     *            The string version tag.
     */
    public Version(Number version) {

        this( version == null? null: version.toString() );
    }

    /**
     * Create a new {@link Version} instance.
     * 
     * @param version
     *            The string version tag.
     */
    public Version(String version) {

        set( version );
    }

    /**
     * Change the version represented by this {@link Version} object.
     * 
     * @param version
     *            The string representation of the version to set this object to.
     */
    public void set(String version) {

        if (version == null)
            this.version = "0";
        else
            this.version = version.trim();

        tags = this.version.split( "\\." );
    }

    /**
     * @return The major version number (the first number in the version). <code>null</code> if the first tag is not a
     *         number or there are no tags.
     */
    public Integer getMajor() {

        if (tags.length == 0)
            return null;

        return Utils.parseInt( tags[0] );
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Version o) {

        if (o == null)
            return 1;

        if (equals( o ))
            return 0;

        int compare = 0;
        for (int i = 0; i < Math.min( tags.length, o.tags.length ); ++i) {
            int len = Math.max( tags[i].length(), o.tags[i].length() );
            String tagOne = String.format( "%" + len + "s", tags[i] );
            String tagTwo = String.format( "%" + len + "s", o.tags[i] );

            if ((compare = tagOne.compareToIgnoreCase( tagTwo )) != 0)
                break;
        }

        return compare;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return version;
    }

    /**
     * Check whether this version is newer than the given one.
     * 
     * @param v
     *            The possibly older version.
     * @return Guess.
     */
    public boolean newerThan(Version v) {

        return compareTo( v ) > 0;
    }

    /**
     * Check whether this version is older than the given one.
     * 
     * @param v
     *            The possibly newer version.
     * @return Guess.
     */
    public boolean olderThan(Version v) {

        return compareTo( v ) < 0;
    }
}
