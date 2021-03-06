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
package com.lyndir.lhunath.opal.gui;

import com.lyndir.lhunath.opal.system.util.UIUtils;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import javax.swing.*;


/**
 * <i>{@link ToolTip} - This panel wraps a component which shows a tooltip on hover.</i><br> <br> By default you would specify the tooltip
 * text (HTML) which would show as the user hovers the panel.<br> You can use the {@link #setContent(JComponent)} method to change the
 * content of the panel. The content will have listeners attached to show the tooltip on hover.<br> <br>
 *
 * @author lhunath
 */
public class ToolTip extends JPanel {

    protected static final Timer tipTimer = new Timer( "Tip Timer", true );
    private static final   int   PADDING  = 15;

    protected static       ToolTip       activeTip;
    protected static final JLabel        stickyHint;
    protected static       JFrame        toolTipFrame;
    protected static       JWindow       toolTipWindow;
    protected static final PaintPanel    toolTipContainer;
    static final           WindowAdapter toolTipContentWindowListener;
    protected static       TimerTask     toolTipSchedule;
    protected static final ScrollPanel   toolTipPanel;
    protected static final JEditorPane   toolTipPane;

    protected static final int maxWidth;
    protected static final int maxHeight;

    protected final TipButtonListener           buttonListener;
    protected final List<ToolTipStickyListener> stickyListeners;
    protected       boolean                     stickable;
    protected       boolean                     stickOnly;

    private   JComponent toolTipContent;
    protected String     toolTipText;

    static {
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        maxWidth = (int) maxBounds.getMaxX() * 1 / 3;
        maxHeight = (int) maxBounds.getMaxY() * 3 / 4;

        stickyHint = new JLabel( "Press F2 to frame this tip." );
        stickyHint.setFont( stickyHint.getFont().deriveFont( 10f ) );
        stickyHint.setHorizontalAlignment( SwingConstants.CENTER );

        toolTipPane = new JEditorPane( "text/html", null );
        toolTipPane.setEditable( false );
        toolTipPane.setOpaque( false );

        toolTipPanel = new ScrollPanel() {

            @Override
            public void paint(final Graphics g) {

                Dimension size = new Dimension( toolTipPane.getWidth() + 5,
                                                toolTipPane.getHeight() + (activeTip.stickable? stickyHint.getHeight(): 0) );
                Window window = toolTipWindow;
                if (window == null || !window.isDisplayable())
                    window = toolTipFrame;

                if (window.getWidth() != size.width && window.getHeight() != size.height) {
                    window.setSize( size );
                    return;
                }

                super.paint( g );
            }
        };
        toolTipPanel.setLayout( new BoxLayout( toolTipPanel, BoxLayout.PAGE_AXIS ) );
        toolTipPanel.add( toolTipPane );

        toolTipContainer = PaintPanel.gradientPanel( new Point( 0, 1 ), new Point( 0, -1 ) );
        toolTipContainer.setLayout( new BorderLayout() );
        toolTipContainer.setAutoColorControl( 4 );

        toolTipContentWindowListener = new WindowAdapter() {

            @Override
            public void windowLostFocus(final WindowEvent e) {

                if (toolTipFrame == null)
                    closeTip();
            }
        };
    }

    /**
     * Create a new {@link ToolTip} instance.
     *
     * @param toolTip The text to show when hovering this button.
     */
    public ToolTip(final String toolTip) {

        this( toolTip, new JLabel( UIUtils.getIcon( "help.png" ) ) );
    }

    /**
     * Create a new {@link ToolTip} instance.
     *
     * @param toolTip   The text to show when hovering this button.
     * @param component The component to use as content.
     */
    public ToolTip(final String toolTip, final JComponent component) {

        super( new BorderLayout() );
        setOpaque( false );

        stickyListeners = new ArrayList<ToolTipStickyListener>();
        buttonListener = new TipButtonListener();

        toolTipPane.getInputMap( WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ), "stick" );
        toolTipPane.getActionMap().put( "stick", new AbstractAction( "stick" ) {

            @Override
            public void actionPerformed(final ActionEvent e) {

                toggleSticky();
            }
        } );

