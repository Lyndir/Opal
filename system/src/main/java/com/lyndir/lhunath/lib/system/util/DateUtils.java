package com.lyndir.lhunath.lib.system.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.lib.system.logging.Logger;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.joda.time.*;


/**
 * <h2>{@link DateUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 29, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class DateUtils {

    private static final ThreadLocal<Stack<Timer>> currentTimer = new ThreadLocal<Stack<Timer>>() {
        @Override
        protected Stack<Timer> initialValue() {

            return new Stack<Timer>();
        }
    };
    private static final ImmutableMap<DurationFieldType, DateTimeFieldType> types;
    private static final ImmutableList<DateTimeFieldType> stdDateTimeFields;

    static {
        // Mapping DurationFieldTypes to DateTimeFieldTypes.
        ImmutableMap.Builder<DurationFieldType, DateTimeFieldType> typesBuilder = ImmutableMap.builder();
        types = typesBuilder.put( DurationFieldType.eras(), DateTimeFieldType.era() ) //
                .put( DurationFieldType.centuries(), DateTimeFieldType.centuryOfEra() ) //
                .put( DurationFieldType.years(), DateTimeFieldType.year() ) //
                .put( DurationFieldType.months(), DateTimeFieldType.monthOfYear() ) //
                .put( DurationFieldType.weeks(), DateTimeFieldType.weekOfWeekyear() ) //
                .put( DurationFieldType.days(), DateTimeFieldType.dayOfMonth() ) //
                .put( DurationFieldType.halfdays(), DateTimeFieldType.halfdayOfDay() ) //
                .put( DurationFieldType.hours(), DateTimeFieldType.hourOfDay() ) //
                .put( DurationFieldType.minutes(), DateTimeFieldType.minuteOfDay() ) //
                .put( DurationFieldType.seconds(), DateTimeFieldType.secondOfMinute() ) //
                .put( DurationFieldType.millis(), DateTimeFieldType.millisOfSecond() ) //
                .build();

        // Order of standard date/time fields from small to large.
        ImmutableList.Builder<DateTimeFieldType> stdDateTimeFieldsBuilder = ImmutableList.builder();
        stdDateTimeFieldsBuilder.add( DateTimeFieldType.millisOfSecond(), DateTimeFieldType.secondOfDay(), DateTimeFieldType.minuteOfHour(),
                                      DateTimeFieldType.hourOfDay(), DateTimeFieldType.dayOfMonth(), DateTimeFieldType.monthOfYear(),
                                      DateTimeFieldType.year() );
        stdDateTimeFields = stdDateTimeFieldsBuilder.build();
    }

    /**
     * Start a new timer and add it to the thread's timer stack.
     *
     * @param format A format specifier for the timer's name/description (used in its log output).
     * @param args   Arguments for the format specifier.
     *
     * @return A new timer.
     */
    public static Timer startTiming(final String format, final Object... args) {

        Timer timer = new Timer( String.format( format, args ) );
        currentTimer.get().push( timer );

        return timer;
    }

    /**
     * @return The most recently started timer that is still running.
     */
    public static Timer popTimer() {

        return currentTimer.get().pop();
    }

    /**
     * @param timer The timer that should no longer be tracked on the thread's timer stack.
     *
     * @return <code>true</code> if the timer was on the stack.
     */
    public static boolean removeTimer(final Timer timer) {

        return currentTimer.get().remove( timer );
    }

    public static class Timer {

        private String name;
        private Instant start = new Instant();
        private Instant end;

        public Timer(final String name) {

            this.name = name;
        }

        public ReadableDuration finish() {

            return new Duration( start(), end() );
        }

        public ReadableDuration logFinish(final Logger logger) {

            ReadableDuration duration = finish();

            logger.trc( "%s finished after %s.", name, duration );
            return duration;
        }

        public ReadableInstant start() {

            return start;
        }

        public ReadableInstant end() {

            if (end == null) {
                removeTimer( this );
                end = new Instant();
            }

            return end;
        }
    }

    /**
     * @param from  The instant from which to start counting.
     * @param to    The instant after the 'from' instant where to stop counting.
     * @param field The field whose occurrences to count between the instants.
     *
     * @return The amount of field occurrences between truncated versions of the given instants.
     */
    public static ReadablePeriod period(final ReadableInstant from, final ReadableInstant to, final DateTimeFieldType field) {

        PeriodType periodType = PeriodType.forFields( convertAll( field ) );

        return new Period( truncate( from, field ), truncate( to, field ), periodType );
    }

    /**
     * @param field Field that is part of a date or time instance representation.
     *
     * @return field in its duration representation.
     */
    public static DurationFieldType convert(final DateTimeFieldType field) {

        return field.getDurationType();
    }

    /**
     * @param fields Fields that are part of a date or time instance representation.
     *
     * @return Respective fields in their duration representation.
     */
    public static DurationFieldType[] convert(final DateTimeFieldType... fields) {

        return convertAll( fields );
    }

    /**
     * @param fields Fields that are part of a date or time instance representation.
     *
     * @return Respective fields in their duration representation.
     */
    public static DurationFieldType[] convertAll(final DateTimeFieldType... fields) {

        DurationFieldType[] durationFields = new DurationFieldType[fields.length];
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; ++i)
            durationFields[i] = convert( fields[i] );

        return durationFields;
    }

    /**
     * @param field Field in its duration representation.
     *
     * @return field that denotes a unit in time.
     */
    public static DateTimeFieldType convert(final DurationFieldType field) {

        return types.get( field );
    }

    /**
     * @param fields Fields in their duration representation.
     *
     * @return Respective fields that denote a unit in time.
     */
    public static DateTimeFieldType[] convert(final DurationFieldType... fields) {

        return convertAll( fields );
    }

    /**
     * @param fields Fields in their duration representation.
     *
     * @return Respective fields that denote a unit in time.
     */
    public static DateTimeFieldType[] convertAll(final DurationFieldType... fields) {

        DateTimeFieldType[] dateTimeFieldTypes = new DateTimeFieldType[fields.length];
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; ++i)
            dateTimeFieldTypes[i] = convert( fields[i] );

        return dateTimeFieldTypes;
    }

    /**
     * Truncate the given instant to the nearest earlier zero point of the given field.
     *
     * @param instant The instant to truncate.
     * @param field   The field on which to truncate the instant.
     *
     * @return A new instant which is either on or the closest instant before the given instant with the given field reset.
     */
    public static ReadableInstant truncate(final ReadableInstant instant, final DateTimeFieldType field) {

        DateTime zero = new DateTime( 0 );
        PeriodType periodType = PeriodType.forFields( convertAll( field ) );

        // Create a period from zero to the instant.
        Period fieldPeriod = new Period( zero, instant, periodType );
        // Find the amount of field occurrences in the period.
        int fieldQuantity = fieldPeriod.get( field.getDurationType() );

        // Create an instant using only the field occurrences.
        return zero.property( field ).addToCopy( fieldQuantity );
    }

    /**
     * @param startField The first field in the standard field series to return.
     *
     * @return All standard date/time fields starting from the given field; ordered from small to large.
     */
    public static ImmutableList<DateTimeFieldType> fieldsFrom(final DateTimeFieldType startField) {

        return fieldsFrom( startField, true );
    }

    /**
     * @param startField   The first field in the standard field series to return.
     * @param smallToLarge Whether to start the fields from small units to large (<code>true</code>) or the other way around.
     *
     * @return All standard date/time fields starting from the given field; ordered from small to large.
     */
    public static ImmutableList<DateTimeFieldType> fieldsFrom(final DateTimeFieldType startField, final boolean smallToLarge) {

        ImmutableList.Builder<DateTimeFieldType> resultFieldsBuilder = ImmutableList.builder();

        // Get the fields to iterate over.
        List<DateTimeFieldType> fields = stdDateTimeFields;
        if (!smallToLarge) {
            fields = Lists.newArrayList( fields );
            Collections.reverse( fields );
        }

        // Add all fields starting from the startField to the result list.
        boolean foundStart = false;
        for (final DateTimeFieldType field : fields) {
            if (field.equals( startField ))
                foundStart = true;
            if (foundStart)
                resultFieldsBuilder.add( field );
        }

        return resultFieldsBuilder.build();
    }
}
