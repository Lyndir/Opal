package com.lyndir.lhunath.opal.math;

import com.google.common.base.Optional;


/**
 * <pre>
 *                 -v
 *               .   .   .   .   .   .
 *                 .   .   .   .   .
 *               .   .   Nw  Ne  .   .
 *            -u   .   W   o   E   .   +u
 *               .   .   Sw  Se  .   .
 *                 .   .   .   .   .
 *               .   .   .   .   .   .
 *                                 +v
 *
 *             u   v
 *        o  = 0 , 0
 *        Nw = 0 , -1
 *        Se = 0 , 1
 *        E  = 1 , 0
 *        W  = -1, 0
 *        Nw = 1 , -1
 *        Sw = -1, 1
 * </pre>
 *
 * <i>10 07, 2012</i>
 *
 * @author lhunath
 */
public enum Side {
    NW( new Vec2( 0, -1 ) ),
    NE( new Vec2( 1, -1 ) ),
    W( new Vec2( -1, 0 ) ),
    E( new Vec2( 1, 0 ) ),
    SW( new Vec2( -1, 1 ) ),
    SE( new Vec2( 0, 1 ) );

    private final Vec2 delta;

    Side(final Vec2 delta) {
        this.delta = delta;
    }

    public Vec2 getDelta() {
        return delta;
    }

    public static Optional<Side> forName(final String name) {
        for (final Side side : values())
            if (side.name().equalsIgnoreCase( name ))
                return Optional.of( side );

        return Optional.absent();
    }
}
