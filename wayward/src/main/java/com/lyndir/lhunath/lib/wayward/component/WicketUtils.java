/*
 *   Copyright 2010, Maarten Billemont
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
package com.lyndir.lhunath.lib.wayward.component;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.wicket.*;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;


/**
 * <h2>{@link WicketUtils}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> [description / usage]. </p>
 *
 * <p> <i>Sep 17, 2008</i> </p>
 *
 * @author lhunath
 */
public abstract class WicketUtils {

    /**
     * @return A formatter according to the given locale in short form.
     */
    public static DateFormat getDateFormat() {

        return DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, Session.get().getLocale() );
    }

    /**
     * @param date The date that should be formatted.
     *
     * @return A string that is the formatted representation of the given date according to the given locale in short form.
     */
    public static String format(final Date date) {

        return getDateFormat().format( date );
    }

    /**
     * @return A formatter according to the given locale's currency.
     */
    public static NumberFormat getCurrencyFormat() {

        return NumberFormat.getCurrencyInstance( Session.get().getLocale() );
    }

    /**
     * @param number The number that should be formatted.
     *
     * @return A string that is the formatted representation of the given amount of currency according to the given locale.
     */
    public static String format(final Number number) {

        return getCurrencyFormat().format( number );
    }

    /**
     * @return The {@link HttpServletRequest} that initiated in the active Wicket {@link Request}.
     */
    public static HttpServletRequest getServletRequest() {

        return ((WebRequest) RequestCycle.get().getRequest()).getHttpServletRequest();
    }

    /**
     * @return The {@link HttpServletResponse} that the active Wicket {@link Response} will be sent to.
     */
    public static HttpServletResponse getServletResponse() {

        Response response = RequestCycle.get().getResponse();
        if (response instanceof WebResponse)
            return ((WebResponse) RequestCycle.get().getResponse()).getHttpServletResponse();

        return null;
    }

    /**
     * @return The {@link HttpSession} that contains in the active Wicket {@link Request}.
     */
    public static HttpSession getHttpSession() {

        return getServletRequest().getSession();
    }

    /**
     * Convenience localization provider method.
     *
     * @param component The component in whose context to resolve localization keys.
     * @param key       The localization key that provides the locale-specific value. The value may be a {@link
     *                  MessageFormat#format(Object)}-string. In this case, the args will be used to fill in variables.
     * @param args      The arguments that contain the data to fill into the locale-specific format specification.
     *
     * @return The localized value according to the application's default localizer.
     */
    public static String localize(final Component component, final String key, final Object... args) {

        // Single argument invocation: format is localization key.
        return new StringResourceModel( key, component, null, args, key ).getString();
    }
}
