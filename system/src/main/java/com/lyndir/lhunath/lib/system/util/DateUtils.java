package com.lyndir.lhunath.lib.system.util;

import org.joda.time.*;


/**
 * <h2>{@link DateUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 29, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class DateUtils {

    /**
     * @param from  The instant from which to start counting.
     * @param to    The instant after the 'from' instant where to stop counting.
     * @param field The field whose occurrences to count between the instants.
     *
     * @return The amount of field occurrences between truncated versions of the given instants.
     */
    public static ReadablePeriod period(final ReadableInstant from, final ReadableInstant to, final DateTimeFieldType field) {

        PeriodType periodType = PeriodType.forFields( convert( field ) );

        return new Period( truncate( from, field ), truncate( to, field ), periodType );
    }

    /**
     * @param fields Fields that are part of a date or time instance representation.
     *
     * @return Respective fields in their duration representation.
     */
    public static DurationFieldType[] convert(final DateTimeFieldType... fields) {

        DurationFieldType[] durationFields = new DurationFieldType[fields.length];
        for (int i = 0, fieldsLength = fields.length; i < fieldsLength; ++i)
            durationFields[i] = fields[i].getDurationType();

        return durationFields;
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
        PeriodType periodType = PeriodType.forFields( convert( field ) );

        // Create a period from zero to the instant.
        Period fieldPeriod = new Period( zero, instant, periodType );
        // Find the amount of field occurrences in the period.
        int fieldQuantity = fieldPeriod.get( field.getDurationType() );

        // Create an instant using only the field occurrences.
        return zero.property( field ).addToCopy( fieldQuantity );
    }
}