        listen( this );
        setContent( component );
        setTip( toolTip );
    }

    /**
     * Check whether this {@link ToolTip} has a sticky frame popping up on click.
     *
     * @return Guess.
     */
    public boolean isStickable() {

        return stickable;
    }

    /**
     * Set whether this {@link ToolTip} has a sticky frame popping up on click.
     *
     * @param stickable Guess.
     */
    public void setStickable(final boolean stickable) {

        this.stickable = stickable;
    }

    /**
     * @return <code>true</code> if this tip should only be sticked not shown on hover.
     */
    public boolean isStickOnly() {

        return stickOnly;
    }

    /**
     * @param stickOnly <code>true</code> if this tip should only be sticked not shown on hover.
     */
    public void setStickOnly(final boolean stickOnly) {

        this.stickOnly = stickOnly;
    }

    /**
     * Define the text that will show up in the tooltip. Set this to null to disable the tooltip momentarily.
     *
     * @param toolTip Guess.
     */
    public void setTip(String toolTip) {

        if (toolTip != null && toolTip.length() > 0 && !toolTip.contains( "<html>" )) {
            toolTip = toolTip.trim();
            toolTip = toolTip.replaceFirst( "^([^\n\r]+)[\n\r]", "<h2>$1</h2>" );
            toolTip = toolTip.replaceAll( "\n", "<br>" );
        }

        toolTipText = toolTip;
        if (activeTip == this && toolTipPane != null)
            toolTipPane.setText( toolTipText );
    }

    /**
     * Make the given object listen to sticky state changes of this tooltip.
     *
     * @param listener The object that will listen to the state changes.
     */
    public void addStickyListener(final ToolTipStickyListener listener) {

        stickyListeners.add( listener );
    }

    /**
     * Set the component used to show the tooltip on hover.
     *
     * @return Guess.
     */
    public JComponent getContent() {

        return toolTipContent;
    }

    /**
     * Set the component used to show the tooltip on hover.
     *
     * @param content Guess.
     */
    public void setContent(final JComponent content) {

        if (toolTipContent != null)
            unlisten( toolTipContent );

        removeAll();
        add( toolTipContent = content, BorderLayout.CENTER );
        listen( toolTipContent );
    }

    /**
     * Recursively add the main mouse listener to the given component and its children.
     *
     * @param component The component to which the main mouse listener needs to be added.
     */
    private void listen(final Component component) {

        if (component instanceof Container)
            for (final Component child : ((Container) component).getComponents())
                listen( child );

        component.addMouseListener( buttonListener );
        component.addMouseMotionListener( buttonListener );
    }

    /**
     * Recursively remove the main mouse listener from the given component and its children.
     *
     * @param component The component from which the main mouse listeners need to be removed.
     */
    private void unlisten(final Component component) {

        if (component instanceof Container)
            for (final Component child : ((Container) component).getComponents())
                unlisten( child );

        component.removeMouseListener( buttonListener );
        component.removeMouseMotionListener( buttonListener );
    }

    /**
     * Convert the tooltip window to a frame.
     */
    public void stick() {

        if (toolTipFrame != null || !stickable)
            return;

        activeTip = this;

        for (final ToolTipStickyListener listener : stickyListeners)
            listener.stickyState( this, true );

        toolTipFrame = new JFrame();
        toolTipFrame.setUndecorated( true );
        toolTipFrame.addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent windowEvent) {

                for (final ToolTipStickyListener listener : stickyListeners)
                    listener.stickyState( ToolTip.this, false );

                toolTipFrame.dispose();
                toolTipFrame = null;
            }
        } );

        PaintPanel gradient = PaintPanel.gradientPanel( new Point( 0, 1 ), new Point( 0, -1 ) );
        gradient.setLayout( new BorderLayout() );
        gradient.setAutoColorControl( 4 );
        gradient.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        toolTipPane.setText( toolTipText );
        JScrollPane pane = new JScrollPane( toolTipPanel );
        pane.setOpaque( false );
        pane.getViewport().setOpaque( false );
        pane.setBorder( BorderFactory.createEmptyBorder() );
        pane.setViewportBorder( BorderFactory.createEmptyBorder() );
        gradient.add( pane, BorderLayout.CENTER );
        JLabel unstickyhint = new JLabel( "Press F2 to close this frame." );
        unstickyhint.setFont( unstickyhint.getFont().deriveFont( 10f ) );
        unstickyhint.setHorizontalAlignment( SwingConstants.CENTER );
        gradient.add( unstickyhint, BorderLayout.SOUTH );

        toolTipFrame.setContentPane( gradient );
        toolTipFrame.pack();
        toolTipFrame.setSize( Math.min( toolTipFrame.getWidth(), maxWidth ), Math.min( toolTipFrame.getHeight(), maxHeight ) );
        toolTipFrame.setLocationRelativeTo( null );

        pane.getViewport().scrollRectToVisible( new Rectangle( 0, 0, 0, 0 ) );
        toolTipFrame.setVisible( true );

        // Dispose of the toolTipWindow if it is still there.
        if (toolTipWindow != null) {
            toolTipWindow.dispose();
            toolTipWindow = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateUI() {

        super.updateUI();

        toolTipContainer.updateUI();
        toolTipContainer.setBorder( BorderFactory.createLineBorder( toolTipContainer.getBackground().darker().darker() ) );
    }

    /**
     * Toggle the stickyness of the active tooltip.
     */
    protected static void toggleSticky() {

        if (toolTipFrame != null) {
            toolTipFrame.dispose();
            toolTipFrame = null;
        } else if (activeTip != null)
            activeTip.stick();
    }

    /**
     * Close the active tooltip.
     */
    protected static void closeTip() {

        if (toolTipSchedule != null)
            toolTipSchedule.cancel();

        if (toolTipWindow != null) {
            toolTipWindow.dispose();
            toolTipWindow = null;
        }

        activeTip = null;
    }

    /**
     * <h2>{@link ToolTipButton}<br> <sub>[in short] (TODO).</sub></h2>
     *
     * <p> <i>Jan 28, 2010</i> </p>
     *
     * @author lhunath
     */
    @SuppressWarnings("unused")
    protected class TipButtonListener extends MouseAdapter {

        private static final long TIP_SHOW_DELAY = 500;

        protected int x;
        protected int y;

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseEntered(final MouseEvent e) {

            // Check if a tooltip is set.
            if (getContent() == null || toolTipText == null || toolTipText.trim().length() == 0 || stickOnly)
                return;

            // Don't do anything if this tip is already showing.
            if (activeTip == ToolTip.this)
                return;
            activeTip = ToolTip.this;

            // Check if tooltip is attached to frame instead of window.
            if (toolTipFrame != null)
                return;

            /* Calculate these guys only once. */
            Point mouseLocation = SwingUtilities.convertPoint( e.getComponent(), e.getPoint(), getContent() );
            x = (int) mouseLocation.getX();
            y = (int) mouseLocation.getY();

            /* Schedule a timer to show the tip after TIP_SHOW_DELAY milliseconds. */
            if (toolTipSchedule != null)
                toolTipSchedule.cancel();
            tipTimer.schedule( toolTipSchedule = new TimerTask() {

                @Override
                public void run() {

                    toolTipSchedule = null;

                    SwingUtilities.invokeLater( new Runnable() {

                        @Override
                        public void run() {

                            try {
                                /* Clean up any possibly already existing tooltip window. */
                                if (toolTipWindow != null)
                                    toolTipWindow.dispose();

                                /* Abort if the content isn't showing on the screen. */
                                if (!getContent().isShowing())
                                    return;

                                /* Put the text of this tooltip in the container. */
                                toolTipWindow = new JWindow( SwingUtilities.getWindowAncestor( ToolTip.this ) );
                                toolTipWindow.setBackground( toolTipContainer.getBackground().darker() );
                                toolTipWindow.setFocusable( false );
                                toolTipWindow.setFocusableWindowState( false );
                                toolTipWindow.setAlwaysOnTop( true );
                                toolTipWindow.addWindowListener( new WindowAdapter() {

                                    @Override
                                    public void windowClosed(final WindowEvent windowEvent) {

                                        if (activeTip == ToolTip.this)
                                            closeTip();
                                    }
                                } );

                                toolTipPane.setSize( 0, 0 );
                                toolTipPane.setText( toolTipText );
                                toolTipPane.setMaximumSize( new Dimension( maxWidth, maxHeight ) );

                                toolTipContainer.removeAll();
                                toolTipContainer.add( toolTipPanel, BorderLayout.CENTER );
                                if (stickable)
                                    toolTipContainer.add( stickyHint, BorderLayout.SOUTH );

                                toolTipWindow.setContentPane( toolTipContainer );
                                toolTipWindow.pack();
                                toolTipWindow.setSize( toolTipPane.getWidth() + 5,
                                                       toolTipPane.getHeight() + (stickable? stickyHint.getHeight(): 0) );

                                /* Determine the window's location. */
                                Point location = getContent().getLocationOnScreen();
                                location.translate( x + PADDING, y + PADDING );
                                double dx = getContent().getGraphicsConfiguration().getBounds().getMaxX()
                                            - (location.getX() + toolTipWindow.getWidth()) - PADDING;
                                if (dx < 0)
                                    location.translate( (int) dx, 0 );

                                /* Place and reveal the tooltip window. */
                                toolTipWindow.setLocation( location );
                                toolTipWindow.setVisible( true );

                                /* Listener that cleans up tip when content's window loses focus. */
                                Window window = SwingUtilities.windowForComponent( getContent() );
                                if (!Arrays.asList( window.getWindowListeners() ).contains( toolTipContentWindowListener ))
                                    window.addWindowFocusListener( toolTipContentWindowListener );
                            }
                            catch (NullPointerException ignored) {
                                /* Tooltip has been removed while timer was running. */
                            }
                        }
                    } );
                }
            }, TIP_SHOW_DELAY );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseExited(final MouseEvent e) {

            if (activeTip != ToolTip.this)
                return;

            // Don't do anything when new component is descendant of this tip's content.
            if (getContent() != null && e.getComponent() != null && getContent().contains(
                    SwingUtilities.convertPoint( e.getComponent(), e.getPoint(), getContent() ) ))
                return;

            if (activeTip == ToolTip.this)
                closeTip();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseMoved(final MouseEvent e) {

            if (e.getSource() instanceof Component) {
                Point pointOnContent = SwingUtilities.convertPoint( (Component) e.getSource(), e.getPoint(), getContent() );
                x = pointOnContent.x;
                y = pointOnContent.y;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseDragged(final MouseEvent e) {

            /* Nothing. */
        }
    }


    /**
     * A listener that is used to listen for sticky state changes.
     */
    public interface ToolTipStickyListener {

        /**
         * Notifies the listener that this tooltip's sticky state has changed.
         *
         * @param toolTip The tooltip whose sticky state changed.
         * @param sticky  true if the tooltip has been made sticky.
         */
        void stickyState(ToolTip toolTip, boolean sticky);
    }
}
