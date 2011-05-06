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
package com.lyndir.lhunath.lib.system;

import com.lyndir.lhunath.lib.system.util.DateUtils;
import java.io.Serializable;
import java.util.*;
import java.util.Locale;


/**
 * <i>{@link Schedule} - [in short] (TODO).</i><br> <br> [description / usage].<br> <br>
 *
 * @author lhunath
 */
public abstract class Schedule implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;

    private static final Timer scheduler = new Timer( "Scheduler", true );

    private           Calendar  nextSchedule;
    private           int       stepAmount;
    private           int       stepField;
    private transient boolean   isScheduled;
    private transient TimerTask scheduleTask;

    /**
     * Update this schedule's timings.
     *
     * @param time  The time at which this scheduled event must take place.
     * @param steps The amount of steps specified by the step field that apply.
     * @param step  An enumeration of the relevant {@link Calendar} fields in the given time specification.
     */
    public void setSchedule(final long time, final int steps, final int step) {

        stepAmount = steps;
        stepField = step;

        /* Calculate the base of this schedule's occurance. */
        nextSchedule = Calendar.getInstance();
        Calendar baseSchedule = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) );
        baseSchedule.setTimeInMillis( time );
        for (final int field : DateUtils.calendarFields) {
            if (field == stepField)
                break;

            nextSchedule.set( field, baseSchedule.get( field ) );
        }

        Date now = new Date();
        while (nextSchedule.getTime().after( now ))
            nextSchedule.add( stepField, -1 );
    }

    /**
     * Schedule this {@link Schedule} for execution in its next iteration.
     */
    protected void schedule() {

        Date now = new Date();
        while (nextSchedule.getTime().before( now ))
            nextSchedule.add( stepField, stepAmount );

        scheduler.schedule(
                scheduleTask = new TimerTask() {

                    @Override
                    public void run() {

                        Schedule.this.run();
                        schedule();
                    }
                }, nextSchedule.getTime() );
        isScheduled = true;
    }

    /**
     * Schedule this {@link Schedule} in case it hadn't been scheduled yet.
     */
    public void start() {

        if (!isScheduled)
            schedule();
    }

    /**
     * Cancel the current scheduled task and stop this schedule.
     */
    public void cancel() {

        if (scheduleTask != null)
            scheduleTask.cancel();
    }

    /**
     * Invoke this {@link Schedule} manually.
     */
    public void now() {

        cancel();
        new Thread( this, "Forced Schedule" ).start();

        /* Reschedule if schedule information is present. */
        if (nextSchedule != null)
            schedule();
    }

    /**
     * Get a description of this schedule.
     *
     * @return A string that describes what this schedule does.
     */
    public abstract String getDescription();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        StringBuffer time = new StringBuffer();
        for (final int field : DateUtils.calendarFields) {
            if (field == stepField)
                break;

            time.insert( 0, String.format( "%02d%s", nextSchedule.get( field ), DateUtils.calendarSuffix( field ) ) );
        }

        String desc = DateUtils.calendarDesc.get( stepField ).toLowerCase( Locale.ENGLISH );
        if (stepAmount > 1)
            desc += 's';

        return String.format( "%s (Every %d %s at %s)", getDescription(), stepAmount, desc, time );
    }
}
