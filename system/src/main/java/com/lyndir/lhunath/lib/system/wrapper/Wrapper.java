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

package com.lyndir.lhunath.lib.system.wrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * <i>{@link Wrapper} - An abstract class that assists in writing wrapper classes for other classes that may or may not
 * be available.</i><br>
 * <br>
 * This class provides utility methods that facilitate access to classes that may or may not be available at compile
 * time or runtime.<br>
 * <br>
 * The idea is that any code that wishes to use the class that may or may not be available uses proxy methods on a proxy
 * class instead. This proxy class implements this {@link Wrapper} and all methods that the wrapped class provides. Upon
 * each call to these proxied methods, the proxy class should call {@link #invoke(String, Class[], Object...)} with the
 * name of the method and parameter data. This call will return the intended result if the wrapper class is available or
 * fail with an {@link UnsupportedOperationException} if the wrapper class is not available or accessible.<br>
 * <br>
 * It is REQUIRED for any implementing classes to provide this bit of code that initializes the wrapper:
 * 
 * <pre>
 * static {
 * 	 initWrapper([Proxy-Class].class, &quot;[Wrapped-Class]&quot;)
 * }
 * </pre>
 * 
 * <br>
 * 
 * @author lhunath
 */
public abstract class Wrapper {

    protected static boolean                                       classNotFound;
    protected static final Map<Class<? extends Wrapper>, Class<?>> wrappedClasses = new HashMap<Class<? extends Wrapper>, Class<?>>();


    protected static Object construct(Class<? extends Wrapper> proxyClass, Class<?>[] classes, Object... args)
            throws UnsupportedOperationException {

        try {
            Constructor<?> constructor = getWrappedClass( proxyClass ).getConstructor( classes );
            return constructor.newInstance( args );
        }

        catch (SecurityException e) {
            throw new UnsupportedOperationException( e );
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException( e );
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException( e );
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException( e );
        } catch (InvocationTargetException e) {
            throw new UnsupportedOperationException( e );
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException( e );
        }
    }

    protected static Class<?> getClass(String className)
            throws UnsupportedOperationException {

        try {
            return ClassLoader.getSystemClassLoader().loadClass( className );
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException( e );
        }
    }

    protected static Class<?> getWrappedClass(Class<? extends Wrapper> proxyClass)
            throws UnsupportedOperationException {

        try {
            if (classNotFound)
                throw new ClassNotFoundException( "Wrapper Class unavailable." );

            if (!wrappedClasses.containsKey( proxyClass ))
                throw new IllegalStateException( "You did not initialize the wrapper for " + proxyClass.getName()
                                                 + " yet!" );

            return wrappedClasses.get( proxyClass );
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException( e );
        }
    }

    protected static boolean initWrapper(Class<? extends Wrapper> proxyClass, String wrappedClassName) {

        try {
            wrappedClasses.put( proxyClass, getClass( wrappedClassName ) );

            return true;
        } catch (UnsupportedOperationException ignored) {
            classNotFound = true;

            return false;
        }
    }

    protected static Object invoke(Class<? extends Wrapper> proxyClass, Object wrappedInstance, String methodName)
            throws UnsupportedOperationException {

        return invoke( proxyClass, wrappedInstance, methodName, new Class[0] );
    }

    protected static Object invoke(Class<? extends Wrapper> proxyClass, Object wrappedInstance, String methodName,
                                   Class<?>[] classes, Object... args)
            throws UnsupportedOperationException {

        try {
            Method method = getWrappedClass( proxyClass ).getMethod( methodName, classes );
            return method.invoke( wrappedInstance, args );
        }

        catch (SecurityException e) {
            throw new UnsupportedOperationException( e );
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException( e );
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException( e );
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException( e );
        } catch (InvocationTargetException e) {
            throw new UnsupportedOperationException( e );
        }
    }

    protected static Object mapEnumValue(Object proxyEnum, Class<?> wrappedEnumClass) {

        for (Object wrappedEnum : wrappedEnumClass.getEnumConstants())
            if (wrappedEnum.toString().equals( proxyEnum.toString() ))
                return wrappedEnum;

        return null;
    }


    protected final Object wrappedInstance;


    protected Wrapper(Object wrappedInstance) {

        this.wrappedInstance = wrappedInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {

        if (obj == null)
            return false;
        if (obj == this)
            return true;

        if (obj.getClass().equals( getClass() ))
            return super.equals( obj );
        else if (obj.getClass().equals( wrappedInstance.getClass() ))
            return wrappedInstance.equals( obj );

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        return wrappedInstance.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return wrappedInstance.toString();
    }

    protected Object getWrappedInstance() {

        return wrappedInstance;
    }

    // Assuming only classes that extend this
    // class can call this method and play nice.
    protected Object invoke(String methodName)
            throws UnsupportedOperationException {

        return invoke( methodName, new Class[0] );
    }

    // Assuming only classes that extend this
    // class can call this method and play nice.
    @SuppressWarnings("unchecked")
    protected Object invoke(String methodName, Class<?>[] classes, Object... args)
            throws UnsupportedOperationException {

        return invoke( (Class<? extends Wrapper>) getClass(), wrappedInstance, methodName, classes, args );
    }
}
