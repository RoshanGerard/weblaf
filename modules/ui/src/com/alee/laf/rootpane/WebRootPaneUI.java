/*
 * This file is part of WebLookAndFeel library.
 *
 * WebLookAndFeel library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebLookAndFeel library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alee.laf.rootpane;

import com.alee.extended.window.ComponentMoveBehavior;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.grouping.GroupPane;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.managers.language.LM;
import com.alee.managers.style.*;
import com.alee.painter.Painter;
import com.alee.painter.PainterSupport;
import com.alee.utils.*;
import com.alee.utils.swing.DataRunnable;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRootPaneUI;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Custom UI for JRootPane component.
 * This UI also includes custom frame and dialog decorations.
 *
 * @author Mikle Garin
 */

public class WebRootPaneUI extends BasicRootPaneUI implements Styleable, ShapeProvider, MarginSupport, PaddingSupport, SwingConstants
{
    /**
     * todo 1. Resizable using sides when decorated
     */

    /**
     * Root pane styling icons.
     */
    public static ImageIcon minimizeIcon = new ImageIcon ( WebRootPaneUI.class.getResource ( "icons/minimize.png" ) );
    public static ImageIcon minimizeActiveIcon = new ImageIcon ( WebRootPaneUI.class.getResource ( "icons/minimize_active.png" ) );
    public static ImageIcon maximizeIcon = new ImageIcon ( WebRootPaneUI.class.getResource ( "icons/maximize.png" ) );
    public static ImageIcon maximizeActiveIcon = new ImageIcon ( WebRootPaneUI.class.getResource ( "icons/maximize_active.png" ) );
    public static ImageIcon restoreIcon = new ImageIcon ( WebRootPaneUI.class.getResource ( "icons/restore.png" ) );
    public static ImageIcon restoreActiveIcon = new ImageIcon ( WebRootPaneUI.class.getResource ( "icons/restore_active.png" ) );
    public static ImageIcon closeIcon = new ImageIcon ( WebRootPaneUI.class.getResource ( "icons/close.png" ) );
    public static ImageIcon closeActiveIcon = new ImageIcon ( WebRootPaneUI.class.getResource ( "icons/close_active.png" ) );

    /**
     * Component painter.
     */
    protected IRootPanePainter painter;

    /**
     * Style settings.
     */
    protected int iconSize;
    protected int maxTitleWidth;
    protected String emptyTitleText;
    protected boolean showTitleComponent;
    protected boolean showWindowButtons;
    protected boolean showMinimizeButton;
    protected boolean showMaximizeButton;
    protected boolean showCloseButton;
    protected boolean showMenuBar;
    protected boolean showResizeCorner;

    /**
     * Additional components used be the UI.
     */
    protected JComponent titleComponent;
    protected GroupPane buttonsPanel;
    protected WebButton minimizeButton;
    protected WebButton maximizeButton;
    protected WebButton closeButton;

    /**
     * Runtime variables
     */
    protected Insets margin = null;
    protected Insets padding = null;
    protected boolean decorated = false;
    protected JRootPane root;
    protected Window window;
    protected Frame frame;
    protected Dialog dialog;
    protected LayoutManager previousLayoutManager;
    protected LayoutManager layoutManager;
    protected PropertyChangeListener titleChangeListener;
    protected PropertyChangeListener resizableChangeListener;
    protected WebResizeCorner resizeCorner;

    /**
     * Returns an instance of the WebRootPaneUI for the specified component.
     * This tricky method is used by UIManager to create component UIs when needed.
     *
     * @param c component that will use UI instance
     * @return instance of the WebRootPaneUI
     */
    @SuppressWarnings ("UnusedParameters")
    public static ComponentUI createUI ( final JComponent c )
    {
        return new WebRootPaneUI ();
    }

    @Override
    public void installUI ( final JComponent c )
    {
        super.installUI ( c );

        // Saving root pane reference
        root = ( JRootPane ) c;

        // Decoration
        installWindowDecorations ();

        // Applying skin
        StyleManager.installSkin ( root );
    }

