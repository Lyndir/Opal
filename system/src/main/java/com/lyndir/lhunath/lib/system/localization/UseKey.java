package com.lyndir.lhunath.lib.system.localization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;
import java.util.ResourceBundle;


/**
 * <h2>{@link UseKey}<br>
 * <sub>Identifies the annotated method as yielding a localized result value from the {@link Properties}.</sub></h2>
 *
 * <p>
 * <i>Mar 28, 2009</i>
 * </p>
 *
 * @author lhunath
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseKey {

    /**
     * This is the key in {@link ResourceBundle} that yields the localization data as value.
     *
     * <p>
     * An empty string indicates that the annotated method's name should be used as the key.
     * </p>
     *
     * @return The key in the resource bundle that references the localization value for the annotated method.
     */
    String value() default "";
}
