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

package com.alee.laf.separator;

import com.alee.painter.Painter;
import com.alee.painter.PainterSupport;
import com.alee.managers.style.StyleId;
import com.alee.managers.style.StyleManager;
import com.alee.utils.SwingUtils;
import com.alee.managers.style.MarginSupport;
import com.alee.managers.style.PaddingSupport;
import com.alee.managers.style.ShapeProvider;
import com.alee.managers.style.Styleable;
import com.alee.utils.swing.DataRunnable;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.SeparatorUI;
import java.awt.*;

/**
 * Custom UI for JSeparator component.
 *
 * @author Mikle Garin
 */

public class WebSeparatorUI extends SeparatorUI implements Styleable, ShapeProvider, MarginSupport, PaddingSupport
{
    /**
     * Component painter.
     */
    protected ISeparatorPainter painter;

    /**
     * Runtime variables.
     */
    protected JSeparator separator = null;
    protected Insets margin = null;
    protected Insets padding = null;

    /**
     * Returns an instance of the WebSeparatorUI for the specified component.
     * This tricky method is used by UIManager to create component UIs when needed.
     *
     * @param c component that will use UI instance
     * @return instance of the WebSeparatorUI
     */
    @SuppressWarnings ( "UnusedParameters" )
    public static ComponentUI createUI ( final JComponent c )
    {
        return new WebSeparatorUI ();
    }

    /**
     * Installs UI in the specified component.
     *
     * @param c component for this UI
     */
    @Override
    public void installUI ( final JComponent c )
    {
        // Saving separator to local variable
        separator = ( JSeparator ) c;

        // Applying skin
        StyleManager.installSkin ( separator );
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
        StyleManager.uninstallSkin ( separator );

        // Cleaning up reference
        separator = null;
    }

    @Override
    public StyleId getStyleId ()
    {
        return StyleManager.getStyleId ( separator );
    }

    @Override
    public StyleId setStyleId ( final StyleId id )
    {
        return StyleManager.setStyleId ( separator, id );
    }

    @Override
    public Shape provideShape ()
    {
        return PainterSupport.getShape ( separator, painter );
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
     * Returns separator painter.
     *
     * @return separator painter
     */
    public Painter getPainter ()
    {
        return PainterSupport.getAdaptedPainter ( painter );
    }

    /**
     * Sets separator painter.
     * Pass null to remove separator painter.
     *
     * @param painter new separator painter
     */
    public void setPainter ( final Painter painter )
    {
        PainterSupport.setPainter ( separator, new DataRunnable<ISeparatorPainter> ()
        {
            @Override
            public void run ( final ISeparatorPainter newPainter )
            {
                WebSeparatorUI.this.painter = newPainter;
            }
        }, this.painter, painter, ISeparatorPainter.class, AdaptiveSeparatorPainter.class );
    }

    /**
     * Paints separator.
     *
     * @param g graphics context
     * @param c separator component
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
    public Dimension getPreferredSize ( final JComponent c )
    {
        return PainterSupport.getPreferredSize ( c, painter );
    }
}