    @Override
    public void uninstallUI ( final JComponent c )
    {
        super.uninstallUI ( c );

        // Uninstalling applied skin
        StyleManager.uninstallSkin ( root );

        // Removing window decorations
        uninstallWindowDecorations ();

        // Cleaning up runtime variables
        layoutManager = null;
        root = null;
    }

    @Override
    public StyleId getStyleId ()
    {
        return StyleManager.getStyleId ( root );
    }

    @Override
    public StyleId setStyleId ( final StyleId id )
    {
        return StyleManager.setStyleId ( root, id );
    }

    @Override
    public Shape provideShape ()
    {
        return PainterSupport.getShape ( root, painter );
    }

    @Override
    public Insets getMargin ()
    {
        return margin;
    }

    @Override
    public void setMargin ( final Insets margin )
    {
        this.margin = margin;
        PainterSupport.updateBorder ( getPainter () );
    }

    @Override
    public Insets getPadding ()
    {
        return padding;
    }

    @Override
    public void setPadding ( final Insets padding )
    {
        this.padding = padding;
        PainterSupport.updateBorder ( getPainter () );
    }

    /**
     * Returns root pane painter.
     *
     * @return root pane painter
     */
    public Painter getPainter ()
    {
        return PainterSupport.getAdaptedPainter ( painter );
    }

    /**
     * Sets root pane painter.
     * Pass null to remove root pane painter.
     *
     * @param painter new root pane painter
     */
    public void setPainter ( final Painter painter )
    {
        PainterSupport.setPainter ( root, new DataRunnable<IRootPanePainter> ()
        {
            @Override
            public void run ( final IRootPanePainter newPainter )
            {
                WebRootPaneUI.this.painter = newPainter;
            }
        }, this.painter, painter, IRootPanePainter.class, AdaptiveRootPanePainter.class );
    }

    /**
     * Returns whether or not this root pane uses custom decoration for its window.
     *
     * @return true if this root pane uses custom decoration for its window, false otherwise
     */
    public boolean isDecorated ()
    {
        return decorated;
    }

    public int getMaxTitleWidth ()
    {
        return maxTitleWidth;
    }

    public void setMaxTitleWidth ( final int maxTitleWidth )
    {
        this.maxTitleWidth = maxTitleWidth;
        if ( isDecorated () && titleComponent != null )
        {
            titleComponent.revalidate ();
            titleComponent.repaint ();
        }
    }

    public String getEmptyTitleText ()
    {
        return emptyTitleText;
    }

    public void setEmptyTitleText ( final String emptyTitleText )
    {
        this.emptyTitleText = emptyTitleText;
        if ( isDecorated () && titleComponent != null )
        {
            titleComponent.revalidate ();
            titleComponent.repaint ();
        }
    }

    public JComponent getTitleComponent ()
    {
        return titleComponent;
    }

    public void setTitleComponent ( final JComponent titleComponent )
    {
        // todo Mark as custom title component
        this.titleComponent = titleComponent;
        root.revalidate ();
    }

    public GroupPane getButtonsPanel ()
    {
        return buttonsPanel;
    }

    public WebResizeCorner getResizeCorner ()
    {
        return resizeCorner;
    }

    public boolean isShowResizeCorner ()
    {
        return showResizeCorner;
    }

    public void setShowResizeCorner ( final boolean showResizeCorner )
    {
        this.showResizeCorner = showResizeCorner;
        root.revalidate ();
    }

    public boolean isShowTitleComponent ()
    {
        return showTitleComponent;
    }

    public void setShowTitleComponent ( final boolean showTitleComponent )
    {
        this.showTitleComponent = showTitleComponent;
        root.revalidate ();
    }

    public boolean isShowWindowButtons ()
    {
        return showWindowButtons;
    }

    public void setShowWindowButtons ( final boolean showWindowButtons )
    {
        this.showWindowButtons = showWindowButtons;
        root.revalidate ();
    }

    public boolean isShowMinimizeButton ()
    {
        return showMinimizeButton;
    }

    public void setShowMinimizeButton ( final boolean showMinimizeButton )
    {
        this.showMinimizeButton = showMinimizeButton;
        updateButtons ();
        root.revalidate ();
    }

