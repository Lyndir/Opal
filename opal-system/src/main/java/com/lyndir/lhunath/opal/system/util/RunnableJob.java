package com.lyndir.lhunath.opal.system.util;

import javax.annotation.Nullable;


/**
 * @author lhunath, 1/22/2014
 */
public abstract class RunnableJob<R> implements Job<R>, Runnable {

    @Nullable
    @Override
    public final R execute() {
        run();
        return null;
    }
}
