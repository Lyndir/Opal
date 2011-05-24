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

package com.lyndir.lhunath.opal.system.wrapper;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;


/**
 * <i>{@link Wrapper} - An abstract class that assists in writing wrapper classes for other classes that may or may not be
 * available.</i><br> <br> This class provides utility methods that facilitate access to classes that may or may not be available at
 * compile
 * time or runtime.<br> <br> The idea is that any code that wishes to use the class that may or may not be available uses proxy methods on
 * a
 * proxy class instead. This proxy class implements this {@link Wrapper} and all methods that the wrapped class provides. Upon each call to
 * these proxied methods, the proxy class should call {@link #invoke(String, Class[], Object...)} with the name of the method and parameter
 * data. This call will return the intended result if the wrapper class is available or fail with an {@link UnsupportedOperationException}
 * if the wrapper class is not available or accessible.<br> <br> It is REQUIRED for any implementing classes to provide this bit of code
 * that initializes the wrapper:
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

    protected static boolean classNotFound;
    protected static final Map<Class<? extends Wrapper>, Class<?>> wrappedClasses = new HashMap<Class<? extends Wrapper>, Class<?>>();

    /**
     * Construct an instance of this wrapper's wrapped class.
     *
     * @param proxyClass The wrapped class.
     * @param classes    The constructor's argument types.
     * @param args       The constructor's argument values.
     *
     * @return An instance of the proxyClass type.
     *
     * @throws UnsupportedOperationException The wrapper could not be instantiated.
     */
    protected static Object construct(final Class<? extends Wrapper> proxyClass, final Class<?>[] classes, final Object... args)
            throws UnsupportedOperationException {

        try {
            Constructor<?> constructor = getWrappedClass( proxyClass ).getConstructor( classes );
            return constructor.newInstance( args );
        }

        catch (SecurityException e) {
            throw new UnsupportedOperationException( e );
        }
        catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException( e );
        }
        catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException( e );
        }
        catch (IllegalAccessException e) {
            throw new UnsupportedOperationException( e );
        }
        catch (InvocationTargetException e) {
            throw new UnsupportedOperationException( e );
        }
        catch (InstantiationException e) {
            throw new UnsupportedOperationException( e );
        }
    }

    /**
     * @param proxyClass The wrapper whose wrapped class we're after.
     *
     * @return The class wrapped by the given wrapper.
     *
     * @throws UnsupportedOperationException If the wrapper's class is not available.
     */
    protected static Class<?> getWrappedClass(final Class<? extends Wrapper> proxyClass)
            throws UnsupportedOperationException {

        try {
            if (classNotFound)
                throw new ClassNotFoundException( "Wrapper Class unavailable." );

            if (!wrappedClasses.containsKey( proxyClass ))
                throw new IllegalStateException( "You did not initialize the wrapper for " + proxyClass.getName() + " yet!" );

            return wrappedClasses.get( proxyClass );
        }
        catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException( e );
        }
    }

    /**
     * Convenience method for class loading using the system classloader.
     *
     * @param className The class to load.
     *
     * @return The loaded class named by className.
     *
     * @throws UnsupportedOperationException The class could not be found.
     */
    protected static Class<?> getClass(final String className)
            throws UnsupportedOperationException {

        try {
            return ClassLoader.getSystemClassLoader().loadClass( className );
        }
        catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException( e );
        }
    }

    /**
     * Initialize a wrapper so that it wraps a class by the given name.
     *
     * @param proxyClass       The wrapper class.
     * @param wrappedClassName The class that should be wrapped.
     *
     * @return <code>true</code> if the wrapped class is supported.
     */
    protected static boolean initWrapper(final Class<? extends Wrapper> proxyClass, final String wrappedClassName) {

        try {
            wrappedClasses.put( proxyClass, getClass( wrappedClassName ) );

            return true;
        }
        catch (UnsupportedOperationException ignored) {
            classNotFound = true;

            return false;
        }
    }

    /**
     * Invoke a method on the wrapper's wrapped instance.
     *
     * @param proxyClass      The wrapper.
     * @param wrappedInstance The wrapped instance that is wrapped by the wrapper.
     * @param methodName      The name of the method to invoke.
     *
     * @return The return value of the invoked method.
     *
     * @throws UnsupportedOperationException
     */
    protected static Object invoke(final Class<? extends Wrapper> proxyClass, final Object wrappedInstance, final String methodName)
            throws UnsupportedOperationException {

        return invoke( proxyClass, wrappedInstance, methodName, new Class[0] );
    }

    /**
     * Invoke a method on the wrapper's wrapped instance.
     *
     * @param proxyClass      The wrapper.
     * @param wrappedInstance The wrapped instance that is wrapped by the wrapper.
     * @param methodName      The name of the method to invoke.
     * @param classes         The method's argument types.
     * @param args            The method's argument values.
     *
     * @return The return value of the invoked method.
     *
     * @throws UnsupportedOperationException
     */
    protected static Object invoke(final Class<? extends Wrapper> proxyClass, final Object wrappedInstance, final String methodName,
                                   final Class<?>[] classes, final Object... args)
            throws UnsupportedOperationException {

        try {
            Method method = getWrappedClass( proxyClass ).getMethod( methodName, classes );
            return method.invoke( wrappedInstance, args );
        }

        catch (SecurityException e) {
            throw new UnsupportedOperationException( e );
        }
        catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException( e );
        }
        catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException( e );
        }
        catch (IllegalAccessException e) {
            throw new UnsupportedOperationException( e );
        }
        catch (InvocationTargetException e) {
            throw new UnsupportedOperationException( e );
        }
    }

    /**
     * @param proxyEnum        The enum wrapper instance.
     * @param wrappedEnumClass The wrapped enum class.
     *
     * @return The instance of the wrapped enum class that has the same value as that of the given enum wrapper instance.
     */
    protected static Object mapEnumValue(final Object proxyEnum, final Class<?> wrappedEnumClass) {

        for (final Object wrappedEnum : wrappedEnumClass.getEnumConstants())
            if (wrappedEnum.toString().equals( proxyEnum.toString() ))
                return wrappedEnum;

        return null;
    }

    protected final Object wrappedInstance;

    /**
     * @param wrappedInstance The instance of the class that should be proxied by this wrapper.
     */
    protected Wrapper(final Object wrappedInstance) {

        this.wrappedInstance = wrappedInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {

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

    /**
     * @return The wrapped class instance.
     */
    protected Object getWrappedInstance() {

        return wrappedInstance;
    }

    /**
     * Invoke a method on the wrapped instance.
     *
     * @param methodName The method to invoke.
     *
     * @return The return value of the invoked method.
     *
     * @throws UnsupportedOperationException The method or invoking it is not supported or failed.
     */
    // Assuming only classes that extend this
    // class can call this method and play nice.
    protected Object invoke(final String methodName)
            throws UnsupportedOperationException {

        return invoke( methodName, new Class[0] );
    }

    /**
     * Invoke a method on the wrapped instance.
     *
     * @param methodName The method to invoke.
     * @param classes    The method's argument types.
     * @param args       The method's argument values.
     *
     * @return The return value of the invoked method.
     *
     * @throws UnsupportedOperationException The method or invoking it is not supported or failed.
     */
    // Assuming only classes that extend this
    // class can call this method and play nice.
    protected Object invoke(final String methodName, final Class<?>[] classes, final Object... args)
            throws UnsupportedOperationException {

        return invoke( getClass(), wrappedInstance, methodName, classes, args );
    }
}