    public boolean isShowMaximizeButton ()
    {
        return showMaximizeButton;
    }

    public void setShowMaximizeButton ( final boolean showMaximizeButton )
    {
        this.showMaximizeButton = showMaximizeButton;
        updateButtons ();
        root.revalidate ();
    }

    public boolean isShowCloseButton ()
    {
        return showCloseButton;
    }

    public void setShowCloseButton ( final boolean showCloseButton )
    {
        this.showCloseButton = showCloseButton;
        updateButtons ();
        root.revalidate ();
    }

    public boolean isShowMenuBar ()
    {
        return showMenuBar;
    }

    public void setShowMenuBar ( final boolean showMenuBar )
    {
        this.showMenuBar = showMenuBar;
        root.revalidate ();
    }

    @Override
    public void propertyChange ( final PropertyChangeEvent e )
    {
        super.propertyChange ( e );

        // Retrieving changed property
        final String propertyName = e.getPropertyName ();
        if ( propertyName == null )
        {
            return;
        }

        // Reinstalling window decorations
        if ( propertyName.equals ( WebLookAndFeel.WINDOW_DECORATION_STYLE_PROPERTY ) )
        {
            uninstallWindowDecorations ();
            installWindowDecorations ();
        }
    }

    /**
     * Installs window decorations.
     */
    protected void installWindowDecorations ()
    {
        if ( root.getWindowDecorationStyle () != JRootPane.NONE )
        {
            window = SwingUtils.getWindowAncestor ( root );
            frame = window instanceof Frame ? ( Frame ) window : null;
            dialog = window instanceof Dialog ? ( Dialog ) window : null;
            installListeners ();
            installTransparency ();
            installLayout ();
            installDecorationComponents ();
            decorated = true;
        }
    }

    /**
     * Uninstalls window decorations.
     */
    protected void uninstallWindowDecorations ()
    {
        if ( decorated )
        {
            uninstallSettings ();
            uninstallListeners ();
            uninstallTransparency ();
            uninstallLayout ();
            uninstallDecorationComponents ();
            window = null;
            frame = null;
            dialog = null;
            decorated = false;
        }
    }

    /**
     * Uninstalls settings used in runtime.
     */
    protected void uninstallSettings ()
    {
        if ( isFrame () )
        {
            // Maximum frame size
            frame.setMaximizedBounds ( null );
        }
    }

    /**
     * Installs listeners.
     */
    protected void installListeners ()
    {
        // Listen to window icon and title changes
        titleChangeListener = new PropertyChangeListener ()
        {
            @Override
            public void propertyChange ( final PropertyChangeEvent evt )
            {
                titleComponent.revalidate ();
                titleComponent.repaint ();
            }
        };
        window.addPropertyChangeListener ( WebLookAndFeel.WINDOW_ICON_PROPERTY, titleChangeListener );
        window.addPropertyChangeListener ( WebLookAndFeel.WINDOW_TITLE_PROPERTY, titleChangeListener );

        // Listen to window resizeability changes
        resizableChangeListener = new PropertyChangeListener ()
        {
            @Override
            public void propertyChange ( final PropertyChangeEvent evt )
            {
                updateButtons ();
            }
        };
        window.addPropertyChangeListener ( WebLookAndFeel.WINDOW_RESIZABLE_PROPERTY, resizableChangeListener );
    }

    /**
     * Uninstalls listeners.
     */
    protected void uninstallListeners ()
    {
        window.removePropertyChangeListener ( WebLookAndFeel.WINDOW_ICON_PROPERTY, titleChangeListener );
        window.removePropertyChangeListener ( WebLookAndFeel.WINDOW_TITLE_PROPERTY, titleChangeListener );
        window.removePropertyChangeListener ( WebLookAndFeel.WINDOW_RESIZABLE_PROPERTY, resizableChangeListener );
    }

    /**
     * Installs window transparency.
     */
    protected void installTransparency ()
    {
        if ( ProprietaryUtils.isWindowTransparencyAllowed () )
        {
            root.setOpaque ( false );
            ProprietaryUtils.setWindowOpaque ( window, false );
        }
    }

