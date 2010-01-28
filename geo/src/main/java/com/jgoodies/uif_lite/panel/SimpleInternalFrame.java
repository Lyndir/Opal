package com.jgoodies.uif_lite.panel;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

import com.lyndir.lhunath.lib.gui.PaintPanel;


/**
 * A <code>JPanel</code> subclass that has a drop shadow border and that provides a header with icon, title and tool
 * bar.
 * <p>
 * This class can be used to replace the <code>JInternalFrame</code>, for use outside of a <code>JDesktopPane</code>.
 * The <code>SimpleInternalFrame</code> is less flexible but often more usable; it avoids overlapping windows and scales
 * well up to IDE size. Several customers have reported that they and their clients feel much better with both the
 * appearance and the UI feel.
 * <p>
 * The SimpleInternalFrame provides the following bound properties: <i>frameIcon, title, toolBar, content, selected.</i>
 * <p>
 * By default the SimpleInternalFrame is in <i>selected</i> state. If you don't do anything, multiple simple internal
 * frames will be displayed as selected.
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.3 $
 * @see JInternalFrame
 * @see JDesktopPane
 */
public class SimpleInternalFrame extends JPanel {

    protected final JLabel   titleLabel;
    private PaintPanel gradientPanel;
    private JPanel     headerPanel;
    private boolean    selected;


    // Instance Creation ****************************************************

    /**
     * Constructs a SimpleInternalFrame with the specified title. The title is intended to be non-blank, or in other
     * words should contain non-space characters.
     *
     * @param title
     *            the initial title
     */
    public SimpleInternalFrame(String title) {

        this( null, title, null, null, true );
    }

    /**
     * Constructs a SimpleInternalFrame with the specified icon, and title.
     *
     * @param icon
     *            the initial icon
     * @param title
     *            the initial title
     */
    public SimpleInternalFrame(Icon icon, String title) {

        this( icon, title, null, null, true );
    }

    /**
     * Constructs a SimpleInternalFrame with the specified title, tool bar, and content panel.
     *
     * @param title
     *            the initial title
     * @param toolbar
     *            the initial tool bar
     * @param content
     *            the initial content pane
     * @param headerTop
     *            Add the header to the top of the frame (or to the bottom).
     */
    public SimpleInternalFrame(String title, JToolBar toolbar, JComponent content, boolean headerTop) {

        this( null, title, toolbar, content, headerTop );
    }

    /**
     * Constructs a SimpleInternalFrame with the specified icon, title, tool bar, and content panel.
     *
     * @param icon
     *            the initial icon
     * @param title
     *            the initial title
     * @param toolbar
     *            the initial tool bar
     * @param content
     *            the initial content pane
     * @param headerTop
     *            Add the header to the top of the frame (or to the bottom).
     */
    public SimpleInternalFrame(Icon icon, String title, JToolBar toolbar, JComponent content, boolean headerTop) {

        super( new BorderLayout() );
        selected = false;
        titleLabel = new JLabel( title, icon, SwingConstants.LEADING );
        JPanel top = buildHeader( titleLabel, toolbar );

        add( top, headerTop? BorderLayout.NORTH: BorderLayout.SOUTH );
        if (content != null)
            setContent( content );
        setBorder( new ShadowBorder() );
        setSelected( true );
        updateHeader();
    }

    // Public API ***********************************************************

    /**
     * Returns the frame's icon.
     *
     * @return the frame's icon
     */
    public Icon getFrameIcon() {

        return titleLabel.getIcon();
    }

    /**
     * Sets a new frame icon.
     *
     * @param newIcon
     *            the icon to be set
     */
    public void setFrameIcon(Icon newIcon) {

        Icon oldIcon = getFrameIcon();
        titleLabel.setIcon( newIcon );
        firePropertyChange( "frameIcon", oldIcon, newIcon );
    }

    /**
     * Returns the frame's title text.
     *
     * @return String the current title text
     */
    public String getTitle() {

        return titleLabel.getText();
    }

