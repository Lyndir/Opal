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
package com.lyndir.lhunath.opal.config;

import com.lyndir.lhunath.opal.system.logging.Logger;
import com.lyndir.lhunath.opal.system.util.ConversionUtils;
import com.thoughtworks.xstream.XStream;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.*;


/**
 * <i>BaseConfig - A configuration back-end system with built-in persistence.</i><br> <br> You should extend this class and create
 * configurable entries as demonstrated by the implementation of {@link #configFile}.<br> <br> NOTE: Any subclass needs to follow the
 * directions outlined in {@link #initClass(Class)}!<br> <br>
 *
 * @param <T> The type of this entry's value.
 *
 * @author lhunath
 */
public class BaseConfig<T extends Serializable> implements Serializable {

    static final Logger logger = Logger.get( BaseConfig.class );

    /**
     * Version of the class. Augment this whenever the class type of a config entry field changes, or the context of a field becomes
     * incompatible.
     */
    private static final long serialVersionUID = 210L;

    /**
     * The size of the file/web read buffer.
     */
    public static final int BUFFER_SIZE = 1024;

    /**
     * The file in which to save the config settings.
     */
    public static final BaseConfig<File> configFile = create( File.class );

    /**
     * Whether to write out this configuration file in XML format.
     */
    public static final BaseConfig<Boolean> writeAsXML = create( true );

    protected static final Collection<Runnable>       shutdownHooks = new HashSet<Runnable>();
    protected static final Map<BaseConfig<?>, String> names         = new HashMap<BaseConfig<?>, String>();

    static {
        /* CALL THIS METHOD IN EVERY SUBCLASS! */
        initClass( BaseConfig.class );

        /* Make a shutdown hook to save the config on exit. */
        Runtime.getRuntime().addShutdownHook( new Thread( "Config ShutdownHook" ) {

            @Override
            public void run() {

                logger.dbg( "stat.saveConfig", configFile.get() );
                try {
                    for (final Runnable hook : shutdownHooks)
                        hook.run();

                    if (configFile.isEmpty()) {
                        logger.wrn( "Config file unset, can't save!" );
                        return;
                    }

                    if (configFile.get().exists())
                        configFile.get().delete();
                    configFile.get().createNewFile();

                    if (writeAsXML.get()) {
                        OutputStreamWriter configWriter = new FileWriter( configFile.get() );

                        try {
                            XStream xstream = new XStream();
                            xstream.toXML( names, configWriter );
                            // xstream.toXML( types, configWriter );
                        }
                        finally {
                            configWriter.close();
                        }
                    } else {
                        FileOutputStream out = new FileOutputStream( configFile.get() );
                        ObjectOutputStream objects = new ObjectOutputStream( out );

                        try {
                            objects.writeObject( names );
                            // objects.writeObject( types );
                        }
                        finally {
                            objects.close();
                        }
                    }
                }
                catch (FileNotFoundException e) {
                    logger.err( e, "Could not find the config file '%s'!", configFile.get() );
                }
                catch (IOException e) {
                    logger.err( e, "Could not create/write to the config file '%s'!", configFile.get() );
                }
                finally {
                    logger.dbg( null );
                }
            }
        } );
    }

    /**
     * Create a new {@link BaseConfig} that defaults to being unset.
     *
     * @param t   The type of value for this {@link BaseConfig}.
     * @param <T> See type.
     *
     * @return The {@link BaseConfig} object for this entry.
     */
    public static <T extends Serializable> BaseConfig<T> create(@SuppressWarnings("unused") final Class<T> t) {

        return new BaseConfig<T>( null );
    }

    /**
     * Create a new {@link BaseConfig} with the given default value.
     *
     * @param defaultValue The value that will be used if no other value is defined.
     * @param <T>          The type of value for this {@link BaseConfig}.
     *
     * @return The {@link BaseConfig} object for this entry.
     */
    public static <T extends Serializable> BaseConfig<T> create(final T defaultValue) {

        return new BaseConfig<T>( defaultValue );
    }

    /**
     * Create a new {@link BaseConfig} with the given default value that will be parsed into a URL.
     *
     * @param defaultValue The value that will be used if no other value is defined.
     *
     * @return The {@link BaseConfig} object for this entry.
     */
    public static BaseConfig<URL> createUrl(final String defaultValue) {

        return create( ConversionUtils.toURLNN( defaultValue ) );
    }

    /**
     * Dump out all known settings for debug purposes to standard output for the given config class.
     */
    public static void dump() {

        for (final BaseConfig<?> entry : names.keySet())
            System.out.println( entry.toString() );
    }

