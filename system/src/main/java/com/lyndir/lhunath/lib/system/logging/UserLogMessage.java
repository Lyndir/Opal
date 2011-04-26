package com.lyndir.lhunath.lib.system.logging;

import com.lyndir.lhunath.lib.system.util.ObjectMeta;


/**
 * Marker interface for {@link UserLog} messages.
 *
 * <p> <i>02 10, 2011</i> </p>
 *
 * @author lhunath
 */
@ObjectMeta
public interface UserLogMessage {

    /**
     * @return A message that describes this condition to the user.
     */
    String getLocalizedMessage();
}
