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
package com.lyndir.lhunath.lib.gui.template.shade;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.transitions.ScreenTransition;
import org.jdesktop.animation.transitions.TransitionTarget;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import com.lyndir.lhunath.lib.gui.DragListener;
import com.lyndir.lhunath.lib.gui.FileDialog;
import com.lyndir.lhunath.lib.gui.PaintPanel;
import com.lyndir.lhunath.lib.gui.ScrollPanel;
import com.lyndir.lhunath.lib.gui.Splash;
import com.lyndir.lhunath.lib.gui.ToolTip;
import com.lyndir.lhunath.lib.system.BaseConfig;
import com.lyndir.lhunath.lib.system.Locale;
import com.lyndir.lhunath.lib.system.Reflective;
import com.lyndir.lhunath.lib.system.TeeThread;
import com.lyndir.lhunath.lib.system.Utils;
import com.lyndir.lhunath.lib.system.logging.HTMLFormatter;
import com.lyndir.lhunath.lib.system.logging.LogListener;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.wrapper.Desktop;
import com.lyndir.lhunath.lib.system.wrapper.SystemTray;
import com.lyndir.lhunath.lib.system.wrapper.TrayIcon;
import com.lyndir.lhunath.lib.system.wrapper.TrayIcon.MessageType;

/**
 * TODO: {@link AbstractUi}<br>
 * 
 * @author lhunath
 */