    /**
     * Create a new {@link BaseConfig} that defaults to being unset.
     *
     * @param <T> The type of value for this {@link BaseConfig}.
     *
     * @return The {@link BaseConfig} object for this entry.
     */
    public static <T extends Serializable> BaseConfig<T> empty() {

        return create( null );
    }

    /**
     * Prepare this config (sub)class for use. This will cause all config fields to be exported into the settings object that will be used
     * for serialization of the settings.<br> <br> <b>YOU MUST CALL THIS FUNCTION IN A STATIC BLOCK WHENEVER YOU SUBCLASS THIS CLASS!</b>
     *
     * @param configClass The class that is being initialized. This is the Class object of the {@link BaseConfig} subclass.
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "RawUseOfParameterizedType" })
    public static void initClass(final Class<? extends BaseConfig> configClass) {

        flushConfig( configClass );
        loadConfig();
    }

    /**
     * Change the location of the config file and reload the config from the new location.
     *
     * @param config The new config file.
     */
    public static void setConfig(final File config) {

        configFile.set( config );
        loadConfig();
    }

    /**
     * Read in all static fields of the given class and if they're {@link BaseConfig} fields, add them to the settings list so that they
     * will be serialized.
     *
     * @param configClass The name of the class whose static {@link BaseConfig} fields should be flushed into the settings list.
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "RawUseOfParameterizedType" })
    private static void flushConfig(final Class<? extends BaseConfig> configClass) {

        for (final Field field : configClass.getFields())
            try {
                if (field.get( null ) instanceof BaseConfig) {
                    BaseConfig<?> config = (BaseConfig<?>) field.get( null );
                    ParameterizedType type = (ParameterizedType) field.getGenericType();

                    config.hashCode = config.getName( configClass ).hashCode();
                    config.type = type.getActualTypeArguments()[0].toString();

                    names.put( config, config.getName( configClass ) );
                }
            }

            catch (IllegalArgumentException ignored) {
            }
            catch (IllegalAccessException ignored) {
            }
    }

    /**
     * Load config settings from the config file and change every existing setting with the same field name to reflect its value from the
     * config file.
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "RawUseOfParameterizedType" })
    private static void loadConfig() {

        try {
            /* Check if the config file exists before trying to read it. */
            if (configFile.isEmpty() || !configFile.get().isFile())
                return;

            /* Read in the config file to a new settings object. */
            boolean loaded = false, useXML = writeAsXML.isSet()? writeAsXML.get(): true;
            Collection<Exception> loadProblems = new ArrayList<Exception>();
            Map<BaseConfig<? extends Serializable>, String> configNames = new HashMap<BaseConfig<? extends Serializable>, String>();
            // Map<String, String> configTypes = new HashMap<String, String>();

            /* XML XStream Method. */
            if (useXML)
                try {
                    InputStreamReader configReader = new FileReader( configFile.get() );

                    try {
                        XStream xstream = new XStream();
                        configNames = names.getClass().cast( xstream.fromXML( configReader ) );
                        // configTypes = types.getClass().cast( xstream.fromXML( configReader ) );
                    }
                    finally {
                        configReader.close();
                    }

                    loaded = true;
                }
                catch (IOException e) {
                    loadProblems.add( e );
                    useXML = false;
                }

            /* ObjectStream Method. */
            if (!useXML)
                try {
                    InputStream stream = new BufferedInputStream( new FileInputStream( configFile.get() ) );
                    ObjectInputStream objects = new ObjectInputStream( stream );

                    try {
                        configNames = names.getClass().cast( objects.readObject() );
                        // configTypes = types.getClass().cast( objects.readObject() );
                    }
                    finally {
                        objects.close();
                    }

                    loaded = true;
                }
                catch (InvalidClassException e) {
                    logger.wrn( e, "Config file is incompatible, reverting to defaults." );
                }
                catch (IOException e) {
                    loadProblems.add( e );
                }

            /* Failed. */
            if (!loaded) {
                logger.err( "Failed to load config file %s.  Reason follows.", configFile.get() );
                for (final Exception loadProblem : loadProblems)
                    logger.err( "Reason:", loadProblem );

                revert();
                return;
            }

