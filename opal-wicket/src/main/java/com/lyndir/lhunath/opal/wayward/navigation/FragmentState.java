package com.lyndir.lhunath.opal.wayward.navigation;

/**
 * <h2>{@link FragmentState}<br> <sub>[in short] (TODO).</sub></h2>
 *
 * <p> <i>07 10, 2010</i> </p>
 *
 * @author lhunath
 */
public interface FragmentState {

    /**
     * @return The representation of this state as it should be used in the URL's fragment for navigation.
     */
    String toFragment();
}
