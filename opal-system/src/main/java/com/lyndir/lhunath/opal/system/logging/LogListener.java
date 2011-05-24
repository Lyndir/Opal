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
package com.lyndir.lhunath.opal.system.logging;

import java.util.logging.LogRecord;


/**
 * <i>LogListener - A listener for log messages.</i><br> <br> Implement this if you wish to be notified of log messages that are being
 * dispatched.<br> <br>
 *
 * @author lhunath
 */
public interface LogListener {

    /**
     * A log message was triggered on this listener.
     *
     * @param record The record that was logged through this listener.
     */
    void logMessage(LogRecord record);
}
