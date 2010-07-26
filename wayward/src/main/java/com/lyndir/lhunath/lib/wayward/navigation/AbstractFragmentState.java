package com.lyndir.lhunath.lib.wayward.navigation;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import java.util.List;


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
        appendFragment( getTabFragment() );
    }

    protected AbstractFragmentState(final String fragment) {

        fragments = Lists.newLinkedList( Splitter.on( '/' ).split( fragment ) );
        assertFragments();
    }

    protected AbstractFragmentState(final List<String> fragments) {

        this.fragments = fragments;
        assertFragments();
    }

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
     * Override this method if you need to do custom checks on the validity of String fragments.  Don't forget to call the super
     * implementation.
     */
    protected void assertFragments() {

        Preconditions.checkArgument( ObjectUtils.equal( findFragment( 0 ), getTabFragment() ),
                                     "Can't load %s state from %s: No fragments found or initial fragment does not match this tab's.",
                                     getClass().getSimpleName(), fragments );
    }

    @Override
    public String toFragment() {

        return Joiner.on( '/' ).useForNull( "" ).join( getStateFragments() );
    }

    protected final Iterable<String> getStateFragments() {

        return fragments;
    }

    protected abstract String getTabFragment();
}
