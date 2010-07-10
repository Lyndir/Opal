package com.lyndir.lhunath.lib.wayward.navigation;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.lyndir.lhunath.lib.system.util.ObjectUtils;
import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link AbstractFragmentState}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 10, 2010</i> </p>
 *
 * @author lhunath
 */
public abstract class AbstractFragmentState<P extends Panel, S extends FragmentState<P, S>> implements FragmentState<P, S> {

    private final ImmutableList<String> fragments;

    protected AbstractFragmentState(ImmutableList<String> fragments) {

        this.fragments = fragments;

        Preconditions.checkArgument( ObjectUtils.equal( findFragment( 0 ), getFragmentTab().getTabFragment() ),
                                     "Can't load %s state from %s: No fragments found or initial fragment does not match this tab's.",
                                     getClass().getSimpleName(), fragments );
    }

    protected AbstractFragmentState(final String fragment) {

        this( ImmutableList.copyOf( Splitter.on( '/' ).split( fragment ) ) );
    }

    protected String findFragment(final int index) {

        if (index < fragments.size())
            return fragments.get( index );

        return null;
    }
}
