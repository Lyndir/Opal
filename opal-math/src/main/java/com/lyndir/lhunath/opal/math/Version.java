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


package com.lyndir.lhunath.opal.math;

import com.google.common.base.Preconditions;
import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.annotation.Nullable;


/**
 * <i>{@link Version} - A class that represents version numbers in a comparable structure.</i><br> <br> Versions delimited by dots can be
 * represented by this class. It assumes that the earlier the number in the version string, the higher its importance is.<br> You can use
 * this version class to compare versions painlessly.<br> <br>
 *
 * @author lhunath
 */
public class Version implements Comparable<Version>, Serializable {

    private static final long    serialVersionUID = 1L;
    private static final Pattern DOT              = Pattern.compile( "\\." );

    private final String   version;
    private final String[] tags;

    /**
     * Create a new {@link Version} instance.
     *
     * @param version The string version tag.
     */
    public Version(final Number version) {

        this( Preconditions.checkNotNull( version, "Given version cannot be null." ).toString() );
    }

    /**
     * Create a new {@link Version} instance.
     *
     * @param version The string version tag.
     */
    public Version(final String version) {

        this.version = version == null? "0": version.trim();

        tags = DOT.split( this.version );
    }

    /**
     * @return The major version number (the first number in the version). {@code null} if the first tag is not a number or there are
     * no tags.
     */
    @Nullable
    public Integer getMajor() {

        if (tags.length == 0)
            return null;

        return ConversionUtils.toInteger( tags[0] ).orNull();
    }

    @Override
    @SuppressWarnings("StringConcatenationInFormatCall")
    public int compareTo(@Nullable final Version o) {

        if (o == null)
            return 1;

        if (equals( o ))
            return 0;

        int compare = 0;
        for (int i = 0; i < Math.min( tags.length, o.tags.length ); ++i) {
            int len = Math.max( tags[i].length(), o.tags[i].length() );
            String tagOne = String.format( "%" + len + 's', tags[i] );
            String tagTwo = String.format( "%" + len + 's', o.tags[i] );

            if ((compare = tagOne.compareToIgnoreCase( tagTwo )) != 0)
                break;
        }

        return compare;
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj)
            return true;
        if (obj instanceof Version)
            return Arrays.equals( tags, ((Version) obj).tags ) && version.equals( ((Version) obj).version );

        return false;
    }

    @Override
    public int hashCode() {

        return 31 * version.hashCode() + Arrays.hashCode( tags );
    }

    @Override
    public String toString() {

        return version;
    }

    /**
     * Check whether this version is newer than the given one.
     *
     * @param v The possibly older version.
     *
     * @return Guess.
     */
    public boolean newerThan(final Version v) {

        return compareTo( v ) > 0;
    }

    /**
     * Check whether this version is older than the given one.
     *
     * @param v The possibly newer version.
     *
     * @return Guess.
     */
    public boolean olderThan(final Version v) {

        return compareTo( v ) < 0;
    }
}
