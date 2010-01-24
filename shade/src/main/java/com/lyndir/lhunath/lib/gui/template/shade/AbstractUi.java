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

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.uif_lite.panel.SimpleInternalFrame;
import com.lyndir.lhunath.lib.gui.*;
import com.lyndir.lhunath.lib.gui.FileDialog;
import com.lyndir.lhunath.lib.system.*;
import com.lyndir.lhunath.lib.system.Locale;
import com.lyndir.lhunath.lib.system.logging.HTMLFormatter;
import com.lyndir.lhunath.lib.system.logging.LogListener;
import com.lyndir.lhunath.lib.system.logging.Logger;
import com.lyndir.lhunath.lib.system.util.Utils;
import com.lyndir.lhunath.lib.system.wrapper.Desktop;
import com.lyndir.lhunath.lib.system.wrapper.SystemTray;
import com.lyndir.lhunath.lib.system.wrapper.TrayIcon;
import com.lyndir.lhunath.lib.system.wrapper.TrayIcon.MessageType;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.transitions.ScreenTransition;
import org.jdesktop.animation.transitions.TransitionTarget;


/**
 * TODO: {@link AbstractUi}<br>
 *
 * @author lhunath
 */
public abstract class AbstractUi
        implements ActionListener, LogListener, CaretListener, ListSelectionListener, ItemListener, Reflective,
        ListDataListener, FocusListener, TransitionTarget {

    static final Logger              logger               = Logger.get( AbstractUi.class );

    protected static final long      LAUNCH_DELAY         = 5000;
    protected static final int       FONT_SIZE            = 12;
    protected static final String    FONT_FACE            = Locale.explain( "conf.font" );
    protected static final Dimension MINIMUM_SIZE         = new Dimension( 1000, 700 );

    private static final String      reportEmail          = Locale.explain( "conf.author" );
    private static final String      reportIssueSubject   = Locale.explain( "ui.reportSubject" ) + ShadeConfig.VERSION;
    private static final String      reportLicenseSubject = Locale.explain( "ui.licenseSubject" );
    private static boolean           startup              = true;

    protected Tab                    showingTab;
    protected Map<Action, Tab>       panelTabs;
    protected List<Stack<String>>    messageStack;
    protected List<Double>           progressStack;
    protected HTMLFormatter          logFormatter;
    protected TrayIcon               systray;
    protected SimpleInternalFrame    window;
    protected JProgressBar           progress;
    protected JEditorPane            log;
    protected JFrame                 frame;
    protected JLabel                 logo;
    protected JPanel                 contentPane;
    protected JCheckBox              systrayButton;
    protected JCheckBox              alwaysOnTop;
    protected JCheckBox              startMini;
    protected File                   defaultLogo;
    private boolean                  showFrame;
    private DragListener             dragListener;
    private JComponent               themesPanel;
    private JCheckBox                verboseLogs;
    private JButton                  windowedTitleButton;
    private JButton                  fullscreenTitleButton;
    private JButton                  closeTitleButton;
    private JDialog                  console;
    private JPanel                   titleBar;
    private PipedInputStream         pipeStdOut;
    private PipedInputStream         pipeStdErr;
    private PrintStream              realStdOut;
    private PrintStream              realStdErr;
    private UpdateUi                 updateUi;
    protected Animator               panelAnimation;
    private ScreenTransition         panelTransition;
    private PaintPanel               contentPanel;
    private PipedOutputStream        consoleStdOut;
    private Tab                      settingsTab;
    private boolean                  overlayed;
    private HashSet<Plugin>          plugins;

    static {
        System.setProperty( "swing.aatext", "true" );
    }

    {
        ShadeConfig.ui = this;

        /*
         * Initialize the logger as early as possible. (before any subclass code other than the main method has ran.)
         */
        process( BasicRequest.LOGGER );

        /* Start the user interface. */
        initTemplate();
    }


    private void initTemplate() {

        /* Set up the backend. */
        messageStack = new ArrayList<Stack<String>>();
        progressStack = new ArrayList<Double>();
        panelTabs = new HashMap<Action, Tab>();
        plugins = new HashSet<Plugin>();
        (updateUi = new UpdateUi( this )).start();

        panelAnimation = new Animator( 800 );
        panelAnimation.setAcceleration( .1f );
        panelAnimation.setDeceleration( .4f );

        SwingUtilities.invokeLater( new Runnable() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {

                /* Build user interface. */
                try {
                    buildUi();
                } catch (RuntimeException e) {
                    if (frame != null)
                        frame.dispose();
                    panelAnimation.cancel();

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
     *            The event that triggered this method call.
     * @param source
     *            The component upon which this event was executed.
     * @param actionCommand
     *            If the event was an {@link ActionEvent}, this contains the action command string.
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
                logger.err( err, Locale.explain( "bug.invalidMailto" ) //$NON-NLS-1$
                                 + Locale.explain( "err.reportManually", Locale.explain( "ui.issue" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                                 + reportEmail );
            } catch (IOException err) {
                logger.err( err, Locale.explain( "err.openingMail" ) //$NON-NLS-1$
                                 + Locale.explain( "err.reportManually", Locale.explain( "ui.issue" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                                 + reportEmail );
            }

        else if ("reportOffense".equals( actionCommand )) //$NON-NLS-1$
            try {
                URI uri = new URI( "mailto:" + reportEmail + "?subject=" //$NON-NLS-1$ //$NON-NLS-2$
                                   + URLEncoder.encode( reportLicenseSubject, "ISO-8859-1" ) ); //$NON-NLS-1$

                launchDelay( "stat.openingMail" ); //$NON-NLS-1$
                Desktop.getDesktop().mail( uri );
            } catch (NoClassDefFoundError err) {
                showJavaVersionWarning();
            } catch (URISyntaxException err) {
                logger.err( err, "bug.invalidMailto" //$NON-NLS-1$
                                 + Locale.explain( "err.reportManually", Locale.explain( "ui.offense" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                                 + reportEmail );
            } catch (IOException err) {
                logger.err( err, "err.openingMail" //$NON-NLS-1$
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
            showingTab = settingsTab;
            execute( BasicRequest.PANEL, false );
            showFrame( true );
        }

        else if (source instanceof AbstractButton && panelTabs.containsKey( ((AbstractButton) source).getAction() )) {
            showingTab = panelTabs.get( ((AbstractButton) source).getAction() );
            execute( BasicRequest.PANEL, false );
        }

        else {
            for (Plugin plugin : plugins)
                if (plugin.handleEvent( e ))
                    return;

            eventNotImplemented( e );
        }

    }

    @SuppressWarnings({"AssignmentToNull", "AssignmentToNull", "AssignmentToNull", "AssignmentToNull", "AssignmentToNull", "AssignmentToNull"})
    protected void toggleConsole() {

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater( new Runnable() {

                @Override
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
                logger.err( e, "Couldn't properly close console output." );
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
                logger.err( e, "Couldn't create replacement stdout/stderr." );
            }
        }
    }

    private void resetConsoleLogger() {

        ConsoleHandler newConsoleLogger = new ConsoleHandler();
        newConsoleLogger.setErrorManager( ShadeConfig.console.getErrorManager() );
        newConsoleLogger.setFormatter( ShadeConfig.console.getFormatter() );
        newConsoleLogger.setFilter( ShadeConfig.console.getFilter() );
        newConsoleLogger.setLevel( ShadeConfig.console.getLevel() );

        // FIXME
        ShadeConfig.console.close();
        // Logger.getGlobal().silence( ShadeConfig.console );

        ShadeConfig.console = newConsoleLogger;
        // Logger.getGlobal().addHandler( ShadeConfig.console );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        event( e, e.getSource(), e.getActionCommand() );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusGained(FocusEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusLost(FocusEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void caretUpdate(CaretEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void itemStateChanged(ItemEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contentsChanged(ListDataEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void intervalAdded(ListDataEvent e) {

        event( e, e.getSource(), null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void intervalRemoved(ListDataEvent e) {

        event( e, e.getSource(), null );
    }

    private void eventNotImplemented(EventObject e) {

        if (e instanceof ActionEvent)
            logger.wrn( "warn.actionNotImplemented", e.getClass(), ((ActionEvent) e).getActionCommand(), //$NON-NLS-1$
                    Utils.getFieldName( this, e.getSource() ) );
        else
            logger.wrn( "warn.eventNotImplemented", e.getClass(), Utils.getFieldName( this, e.getSource() ) ); //$NON-NLS-1$
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
     *            The element to update, or null to update them all.
     */
    public void execute(final Request element) {

        execute( element, true );
    }

    /**
     * Update a given element in this UI.
     *
     * @param element
     *            The element to update, or null to update them all.
     * @param useThread
     *            Use the UpdateUI thread for this update.
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
    @Override
    public Object getFieldValue(Field field)
            throws IllegalArgumentException, IllegalAccessException {

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
     *            Description of the launch event.
     * @param args
     *            Arguments used to format the description string.
     */
    public void launchDelay(String desc, Object... args) {

        logger.dbg( desc, args );
        new Timer().schedule( new TimerTask() {

            @Override
            public void run() {

                logger.dbg( null );
            }
        }, LAUNCH_DELAY );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logMessage(final LogRecord record) {

        if ((log == null || log.getParent() == null) && record.getLevel().intValue() > Level.CONFIG.intValue()
            && console == null)
            toggleConsole();

        if (log != null)
            SwingUtilities.invokeLater( new Runnable() {

                @Override
                public void run() {

                    String message = record.getMessage();
                    Level level = record.getLevel();

                    /* Send log messages to our log field. */
                    if (message != null || record.getThrown() != null)
                        try {
                            log.getEditorKit().read( new StringReader( logFormatter.format( record ) ),
                                    log.getDocument(), log.getDocument().getLength() );
                        } catch (IOException e) {
                            logger.err( e, "Couldn't read the log message from the record!" );
                        } catch (BadLocationException e) {
                            logger.err( e, "Invalid location in the log pane specified for log record insertion!" );
                        }

                    /* Scroll to the bottom. */
                    log.setCaretPosition( log.getDocument().getLength() );

                    /* Manage the progress bar. */
                    if (level.intValue() < Level.CONFIG.intValue()) {
                        int progressLevel = 5 - level.intValue() / 100;
                        if (message != null) {
                            messageStack.get( progressLevel ).push( message );
                            setProgress( null, level );
                        }

                        else {
                            message = messageStack.get( progressLevel ).isEmpty()? "": messageStack.get( progressLevel ).pop();

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

                            systray.displayMessage( level.getLocalizedName(), message, type );
                        }
                }
            } );
    }

    /**
     * Set the value of the progress bar.
     *
     * @param percent
     *            Set the percent to show completed. Use a decimal value in the range 0-1. Use null to switch to
     *            indeterminate mode.
     * @param level
     *            The level of progress bar to use. See {@link Level} (fine, finer, finest).
     */
    public void setProgress(final Double percent, final Level level) {

        SwingUtilities.invokeLater( new Runnable() {

            @Override
            public void run() {

                int progressLevel = 5 - level.intValue() / 100;
                progressStack.set( progressLevel, percent );

                if (progress.getParent() == null)
                    return;

                for (int i = progressStack.size() - 1; i >= 0; --i) {
                    Double levelValue = progressStack.get( i );

                    // Show value if non-null and higher than zero, or zero if last progress in the stack.
                    if (levelValue == null) {
                        progress.setIndeterminate( true );
                        if (messageStack.get( i ).isEmpty())
                            progress.setString( "" );
                        else
                            progress.setString( String.format( "[ %s ]", messageStack.get( i ).peek() ) );
                        return;
                    }

                    else if (levelValue > 0 || i == 0) {
                        progress.setIndeterminate( false );
                        progress.setMaximum( 100 );
                        progress.setMinimum( 0 );

                        int value = (int) (levelValue * 100);
                        progress.setValue( value );
                        if (messageStack.get( i ).isEmpty())
                            progress.setString( "" );
                        else
                            progress.setString( String.format( "[ %s - %d%% ]", messageStack.get( i ).peek(), value ) );

                        return;
                    }
                }

            }
        } );
    }

    /**
     * Show a message explaining the limitations of running jUniUploader with a pre-Java 1.6 VM.
     */
    public void showJavaVersionWarning() {

        JOptionPane.showMessageDialog( frame, Locale.explain( "ui.requireJava6" ), null, JOptionPane.WARNING_MESSAGE );
    }

    private AbstractAction addPanelButton(Tab tab) {

        AbstractAction action;
        final AbstractUi ui = this;
        JToggleButton button = new JToggleButton( action = new AbstractAction( tab.getTitle(), tab.getIcon() ) {

            private static final long serialVersionUID = 1L;


            @Override
            public void actionPerformed(ActionEvent e) {

                ui.actionPerformed( e );
            }
        } );

        button.setText( null );
        button.setBorderPainted( false );
        button.setOpaque( false );
        window.getToolBar().add( new ToolTip( tab.getTitle(), button ) );
        panelTabs.put( button.getAction(), tab );

        return action;
    }

    /**
     * Signal the UI to show a launch notification and start the platform's web browser to the given URL.
     *
     * @param url
     *            The URL to open in the web browser.
     */
    public void browseTo(URL url) {

        try {
            launchDelay( "stat.openingBrowser" );
            Desktop.getDesktop().browse( url.toURI() );
        } catch (NoClassDefFoundError e) {
            showJavaVersionWarning();
        } catch (UnsupportedOperationException e) {
            logger.err( e, "err.browserSupported" );
        } catch (IOException e) {
            logger.err( e, "err.openingBrowser" );
        } catch (URISyntaxException e) {
            logger.err( e, "err.browseUri", url );
        }
    }

    /**
     * Override this method if you have stuff that needs to be initialized before or after the UI building.<br>
     * <br>
     * Don't forget to call super.buildUi() as well.
     */
    protected void buildUi() {

        /* Container. */
        contentPane = new JPanel( new BorderLayout() );
        contentPane.add( contentPanel = PaintPanel.gradientPanel() );
        FormLayout layout = new FormLayout( "0dlu, 0dlu, 0dlu:g, 0dlu, r:p", "t:m, f:0dlu:g, 4dlu, m" );
        DefaultFormBuilder builder = new DefaultFormBuilder( layout, contentPanel );
        layout.setColumnGroups( new int[][] { { 1, 5 } } );
        builder.setDefaultDialogBorder();

        /* Prepare the look and feel. */
        execute( BasicRequest.THEME, false );

        /* Header */
        logo = new JLabel();
        logo.setHorizontalAlignment( SwingConstants.CENTER );
        logo.addMouseListener( new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON3)
                    BaseConfig.dump();
                else
                    toggleOverlay();
            }
        } );

        titleBar = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        titleBar.setLayout( new BoxLayout( titleBar, BoxLayout.X_AXIS ) );
        titleBar.setOpaque( false );

        windowedTitleButton = new JButton( UIUtils.getIcon( "windowed-sss.png" ) );
        windowedTitleButton.setActionCommand( "windowed" );
        windowedTitleButton.addActionListener( this );
        windowedTitleButton.setBorderPainted( false );
        windowedTitleButton.setContentAreaFilled( false );
        fullscreenTitleButton = new JButton( UIUtils.getIcon( "fullscreen-sss.png" ) );
        fullscreenTitleButton.setActionCommand( "fullscreen" );
        fullscreenTitleButton.addActionListener( this );
        fullscreenTitleButton.setBorderPainted( false );
        fullscreenTitleButton.setContentAreaFilled( false );
        closeTitleButton = new JButton( UIUtils.getIcon( "close-sss.png" ) );
        closeTitleButton.setActionCommand( "close" );
        closeTitleButton.addActionListener( this );
        closeTitleButton.setBorderPainted( false );
        closeTitleButton.setContentAreaFilled( false );
        titleBar.add( closeTitleButton );

        builder.nextColumn( 2 );
        builder.append( logo );
        builder.append( titleBar );

        /* Panels */
        window = new SimpleInternalFrame( null, new JToolBar(), null, true );
        window.setSelected( true );
        window.setOpaque( false );

        /* Tabs */
        List<Tab> tabs = appendCustomTabs();
        tabs.addAll( buildTabs() );
        for (Plugin plugin : plugins) {
            List<? extends Tab> pluginTabs = plugin.buildTabs();
            if (pluginTabs != null)
                tabs.addAll( pluginTabs );
        }
        for (Tab tab : tabs)
            tab.setAction( addPanelButton( tab ) );
        if (showingTab == null)
            showingTab = tabs.get( 0 );
        execute( BasicRequest.PANEL, false );
        builder.append( window, 5 );
        builder.nextLine( 2 );

        progress = new JProgressBar() {

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

        progress.setString( "" );
        progress.setStringPainted( true );
        progress.setBorderPainted( false );
        progress.setBackground( UIUtils.setAlpha( progress.getBackground(), 100 ) );

        for (int level = 0; level <= 5; ++level) {
            messageStack.add( new Stack<String>() );
            progressStack.add( 0d );
        }

        builder.append( progress, 5 );

        /* Frame. */
        execute( BasicRequest.LOGO, false );
        execute( BasicRequest.FULLSCREEN, false );
    }

    /**
     * Toggle the visibility of the overlay screen.
     */
    protected void toggleOverlay() {

        if (overlayed)
            frame.getGlassPane().setVisible( false );

        else {
            frame.setGlassPane( getOverlay() );
            frame.getGlassPane().setVisible( true );
            frame.getGlassPane().requestFocusInWindow();
            overlayListener( frame.getGlassPane() );
        }

        overlayed = !overlayed;
    }

    private void overlayListener(Component c) {

        c.addMouseListener( new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                toggleOverlay();
            }
        } );

        if (c instanceof JComponent)
            for (Component child : ((JComponent) c).getComponents())
                overlayListener( child );
    }

    /**
     * Build the overlay that will be shown when the user toggles it on.<br>
     * <br>
     * To make your own overlay, you are recommended to use the panel returned by super.getOverlay().
     *
     * @return The overlay panel.
     */
    protected JPanel getOverlay() {

        final JPanel pane = new JPanel( new BorderLayout() );
        pane.setBackground( UIUtils.setAlpha( Color.black, 150 ) );
        pane.addFocusListener( new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {

                if (pane.isVisible())
                    pane.requestFocus();
            }
        } );

        return pane;
    }

    /**
     * @param image
     *            The image to use as a background on the main content pane.
     */
    protected void setBackgroundImage(final Image image) {

        if (contentPanel == null)
            SwingUtilities.invokeLater( new Runnable() {

                @Override
                public void run() {

                    setBackgroundImage( image );
                }
            } );

        else
            contentPanel.setBackgroundImage( image );
    }

    /**
     * Feel free to override this method to perform actions before and after the panel changes, but DO NOT FORGET to
     * call the parent implementation.
     *
     * @{inheritDoc
     */
    @Override
    public void setupNextScreen() {

        JToolBar toolbar = window.getToolBar();
        for (Component c : toolbar.getComponents())
            if (c instanceof ToolTip && ((ToolTip) c).getContent() instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) ((ToolTip) c).getContent();
                button.setSelected( showingTab.getAction() == button.getAction() );
            }

        window.setTitle( "     " + showingTab.getTitle() + " ~" );
        window.setContent( showingTab.getContent() );
    }

    /**
     * @return The list of additional tabs to show in the UI.
     */
    protected abstract List<Tab> appendCustomTabs();

    private List<Tab> buildTabs() {

        List<Tab> tabs = new LinkedList<Tab>();
        tabs.add( settingsTab = new Tab( Locale.explain( "ui.configuration" ), UIUtils.getIcon( "settings-s.png" ), //$NON-NLS-1$ //$NON-NLS-2$
                getSettingsPane() ) );
        tabs.add( new Tab( Locale.explain( "ui.logs" ), UIUtils.getIcon( "log-s.png" ), //$NON-NLS-1$ //$NON-NLS-2$
                getOperationsPane() ) );
        tabs.add( new Tab( Locale.explain( "ui.licensing" ), UIUtils.getIcon( "license-s.png" ), //$NON-NLS-1$ //$NON-NLS-2$
                getLicensePane() ) );
        tabs.add( new Tab( Locale.explain( "ui.development" ), UIUtils.getIcon( "develop-s.png" ), //$NON-NLS-1$ //$NON-NLS-2$
                getDevelopmentPane() ) );

        return tabs;
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

        for (Plugin plugin : plugins) {
            builder.appendSeparator( plugin.getName() );
            plugin.buildSettings( builder );
        }

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
     *            The {@link DefaultFormBuilder} to which you should add your settings components.
     */
    protected abstract void appendCustomSettings(DefaultFormBuilder builder);

    private JComponent getOperationsPane() {

        FormLayout layout = new FormLayout( "10dlu, 15dlu, p:g, 10dlu, p:g, 15dlu, 10dlu", //$NON-NLS-1$
                "0dlu, f:1dlu:g, 5dlu, p, 10dlu" ); //$NON-NLS-1$
        layout.setColumnGroups( new int[][] { { 3, 5 } } );

        JButton button;
        PanelBuilder builder = new PanelBuilder( layout, new ScrollPanel() );
        CellConstraints constraints = new CellConstraints();

        log = new JEditorPane( "text/html", "" );
        log.setOpaque( false );
        log.setEditable( false );

        JScrollPane pane = new JScrollPane( log );
        pane.setBorder( Borders.EMPTY_BORDER );
        pane.setOpaque( false );
        pane.getViewport().setOpaque( false );
        builder.add( pane, constraints.xyw( 2, 2, 5 ) );

        button = new JButton( Locale.explain( "ui.clearLog" ), UIUtils.getIcon( "clear-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "logClear" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( button, constraints.xy( 3, 4 ) );

        button = new JButton( Locale.explain( "ui.saveLog" ), UIUtils.getIcon( "save-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "logSave" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( button, constraints.xy( 5, 4 ) );

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
        CellConstraints constraints = new CellConstraints();

        try {
            doc = getLicense();
        } catch (IOException e) {
            logger.err( e, "err.readLicense" );
        }

        JEditorPane changelog = new JEditorPane( "text/html", doc.toString() ); //$NON-NLS-1$
        changelog.setOpaque( false );
        changelog.setEditable( false );
        changelog.setFont( Font.decode( "Monospaced-15" ) ); //$NON-NLS-1$

        JScrollPane pane = new JScrollPane( changelog );
        pane.setBorder( Borders.EMPTY_BORDER );
        pane.setOpaque( false );
        pane.getViewport().setOpaque( false );
        builder.add( pane, constraints.xyw( 2, 2, 5 ) );

        button = new JButton( Locale.explain( "ui.reportOffense" ), UIUtils.getIcon( "problem-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "reportOffense" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( new ToolTip( Locale.explain( "ui.reportOffenceTip" ), button ), constraints.xyw( 3, 4, 3 ) );

        builder.getPanel().setOpaque( false );
        return builder.getPanel();
    }

    @SuppressWarnings("unused")
    protected String getLicense()
            throws IOException {

        return Locale.explain( "ui.licenseNotFound" );
    }

    private JComponent getDevelopmentPane() {

        FormLayout layout = new FormLayout( "10dlu, 15dlu, p:g, 10dlu, p:g, 15dlu, 10dlu", //$NON-NLS-1$
                "0dlu, f:1dlu:g, 5dlu, p, 10dlu" ); //$NON-NLS-1$
        layout.setColumnGroups( new int[][] { { 3, 5 } } );

        JButton button;
        PanelBuilder builder = new PanelBuilder( layout, new ScrollPanel() );
        CellConstraints constraints = new CellConstraints();

        builder.add( getDevelopmentComponent(), constraints.xyw( 2, 2, 5 ) );

        button = new JButton( Locale.explain( "ui.reportProblem" ), UIUtils.getIcon( "problem-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "reportIssue" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( new ToolTip( Locale.explain( "ui.reportProblemTip" ), button ), constraints.xy( 3, 4 ) );

        button = new JButton( Locale.explain( "ui.toggleConsole" ), UIUtils.getIcon( "terminal-s.png" ) ); //$NON-NLS-1$ //$NON-NLS-2$
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setVerticalTextPosition( SwingConstants.BOTTOM );
        button.setActionCommand( "toggleConsole" ); //$NON-NLS-1$
        button.addActionListener( this );
        builder.add( new ToolTip( Locale.explain( "ui.toggleConsoleTip" ), button ), constraints.xy( 5, 4 ) );

        builder.getPanel().setOpaque( false );
        return builder.getPanel();
    }

    /**
     * @return The component to put on the center of the development pane.
     */
    protected JComponent getDevelopmentComponent() {

        String doc = "";
        try {
            doc = getChangeLog();
        } catch (IOException e) {
            logger.err( e, "err.readChangelog" );
        }

        JEditorPane changelog = new JEditorPane( "text/html", doc.toString() ); //$NON-NLS-1$
        changelog.setOpaque( false );
        changelog.setEditable( false );
        changelog.setFont( Font.decode( "Monospaced-15" ) ); //$NON-NLS-1$

        JScrollPane pane = new JScrollPane( changelog );
        pane.setBorder( Borders.EMPTY_BORDER );
        pane.setOpaque( false );
        pane.getViewport().setOpaque( false );

        return pane;
    }

    @SuppressWarnings("unused")
    protected String getChangeLog()
            throws IOException {

        return Locale.explain( "ui.changelogNotFound" );
    }

    private void setLookAndFeel() {

        try {
            /* Prepare the theme and apply it. */
            MyTheme.initialize();
            ShadeConfig.theme.get().setup();
            if (frame != null) {
                ShadeConfig.theme.get().reconfigure( frame );
                for (Tab tab : panelTabs.values())
                    if (!tab.getContent().isDisplayable())
                        ShadeConfig.theme.get().reconfigure( tab.getContent() );
            }

            /* Set some look and feel properties. */
            UIUtils.setUIFont( new Font( FONT_FACE, Font.PLAIN, FONT_SIZE ) );
            contentPane.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED, ShadeConfig.theme.get()
                                                                                                         .getBright(),
                    ShadeConfig.theme.get().getDark() ) );

            /* Force update on hidden panels. */
            if (frame != null && frame.isVisible())
                SwingUtilities.invokeAndWait( new Runnable() {

                    @Override
                    public void run() {

                        SwingUtilities.updateComponentTreeUI( frame );

                        /* Also update invisible panels, please. */
                        for (Tab tab : panelTabs.values())
                            if (!tab.getContent().isDisplayable())
                                SwingUtilities.updateComponentTreeUI( tab.getContent() );
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
                    logger.err( err, "err.saveLog" ); //$NON-NLS-1$
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
     * @param element The request that should be processed.
     */
    protected void process(Request element) {

        /* Switch to fullscreen or windowed mode. */
        if (element.equals( BasicRequest.FULLSCREEN )) {
            /* Remove the frame drag listeners. */
            if (dragListener != null) {
                dragListener.uninstall();
                for (Tab tab : panelTabs.values())
                    if (tab.getContent() != window.getContent())
                        dragListener.uninstall( tab.getContent() );
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
                for (Tab tab : panelTabs.values())
                    if (tab.getContent() != window.getContent())
                        dragListener.install( tab.getContent() );
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
                    frame.setExtendedState( ShadeConfig.startMini.get()? Frame.ICONIFIED: Frame.NORMAL );
            }

            frame.setLocationRelativeTo( null );
            process( BasicRequest.SYSTRAY );
        }

        /* Configure the logging backend. */
        else if (element.equals( BasicRequest.LOGGER )) {
            if (logFormatter == null)
                logFormatter = new HTMLFormatter();

            // FIXME
            // logger.setLevel( ShadeConfig.verbose.get()? Level.FINEST: Level.INFO );
            ShadeConfig.formatter.setVerbose( ShadeConfig.verbose.get() );
            logFormatter.setVerbose( ShadeConfig.verbose.get() );

            if (verboseLogs != null)
                verboseLogs.setSelected( ShadeConfig.verbose.get() );

            // FIXME
            // Logger.getGlobal().addListener( this, Level.ALL );
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

            if (iconFile != null && iconFile.isFile())
                logo.setIcon( new ImageIcon( iconFile.getPath() ) );
        }

        /* Activate / Disable the system tray. */
        else if (element.equals( BasicRequest.SYSTRAY )) {
            if (ShadeConfig.sysTray.get() && !SystemTray.isSupported())
                ShadeConfig.sysTray.set( false );

            if (ShadeConfig.sysTray.get())
                try {
                    if (systray == null) {
                        PopupMenu sysMenu = new PopupMenu( Locale.explain( "conf.application" ) );
                        appendCustomSystrayMenuItems( sysMenu );

                        MenuItem item;
                        item = new MenuItem( Locale.explain( "ui.settings" ) );
                        item.setActionCommand( "openSettings" );
                        item.addActionListener( this );
                        sysMenu.add( item );
                        sysMenu.addSeparator();
                        item = new MenuItem( Locale.explain( "ui.exit" ) );
                        item.setActionCommand( "exit" );
                        item.addActionListener( this );
                        sysMenu.add( item );

                        Image icon = UIUtils.getIcon( "warcraft.png" ).getImage();
                        Dimension traySize = SystemTray.getSystemTray().getTrayIconSize();
                        icon = icon.getScaledInstance( traySize.width, traySize.height, Image.SCALE_SMOOTH );

                        systray = new TrayIcon( icon );
                        systray.setActionCommand( "openLoader" );
                        systray.addActionListener( this );
                        systray.setImageAutoSize( true );
                        systray.setPopupMenu( sysMenu );

                        try {
                            SystemTray.getSystemTray().add( systray );
                            frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
                        } catch (AWTException e) {
                            logger.err( e, "err.sysTray" );

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
            if (panelTransition == null)
                panelTransition = new ScreenTransition( window, this, panelAnimation );
        }

        /* Show the selected panel and ensure the correct toolbar button states. */
        else if (element.equals( BasicRequest.PANEL ))
            if (panelTransition != null && !panelAnimation.isRunning())
                panelTransition.start();
            else
                setupNextScreen();

        /* Change the color theme of UI elements. */
        else if (element.equals( BasicRequest.THEME )) {
            logger.inf( "stat.theme" ); //$NON-NLS-1$
            try {
                setLookAndFeel();
            } finally {
                logger.inf( null );
            }
        } else
            for (Plugin plugin : plugins)
                if (plugin.handleRequest( element ))
                    return;
    }

    /**
     * Override this method if you want to perform an action when the frame changes visibility state or has just been
     * made valid.
     *
     * @param newVisibilityState
     *            <code>true</code>: frame is visible.
     */
    protected void frameVisibilityChanged(@SuppressWarnings("unused") boolean newVisibilityState) {

    /* Nothing custom here. */
    }

    /**
     * Override this method to add custom {@link MenuItem}s to the system tray menu.
     *
     * @param sysMenu
     *            The menu to add the items to.
     */
    protected void appendCustomSystrayMenuItems(@SuppressWarnings("unused") PopupMenu sysMenu) {

        for (Plugin plugin : plugins)
            plugin.buildSystray( sysMenu );
    }
}
