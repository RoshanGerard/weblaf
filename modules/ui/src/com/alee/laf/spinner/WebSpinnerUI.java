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

package com.alee.laf.spinner;

import com.alee.extended.layout.AbstractLayoutManager;
import com.alee.laf.button.WebButton;
import com.alee.managers.style.*;
import com.alee.painter.Painter;
import com.alee.painter.PainterSupport;
import com.alee.utils.SwingUtils;
import com.alee.utils.swing.DataRunnable;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * @author Mikle Garin
 */

public class WebSpinnerUI extends BasicSpinnerUI implements Styleable, ShapeProvider, MarginSupport, PaddingSupport
{
    /**
     * Spinner button icons.
     */
    protected static final ImageIcon UP_ICON = new ImageIcon ( WebSpinnerUI.class.getResource ( "icons/up.png" ) );
    protected static final ImageIcon DOWN_ICON = new ImageIcon ( WebSpinnerUI.class.getResource ( "icons/down.png" ) );

    /**
     * Component painter.
     */
    protected ISpinnerPainter painter;

    /**
     * Runtime variables.
     */
    protected Insets margin = null;
    protected Insets padding = null;

    /**
     * Returns an instance of the WebSpinnerUI for the specified component.
     * This tricky method is used by UIManager to create component UIs when needed.
     *
     * @param c component that will use UI instance
     * @return instance of the WebSpinnerUI
     */
    @SuppressWarnings ("UnusedParameters")
    public static ComponentUI createUI ( final JComponent c )
    {
        return new WebSpinnerUI ();
    }

    /**
     * Installs UI in the specified component.
     *
     * @param c component for this UI
     */
    @Override
    public void installUI ( final JComponent c )
    {
        // Installing UI
        super.installUI ( c );

        // Applying skin
        StyleManager.installSkin ( spinner );
    }

    /**
     * Uninstalls UI from the specified component.
     *
     * @param c component with this UI
     */
    @Override
    public void uninstallUI ( final JComponent c )
    {
        // Uninstalling applied skin
        StyleManager.uninstallSkin ( spinner );

        // Uninstalling UI
        super.uninstallUI ( c );
    }

    @Override
    public StyleId getStyleId ()
    {
        return StyleManager.getStyleId ( spinner );
    }

    @Override
    public StyleId setStyleId ( final StyleId id )
    {
        return StyleManager.setStyleId ( spinner, id );
    }

