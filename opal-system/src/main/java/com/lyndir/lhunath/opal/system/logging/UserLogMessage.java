package com.lyndir.lhunath.opal.system.logging;

import com.lyndir.lhunath.opal.system.util.ObjectMeta;


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