            /* Apply config file and check its settings for any keys not defined by the application. */
            for (final Map.Entry<BaseConfig<? extends Serializable>, String> configNameEntry : configNames.entrySet())
                for (final BaseConfig currEntry : names.keySet()) {

                    /* Don't load the config file entry.. */
                    if (currEntry.equals( configFile ))
                        continue;

                    /* Persistent entry info. */
                    String configName = configNameEntry.getValue();
                    // String configType = configTypes.get( configName );

                    /* If field names match .. */
                    if (currEntry.getName().equals( configName )) {
                        try {
                            /* Abort if types don't match. */
                            // if (!currEntry.getType().equals( configType ))
                            // throw new ClassCastException( "Generic types don't match." );
                            currEntry.set( configNameEntry.getKey().get() );
                        }

                        /* Value type does not match. */
                        catch (ClassCastException e) {
                            logger.wrn( e, "Couldn't load value for %s, its config value is longer compatible.", currEntry.getName() );
                        }
                        break;
                    }
                }
        }

        /* Names and/or Types Map has become incompatible. */
        catch (ClassCastException e) {
            logger.wrn( e, "Config file is incompatible, reverting to defaults." );
            revert();
        }
        catch (ClassNotFoundException e) {
            logger.err( e, "Object in config file not supported, reverting to defaults." );
            revert();
        }
    }

    private static void revert() {

        names.clear();
    }

    /**
     * Add a {@link Runnable} to execute as we are shutting down.
     *
     * @param hook The hook to execute during shutdown.
     */
    public static void addShutdownHook(final Runnable hook) {

        shutdownHooks.add( hook );
    }

    private T      value;
    private int    hashCode;
    private String type;

    private final transient Set<ConfigChangedListener<T>> listeners;

    /**
     * Create a new {@link BaseConfig} instance.
     *
     * @param defaultValue The default value for this entry.
     */
    protected BaseConfig(final T defaultValue) {

        value = defaultValue;
        listeners = new HashSet<ConfigChangedListener<T>>();
    }

    /**
     * Get the configured value for this setting.
     *
     * @return Guess.
     */
    public T get() {

        return value;
    }

    /**
     * Get the name of this {@link BaseConfig}.
     *
     * @return The field name of this {@link BaseConfig}.
     */
    public String getName() {

        if (names.containsKey( this ))
            return names.get( this );

        // Logger.warn( "This config entry has not (yet) been flushed, trying a fall back to find
        // its name." );
        return getName( getClass() );
    }

    /**
     * Get the name of this {@link BaseConfig}.
     *
     * @return The field name of this {@link BaseConfig}.
     */
    public String getType() {

        if (type != null)
            return type;

        logger.wrn( "This config entry has not (yet) been flushed, returning its superclass." );

        if (value == null)
            return "Object";
        return value.getClass().toString();
    }

    /**
     * Check whether this {@link BaseConfig} is unset (<code>null</code> or empty {@link #toString()} or {@link Collection#isEmpty()}).
     *
     * @return Guess.
     */
    public boolean isEmpty() {

        if (value == null)
            return true;

        if (value instanceof Collection<?>)
            return ((Collection<?>) value).isEmpty();

        return value.toString().length() == 0;
    }

    /**
     * Check whether there is a value defined for this {@link BaseConfig}.
     *
     * @return Guess.
     */
    public boolean isSet() {

        return !isEmpty();
    }

    /**
     * Change the configured value for this setting.
     *
     * @param newValue The setting's new value.
     *
     * @return <code>true</code> in case the original value was not the same as the new value.
     */
    public boolean set(final T newValue) {

        if (value == null && newValue == null)
            return false;
        if (value != null && newValue != null && value.equals( newValue ))
            return false;

        value = newValue;

        for (final ConfigChangedListener<T> listener : listeners)
            listener.configValueChanged( this, value, newValue );

        return true;
    }

    /**
     * Forcefully change the configured value for this setting, even if the current setting is already equal to the new value.<br> You
     * should use this if you need the object in the config file to be a reference to the new value.
     *
     * @param newValue The setting's new value.
     *
     * @return true if the value was changed.
     */
    public boolean force(final T newValue) {

        value = null;
        return set( newValue );
    }

    /**
     * Register an event handler that will be called when this value changes.
     *
     * @param listener The {@link ConfigChangedListener} to register.
     */
    public void register(final ConfigChangedListener<T> listener) {

        listeners.add( listener );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        String field = getType().replaceFirst( "[^ <]* |\\w+\\.", "" );
        return String.format( "  %-20s %15s  =  %s", field, getName(), value );
    }

    /**
     * Unset this {@link BaseConfig}, setting its value to <code>null</code>.
     */
    public void unset() {

        value = null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "RawUseOfParameterizedType" })
    private String getName(final Class<? extends BaseConfig> configClass) {

        for (final Field field : configClass.getFields())
            try {
                if (field.get( null ) != null && field.get( null ) == this)
                    return field.getName();
            }

            catch (IllegalArgumentException ignored) {
            }
            catch (IllegalAccessException ignored) {
            }

        logger.wrn( "Could not find the name of config entry with value: %s", get() );
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {

        if (hashCode == 0) {
            logger.wrn( "Could not find the hash code of config entry with value: %s", get() );
            return super.hashCode();
        }

        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {

        if (obj == this)
            return true;
        if (!(obj instanceof BaseConfig<?>))
            return false;

        return obj.hashCode() == hashCode();
    }
}