    @Override
    public Shape provideShape ()
    {
        return PainterSupport.getShape ( spinner, painter );
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
     * Returns spinner painter.
     *
     * @return spinner painter
     */
    public Painter getPainter ()
    {
        return PainterSupport.getAdaptedPainter ( painter );
    }

    /**
     * Sets spinner painter.
     * Pass null to remove spinner painter.
     *
     * @param painter new spinner painter
     */
    public void setPainter ( final Painter painter )
    {
        PainterSupport.setPainter ( spinner, new DataRunnable<ISpinnerPainter> ()
        {
            @Override
            public void run ( final ISpinnerPainter newPainter )
            {
                WebSpinnerUI.this.painter = newPainter;
            }
        }, this.painter, painter, ISpinnerPainter.class, AdaptiveSpinnerPainter.class );
    }

    @Override
    protected LayoutManager createLayout ()
    {
        return new WebSpinnerLayout ();
    }

    /**
     * Paints slider.
     *
     * @param g graphics
     * @param c component
     */
    @Override
    public void paint ( final Graphics g, final JComponent c )
    {
        if ( painter != null )
        {
            painter.paint ( ( Graphics2D ) g, SwingUtils.size ( c ), c, this );
        }
    }

    @Override
    protected Component createNextButton ()
    {
        final WebButton nextButton = new WebButton ( StyleId.spinnerNextButton.at ( spinner ), UP_ICON );
        nextButton.setName ( "Spinner.nextButton" );
        installNextButtonListeners ( nextButton );
        return nextButton;
    }

    @Override
    protected Component createPreviousButton ()
    {
        final WebButton prevButton = new WebButton ( StyleId.spinnerPreviousButton.at ( spinner ), DOWN_ICON );
        prevButton.setName ( "Spinner.previousButton" );
        installPreviousButtonListeners ( prevButton );
        return prevButton;
    }

    @Override
    protected JComponent createEditor ()
    {
        final JComponent editor = super.createEditor ();
        if ( editor instanceof JTextComponent )
        {
            configureEditor ( ( JTextComponent ) editor, spinner );
        }
        else
        {
            configureEditor ( ( ( JSpinner.DefaultEditor ) editor ).getTextField (), spinner );
        }
        return editor;
    }

    /**
     * Configures editor field.
     *
     * @param field   editor field
     * @param spinner spinner
     */
    public static void configureEditor ( final JTextComponent field, final JSpinner spinner )
    {
        // Installing proper styling
        StyleId.spinnerEditor.at ( spinner ).set ( field );

        // Adding editor focus listener
        field.addFocusListener ( new FocusAdapter ()
        {
            @Override
            public void focusGained ( final FocusEvent e )
            {
                spinner.repaint ();
            }

            @Override
            public void focusLost ( final FocusEvent e )
            {
                spinner.repaint ();
            }
        } );
    }

    @Override
    public Dimension getPreferredSize ( final JComponent c )
    {
        return PainterSupport.getPreferredSize ( c, super.getPreferredSize ( c ), painter );
    }

    /**
     * Replacement for spinner layout provided by {@link javax.swing.plaf.basic.BasicSpinnerUI}.
     * It properly provides equal space for both spinner buttons and calculates preferred size.
     * It also fixes a few minor issues and flaws in the layout.
     */
    protected static class WebSpinnerLayout extends AbstractLayoutManager
    {
        /**
         * Editor layout constraint.
         */
        public static final String EDITOR = "Editor";

        /**
         * Next (down) button layout constraint.
         */
        public static final String NEXT = "Next";

        /**
         * Previous (up) button layout constraint.
         */
        public static final String PREVIOUS = "Previous";

        /**
         * Editor component.
         */
        protected Component editor = null;

        /**
         * Next (down) button.
         */
        protected Component nextButton = null;

        /**
         * Previous (up) button.
         */
        protected Component previousButton = null;

        @Override
        public void addComponent ( final Component component, final Object constraints )
        {
            if ( EDITOR.equals ( constraints ) )
            {
                editor = component;
            }
            else if ( NEXT.equals ( constraints ) )
            {
                nextButton = component;
            }
            else if ( PREVIOUS.equals ( constraints ) )
            {
                previousButton = component;
            }
        }

        @Override
        public void removeComponent ( final Component component )
        {
            if ( component == editor )
            {
                editor = null;
            }
            else if ( component == nextButton )
            {
                nextButton = null;
            }
            else if ( component == previousButton )
            {
                previousButton = null;
            }
        }

        @Override
        public void layoutContainer ( final Container parent )
        {
            final Insets b = parent.getInsets ();
            final Dimension s = parent.getSize ();
            final Dimension next = nextButton != null ? nextButton.getPreferredSize () : new Dimension ( 0, 0 );
            final Dimension prev = previousButton != null ? previousButton.getPreferredSize () : new Dimension ( 0, 0 );
            final int bw = Math.max ( next.width, prev.width );
            final int bah = s.height - b.top - b.bottom;
            final int nh = bah % 2 == 0 ? bah / 2 : ( bah - 1 ) / 2 + 1;
            final int ph = bah % 2 == 0 ? bah / 2 : ( bah - 1 ) / 2;
            final boolean ltr = parent.getComponentOrientation ().isLeftToRight ();
            if ( editor != null )
            {
                editor.setBounds ( b.left + ( ltr ? 0 : bw ), b.top, s.width - b.left - b.right - bw, s.height - b.top - b.bottom );
            }
            if ( nextButton != null )
            {
                nextButton.setBounds ( ltr ? s.width - b.right - bw : b.left, b.top, bw, nh );
            }
            if ( previousButton != null )
            {
                previousButton.setBounds ( ltr ? s.width - b.right - bw : b.left, b.top + nh, bw, ph );
            }
        }

        @Override
        public Dimension preferredLayoutSize ( final Container parent )
        {
            final Insets b = parent.getInsets ();
            final Dimension ed = editor != null ? editor.getPreferredSize () : new Dimension ( 0, 0 );
            final Dimension next = nextButton != null ? nextButton.getPreferredSize () : new Dimension ( 0, 0 );
            final Dimension prev = previousButton != null ? previousButton.getPreferredSize () : new Dimension ( 0, 0 );
            final int w = b.left + ed.width + Math.max ( next.width, prev.width ) + b.right;
            final int h = b.top + Math.max ( ed.height, next.height + prev.height ) + b.bottom;
            return new Dimension ( w, h );
        }
    }
}