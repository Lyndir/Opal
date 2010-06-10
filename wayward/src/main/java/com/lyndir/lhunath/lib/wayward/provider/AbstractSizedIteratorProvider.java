package com.lyndir.lhunath.lib.wayward.provider;

import com.lyndir.lhunath.lib.system.collection.SizedIterator;


/**
 * <h2>{@link AbstractSizedIteratorProvider}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>06 06, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AbstractSizedIteratorProvider<T> extends AbstractIteratorProvider<T> {

    @Override
    protected SizedIterator<T> getIterator() {

        return (SizedIterator<T>) super.getIterator();
    }

    @Override
    protected abstract SizedIterator<T> load();

    @Override
    public int size() {

        return getIterator().size();
    }
}
