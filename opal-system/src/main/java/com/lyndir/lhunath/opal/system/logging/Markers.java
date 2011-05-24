package com.lyndir.lhunath.opal.system.logging;

import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;


/**
 * <i>05 04, 2011</i>
 *
 * @author lhunath
 */
public abstract class Markers {

    private static final BasicMarkerFactory factory = new BasicMarkerFactory();

    public static final Marker AUDIT    = factory.getMarker( "AUDIT" );
    public static final Marker BUG      = factory.getMarker( "BUG" );
    public static final Marker SECURITY = factory.getMarker( "SECURITY" );
}
