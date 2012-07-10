package com.lyndir.lhunath.opal.json;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.lyndir.lhunath.opal.system.util.MetaObject;
import com.lyndir.lhunath.opal.system.util.ObjectUtils;
import java.io.Serializable;
import org.jetbrains.annotations.Nullable;


/**
 * A wrapper for sending {@link Gson} serializable data via a webservice and provide success/failure context data.  Works well with the
 * Pearl webservice classes.
 *
 * <p> <i>11 22, 2010</i> </p>
 *
 * @author lhunath
 */
public class JSONResult extends MetaObject {

    public static final int CODE_SUCCESS                 = 0;
    public static final int CODE_FAILURE_GENERIC         = -1;
    public static final int CODE_FAILURE_UPDATE_REQUIRED = -2;

    public static final String REQUEST_KEY_VERSION = "version";

    @Expose
    private final int      code;
    @Expose
    private       boolean  outdated;
    @Expose
    private final String   userDescription;
    @Expose
    private final String[] userDescriptionArguments;
    @Expose
    private final String   technicalDescription;

    @Expose
    private final Object   result;

    private JSONResult(final Object result, final int code, final String technicalDescription, final String userDescription,
                       final Object... userDescriptionArguments) {

        this.result = result;
        this.code = code;
        this.technicalDescription = technicalDescription;
        this.userDescription = userDescription;
        if (this.userDescription == null)
            this.userDescriptionArguments = null;
        else {
            this.userDescriptionArguments = new String[userDescriptionArguments.length];
            for (int o = 0; o < userDescriptionArguments.length; ++o)
                this.userDescriptionArguments[o] = ObjectUtils.ifNotNullElse( userDescriptionArguments[o], "" ).toString();
        }
    }

    public static JSONResult success(@Nullable final Object result) {

        return new JSONResult( result, CODE_SUCCESS, null, null );
    }

    public static JSONResult failureUpdateRequired(Serializable requiredVersion) {

        return new JSONResult( null, CODE_FAILURE_UPDATE_REQUIRED, "The server does not work with clients older than " + requiredVersion,
                               "server.error.outdated", requiredVersion ).setOutdated( true );
    }

    public static JSONResult failure(final String technicalDescription, final String userDescription,
                                     final Object... userDescriptionArguments) {

        return failure( CODE_FAILURE_GENERIC, technicalDescription, userDescription, userDescriptionArguments );
    }

    public static JSONResult failure(final int code, final String technicalDescription, final String userDescription,
                                     final Object... userDescriptionArguments) {

        return failure( code, null, technicalDescription, userDescription, userDescriptionArguments );
    }

    public static JSONResult failure(final int code, @Nullable final Object result, final String technicalDescription, final String userDescription,
                                     final Object... userDescriptionArguments) {

        return new JSONResult( result, code, technicalDescription, userDescription, userDescriptionArguments );
    }

    public int getCode() {

        return code;
    }

    public boolean isOutdated() {

        return outdated;
    }

    public JSONResult setOutdated(final boolean outdated) {

        this.outdated = outdated;

        return this;
    }

    public String getUserDescription() {

        return userDescription;
    }

    public String[] getUserDescriptionArguments() {

        return userDescriptionArguments;
    }

    public String getTechnicalDescription() {

        return technicalDescription;
    }

    public Object getResult() {

        return result;
    }
}
