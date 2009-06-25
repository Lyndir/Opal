package com.lyndir.lhunath.lib.system.localization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;


/**
 * <h2>{@link UseBundle}<br>
 * <sub>Identifies the {@link Properties} that the {@link UseKey} annotated methods obtain their data from.</sub></h2>
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
     * The property file resource.
     * 
     * <p>
     * This is the resource that provides the {@link Properties}. We use the {@link Thread#getContextClassLoader()} to
     * resolve the resource.
     * </p>
     * 
     * @see ClassLoader#getResourceAsStream(String)
     */
    String value();
}
