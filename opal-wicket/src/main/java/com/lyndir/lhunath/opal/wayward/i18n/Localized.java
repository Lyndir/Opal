package com.lyndir.lhunath.opal.wayward.i18n;

import org.apache.wicket.IClusterable;


/**
 * <h2>{@link Localized}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 08, 2010</i> </p>
 *
 * @author lhunath
 */
public interface Localized extends IClusterable {

    /**
     * @return A localized description of this type.
     */
    String typeDescription();

    /**
     * @return A localized description of this object.
     */
    String objectDescription();
}