public abstract class AbstractUi
        implements ActionListener, LogListener, CaretListener, ListSelectionListener, ItemListener, Reflective,
        ListDataListener, FocusListener, TransitionTarget {

    protected static final long       LAUNCH_DELAY         = 5000;
    protected static final int        FONT_SIZE            = 14;
    protected static final String     FONT_FACE            = Locale.explain( "conf.font" );
    protected static final Dimension  MINIMUM_SIZE         = new Dimension( 1000, 700 );

    private static final String       reportEmail          = Locale.explain( "conf.author" );
    private static final String       reportIssueSubject   = Locale.explain( "ui.reportSubject" ) + ShadeConfig.VERSION;
    private static final String       reportLicenseSubject = Locale.explain( "ui.licenseSubject" );
    private static boolean            startup              = true;

    protected int                     showPanel;
    protected Map<Action, JComponent> panelComponents;
    protected List<Stack<String>>     messageStack;
    protected HTMLFormatter           logFormatter;
    protected TrayIcon                systray;
    protected SimpleInternalFrame     window;
    protected List<JProgressBar>      progress;
    protected JEditorPane             log;
    protected JFrame                  frame;
    protected JPanel                  logo;
    protected JPanel                  contentPane;
    protected ImageIcon               logoIcon;
    protected JCheckBox               systrayButton;
    protected JCheckBox               alwaysOnTop;
    protected JCheckBox               startMini;
    protected File                    defaultLogo;
    private boolean                   showFrame;
    private DragListener              dragListener;
    private JComponent                themesPanel;
    private JCheckBox                 verboseLogs;
    private JButton                   windowedTitleButton;
    private JButton                   fullscreenTitleButton;
    private JButton                   closeTitleButton;
    private JDialog                   console;
    private JPanel                    titleBar;
    private PipedInputStream          pipeStdOut;
    private PipedInputStream          pipeStdErr;
    private PrintStream               realStdOut;
    private PrintStream               realStdErr;
    private UpdateUi                  updateUi;
    protected Animator                animation;
    private ScreenTransition          transition;
    private PaintPanel                contentPanel;
    private PipedOutputStream         consoleStdOut;

    static {
        System.setProperty( "swing.aatext", "true" );
    }

    {
        ShadeConfig.ui = this;

        /* Initialize the logger as early as possible.
         * (before any subclass code other than the main method has ran.) */
        process( BasicRequest.LOGGER );

        /* Fallback Logo. */
        defaultLogo = Utils.res( "/res/splash.png" );

        /* Start the user interface. */
        initTemplate();
    }

    private void initTemplate() {

        /* Set up the backend. */
        messageStack = new ArrayList<Stack<String>>();
        panelComponents = new HashMap<Action, JComponent>();
        (updateUi = new UpdateUi( this )).start();
        animation = new Animator( 800 );
        animation.setAcceleration( .1f );
        animation.setDeceleration( .4f );

        /* Build user interface. */
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                try {
                    buildUi();
                } catch (RuntimeException e) {
                    if (frame != null)
                        frame.dispose();
                    animation.cancel();

                    throw e;
                }

                executeAll();
            }
        } );
    }

    /**
     * Process an event that was registered and triggered in the user interface.<br>
     * <br>
     * When overriding this method; call the original method (super.event(..)) if the event is not processed by your
     * code. This code processes default interface events.
     * 
     * @param e
     *        The event that triggered this method call.
     * @param source
     *        The component upon which this event was executed.
     * @param actionCommand
     *        If the event was an {@link ActionEvent}, this contains the action command string.
     */
    public void event(EventObject e, Object source, String actionCommand) {

        if ("logClear".equals( actionCommand )) //$NON-NLS-1$
            log.setText( "" ); //$NON-NLS-1$

        else if ("toggleConsole".equals( actionCommand )) //$NON-NLS-1$
            toggleConsole();

        else if ("logSave".equals( actionCommand )) //$NON-NLS-1$
            showLogBrowser();

        else if ("close".equals( actionCommand )) { //$NON-NLS-1$
            if (WindowConstants.DISPOSE_ON_CLOSE == frame.getDefaultCloseOperation())
                frame.dispose();

            else if (WindowConstants.HIDE_ON_CLOSE == frame.getDefaultCloseOperation())
                showFrame( false );

            else if (JFrame.EXIT_ON_CLOSE == frame.getDefaultCloseOperation())
                System.exit( 0 );
        }

        else if ("exit".equals( actionCommand )) //$NON-NLS-1$
            System.exit( 0 );

        else if ("fullscreen".equals( actionCommand )) { //$NON-NLS-1$
            if (ShadeConfig.fullScreen.set( true ))
                execute( BasicRequest.FULLSCREEN, true );
        }

        else if ("windowed".equals( actionCommand )) { //$NON-NLS-1$
            if (ShadeConfig.fullScreen.set( false ))
                execute( BasicRequest.FULLSCREEN, true );
        }

        else if (verboseLogs.equals( source )) {
            if (ShadeConfig.verbose.set( verboseLogs.isSelected() ))
                execute( BasicRequest.SETTINGS );
        }

        else if ("reportIssue".equals( actionCommand )) //$NON-NLS-1$
            try {
                URI uri = new URI( "mailto:" + reportEmail + "?subject=" //$NON-NLS-1$ //$NON-NLS-2$
                                   + URLEncoder.encode( reportIssueSubject, "ISO-8859-1" ) ); //$NON-NLS-1$

                launchDelay( "stat.openingMail" ); //$NON-NLS-1$
                Desktop.getDesktop().mail( uri );
            } catch (NoClassDefFoundError err) {
                showJavaVersionWarning();
            } catch (URISyntaxException err) {
                Logger.error( err, Locale.explain( "bug.invalidMailto" ) //$NON-NLS-1$
                                   + Locale.explain( "err.reportManually", Locale.explain( "ui.issue" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                                   + reportEmail );
            } catch (IOException err) {
                Logger.error( err, Locale.explain( "err.openingMail" ) //$NON-NLS-1$
                                   + Locale.explain( "err.reportManually", Locale.explain( "ui.issue" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                                   + reportEmail );
            }

        else if ("reportOffense".equals( actionCommand )) //$NON-NLS-1$
            try {
                URI uri = new URI( "mailto: " + reportEmail + "?subject=" //$NON-NLS-1$ //$NON-NLS-2$
                                   + URLEncoder.encode( reportLicenseSubject, "ISO-8859-1" ) ); //$NON-NLS-1$

                launchDelay( "stat.openingMail" ); //$NON-NLS-1$
                Desktop.getDesktop().mail( uri );
            } catch (NoClassDefFoundError err) {
                showJavaVersionWarning();
            } catch (URISyntaxException err) {
                Logger.error( err, "bug.invalidMailto" //$NON-NLS-1$
                                   + Locale.explain( "err.reportManually", Locale.explain( "ui.offense" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                                   + reportEmail );
            } catch (IOException err) {
                Logger.error( err, "err.openingMail" //$NON-NLS-1$
                                   + Locale.explain( "err.reportManually", Locale.explain( "ui.offense" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                                   + reportEmail );
            }

        else if (systrayButton.equals( source )) {
            if (ShadeConfig.sysTray.set( systrayButton.isSelected() ))
                execute( BasicRequest.SYSTRAY );
        }

        else if (alwaysOnTop.equals( source )) {
            if (ShadeConfig.alwaysOnTop.set( alwaysOnTop.isSelected() ))
                execute( BasicRequest.FULLSCREEN );
        }

        else if (startMini.equals( source ))
            ShadeConfig.startMini.set( startMini.isSelected() );

        else if ("openSettings".equals( actionCommand )) { //$NON-NLS-1$
            showPanel = 2;
            execute( BasicRequest.PANEL, false );
            showFrame( true );
        }

        else if (source instanceof JComponent && ((JComponent) source).getParent().equals( window.getToolBar() )) {
            showPanel = window.getToolBar().getComponentIndex( (Component) source );
            execute( BasicRequest.PANEL, false );
        }

        else
            eventNotImplemented( e );

    }

    protected void toggleConsole() {

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater( new Runnable() {

                public void run() {

                    toggleConsole();
                }
            } );

            return;
        }

        if (console != null) {
            /* Reset the FDs. */
            System.setOut( realStdOut );
            System.setErr( realStdErr );
            resetConsoleLogger();

            /* Destroy the console. */
            console.dispose();
            console = null;

            /* Close the redirection FDs. */
            try {
                pipeStdOut.close();
                pipeStdErr.close();
                consoleStdOut.close();
            } catch (IOException e) {
                Logger.error( e, "Couldn't properly close console output." );
            }
        }

        else {
            /* Build the console. */
            JTextArea terminal = new JTextArea();
            terminal.setFont( Font.decode( "Monospaced" ) );
            terminal.setColumns( 80 );
            terminal.setRows( 20 );

            console = new JDialog( frame, "Text Console", false );
            console.setContentPane( new JPanel() );
            console.getContentPane().setLayout( new BorderLayout() );
            console.getContentPane().add( new JScrollPane( terminal ), BorderLayout.CENTER );
            console.pack();
            console.setLocationByPlatform( true );
            console.setVisible( true );
            console.addWindowListener( new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {

                    toggleConsole();
                }
            } );

            /* Make new FDs that redirect to the console. */
            try {
                /* Store the current FDs. */
                realStdOut = System.out;
                realStdErr = System.err;

                /* Replace the real FDs with pipes. */
                System.setOut( new PrintStream( new PipedOutputStream( pipeStdOut = new PipedInputStream() ) ) );
                System.setErr( new PrintStream( new PipedOutputStream( pipeStdErr = new PipedInputStream() ) ) );
                resetConsoleLogger();

                /* Make endpoint FDs for the console window. */
                consoleStdOut = new PipedOutputStream();

                new TeeThread( pipeStdOut, realStdOut, consoleStdOut ).start();
                new TeeThread( pipeStdErr, realStdErr, consoleStdOut ).start();
                new ConsoleThread( new PipedInputStream( consoleStdOut ), terminal ).start();
            } catch (IOException e) {
                Logger.error( e, "Couldn't create replacement stdout/stderr." );
            }
        }
    }

    private void resetConsoleLogger() {

        ConsoleHandler newConsoleLogger = new ConsoleHandler();
        newConsoleLogger.setErrorManager( ShadeConfig.console.getErrorManager() );
        newConsoleLogger.setFormatter( ShadeConfig.console.getFormatter() );
        newConsoleLogger.setFilter( ShadeConfig.console.getFilter() );
        newConsoleLogger.setLevel( ShadeConfig.console.getLevel() );

        ShadeConfig.console.close();
        Logger.getGlobal().silence( ShadeConfig.console );

        ShadeConfig.console = newConsoleLogger;
        Logger.getGlobal().addHandler( ShadeConfig.console );
    }

    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {

        event( e, e.getSource(), e.getActionCommand() );
    }

    /**
     * {@inheritDoc}
     */
    public void focusGained(FocusEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    public void focusLost(FocusEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    public void caretUpdate(CaretEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    public void itemStateChanged(ItemEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    public void valueChanged(ListSelectionEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    public void contentsChanged(ListDataEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    public void intervalAdded(ListDataEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    public void intervalRemoved(ListDataEvent e) {

        event( e, e.getSource(), null );
    }

    private void eventNotImplemented(EventObject e) {

        if (e instanceof ActionEvent)
            Logger.warn( "warn.actionNotImplemented", e.getClass(), ((ActionEvent) e).getActionCommand(), //$NON-NLS-1$
                    Utils.getFieldName( this, e.getSource() ) );
        else
            Logger.warn( "warn.eventNotImplemented", e.getClass(), Utils.getFieldName( this, e.getSource() ) ); //$NON-NLS-1$
    }

    /**
     * Update all elements in this UI in the {@link UpdateUi} thread.
     */
    protected void executeAll() {

        execute( null );
    }

    /**
     * Update a given element in this UI in the {@link UpdateUi} thread.
     * 
     * @param element
     *        The element to update, or null to update them all.
     */
    public void execute(final Request element) {

        execute( element, true );
    }

    /**
     * Update a given element in this UI.
     * 
     * @param element
     *        The element to update, or null to update them all.
     * @param useThread
     *        Use the UpdateUI thread for this update.
     */
    protected void execute(final Request element, final boolean useThread) {

        if (element == null)
            for (Request e : BasicRequest.getAutoruns())
                execute( e, useThread );

        else if (useThread)
            updateUi.request( element );
        else
            process( element );
    }

    /**
     * {@inheritDoc}
     */
    public Object getFieldValue(Field field) throws IllegalArgumentException, IllegalAccessException {

        return field.get( this );
    }

    /**
     * Retrieve the frame of this user interface.
     * 
     * @return Guess.
     */
    public JFrame getFrame() {

        return frame;
    }

    /**
     * Send out a log message about a task being launched. After {@link AbstractUi#LAUNCH_DELAY} milliseconds, the
     * message will be removed from the progress bar.
     * 
     * @param desc
     *        Description of the launch event.
     * @param args
     *        Arguments used to format the description string.
     */
    public void launchDelay(String desc, Object... args) {

        Logger.finest( desc, args );
        new Timer().schedule( new TimerTask() {

            @Override
            public void run() {

                Logger.finest( null );
            }
        }, LAUNCH_DELAY );
    }

    /**
     * {@inheritDoc}
     */
    public void logMessage(final LogRecord record) {

        if ((log == null || log.getParent() == null) && record.getLevel().intValue() > Level.CONFIG.intValue()
            && console == null)
            toggleConsole();

        if (log != null)
            SwingUtilities.invokeLater( new Runnable() {

                public void run() {

                    String message = record.getMessage();
                    Level level = record.getLevel();

                    /* Send log messages to our log field. */
                    if (message != null || record.getThrown() != null)
                        try {
                            log.getEditorKit().read( new StringReader( logFormatter.format( record ) ),
                                    log.getDocument(), log.getDocument().getLength() );
                        } catch (IOException e) {
                            Logger.error( e, "Couldn't read the log message from the record!" );
                        } catch (BadLocationException e) {
                            Logger.error( e, "Invalid location in the log pane specified for log record insertion!" );
                        }

                    /* Scroll to the bottom. */
                    log.setCaretPosition( log.getDocument().getLength() );

                    /* Manage the progress bar. */
                    if (level.intValue() < Level.CONFIG.intValue()) {
                        int progressLevel = 5 - level.intValue() / 100;
                        if (message != null) {
                            messageStack.get( progressLevel ).push( progress.get( progressLevel ).getString() );
                            progress.get( progressLevel ).setString( "[ " + message + " ]" ); //$NON-NLS-1$ //$NON-NLS-2$
                            setProgress( null, level );
                        }

                        else {
                            if (!messageStack.get( progressLevel ).isEmpty())
                                message = messageStack.get( progressLevel ).pop();
                            else
                                message = ""; //$NON-NLS-1$

                            progress.get( progressLevel ).setString( message );
                            setProgress( 0d, level );
                        }
                    }

                    /* Emit messages on the system tray. */
                    else if (level.intValue() > Level.CONFIG.intValue())
                        if (systray != null) {
                            MessageType type = MessageType.INFO;
                            if (level.equals( Level.WARNING ))
                                type = MessageType.WARNING;
                            else if (level.equals( Level.SEVERE ))
                                type = MessageType.ERROR;

                            systray.displayMessage( Utils.reformat( level.getLocalizedName() ), message, type );
                        }
                }
            } );
    }

    /**
     * Set the value of the progress bar.
     * 
     * @param percent
     *        Set the percent to show completed. Use a decimal value in the range 0-1. Use null to switch to
     *        indeterminate mode.
     * @param level
     *        The level of progress bar to use. See {@link Level} (fine, finer, finest).
     */
    public void setProgress(final Double percent, final Level level) {

        SwingUtilities.invokeLater( new Runnable() {

            public void run() {

                int progressLevel = 5 - level.intValue() / 100;
                JProgressBar bar = progress.get( progressLevel );

                if (bar.getParent() == null)
                    return;

                if (percent == null) {
                    bar.setIndeterminate( true );
                    bar.setString( bar.getString().replaceFirst( "[\\s\\d%]*\\]$", " ]" ) ); //$NON-NLS-1$ //$NON-NLS-2$
                }

                else {
                    bar.setIndeterminate( false );
                    bar.setMaximum( 100 );
                    bar.setMinimum( 0 );

                    int value = (int) (percent * 100);
                    bar.setValue( value );
                    bar.setString( bar.getString().replaceFirst( "[\\s\\d%]*\\]$", " " + value + "% ]" ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }
        } );
    }

    /**
     * Show a message explaining the limitations of running jUniUploader with a pre-Java 1.6 VM.
     */
    public void showJavaVersionWarning() {

        JOptionPane.showMessageDialog( frame, Locale.explain( "ui.requireJava6" ) //$NON-NLS-1$

                , null, JOptionPane.WARNING_MESSAGE );
    }

    protected void addPanelButton(String description, Icon icon, JComponent panel) {

        final AbstractUi ui = this;
        JToggleButton button = new JToggleButton( new AbstractAction( description, icon ) {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {

                ui.actionPerformed( e );
            }
        } );

        button.setText( null );
        button.setBorderPainted( false );
        button.setOpaque( false );
        window.getToolBar().add( button );
        panelComponents.put( button.getAction(), panel );
    }

    /**
     * Signal the UI to show a launch notification and start the platform's web browser to the given URL.
     * 
     * @param url
     *        The URL to open in the web browser.
     */
    public void browseTo(URL url) {

        try {
            launchDelay( "stat.openingBrowser" );
            Desktop.getDesktop().browse( url.toURI() );
        } catch (NoClassDefFoundError e) {
            showJavaVersionWarning();
        } catch (UnsupportedOperationException e) {
            Logger.error( e, "err.browserSupported" );
        } catch (IOException e) {
            Logger.error( e, "err.openingBrowser" );
        } catch (URISyntaxException e) {
            Logger.error( e, "err.browseUri", url );
        }
    }

    /**
     * Override this method if you have stuff that needs to be initialized before or after the UI building.<br>
     * <br>
     * Don't forget to call super.buildUi() as well.
     */
    protected void buildUi() {

        /* Prepare the look and feel. */
        execute( BasicRequest.THEME, false );

        /* Container. */
        FormLayout layout = new FormLayout( "c:3dlu, p:g, 1dlu:g, r:1dlu:g, 3dlu",
                "3dlu, t:p, 3dlu, p, 3dlu, f:m:g, 6dlu" );

        contentPanel = PaintPanel.gradientPanel();
        PanelBuilder builder = new PanelBuilder( layout, contentPanel );
        CellConstraints cc = new CellConstraints();

        /* Buttons */
        titleBar = new JPanel();
        titleBar.setLayout( new BoxLayout( titleBar, BoxLayout.X_AXIS ) );
        titleBar.setOpaque( false );
        builder.add( titleBar, cc.xy( 4, 2 ) );

        windowedTitleButton = new JButton( Utils.getIcon( "/res/windowed-sss.png" ) );
        windowedTitleButton.setActionCommand( "windowed" );
        windowedTitleButton.addActionListener( this );
        windowedTitleButton.setBorderPainted( false );
        windowedTitleButton.setContentAreaFilled( false );
        fullscreenTitleButton = new JButton( Utils.getIcon( "/res/fullscreen-sss.png" ) );
        fullscreenTitleButton.setActionCommand( "fullscreen" );
        fullscreenTitleButton.addActionListener( this );
        fullscreenTitleButton.setBorderPainted( false );
        fullscreenTitleButton.setContentAreaFilled( false );
        closeTitleButton = new JButton( Utils.getIcon( "/res/close-sss.png" ) );
        closeTitleButton.setActionCommand( "close" );
        closeTitleButton.addActionListener( this );
        closeTitleButton.setBorderPainted( false );
        closeTitleButton.setContentAreaFilled( false );
        titleBar.add( closeTitleButton );

        /* Header */
        logo = new JPanel( new BorderLayout() ) {

            @Override
            protected void paintComponent(Graphics g) {

                if (logoIcon != null) {
                    int x = (getWidth() - logoIcon.getIconWidth()) / 2;
                    int y = (getHeight() - logoIcon.getIconHeight()) / 2;
                    logoIcon.paintIcon( this, g, x, y );
                }

                super.paintComponent( g );
            }
        };
        logo.addMouseListener( new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1)
                    BaseConfig.dump();
            }
        } );
        logo.setOpaque( false );
        execute( BasicRequest.LOGO, false );
        builder.add( new ToolTip( Locale.explain( "conf.application" ) + " v" + ShadeConfig.VERSION, logo ), cc.xyw( 2,
                2, 3 ) );

        progress = new ArrayList<JProgressBar>();
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout( new BoxLayout( progressPanel, BoxLayout.Y_AXIS ) );
        progressPanel.setOpaque( false );
        JPanel strut = new JPanel();
        strut.setPreferredSize( new Dimension( 0, 25 ) );
        strut.setOpaque( false );
        progressPanel.add( strut );
        for (int i = 0; i < 3; ++i) {
            JProgressBar bar = new JProgressBar() {

                @Override
                public void repaint(long tm, int x, int y, int width, int height) {

                    setOpaque( getValue() != 0 || isIndeterminate() );

                    if (getParent() != null)
                        getParent().repaint( tm, x, y, width, height );
                    super.repaint( tm, x, y, width, height );
                }

                @Override
                protected void paintComponent(Graphics g) {

                    super.paintComponent( g );

                    if (isOpaque()) {
                        ((Graphics2D) g).setPaint( getBackground() );
                        g.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );
                    }
                }
            };

            bar.setString( "" );
            bar.setStringPainted( true );
            bar.setBorderPainted( false );
            bar.setBackground( Utils.setAlpha( bar.getBackground(), 100 ) );

            messageStack.add( new Stack<String>() );
            progressPanel.add( bar );
            progress.add( bar );
        }
        logo.add( progressPanel, BorderLayout.NORTH );

        window = new SimpleInternalFrame( null, new JToolBar(), null, false );
        window.setBorder( Borders.EMPTY_BORDER );
        window.setSelected( true );
        window.setOpaque( false );

        /* Tabs */
        buildTabs();
        execute( BasicRequest.PANEL, false );
        builder.add( window, cc.xyw( 2, 6, 3 ) );

        /* Frame. */
        contentPane = builder.getPanel();
        contentPane.setBorder( BorderFactory.createRaisedBevelBorder() );
        execute( BasicRequest.FULLSCREEN, false );
    }
    /**
     * @param image
     *        The image to use as a background on the main content pane.
     */
    protected void setBackgroundImage(final Image image) {

        if (contentPanel == null)
            SwingUtilities.invokeLater( new Runnable() {

                public void run() {

                    setBackgroundImage( image );
                }
            } );

        else
            contentPanel.setBackgroundImage( image );
    }

    /**
     * Feel free to override this method to perform actions before and after the panel changes, but don't remember to
     * still call the parent implementation.
     * 
     * @{inheritDoc}
     */
    public void setupNextScreen() {

        JToolBar toolbar = window.getToolBar();
        AbstractButton activeButton = (AbstractButton) toolbar.getComponent( showPanel );
        for (Component c : toolbar.getComponents())
            if (c instanceof JToggleButton)
                ((JToggleButton) c).setSelected( c.equals( activeButton ) );

        window.setTitle( "     " + (showPanel + 1) + ".  "
                         + activeButton.getAction().getValue( Action.NAME ).toString() + " ~" );
        window.setContent( panelComponents.get( activeButton.getAction() ) );
    }

    /**
     * Override this method to add your own set of tabs; but don't forget to call this one too if you want to have the
     * default tabs showing as well!
     */
    public void buildTabs() {

        addPanelButton( Locale.explain( "ui.configuration" ), Utils.getIcon( "/res/settings-s.png" ), //$NON-NLS-1$ //$NON-NLS-2$
                getSettingsPane() );
        addPanelButton( Locale.explain( "ui.logs" ), Utils.getIcon( "/res/log-s.png" ), //$NON-NLS-1$ //$NON-NLS-2$
                getOperationsPane() );
        addPanelButton( Locale.explain( "ui.licensing" ), Utils.getIcon( "/res/license-s.png" ), //$NON-NLS-1$ //$NON-NLS-2$
                getLicensePane() );
        addPanelButton( Locale.explain( "ui.development" ), Utils.getIcon( "/res/develop-s.png" ), //$NON-NLS-1$ //$NON-NLS-2$
                getDevelopmentPane() );
    }

    private JComponent getSettingsPane() {

        FormLayout layout = new FormLayout(
                "20dlu:g(3), r:p, 5dlu, f:100dlu:g(2), 10dlu, r:p, 5dlu, f:100dlu:g(2), 5dlu, l:20dlu:g(4)" ); //$NON-NLS-1$
        DefaultFormBuilder builder = new DefaultFormBuilder( layout, new ScrollPanel() );
        builder.setBorder( Borders.DLU4_BORDER );
        builder.setLeadingColumnOffset( 1 );

        builder.appendSeparator( Locale.explain( "ui.appearance" ) ); //$NON-NLS-1$

        builder.append(
                Locale.explain( "ui.theme" ), new ToolTip( Locale.explain( "ui.themeTitle" ) //$NON-NLS-1$ //$NON-NLS-2$
                                                           + Locale.explain( "ui.themeTip" ), themesPanel = new JPanel() ), 5 ); //$NON-NLS-1$
        for (MyTheme theme : MyTheme.values())
            themesPanel.add( theme.getButton() );
        themesPanel.setOpaque( false );
        builder.nextLine();

        builder.append( Locale.explain( "ui.systray" ), new ToolTip( Locale.explain( "ui.systrayTitle" ) //$NON-NLS-1$ //$NON-NLS-2$
                                                                     + Locale.explain( "ui.systrayTip" ), //$NON-NLS-1$
                systrayButton = new JCheckBox( Locale.explain( "ui.enable" ) ) ) ); //$NON-NLS-1$
        builder.append( Locale.explain( "ui.ontop" ), new ToolTip( Locale.explain( "ui.ontopTitle" ) //$NON-NLS-1$ //$NON-NLS-2$
                                                                   + Locale.explain( "ui.ontopTip" ), //$NON-NLS-1$
                alwaysOnTop = new JCheckBox( Locale.explain( "ui.enable" ) ) ) ); //$NON-NLS-1$
        builder.nextLine();

        builder.append( Locale.explain( "ui.startmini" ), new ToolTip( Locale.explain( "ui.startminiTitle" ) //$NON-NLS-1$ //$NON-NLS-2$
                                                                       + Locale.explain( "ui.startminiTip" ), //$NON-NLS-1$
                startMini = new JCheckBox( Locale.explain( "ui.enable" ) ) ) ); //$NON-NLS-1$
        builder.append( Locale.explain( "ui.verbose" ), new ToolTip( Locale.explain( "ui.verboseTitle" ) //$NON-NLS-1$ //$NON-NLS-2$
                                                                     + Locale.explain( "ui.verboseTip" ), //$NON-NLS-1$
                verboseLogs = new JCheckBox( Locale.explain( "ui.enable" ) ) ) ); //$NON-NLS-1$
        builder.nextLine();

        appendCustomSettings( builder );

        systrayButton.addActionListener( this );
        alwaysOnTop.addActionListener( this );
        startMini.addActionListener( this );
        verboseLogs.addActionListener( this );

        systrayButton.setOpaque( false );
        alwaysOnTop.setOpaque( false );
        startMini.setOpaque( false );
        verboseLogs.setOpaque( false );

        JScrollPane pane = new JScrollPane( builder.getPanel() );
        pane.setBorder( Borders.EMPTY_BORDER );
        builder.getPanel().setOpaque( false );
        pane.getViewport().setOpaque( false );
        pane.setOpaque( false );

        return pane;
    }

    /**
     * Override this method to add custom settings to the settings panel.<br>
     * <br>
     * <i>Note: The builder that is used for the settings panel is created using the following layout:<br>
     * "20dlu:g(3), r:p, 5dlu, f:100dlu:g(2), 10dlu, r:p, 5dlu, f:100dlu:g(2), 5dlu, l:20dlu:g(4)"</i><br>
     * <br>
     * You probably want to do something like:
     * 
     * <pre>
     * builder.appendSeparator( &quot;Custom Settings&quot; );
     * builder.append( &quot;Foo:&quot;, new JCheckBox( &quot;Yes&quot; ) );
     * builder.append( &quot;Bar:&quot;, new JCheckBox( &quot;Yes&quot; ) );
     * builder.nextLine();
     * </pre>
     * 
     * @param builder
     *        The {@link DefaultFormBuilder} to which you should add your settings components.
     */
    protected abstract void appendCustomSettings(DefaultFormBuilder builder);

    private JComponent getOperationsPane() {

        FormLayout layout = new FormLayout( "10dlu, 15dlu, p:g, 10dlu, p:g, 15dlu, 10dlu", //$NON-NLS-1$
                "0dlu, f:1dlu:g, 5dlu, p, 10dlu" ); //$NON-NLS-1$
        layout.setColumnGroups( new int[][] { { 3, 5 } } );

        JButton button;
        PanelBuilder builder = new PanelBuilder( layout, new ScrollPanel() );
        CellConstraints cc = new CellConstraints();

        log = new JEditorPane( "text/html", "" );
        log.setOpaque( false );
        log.setEditable( false );

        JScrollPane pane = new JScrollPane( log );
        pane.setBorder( Borders.EMPTY_BORDER );
        pane.setOpaque( false );
        pane.getViewport().setOpaque( false );
        builder.add( pane, cc.xyw( 2, 2, 5 ) );

        button = new JButton( Locale.explain( "ui.clearLog" ), Utils.getIcon( "/res/clear-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "logClear" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( button, cc.xy( 3, 4 ) );

        button = new JButton( Locale.explain( "ui.saveLog" ), Utils.getIcon( "/res/save-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "logSave" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( button, cc.xy( 5, 4 ) );

        builder.getPanel().setOpaque( false );
        return builder.getPanel();
    }

    private JComponent getLicensePane() {

        FormLayout layout = new FormLayout( "10dlu, 15dlu, p:g, 10dlu, p:g, 15dlu, 10dlu", //$NON-NLS-1$
                "0dlu, f:1dlu:g, 5dlu, p, 10dlu" ); //$NON-NLS-1$
        layout.setColumnGroups( new int[][] { { 3, 5 } } );

        String doc = "";
        JButton button;
        PanelBuilder builder = new PanelBuilder( layout, new ScrollPanel() );
        CellConstraints cc = new CellConstraints();

        try {
            doc = getLicense();
        } catch (IOException e) {
            Logger.error( e, "err.readLicense" );
        }

        JEditorPane changelog = new JEditorPane( "text/html", doc.toString() ); //$NON-NLS-1$
        changelog.setOpaque( false );
        changelog.setEditable( false );
        changelog.setFont( Font.decode( "Monospaced-15" ) ); //$NON-NLS-1$

        JScrollPane pane = new JScrollPane( changelog );
        pane.setBorder( Borders.EMPTY_BORDER );
        pane.setOpaque( false );
        pane.getViewport().setOpaque( false );
        builder.add( pane, cc.xyw( 2, 2, 5 ) );

        button = new JButton( Locale.explain( "ui.reportOffense" ), Utils.getIcon( "/res/problem-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "reportOffense" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( new ToolTip( Locale.explain( "ui.reportOffenceTip" ), button ), cc.xyw( 3, 4, 3 ) );

        builder.getPanel().setOpaque( false );
        return builder.getPanel();
    }

    @SuppressWarnings("unused")
    protected String getLicense() throws IOException {

        return Locale.explain( "ui.licenseNotFound" );
    }

    private JComponent getDevelopmentPane() {

        FormLayout layout = new FormLayout( "10dlu, 15dlu, p:g, 10dlu, p:g, 15dlu, 10dlu", //$NON-NLS-1$
                "0dlu, f:1dlu:g, 5dlu, p, 10dlu" ); //$NON-NLS-1$
        layout.setColumnGroups( new int[][] { { 3, 5 } } );

        String doc = "";
        JButton button;
        PanelBuilder builder = new PanelBuilder( layout, new ScrollPanel() );
        CellConstraints cc = new CellConstraints();

        try {
            doc = getChangeLog();
        } catch (IOException e) {
            Logger.error( e, "err.readChangelog" );
        }

        JEditorPane changelog = new JEditorPane( "text/html", doc.toString() ); //$NON-NLS-1$
        changelog.setOpaque( false );
        changelog.setEditable( false );
        changelog.setFont( Font.decode( "Monospaced-15" ) ); //$NON-NLS-1$

        JScrollPane pane = new JScrollPane( changelog );
        pane.setBorder( Borders.EMPTY_BORDER );
        pane.setOpaque( false );
        pane.getViewport().setOpaque( false );
        builder.add( pane, cc.xyw( 2, 2, 5 ) );

        button = new JButton( Locale.explain( "ui.reportProblem" ), Utils.getIcon( "/res/problem-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "reportIssue" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( new ToolTip( Locale.explain( "ui.reportProblemTip" ), button ), cc.xy( 3, 4 ) );

        button = new JButton( Locale.explain( "ui.toggleConsole" ), Utils.getIcon( "/res/terminal-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "toggleConsole" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( new ToolTip( Locale.explain( "ui.toggleConsoleTip" ), button ), cc.xy( 5, 4 ) );

        builder.getPanel().setOpaque( false );
        return builder.getPanel();
    }

    @SuppressWarnings("unused")
    protected String getChangeLog() throws IOException {

        return Locale.explain( "ui.changelogNotFound" );
    }

    private void setLookAndFeel() {

        try {
            /* Prepare the theme and apply it. */
            MyTheme.initialize();
            ShadeConfig.theme.get().setup();
            if (frame != null) {
                ShadeConfig.theme.get().reconfigure( frame );
                for (JComponent panel : panelComponents.values())
                    if (!panel.equals( window.getContent() ))
                        ShadeConfig.theme.get().reconfigure( panel );
            }

            /* Set some look and feel properties. */
            // Utils.setUIFont( new Font( FONT_FACE, Font.PLAIN, FONT_SIZE ) );
            // Wrapper.wrap( new AntiAliasingBehavior() );
            /* Force update on hidden panels. */
            if (frame != null && frame.isVisible())
                SwingUtilities.invokeAndWait( new Runnable() {

                    public void run() {

                        SwingUtilities.updateComponentTreeUI( frame );

                        /* Also update invisible panels, please. */
                        for (JComponent panel : panelComponents.values())
                            if (!panel.equals( window.getContent() ))
                                SwingUtilities.updateComponentTreeUI( panel );
                    }
                } );
        } catch (InterruptedException e) {} catch (InvocationTargetException e) {}
    }

    protected void showFrame(boolean shown) {

        if (ShadeConfig.sysTray.get()) {
            showFrame = shown;
            execute( BasicRequest.SYSTRAY );
        }
    }

    private void showLogBrowser() {

        FileDialog chooser = new FileDialog( ShadeConfig.res, Locale.explain( "ui.saveLogDialog" ), frame ) { //$NON-NLS-1$

            private static final long serialVersionUID = 1L;

            @Override
            public void approved() {

                try {
                    FileWriter writer = new FileWriter( getSelectedFile() );
                    writer.write( log.getText() );
                    writer.close();
                } catch (IOException err) {
                    Logger.error( err, "err.saveLog" ); //$NON-NLS-1$
                }
            }
        };

        /* Filters. */
        chooser.setAcceptAllFileFilterUsed( false );
        chooser.setFileFilter( FileDialog.createExtensionFilter( "log", Locale.explain( "ui.logExtension" ) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        chooser.activate();
    }

    /**
     * Override this method to process custom requests.<br>
     * <br>
     * <b>Make sure you call super.process if you override this method!</b>
     * 
     * @param element
     */
    protected void process(Request element) {

        /* Switch to fullscreen or windowed mode. */
        if (element.equals( BasicRequest.FULLSCREEN )) {
            /* Remove the frame drag listeners. */
            if (dragListener != null) {
                dragListener.uninstall();
                for (JComponent panel : panelComponents.values())
                    if (!panel.equals( window.getContent() ))
                        dragListener.uninstall( panel );
            }

            /* Rebuild frame. */
            int closeOp = JFrame.EXIT_ON_CLOSE;
            if (frame != null) {
                closeOp = frame.getDefaultCloseOperation();
                frame.dispose();
            }
            frame = new JFrame( Locale.explain( "conf.application" ) ); //$NON-NLS-1$
            frame.setAlwaysOnTop( ShadeConfig.alwaysOnTop.get() );
            frame.setResizable( !ShadeConfig.fullScreen.get() );
            frame.setDefaultCloseOperation( closeOp );
            frame.setContentPane( contentPane );
            frame.setUndecorated( true );

            if (ShadeConfig.fullScreen.get())
                frame.setSize( GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getSize() );

            else {
                frame.setPreferredSize( MINIMUM_SIZE );
                frame.pack();
            }

            /* Install the frame drag listeners, also on invisible panels. */
            if (!ShadeConfig.fullScreen.get()) {
                dragListener = new DragListener( frame );
                dragListener.install();
                for (JComponent panel : panelComponents.values())
                    if (!panel.equals( window.getContent() ))
                        dragListener.install( panel );
            }

            /* All set, start the show. */
            titleBar.removeAll();
            if (ShadeConfig.fullScreen.get())
                titleBar.add( windowedTitleButton );
            else
                titleBar.add( fullscreenTitleButton );
            titleBar.add( closeTitleButton );
            titleBar.revalidate();

            /* Set the minimized on startup state if configured. */
            if (startup) {
                startup = false;
                if (ShadeConfig.sysTray.get())
                    showFrame = !ShadeConfig.startMini.get();
                else
                    frame.setExtendedState( ShadeConfig.startMini.get() ? Frame.ICONIFIED : Frame.NORMAL );
            }

            frame.setLocationRelativeTo( null );
            process( BasicRequest.SYSTRAY );
        }

        /* Configure the logging backend. */
        else if (element.equals( BasicRequest.LOGGER )) {
            if (logFormatter == null)
                logFormatter = new HTMLFormatter();

            Logger.getGlobal().setLevel( ShadeConfig.verbose.get() ? Level.FINEST : Level.INFO );
            ShadeConfig.formatter.setVerbose( ShadeConfig.verbose.get() );
            logFormatter.setVerbose( ShadeConfig.verbose.get() );

            if (verboseLogs != null)
                verboseLogs.setSelected( ShadeConfig.verbose.get() );

            Logger.getGlobal().addListener( this, Level.ALL );
        }

        /* Update the settings buttons and fields. */
        else if (element.equals( BasicRequest.SETTINGS )) {
            process( BasicRequest.LOGGER );

            systrayButton.setSelected( ShadeConfig.sysTray.get() );
            alwaysOnTop.setSelected( ShadeConfig.alwaysOnTop.get() );
            startMini.setSelected( ShadeConfig.startMini.get() );
        }

        /* Update the logos. */
        else if (element.equals( BasicRequest.LOGO )) {
            File iconFile = null;
            if (ShadeConfig.logos.isSet() && ShadeConfig.logos.get().size() > 0)
                iconFile = ShadeConfig.logos.get().get( 0 );

            if (iconFile == null || !iconFile.isFile())
                iconFile = defaultLogo;

            if (iconFile != null && iconFile.isFile()) {
                logoIcon = new ImageIcon( iconFile.getPath() );
                logo.repaint();
                logo.setPreferredSize( new Dimension( logoIcon.getIconWidth(), logoIcon.getIconHeight() + 10 ) );
            }
        }

        /* Activate / Disable the system tray. */
        else if (element.equals( BasicRequest.SYSTRAY )) {
            if (ShadeConfig.sysTray.get() && !SystemTray.isSupported())
                ShadeConfig.sysTray.set( false );

            if (ShadeConfig.sysTray.get())
                try {
                    if (systray == null) {
                        TrayMenu sysMenu = new TrayMenu( frame );
                        appendCustomSystrayMenuItems( sysMenu );

                        JMenuItem item;
                        item = new JMenuItem( Locale.explain( "ui.settings" ) ); //$NON-NLS-1$
                        item.setActionCommand( "openSettings" ); //$NON-NLS-1$
                        item.addActionListener( this );
                        sysMenu.add( item );
                        sysMenu.addSeparator();
                        item = new JMenuItem( Locale.explain( "ui.exit" ) ); //$NON-NLS-1$
                        item.setActionCommand( "exit" ); //$NON-NLS-1$
                        item.addActionListener( this );
                        sysMenu.add( item );

                        Image icon = Utils.getIcon( "/res/warcraft.png" ).getImage(); //$NON-NLS-1$
                        Dimension traySize = SystemTray.getSystemTray().getTrayIconSize();
                        icon = icon.getScaledInstance( traySize.width, traySize.height, Image.SCALE_SMOOTH );

                        systray = new TrayIcon( icon );
                        systray.setActionCommand( "openLoader" ); //$NON-NLS-1$
                        systray.addActionListener( this );
                        systray.setImageAutoSize( true );
                        systray.addMouseListener( sysMenu );

                        try {
                            SystemTray.getSystemTray().add( systray );
                            frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
                        } catch (AWTException e) {
                            Logger.error( e, "err.sysTray" ); //$NON-NLS-1$

                            showFrame = true;
                            ShadeConfig.sysTray.set( false );
                            process( BasicRequest.SETTINGS );
                        }
                    }
                } catch (UnsupportedOperationException err) {
                    showJavaVersionWarning();

                    showFrame = true;
                    ShadeConfig.sysTray.set( false );
                    process( BasicRequest.SETTINGS );
                }

            else {
                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                showFrame = true;

                if (systray != null) {
                    SystemTray.getSystemTray().remove( systray );
                    systray = null;
                }
            }

            if (frame == null)
                process( BasicRequest.FULLSCREEN );

            /* If the splash screen is showing; get rid of it first. */
            if (Splash.getSplash() != null)
                Splash.getSplash().dispose();

            /* Show ourselves */
            if (frame.isVisible() != showFrame) {
                frame.setVisible( showFrame );
                frameVisibilityChanged( frame.isVisible() );
            }

            /* Create the transition manager if it's not yet there. */
            if (transition == null)
                transition = new ScreenTransition( window, this, animation );
        }

        /* Show the selected panel and ensure the correct toolbar button states. */
        else if (element.equals( BasicRequest.PANEL )) {
            if (window.getToolBar().getComponent( showPanel ) instanceof AbstractButton)
                if (transition != null)
                    transition.start();
                else
                    setupNextScreen();
        }

        /* Change the color theme of UI elements. */
        else if (element.equals( BasicRequest.THEME )) {
            Logger.fine( "stat.theme" ); //$NON-NLS-1$
            try {
                setLookAndFeel();
            } finally {
                Logger.fine( null );
            }
        }
    }

    /**
     * Override this method if you want to perform an action when the frame changes visibility state or has just been
     * made valid.
     */
    protected void frameVisibilityChanged(@SuppressWarnings("unused") boolean newVisibilityState) {

    /* Nothing custom here. */
    }

    /**
     * Override this method to add custom {@link MenuItem}s to the system tray menu.
     * 
     * @param sysMenu
     *        The menu to add the items to.
     */
    protected void appendCustomSystrayMenuItems(JPopupMenu sysMenu) {

    /* Nothing custom here. */
    }
}


/**
 * Read line-based data from an input stream and write it out on a component.
 */
class ConsoleThread extends Thread {

    private InputStreamReader in;
    private JTextArea         console;

    /**
     * Create a new {@link ConsoleThread} instance.
     * 
     * @param in
     *        The source of the data to write in the console.
     * @param console
     *        The component to output the console data to.
     */
    public ConsoleThread(InputStream in, JTextArea console) {

        super( "Redirect stdout to Virtual Console" );
        setDaemon( true );

        this.in = new InputStreamReader( in );
        this.console = console;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        try {
            int bytesRead;
            char[] buf = new char[BaseConfig.BUFFER_SIZE];

            while ((bytesRead = in.read( buf )) > 0) {
                console.append( new String( buf, 0, bytesRead ) );

                /* Scroll to the bottom. */
                console.setCaretPosition( console.getDocument().getLength() );
            }
        } catch (IOException e) {
            Logger.error( e, "Could not read from the console source." );
        }
    }
}
