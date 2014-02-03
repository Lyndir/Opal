package com.lyndir.lhunath.opal.system.logging;

import com.google.common.collect.ImmutableMap;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import java.util.*;


/**
 * <h2>{@link UserLog}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>02 10, 2011</i> </p>
 *
 * @author lhunath
 */
public class UserLog {

    private static final ThreadLocal<EnumMap<Level, Map<Class<?>, List<UserLogMessage>>>> log = new ThreadLocal<EnumMap<Level, Map<Class<?>, List<UserLogMessage>>>>() {
        @Override
        protected EnumMap<Level, Map<Class<?>, List<UserLogMessage>>> initialValue() {

            return new EnumMap<>( Level.class );
        }
    };

    @SuppressWarnings("NonConstantLogger")
    private final Logger   logger;
    private final Class<?> type;

    public static UserLog get(final Class<?> type) {

        return new UserLog( type );
    }

    /**
     * Retrieve all log messages recorded for the given level.
     *
     * @param level The log level for which to look up messages.
     *
     * @return The messages recorded for this log level, mapped by the type that recorded them.
     */
    public static Map<Class<?>, List<UserLogMessage>> popLogForLevel(final Level level) {

        Map<Class<?>, List<UserLogMessage>> levelLog = log.get().get( level );
        if (levelLog == null)
            return ImmutableMap.of();

        // Remove from memory.
        log.get().put( level, null );

        return levelLog;
    }

    private UserLog(final Class<?> type) {

        this.type = type;

        logger = Logger.get( type );
    }

    public UserLog inf(final UserLogMessage message) {

        logger.inf( ObjectUtils.toString( message ) );
        return log( Level.INFO, message );
    }

    public UserLog wrn(final UserLogMessage message) {

        logger.inf( ObjectUtils.toString( message ) );
        return log( Level.WARNING, message );
    }

    public UserLog err(final UserLogMessage message) {

        logger.inf( ObjectUtils.toString( message ) );
        return log( Level.ERROR, message );
    }

    private UserLog log(final Level level, final UserLogMessage message) {

        Map<Class<?>, List<UserLogMessage>> levelLog = log.get().get( level );
        if (levelLog == null)
            log.get().put( level, levelLog = new HashMap<>() );

        List<UserLogMessage> typeLog = levelLog.get( type );
        if (typeLog == null)
            levelLog.put( type, typeLog = new LinkedList<>() );

        typeLog.add( message );
        return this;
    }

    public enum Level {
        INFO,
        WARNING,
        ERROR,
    }
}
