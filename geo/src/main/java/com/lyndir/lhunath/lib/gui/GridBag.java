/*
 *   Copyright 2005-2007 Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.lib.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.lyndir.lhunath.lib.math.Vec2;
import com.lyndir.lhunath.lib.system.Utils;


/**
 * <i>{@link GridBag} - [in short] (TODO).</i><br>
 * <br>
 * [description / usage].<br>
 * <br>
 * 
 * @author lhunath
 */
public class GridBag extends GridBagConstraints {

    private ArrayList<ArrayList<Vec2>> grid;
    private JComponent                 container;
    private Font                       font;
    private Color                      back;
    private Color                      front;


    /**
     * Create a new {@link GridBag} instance.
     */
    public GridBag() {

        this( new JPanel() );
    }

    /**
     * Create a new {@link GridBag} instance.
     * 
     * @param c
     *            The container that will have the grid applied to it and the components {@link #add(Component...)}ed to
     *            it.
     */
    public GridBag(JComponent c) {

        container = c;

        grid = new ArrayList<ArrayList<Vec2>>();
        container.setLayout( new GridBagLayout() );

        fill = BOTH;
        weightx = 1;
        weighty = 0;
        gridx = gridy = 0;
        gridwidth = gridheight = 1;
        updateGrid();
    }

    /**
     * @return The font of this {@link GridBag}.
     */
    public Font getFont() {

        return font;
    }

    /**
     * Set the font to use for auto-created labels.
     * 
     * @param font
     *            The font to use.
     * @return The {@link GridBag}.
     */
    public GridBag setFont(Font font) {

        this.font = font;

        return this;
    }

    /**
     * @return The foreground of this {@link GridBag}.
     */
    public Color getForeground() {

        return front;
    }

    /**
     * Set the foreground to use for auto-created items.
     * 
     * @param color
     *            The color to use.
     * @return The {@link GridBag}.
     */
    public GridBag setForeground(Color color) {

        front = color;

        return this;
    }

    /**
     * @return The background of this {@link GridBag}.
     */
    public Color getBackground() {

        return back;
    }

    /**
     * Set the background to use for auto-created items.
     * 
     * @param color
     *            The color to use.
     * @return The {@link GridBag}.
     */
    public GridBag setBackground(Color color) {

        back = color;

        return this;
    }

    /**
     * @return The container that this {@link GridBag} is applied to.
     */
    public JComponent getContainer() {

        return container;
    }

    /**
     * @param weightx
     *            The horizontal weight for {@link GridBag}.
     * @return The {@link GridBag}.
     */
    public GridBag setWeightX(int weightx) {

        this.weightx = weightx;

        return this;
    }

    /**
     * @param weighty
     *            The horizontal weight for {@link GridBag}.
     * @return The {@link GridBag}.
     */
    public GridBag setWeightY(int weighty) {

        this.weighty = weighty;

        return this;
    }

    /**
     * Add the given title to the active grid and the given component in the {@link #nextGrid()}.
     * 
     * @param title
     *            A string to use as text for a {@link JLabel} that will be added as title for the component.
     * @param component
     *            The component to assign to the active grid.
     * @return The {@link GridBag}.
     */
    public GridBag add(String title, Component component) {

        return add( title, gridwidth, component );
    }

    /**
     * Add the given title to the active grid and the given component in the {@link #nextGrid()}.
     * 
     * @param title
     *            A string to use as text for a {@link JLabel} that will be added as title for the component.
     * @param components
     *            The component to assign to the active grid.
     * @param width
     *            Defines the width that the grid of the component should span.
     * @return The {@link GridBag}.
     */
    public GridBag add(String title, int width, Component components) {

        JLabel label = new JLabel( title );
        if (font != null)
            label.setFont( font );
        if (front != null)
            label.setBackground( back );
        if (back != null)
            label.setForeground( front );
        add( label );

        return nextGrid().add( width, components );
    }

    /**
     * Add the given component to the active grid.
     * 
     * @param components
     *            The component to assign to the active grid.
     * @return The {@link GridBag}.
     */
    public GridBag add(Component... components) {

        for (Component c : components) {
            container.add( c, this );
            nextGrid();
        }

        return this;
    }

    /**
     * Add the given component to the active grid.
     * 
     * @param components
     *            The component to assign to the active grid.
     * @param width
     *            Defines the width that the grid of the component should span.
     * @return The {@link GridBag}.
     */
    public GridBag add(int width, Component... components) {

        return xyw( gridx, gridy, width ).add( components );
    }