    /**
     * Sets a new title text.
     *
     * @param newText
     *            the title text tp be set
     */
    public void setTitle(String newText) {

        String oldText = getTitle();
        titleLabel.setText( newText );
        firePropertyChange( "title", oldText, newText );
    }

    /**
     * Returns the current toolbar, null if none has been set before.
     *
     * @return the current toolbar - if any
     */
    public JToolBar getToolBar() {

        return headerPanel.getComponentCount() > 1? (JToolBar) headerPanel.getComponent( 1 ): null;
    }

    /**
     * Sets a new tool bar in the header.
     *
     * @param newToolBar
     *            the tool bar to be set in the header
     */
    public void setToolBar(JToolBar newToolBar) {

        JToolBar oldToolBar = getToolBar();
        if (oldToolBar == newToolBar)
            return;
        if (oldToolBar != null)
            headerPanel.remove( oldToolBar );
        if (newToolBar != null) {
            newToolBar.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 0 ) );
            headerPanel.add( newToolBar, BorderLayout.EAST );
        }
        updateHeader();
        firePropertyChange( "toolBar", oldToolBar, newToolBar );
    }

    /**
     * Returns the content - null, if none has been set.
     *
     * @return the current content
     */
    public Component getContent() {

        return hasContent()? getComponent( 1 ): null;
    }

    /**
     * Sets a new panel content; replaces any existing content, if existing.
     *
     * @param newContent
     *            the panel's new content
     */
    public void setContent(Component newContent) {

        Component oldContent = getContent();
        if (hasContent())
            remove( oldContent );
        add( newContent, BorderLayout.CENTER );
        validate();
        repaint();

        firePropertyChange( "content", oldContent, newContent );
    }

    /**
     * Answers if the panel is currently selected (or in other words active) or not. In the selected state, the header
     * background will be rendered differently.
     *
     * @return boolean a boolean, where true means the frame is selected (currently active) and false means it is not
     */
    public boolean isSelected() {

        return selected;
    }

    /**
     * This panel draws its title bar differently if it is selected, which may be used to indicate to the user that this
     * panel has the focus, or should get more attention than other simple internal frames.
     *
     * @param newValue
     *            a boolean, where true means the frame is selected (currently active) and false means it is not
     */
    public void setSelected(boolean newValue) {

        boolean oldValue = isSelected();
        selected = newValue;
        updateHeader();
        firePropertyChange( "selected", oldValue, newValue );
    }

    // Building *************************************************************

    /**
     * Creates and answers the header panel, that consists of: an icon, a title label, a tool bar, and a gradient
     * background.
     *
     * @param label
     *            the label to paint the icon and text
     * @param toolbar
     *            the panel's tool bar
     * @return the panel's built header area
     */
    private JPanel buildHeader(JLabel label, JToolBar toolbar) {

        gradientPanel = PaintPanel.gradientPanel( getHeaderBackground(), UIManager.getColor( "control" ) );
        label.setOpaque( false );

        gradientPanel.add( label, BorderLayout.WEST );
        gradientPanel.setBorder( BorderFactory.createEmptyBorder( 3, 4, 3, 1 ) );

        headerPanel = new JPanel( new BorderLayout() );
        headerPanel.add( gradientPanel, BorderLayout.CENTER );
        setToolBar( toolbar );
        headerPanel.setBorder( new RaisedHeaderBorder() );
        headerPanel.setOpaque( false );
        return headerPanel;
    }

    /**
     * Updates the header.
     */
    private void updateHeader() {

        gradientPanel.setPaint( PaintPanel.gradientPaint( getHeaderBackground(), UIManager.getColor( "control" ) ) );
        gradientPanel.setOpaque( isSelected() );
        SwingUtilities.invokeLater( new Runnable() {

            @Override
            public void run() {

                titleLabel.setForeground( getTextForeground( isSelected() ) );
            }
        } );
    }

    /**
     * Updates the UI. In addition to the superclass behavior, we need to update the header component.
     */
    @Override
    public void updateUI() {

        super.updateUI();
        if (titleLabel != null)
            updateHeader();
    }

    // Helper Code **********************************************************

    /**
     * Checks and answers if the panel has a content component set.
     *
     * @return true if the panel has a content, false if it's empty
     */
    private boolean hasContent() {

        return getComponentCount() > 1;
    }

    /**
     * Determines and answers the header's text foreground color. Tries to lookup a special color from the L&amp;F. In
     * case it is absent, it uses the standard internal frame forground.
     *
     * @param isSelected
     *            true to lookup the active color, false for the inactive
     * @return the color of the foreground text
     */
    protected Color getTextForeground(boolean isSelected) {

        Color c = UIManager.getColor( isSelected? "SimpleInternalFrame.activeTitleForeground"
                                                : "SimpleInternalFrame.inactiveTitleForeground" );
        if (c != null)
            return c;
        return UIManager.getColor( isSelected? "InternalFrame.activeTitleForeground": "Label.foreground" );

    }

    /**
     * Determines and answers the header's background color. Tries to lookup a special color from the L&amp;F. In case
     * it is absent, it uses the standard internal frame background.
     *
     * @return the color of the header's background
     */
    protected Color getHeaderBackground() {

        Color c = UIManager.getColor( "SimpleInternalFrame.activeTitleBackground" );
        return c != null? c: UIManager.getColor( "InternalFrame.activeTitleBackground" );
    }


    // Helper Classes *******************************************************

    // A custom border for the raised header pseudo 3D effect.
    private static class RaisedHeaderBorder extends AbstractBorder {

        private static final Insets INSETS = new Insets( 1, 1, 1, 0 );


        /**
         * @inheritDoc
         */
        @Override
        public Insets getBorderInsets(Component c) {

            return INSETS;
        }

        /**
         * @inheritDoc
         */
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {

            g.translate( x, y );
            g.setColor( UIManager.getColor( "controlLtHighlight" ) );
            g.fillRect( 0, 0, w, 1 );
            g.fillRect( 0, 1, 1, h - 1 );
            g.setColor( UIManager.getColor( "controlShadow" ) );
            g.fillRect( 0, h - 1, w, 1 );
            g.translate( -x, -y );
        }
    }


    // A custom border that has a shadow on the right and lower sides.
    private static class ShadowBorder extends AbstractBorder {

        private static final Insets INSETS = new Insets( 1, 1, 3, 3 );


        /**
         * @inheritDoc
         */
        @Override
        public Insets getBorderInsets(Component c) {

            return INSETS;
        }

        /**
         * @inheritDoc
         */
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {

            Color shadow = UIManager.getColor( "controlShadow" );
            if (shadow == null)
                shadow = Color.GRAY;
            Color lightShadow = new Color( shadow.getRed(), shadow.getGreen(), shadow.getBlue(), 170 );
            Color lighterShadow = new Color( shadow.getRed(), shadow.getGreen(), shadow.getBlue(), 70 );
            g.translate( x, y );

            g.setColor( shadow );
            g.fillRect( 0, 0, w - 3, 1 );
            g.fillRect( 0, 0, 1, h - 3 );
            g.fillRect( w - 3, 1, 1, h - 3 );
            g.fillRect( 1, h - 3, w - 3, 1 );
            // Shadow line 1
            g.setColor( lightShadow );
            g.fillRect( w - 3, 0, 1, 1 );
            g.fillRect( 0, h - 3, 1, 1 );
            g.fillRect( w - 2, 1, 1, h - 3 );
            g.fillRect( 1, h - 2, w - 3, 1 );
            // Shadow line2
            g.setColor( lighterShadow );
            g.fillRect( w - 2, 0, 1, 1 );
            g.fillRect( 0, h - 2, 1, 1 );
            g.fillRect( w - 2, h - 2, 1, 1 );
            g.fillRect( w - 1, 1, 1, h - 2 );
            g.fillRect( 1, h - 1, w - 2, 1 );
            g.translate( -x, -y );
        }
    }
}
