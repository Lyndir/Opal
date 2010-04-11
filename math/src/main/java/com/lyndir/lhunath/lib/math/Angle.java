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
 * <i>Angle - Any angle; available in degrees or radians.</i><br>
 * <br>
 * The Angle object handles angles in their dual form (degree / radian). After an Angle object has been created (either
 * through degrees or radians); both degree and radial form are available at all times.<br>
 * <br>
 * Both the retrieval of and mathematic functions applied upon these angles are optimized for repeated retrieval by
 * caching of all calculated results.<br>
 * <br>
 *
 * @author lhunath
 */
public class Angle {

    private static final float rToD = 180 / (float) Math.PI;

    private float degrees, radians, sin, cos;
    private boolean hasDegrees, hasRadians, hasSin, hasCos;


    /**
     * Create a new Angle, specifying degrees.
     *
     * @param degrees The degrees value of this Angle.
     */
    public Angle(float degrees) {

        this( degrees, true );
    }

    /**
     * Create a new Angle.
     *
     * @param angle     The angle of this Angle object.
     * @param isDegrees Whether the specified angles are in degrees (<code>true</code>), or radians (<code>false</code>).
     */
    public Angle(float angle, boolean isDegrees) {

        if (isDegrees)
            setDegrees( angle );
        else
            setRadians( angle );
    }

    /**
     * Calculate the sine of this angle.
     *
     * @return The sine of this angle.
     */
    public float sin() {

        if (hasSin)
            return sin;

        hasSin = true;
        return sin = (float) Math.sin( getRadians() );
    }

    /**
     * Calculate the cosine of this angle.
     *
     * @return The cosine of this angle.
     */
    public float cos() {

        if (hasCos)
            return cos;

        hasCos = true;
        return cos = (float) Math.cos( getRadians() );
    }

    /**
     * Retrieve the degrees of this Angle.
     *
     * @return This angle in degrees.
     */
    public float getDegrees() {

        if (!hasDegrees) {
            if (!hasRadians)
                return 0;

            degrees = radians * rToD;
            hasDegrees = true;
        }

        return degrees;
    }

    /**
     * Retrieve the radians of this Angle.
     *
     * @return This angle in radians.
     */
    public float getRadians() {

        if (!hasRadians) {
            if (!hasDegrees)
                return 0;

            radians = degrees / rToD;
            hasRadians = true;
        }

        return radians;
    }

    /**
     * Set the radians of this Angle.
     *
     * @param radians The radians to set this angle by.
     */
    public void setRadians(float radians) {

        if (hasRadians && this.radians == radians)
            return;

        reset();

        hasRadians = true;
        this.radians = radians;
    }

    /**
     * Set the degrees of this Angle.
     *
     * @param degrees The degrees to set this angle by.
     */
    public void setDegrees(float degrees) {

        if (hasDegrees && this.degrees == degrees)
            return;

        reset();

        hasDegrees = true;
        this.degrees = degrees;
    }

    /**
     * Unset all hasX settings.
     */
    private void reset() {

        hasDegrees = hasRadians = hasSin = hasCos = false;
    }

}
