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

/**
 * <i>Axis - An axis enumeration.</i><br>
 * <br>
 * Available axises are: X, Y, Z.<br>
 * <br>
 *
 * @author lhunath
 */
public enum Axis {

    /**
     * Origin vector.
     */
    O( null ),

    /**
     * X-Axis oriented base vector.
     */
    X( new Vec3( 1, 0, 0 ) ),

    /**
     * Y-Axis oriented base vector.
     */
    Y( new Vec3( 0, 1, 0 ) ),

    /**
     * Z-Axis oriented base vector.
     */
    Z( new Vec3( 0, 0, 1 ) );

    private final Vec3 vector;


    Axis(Vec3 vector) {

        this.vector = vector != null? vector: new Vec3();
    }

    /**
     * Retrieve the vector for this axis in three dimentional space.
     *
     * @return The vector representation of this axis in space.
     */
    public Vec3 getVec3() {

        return vector;
    }

    /**
     * Retrieve the vector for this axis in two dimentional space (trim z).
     *
     * @return The vector representation of this axis on a plane.
     */
    public Vec2 getVec2() {

        return new Vec2( vector.getX(), vector.getY() );
    }
}
