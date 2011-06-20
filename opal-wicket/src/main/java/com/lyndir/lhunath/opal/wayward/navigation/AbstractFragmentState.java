package com.lyndir.lhunath.opal.wayward.navigation;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import org.jetbrains.annotations.Nullable;


/**
 * <h2>{@link AbstractFragmentState}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 10, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AbstractFragmentState implements FragmentState {

    private final List<String> fragments;

    protected AbstractFragmentState() {

        fragments = Lists.newLinkedList();
    }

    protected AbstractFragmentState(final String fragment) {

        fragments = Lists.newLinkedList( Splitter.on( '/' ).split( fragment ) );
        assertFragments();
    }

    protected AbstractFragmentState(final List<String> fragments) {

        this.fragments = fragments;
        assertFragments();
    }

    @Nullable
    protected String findFragment(final int index) {

        if (index < fragments.size())
            return fragments.get( index );

        return null;
    }

    protected void appendFragment(final String fragment) {

        fragments.add( fragment );
        assertFragments();
    }

    /**
     * Override this method if you need to do custom checks on the validity of String fragments.
     */
    protected void assertFragments() {

    }

    @Override
    public String toFragment() {

        return Joiner.on( '/' ).useForNull( "" ).join( getStateFragments() );
    }

    protected final Iterable<String> getStateFragments() {

        return fragments;
    }

    @Override
    public String toString() {

        return String.format( "{%s: %s}", getClass().getSimpleName(), toFragment() );
    }
}