    /**
     * Uninstalls window transparency.
     */
    protected void uninstallTransparency ()
    {
        if ( ProprietaryUtils.isWindowTransparencyAllowed () )
        {
            root.setOpaque ( true );
            ProprietaryUtils.setWindowOpaque ( window, true );
        }
    }

    /**
     * Installs appropriate layout manager.
     */
    protected void installLayout ()
    {
        if ( layoutManager == null )
        {
            layoutManager = new WebRootPaneLayout ();
        }
        previousLayoutManager = root.getLayout ();
        root.setLayout ( layoutManager );
    }

    /**
     * Uninstalls layout manager.
     */
    protected void uninstallLayout ()
    {
        if ( previousLayoutManager != null )
        {
            root.setLayout ( previousLayoutManager );
            previousLayoutManager = null;
        }
    }

    /**
     * Installs decoration components.
     */
    protected void installDecorationComponents ()
    {
        // Title
        titleComponent = createDefaultTitleComponent ();
        root.add ( titleComponent );

        // Buttons
        updateButtons ();

        // Resize corner
        resizeCorner = new WebResizeCorner ();
        root.add ( resizeCorner );
    }

    /**
     * Uninstalls decoration components.
     */
    protected void uninstallDecorationComponents ()
    {
        // Title
        if ( titleComponent != null )
        {
            root.remove ( titleComponent );
            titleComponent = null;
        }

        // Buttons
        if ( buttonsPanel != null )
        {
            root.remove ( buttonsPanel );
            buttonsPanel = null;
            minimizeButton = null;
            maximizeButton = null;
            closeButton = null;
        }

        // Resize corner
        if ( resizeCorner != null )
        {
            root.remove ( resizeCorner );
            resizeCorner = null;
        }
    }

    /**
     * Returns default window title component.
     *
     * @return default window title component
     */
    protected JComponent createDefaultTitleComponent ()
    {
        final StyleId titlePanelId = StyleId.rootpaneTitlePanel.at ( root );
        final WebPanel titlePanel = new WebPanel ( titlePanelId, new BorderLayout ( 5, 0 ) );

        final WebLabel titleIcon = new WebLabel ( StyleId.rootpaneTitleIcon.at ( titlePanel ) )
        {
            @Override
            public Icon getIcon ()
            {
                return getWindowIcon ();
            }
        };
        titlePanel.add ( titleIcon, BorderLayout.LINE_START );

        final TitleLabel titleLabel = new TitleLabel ( StyleId.rootpaneTitleLabel.at ( titlePanel ) );
        titleLabel.setFont ( WebLookAndFeel.globalTitleFont );
        titleLabel.setFontSize ( 13 );
        titleLabel.setHorizontalAlignment ( CENTER );
        titleLabel.addComponentListener ( new ComponentAdapter ()
        {
            @Override
            public void componentResized ( final ComponentEvent e )
            {
                titleLabel.setHorizontalAlignment ( titleLabel.getRequiredSize ().width > titleLabel.getWidth () ? LEADING : CENTER );
            }
        } );
        titlePanel.add ( titleLabel, BorderLayout.CENTER );

        // Window move and max/restore listener
        final ComponentMoveBehavior cma = new ComponentMoveBehavior ()
        {
            @Override
            public void mouseClicked ( final MouseEvent e )
            {
                if ( isFrame () && isShowMaximizeButton () && SwingUtils.isLeftMouseButton ( e ) && e.getClickCount () == 2 )
                {
                    if ( isMaximized () )
                    {
                        restore ();
                    }
                    else
                    {
                        maximize ();
                    }
                }
            }

            @Override
            public void mouseDragged ( final MouseEvent e )
            {
                if ( dragging && isMaximized () )
                {
                    // todo provide shade width
                    //initialPoint = new Point ( initialPoint.x + shadeWidth, initialPoint.y + shadeWidth );
                    restore ();
                }
                super.mouseDragged ( e );
            }
        };
        titlePanel.addMouseListener ( cma );
        titlePanel.addMouseMotionListener ( cma );

        return titlePanel;
    }

