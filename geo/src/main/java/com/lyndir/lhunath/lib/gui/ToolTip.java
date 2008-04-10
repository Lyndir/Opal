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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.lyndir.lhunath.lib.system.Utils;
import com.lyndir.lhunath.lib.system.logging.Logger;


/**
 * <i>{@link ToolTip} - This panel wraps a component which shows a tooltip on hover.</i><br>
 * <br>
 * By default you would specify the tooltip text (HTML) which would show as the user hovers the panel.<br>
 * You can use the {@link #setContent(JComponent)} method to change the content of the panel. The content will have
 * listeners attached to show the tooltip on hover.<br>
 * <br>
 * 
 * @author lhunath
 */
public class ToolTip extends JPanel {

    protected static final Timer          tipTimer  = new Timer( "Tip Timer", true );
    private static final int              PADDING   = 15;

    protected static ToolTip              activeTip;
    protected static JLabel               stickyhint;
    protected static JFrame               toolTipFrame;
    protected static JWindow              toolTipWindow;
    protected static PaintPanel           toolTipContainer;
    protected static JEditorPane          toolTipPane;

    protected static int                  maxWidth  = 0;
    protected static int                  maxHeight = 0;

    protected TipButtonListener           buttonListener;
    protected List<ToolTipStickyListener> stickyListeners;
    protected boolean                     stickable;

    private JComponent                    toolTipContent;
    protected String                      toolTipText;

    static {
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        maxWidth = (int) maxBounds.getMaxX() * 1 / 3;
        maxHeight = (int) maxBounds.getMaxY() * 3 / 4;

        stickyhint = new JLabel( "Press F2 to frame this tip." );
        stickyhint.setFont( stickyhint.getFont().deriveFont( 10f ) );

        toolTipPane = new JEditorPane( "text/html", null );
        toolTipPane.setEditable( false );
        toolTipPane.setOpaque( false );

        toolTipContainer = PaintPanel.gradientPanel( new Point( 0, 1 ), new Point( 0, -1 ) );
        toolTipContainer.setLayout( new BoxLayout( toolTipContainer, BoxLayout.Y_AXIS ) );
        toolTipContainer.setAutoColorControl( 4 );
    }


    /**
     * Create a new {@link ToolTip} instance.
     * 
     * @param toolTip
     *        The text to show when hovering this button.
     */
    public ToolTip(final String toolTip) {

        this( toolTip, new JLabel( Utils.getIcon( "help.png" ) ) );
    }

