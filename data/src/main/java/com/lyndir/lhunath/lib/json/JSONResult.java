package com.lyndir.lhunath.lib.json;

import static com.lyndir.lhunath.lib.system.util.ObjectUtils.getOrDefault;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.lyndir.lhunath.lib.system.util.ObjectMeta;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import java.io.Serializable;


/**
 * A wrapper for sending {@link Gson} serializable data via a webservice and provide success/failure context data.  Works well with the
 * iLibs webservice classes.
 *
 * <p> <i>11 22, 2010</i> </p>
 *
 * @author lhunath
 */
@ObjectMeta
public class JSONResult {

    public static final int CODE_SUCCESS                 = 0;
    public static final int CODE_FAILURE_GENERIC         = -1;
    public static final int CODE_FAILURE_UPDATE_REQUIRED = -2;

    public static final String REQUEST_KEY_VERSION = "version";

    @Expose
    private final int      code;
    @Expose
    private final String   userDescription;
    @Expose
    private final String[] userDescriptionArguments;
    @Expose
    private final String   technicalDescription;
    @Expose
    private       boolean  outdated;

    @Expose
    private final Object result;

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
                this.userDescriptionArguments[o] = getOrDefault( userDescriptionArguments[o], "" ).toString();
        }
    }

    public static JSONResult success(final Object result) {

        return new JSONResult( result, CODE_SUCCESS, null, null );
    }

    public static JSONResult failureUpdateRequired(Serializable requiredVersion) {

        return new JSONResult(
                null, CODE_FAILURE_UPDATE_REQUIRED, "The server does not work with clients older than " + requiredVersion,
                "server.error.outdated", requiredVersion ).setOutdated( true );
    }

    public static JSONResult failure(final String technicalDescription, final String userDescription,
                                     final Object... userDescriptionArguments) {

        return new JSONResult( null, CODE_FAILURE_GENERIC, technicalDescription, userDescription, userDescriptionArguments );
    }

    public static JSONResult failure(final int code, final String technicalDescription, final String userDescription,
                                     final Object... userDescriptionArguments) {

        return new JSONResult( null, code, technicalDescription, userDescription, userDescriptionArguments );
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

    public int getCode() {

        return code;
    }

    @Override
    public int hashCode() {

        return ObjectUtils.hashCode( this );
    }

    @Override
    public boolean equals(final Object obj) {

        return ObjectUtils.equals( this, obj );
    }

    @Override
    public String toString() {

        return ObjectUtils.toString( this );
    }
}