    /**
     * Custom decoration title label.
     */
    public class TitleLabel extends WebLabel
    {
        /**
         * Constructs new title label.
         *
         * @param id style ID
         */
        public TitleLabel ( final StyleId id )
        {
            super ( id );
        }

        /**
         * Returns window title text.
         * There is a small workaround to show window title even when it is empty.
         * That workaround allows window dragging even when title is not set.
         *
         * @return window title text
         */
        @Override
        public String getText ()
        {
            final String title = getWindowTitle ();
            return !TextUtils.isEmpty ( title ) ? title : LM.get ( getEmptyTitleText () );
        }

        /**
         * Returns preferred title size.
         * There is also a predefined title width limit to force it shrink.
         *
         * @return preferred title size
         */
        @Override
        public Dimension getPreferredSize ()
        {
            final Dimension ps = super.getPreferredSize ();
            ps.width = Math.min ( ps.width, maxTitleWidth );
            return ps;
        }

        /**
         * Returns actual preferred size of the title label.
         *
         * @return actual preferred size of the title label
         */
        public Dimension getRequiredSize ()
        {
            return super.getPreferredSize ();
        }
    }

    /**
     * Updates displayed buttons
     */
    protected void updateButtons ()
    {
        // Creating new buttons panel
        if ( buttonsPanel == null )
        {
            buttonsPanel = new GroupPane ( StyleId.rootpaneButtonsPanel.at ( root ) );
            root.add ( buttonsPanel );
        }

        // Minimize button
        if ( showMinimizeButton && isFrame () )
        {
            if ( minimizeButton == null )
            {
                final StyleId minimizeId = StyleId.rootpaneMinimizeButton.at ( buttonsPanel );
                minimizeButton = new WebButton ( minimizeId, minimizeIcon, minimizeActiveIcon );
                minimizeButton.setName ( "minimize" );
                minimizeButton.addActionListener ( new ActionListener ()
                {
                    @Override
                    public void actionPerformed ( final ActionEvent e )
                    {
                        iconify ();
                    }
                } );
            }
            buttonsPanel.add ( minimizeButton );
        }
        else
        {
            if ( minimizeButton != null )
            {
                buttonsPanel.remove ( minimizeButton );
            }
        }

        // Maximize button
        if ( showMaximizeButton && isResizable () && isFrame () )
        {
            if ( maximizeButton == null )
            {
                final StyleId maximizeId = StyleId.rootpaneMaximizeButton.at ( buttonsPanel );
                maximizeButton = new WebButton ( maximizeId, maximizeIcon, maximizeActiveIcon )
                {
                    @Override
                    public Icon getIcon ()
                    {
                        return isMaximized () ? restoreIcon : maximizeIcon;
                    }

                    @Override
                    public Icon getRolloverIcon ()
                    {
                        return isMaximized () ? restoreActiveIcon : maximizeActiveIcon;
                    }
                };
                maximizeButton.setName ( "maximize" );
                maximizeButton.addActionListener ( new ActionListener ()
                {
                    @Override
                    public void actionPerformed ( final ActionEvent e )
                    {
                        if ( isFrame () )
                        {
                            if ( isMaximized () )
                            {
                                restore ();
                            }
                            else
                            {
                                maximize ();
                            }
                        }
                    }
                } );
            }
            buttonsPanel.add ( maximizeButton );
        }
        else
        {
            if ( maximizeButton != null )
            {
                buttonsPanel.remove ( maximizeButton );
            }
        }

        // Close button
        if ( showCloseButton )
        {
            if ( closeButton == null )
            {
                final StyleId closeId = StyleId.rootpaneCloseButton.at ( buttonsPanel );
                closeButton = new WebButton ( closeId, closeIcon, closeActiveIcon );
                closeButton.setName ( "close" );
                closeButton.addActionListener ( new ActionListener ()
                {
                    @Override
                    public void actionPerformed ( final ActionEvent e )
                    {
                        close ();
                    }
                } );
            }
            buttonsPanel.add ( closeButton );
        }
        else
        {
            if ( closeButton != null )
            {
                buttonsPanel.remove ( closeButton );
            }
        }
    }