    /**
     * Add a dummy component that can be used to simply create an empty grid that adheres to weight values.
     * 
     * @return The {@link GridBag}.
     */
    public GridBag addGlue() {

        return add( createGlue() );
    }

    /**
     * TODO: Describe method.
     */
    private Component createGlue() {

        JComponent glue = new GLabel( Utils.RED );
        glue.setOpaque( false );

        return glue;
    }

    /**
     * Move to the specified grid location.<br>
     * <br>
     * This method will set the active grid width and height to one.
     * 
     * @param x
     *            The horizontal grid location (0-based).
     * @param y
     *            The vertical grid location (0-based).
     * @return The {@link GridBag}.
     */
    public GridBag xy(int x, int y) {

        return xywh( x, y, 1, 1 );
    }

    /**
     * Move to the specified grid location.<br>
     * <br>
     * This method will set the active grid height to one.
     * 
     * @param x
     *            The horizontal grid location (0-based).
     * @param y
     *            The vertical grid location (0-based).
     * @param w
     *            The width to assign to the new grid.
     * @return The {@link GridBag}.
     */
    public GridBag xyw(int x, int y, int w) {

        return xywh( x, y, w, 1 );
    }

    /**
     * Move to the specified grid location.<br>
     * 
     * @param x
     *            The horizontal grid location (0-based).
     * @param y
     *            The vertical grid location (0-based).
     * @param w
     *            The width to assign to the new grid.
     * @param h
     *            The height to assign to the new grid.
     * @return The {@link GridBag}.
     */
    public GridBag xywh(int x, int y, int w, int h) {

        gridx = x;
        gridy = y;
        gridwidth = w;
        gridheight = h;
        updateGrid();

        return this;
    }

    /**
     * Move to the next grid in this line.<br>
     * The amount of horizontal grids this operation will skip depends on the initial width of the active grid.<br>
     * <br>
     * This method will set the active grid width and height to one.
     * 
     * @return The {@link GridBag}.
     */
    public GridBag nextGrid() {

        return nextGrid( 1, 1 );
    }

    /**
     * Move to the next grid in this line.<br>
     * The amount of horizontal grids this operation will skip depends on the initial width of the active grid.
     * 
     * @param w
     *            The width to assign to the new grid.
     * @param h
     *            The height to assign to the new grid.
     * @return The {@link GridBag}.
     */
    public GridBag nextGrid(int w, int h) {

        gridx += gridwidth;
        gridwidth = w;
        gridheight = h;
        updateGrid();

        return this;
    }

    /**
     * Move to the the first available grid in the next line.<br>
     * The amount of vertical grids this operation will descend depends on the initial height of the active grid.<br>
     * <br>
     * This method will set the active grid width and height to one.
     * 
     * @return The {@link GridBag}.
     */
    public GridBag nextLine() {

        return nextLine( 1, 1 );
    }

    /**
     * Move to the the first available grid in the next line.<br>
     * The amount of vertical grids this operation will descend depends on the initial height of the active grid.
     * 
     * @param w
     *            The width to assign to the new grid.
     * @param h
     *            The height to assign to the new grid.
     * @return The {@link GridBag}.
     */
    public GridBag nextLine(int w, int h) {

        gridy += gridheight;
        gridx = getFirstGridInLine();
        gridwidth = w;
        gridheight = h;
        updateGrid();

        return this;
    }

    private int getFirstGridInLine() {

        int firstGrid = 0;

        for (int x = 0; x < grid.size() && x <= gridx; ++x)
            for (int y = 0; y < grid.get( x ).size() && y <= gridy; ++y) {
                Vec2 gridSize = grid.get( x ).get( y );
                if (gridSize.y > 0 && y + gridSize.y > gridy)
                    if (gridSize.x > 0 && x + gridSize.x > firstGrid)
                        ++firstGrid;
            }

        return firstGrid;
    }

    private void updateGrid() {

        while (grid.size() <= gridx)
            grid.add( new ArrayList<Vec2>() );

        while (grid.get( gridx ).size() <= gridy)
            grid.get( gridx ).add( new Vec2( 0, 0 ) );

        grid.get( gridx ).set( gridy, new Vec2( gridwidth, gridheight ) );
    }
}
