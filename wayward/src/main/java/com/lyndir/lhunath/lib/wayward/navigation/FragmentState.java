package com.lyndir.lhunath.lib.wayward.navigation;

import org.apache.wicket.markup.html.panel.Panel;


/**
 * <h2>{@link FragmentState}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 10, 2010</i> </p>
 *
 * @author lhunath
 */
public interface FragmentState<P extends Panel, F extends FragmentState<P, F>> {

    FragmentNavigationTab<P, F> getFragmentTab();
}
