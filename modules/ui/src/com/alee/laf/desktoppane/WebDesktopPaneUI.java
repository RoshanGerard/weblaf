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

package com.alee.laf.desktoppane;

import com.alee.painter.Painter;
import com.alee.painter.PainterSupport;
import com.alee.managers.style.*;
import com.alee.utils.SwingUtils;
import com.alee.utils.swing.DataRunnable;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopPaneUI;
import java.awt.*;

/**
 * @author Mikle Garin
 */

public class WebDesktopPaneUI extends BasicDesktopPaneUI implements Styleable, ShapeProvider, MarginSupport, PaddingSupport
{
    /**
     * Component painter.
     */
    protected IDesktopPanePainter painter;

    /**
     * Runtime variables.
     */
    protected JDesktopPane desktopPane = null;
    protected Insets margin = null;
    protected Insets padding = null;

    /**
     * Returns an instance of the WebDesktopPaneUI for the specified component.
     * This tricky method is used by UIManager to create component UIs when needed.
     *
     * @param c component that will use UI instance
     * @return instance of the WebDesktopPaneUI
     */
    @SuppressWarnings ("UnusedParameters")
    public static ComponentUI createUI ( final JComponent c )
    {
        return new WebDesktopPaneUI ();
    }

    /**
     * Installs UI in the specified component.
     *
     * @param c component for this UI
     */
    @Override
    public void installUI ( final JComponent c )
    {
        super.installUI ( c );

        // Saving desktop pane to local variable
        desktopPane = ( JDesktopPane ) c;

        // Applying skin
        StyleManager.installSkin ( desktopPane );
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
        StyleManager.uninstallSkin ( desktopPane );

        // Cleaning up reference
        desktopPane = null;

        // Uninstalling UI
        super.uninstallUI ( c );
    }

    @Override
    public StyleId getStyleId ()
    {
        return StyleManager.getStyleId ( desktopPane );
    }

    @Override
    public StyleId setStyleId ( final StyleId id )
    {
        return StyleManager.setStyleId ( desktopPane, id );
    }

    @Override
    public Shape provideShape ()
    {
        return PainterSupport.getShape ( desktopPane, painter );
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
     * Returns desktop pane painter.
     *
     * @return desktop pane painter
     */
    public Painter getPainter ()
    {
        return PainterSupport.getAdaptedPainter ( painter );
    }

    /**
     * Sets desktop pane painter.
     * Pass null to remove desktop pane painter.
     *
     * @param painter new desktop pane painter
     */
    public void setPainter ( final Painter painter )
    {
        PainterSupport.setPainter ( desktopPane, new DataRunnable<IDesktopPanePainter> ()
        {
            @Override
            public void run ( final IDesktopPanePainter newPainter )
            {
                WebDesktopPaneUI.this.painter = newPainter;
            }
        }, this.painter, painter, IDesktopPanePainter.class, AdaptiveDesktopPanePainter.class );
    }

    /**
     * Paints desktop pane.
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
}