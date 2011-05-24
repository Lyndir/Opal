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

import com.lyndir.lhunath.opal.system.UIUtils;
import java.awt.*;
import java.util.logging.Level;


/**
 * <i>HTMLFormatter - A log formatter which wraps the output of {@link LogFormatter} in styling HTML.</i><br> <br> Uses HTML to style the
 * output of {@link LogFormatter} and color it depending on the log level.<br> <br>
 *
 * @author lhunath
 */
public class HTMLFormatter extends LogFormatter {

    @Override
    protected void setColors() {

        levelColor.put( null, "</pre>" );
        levelColor.put( Level.SEVERE, "<pre style='color: " + UIUtils.colorToHex( UIUtils.RED ) + "'>" );
        levelColor.put( Level.WARNING, "<pre style='color: " + UIUtils.colorToHex( UIUtils.YELLOW ) + "'>" );
        levelColor.put( Level.INFO, "<pre style='color: " + UIUtils.colorToHex( Color.CYAN ) + "'>" );
        levelColor.put( Level.CONFIG, "<pre style='color: " + UIUtils.colorToHex( UIUtils.DARK_BLUE ) + "'>" );
        levelColor.put( Level.FINE, "<pre style='color: " + UIUtils.colorToHex( UIUtils.DARK_GREEN ) + "'>" );
        levelColor.put( Level.FINER, "<pre style='color: " + UIUtils.colorToHex( UIUtils.DARK_GREEN ) + "'>" );
        levelColor.put( Level.FINEST, "<pre style='color: " + UIUtils.colorToHex( UIUtils.DARK_GREEN ) + "'>" );
    }
}