    /**
     * Returns window title.
     *
     * @return window title
     */
    protected String getWindowTitle ()
    {
        if ( isDialog () )
        {
            return dialog.getTitle ();
        }
        else if ( isFrame () )
        {
            return frame.getTitle ();
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns window icon of suitable size.
     *
     * @return window icon of suitable size
     */
    protected ImageIcon getWindowIcon ()
    {
        final List<Image> images = window != null ? window.getIconImages () : null;
        if ( images != null && images.size () > 1 )
        {
            int bestIndex = 0;
            int bestDiff = Math.abs ( images.get ( bestIndex ).getWidth ( null ) - iconSize );
            for ( int i = 1; i < images.size (); i++ )
            {
                if ( bestDiff == 0 )
                {
                    break;
                }
                final int diff = Math.abs ( images.get ( i ).getWidth ( null ) - iconSize );
                if ( diff < bestDiff )
                {
                    bestIndex = i;
                    bestDiff = diff;
                }
            }
            return generateProperIcon ( images.get ( bestIndex ) );
        }
        else if ( images != null && images.size () == 1 )
        {
            return generateProperIcon ( images.get ( 0 ) );
        }
        else
        {
            return new ImageIcon ();
        }
    }

    /**
     * Returns generated window icon of suitable size.
     *
     * @param image image used to generate icon of suitable size
     * @return generated window icon of suitable size
     */
    protected ImageIcon generateProperIcon ( final Image image )
    {
        if ( image.getWidth ( null ) <= iconSize )
        {
            return new ImageIcon ( image );
        }
        else
        {
            return ImageUtils.createPreviewIcon ( image, iconSize );
        }
    }

    /**
     * Closes the Window.
     */
    protected void close ()
    {
        if ( window != null )
        {
            window.dispatchEvent ( new WindowEvent ( window, WindowEvent.WINDOW_CLOSING ) );
        }
    }

    /**
     * Iconifies the Frame.
     */
    protected void iconify ()
    {
        if ( frame != null )
        {
            frame.setExtendedState ( Frame.ICONIFIED );
        }
    }

    /**
     * Maximizes the Frame.
     */
    protected void maximize ()
    {
        if ( frame != null )
        {
            // Retrieving screen device configuration
            final GraphicsConfiguration gc = frame.getGraphicsConfiguration ().getDevice ().getDefaultConfiguration ();

            // Updating maximized bounds for the frame
            frame.setMaximizedBounds ( SystemUtils.getMaxWindowBounds ( gc, true ) );

            // Forcing window to go into maximized state
            frame.setExtendedState ( Frame.MAXIMIZED_BOTH );
        }
    }

    /**
     * Restores the Frame size.
     */
    protected void restore ()
    {
        if ( frame != null )
        {
            frame.setExtendedState ( Frame.NORMAL );
        }
    }

    /**
     * Returns whether or not window is resizable.
     *
     * @return true if window is resizable, false otherwise
     */
    protected boolean isResizable ()
    {
        return isDialog () ? dialog.isResizable () : isFrame () && frame.isResizable ();
    }

    /**
     * Returns whether or not this root pane is attached to frame.
     *
     * @return true if this root pane is attached to frame, false otherwise
     */
    public boolean isFrame ()
    {
        return frame != null;
    }

    /**
     * Returns whether or not window this root pane is attached to is maximized.
     *
     * @return true if window this root pane is attached to is maximized, false otherwise
     */
    public boolean isMaximized ()
    {
        return isFrame () && frame.getState () == Frame.MAXIMIZED_BOTH;
    }

    /**
     * Returns whether or not this root pane is attached to dialog.
     *
     * @return true if this root pane is attached to dialog, false otherwise
     */
    public boolean isDialog ()
    {
        return dialog != null;
    }

    @Override
    public void paint ( final Graphics g, final JComponent c )
    {
        if ( painter != null )
        {
            painter.paint ( ( Graphics2D ) g, SwingUtils.size ( c ), c, this );
        }
    }

    @Override
    public Dimension getPreferredSize ( final JComponent c )
    {
        return PainterSupport.getPreferredSize ( c, painter );
    }
}