package com.lyndir.lhunath.lib.system.localization;

import java.lang.annotation.*;


/**
 * <h2>{@link UseLocalizationProvider}<br> <sub>References the provider which will provide values for the keys in the annotated
 * class.</sub></h2>
 *
 * <p> <i>Mar 28, 2009</i> </p>
 *
 * @author lhunath
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseLocalizationProvider {

    /**
     * @return The {@link LocalizationProvider} that provides values for keys.
     */
    Class<? extends LocalizationProvider> value();
}
