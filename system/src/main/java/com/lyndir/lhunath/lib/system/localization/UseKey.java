package com.lyndir.lhunath.lib.system.localization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;


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
     * The property key that identifies the value of the localized result format.
     * 
     * <p>
     * This is the key in the {@link Properties} that yields the value that contains the localization source data.
     * </p>
     */
    String value() default "";
}
