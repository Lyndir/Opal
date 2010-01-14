package com.lyndir.lhunath.lib.system.localization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;
import java.util.ResourceBundle;


/**
 * <h2>{@link UseBundle}<br>
 * <sub>References the resource bundle that provides the values for the keys in the annotated class.</sub></h2>
 * 
 * <p>
 * You can choose between several methods of referencing the source for the resource bundle. You may also specify
 * multiple sources, in which case precedence is as follows (highest to lowest):
 * <ol>
 * <li>{@link #type()}</li>
 * <li>{@link #resource()}</li>
 * </ol>
 * </p>
 * 
 * <p>
 * <i>Mar 28, 2009</i>
 * </p>
 * 
 * @author lhunath
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseBundle {

    /**
     * A property file resource.
     * 
     * <p>
     * Use this to load the resource bundle from {@link Properties}. {@link Thread#getContextClassLoader()} is used to
     * load the given resource.
     * </p>
     * 
     * @see ClassLoader#getResourceAsStream(String)
     */
    String resource() default "";

    /**
     * A {@link ResourceBundle} class.
     * 
     * <p>
     * Use this to load the resource bundle by instantiating the given class.
     * </p>
     */
    Class<? extends ResourceBundle> type() default UnspecifiedBundle.class;


    public static abstract class UnspecifiedBundle extends ResourceBundle {}
}
