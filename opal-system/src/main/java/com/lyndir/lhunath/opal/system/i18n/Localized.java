package com.lyndir.lhunath.opal.system.i18n;


/**
 * <h2>{@link Localized}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>05 08, 2010</i> </p>
 *
 * @author lhunath
 */
public interface Localized {

    /**
     * @return A localized description of this type.
     */
    String getLocalizedType();

    /**
     * @return A localized description of this object.
     */
    String getLocalizedInstance();
}