    /**
     * Create a new {@link ToolTip} instance.
     * 
     * @param toolTip
     *        The text to show when hovering this button.
     * @param c
     *        The component to use as content.
     */
    public ToolTip(String toolTip, final JComponent c) {

        super( new BorderLayout() );
        setOpaque( false );

        stickyListeners = new ArrayList<ToolTipStickyListener>();
        buttonListener = new TipButtonListener();

        c.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ), "stick" );
        c.getActionMap().put( "stick", new AbstractAction( "stick" ) {

            public void actionPerformed(ActionEvent e) {

                toggleSticky();
            }
        } );

        listen( this );
        setContent( c );
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
     * @param stickable
     *        Guess.
     */
    public void setStickable(boolean stickable) {

        this.stickable = stickable;
    }

    /**
     * Define the text that will show up in the tooltip. Set this to null to disable the tooltip momentarily.
     * 
     * @param toolTip
     *        Guess.
     */
    public void setTip(String toolTip) {

        if (toolTip == null || toolTip.length() == 0)
            return;

        if (!toolTip.contains( "<html>" )) {
            toolTip = toolTip.trim();
            toolTip = toolTip.replaceFirst( "^([^\n\r]+)[\n\r]", "<h2>$1</h2>" );
            toolTip = toolTip.replaceAll( "\n", "<br>" );
        }

        toolTipText = toolTip;
    }

    /**
     * Make the given object listen to sticky state changes of this tooltip.
     * 
     * @param listener
     *        The object that will listen to the state changes.
     */
    public void addStickyListener(ToolTipStickyListener listener) {

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
     * @param content
     *        Guess.
     */
    public void setContent(JComponent content) {

        if (toolTipContent != null)
            unlisten( toolTipContent );

        removeAll();
        add( toolTipContent = content, BorderLayout.CENTER );
        listen( toolTipContent );
    }

    /**
     * Recursively add the main mouse listener to the given component and its children.
     * 
     * @param c
     *        The component to which the main mouse listener needs to be added.
     */
    private void listen(Component c) {

        if (c instanceof Container)
            for (Component cc : ((Container) c).getComponents())
                listen( cc );

        c.addMouseListener( buttonListener );
        c.addMouseMotionListener( buttonListener );
    }

    /**
     * Recursively remove the main mouse listener from the given component and its children.
     * 
     * @param c
     *        The component from which the main mouse listeners need to be removed.
     */
    private void unlisten(Component c) {

        if (c instanceof Container)
            for (Component cc : ((Container) c).getComponents())
                unlisten( cc );

        c.removeMouseListener( buttonListener );
        c.removeMouseMotionListener( buttonListener );
    }

    /**
     * Convert the tooltip window to a frame.
     */
    public void stick() {

        if (toolTipFrame != null || !stickable)
            return;

        for (ToolTipStickyListener listener : stickyListeners)
            listener.stickyState( ToolTip.this, true );

        toolTipFrame = new JFrame();
        toolTipFrame.setUndecorated( true );
        toolTipFrame.addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent ee) {

                for (ToolTipStickyListener listener : stickyListeners)
                    listener.stickyState( ToolTip.this, false );

                toolTipFrame.dispose();
                toolTipFrame = null;
            }
        } );

        PaintPanel gradient = PaintPanel.gradientPanel( new Point( 0, 1 ), new Point( 0, -1 ) );
        gradient.setLayout( new BoxLayout( gradient, BoxLayout.Y_AXIS ) );
        gradient.setAutoColorControl( 4 );
        gradient.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        toolTipPane.setText( toolTipText );
        JScrollPane pane = new JScrollPane( toolTipPane );
        pane.setOpaque( false );
        pane.getViewport().setOpaque( false );
        pane.setBorder( BorderFactory.createEmptyBorder() );
        pane.setViewportBorder( BorderFactory.createEmptyBorder() );
        gradient.add( pane );
        JLabel unstickyhint = new JLabel( "Press F2 to close this frame." );
        unstickyhint.setFont( unstickyhint.getFont().deriveFont( 10f ) );
        gradient.add( unstickyhint );

        gradient.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ),
                "unstick" );
        gradient.getActionMap().put( "unstick", new AbstractAction( "unstick" ) {

            public void actionPerformed(ActionEvent e) {

                toggleSticky();
            }
        } );

        toolTipFrame.setContentPane( gradient );
        toolTipFrame.pack();
        toolTipFrame.setSize( Math.min( toolTipFrame.getWidth(), maxWidth ), Math.min( toolTipFrame.getHeight(),
                maxHeight ) );
        toolTipFrame.setLocationRelativeTo( null );
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

    protected static void toggleSticky() {

        if (toolTipFrame != null) {
            toolTipFrame.dispose();
            toolTipFrame = null;
        } else if (activeTip != null)
            activeTip.stick();
    }


    class TipButtonListener extends MouseAdapter implements MouseMotionListener {

        private static final long TIP_SHOW_DELAY = 500;

        protected int             x;
        protected int             y;
        private TimerTask         tipSchedule;


        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseClicked(MouseEvent e) {

            stick();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mouseEntered(final MouseEvent e) {

            // Check if a tooltip is set.
            if (getContent() == null || toolTipText == null || toolTipText.length() == 0)
                return;

            // Check if tooltip is attached to frame instead of window.
            if (toolTipFrame != null)
                return;

            /* Calculate these guys only once. */
            x = e.getX();
            y = e.getY();

            /* Schedule a timer to show the tip after TIP_SHOW_DELAY milliseconds. */
            tipTimer.schedule( tipSchedule = new TimerTask() {

                @Override
                public void run() {

                    SwingUtilities.invokeLater( new Runnable() {

                        public void run() {

                            try {
                                /* Clean up any possibly already existing tooltip window. */
                                if (toolTipWindow != null)
                                    toolTipWindow.dispose();

                                /* Abort if the content isn't showing on the screen. */
                                if (!getContent().isShowing())
                                    return;

                                /* Put the text of this tooltip in the container. */
                                activeTip = ToolTip.this;
                                toolTipWindow = new JWindow( SwingUtilities.getWindowAncestor( ToolTip.this ) );
                                toolTipWindow.setBackground( toolTipContainer.getBackground().darker() );
                                toolTipWindow.setFocusable( false );
                                toolTipWindow.setFocusableWindowState( false );
                                toolTipWindow.setAlwaysOnTop( true );

                                toolTipPane.setSize( 0, 0 );
                                toolTipPane.setText( toolTipText );
                                toolTipPane.setMaximumSize( new Dimension( maxWidth, maxHeight ) );

                                toolTipContainer.removeAll();
                                toolTipContainer.add( toolTipPane );
                                if (stickable)
                                    toolTipContainer.add( stickyhint );

                                toolTipWindow.setContentPane( toolTipContainer );
                                toolTipWindow.pack();
                                toolTipWindow.setSize( toolTipPane.getWidth() + 5, toolTipPane.getHeight()
                                                                                   + (stickable
                                                                                               ? stickyhint.getHeight()
                                                                                               : 0) );

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
                                toolTipWindow.addMouseListener( new MouseAdapter() {

                                    @Override
                                    public void mouseEntered(MouseEvent ee) {

                                        TipButtonListener.this.mouseExited( ee );
                                    }

                                    @Override
                                    public void mouseClicked(MouseEvent ee) {

                                        Logger.config( "You clicked the tooltip!" );
                                        TipButtonListener.this.mouseExited( ee );
                                    }
                                } );

                                // Small adjustment to the size which is only known after paint has been performed.
                                SwingUtilities.invokeLater( new Runnable() {

                                    public void run() {

                                        if (toolTipWindow == null)
                                            return;

                                        toolTipWindow.setSize( toolTipPane.getWidth() + 5,
                                                toolTipPane.getHeight() + (stickable ? stickyhint.getHeight() : 0) );
                                    }
                                } );
                            } catch (NullPointerException err) {
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
        public void mouseExited(MouseEvent e) {

            if (tipSchedule != null)
                tipSchedule.cancel();
            if (toolTipWindow == null)
                return;

            toolTipWindow.dispose();
            toolTipWindow = null;
        }

        /**
         * {@inheritDoc}
         */
        public void mouseMoved(MouseEvent e) {

            if (e.getSource() instanceof Component) {
                Point pointOnContent = SwingUtilities.convertPoint( (Component) e.getSource(), e.getPoint(),
                        getContent() );
                x = pointOnContent.x;
                y = pointOnContent.y;
            }
        }

        /**
         * @{inheritDoc}
         */
        public void mouseDragged(MouseEvent e) {

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
         * @param toolTip
         *        The tooltip whose sticky state changed.
         * @param sticky
         *        true if the tooltip has been made sticky.
         */
        public void stickyState(ToolTip toolTip, boolean sticky);
    }
}
