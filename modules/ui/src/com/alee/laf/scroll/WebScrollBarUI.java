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

package com.alee.laf.scroll;

import com.alee.laf.button.WebButton;
import com.alee.managers.style.*;
import com.alee.painter.Painter;
import com.alee.painter.PainterSupport;
import com.alee.utils.SwingUtils;
import com.alee.utils.swing.DataRunnable;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Custom UI for JScrollBar component.
 *
 * @author Mikle Garin
 */

public class WebScrollBarUI extends BasicScrollBarUI implements Styleable, ShapeProvider, MarginSupport, PaddingSupport
{
    /**
     * Whether or not scroll bar buttons should be displayed.
     */
    protected boolean paintButtons = WebScrollBarStyle.paintButtons;

    /**
     * Whether or not scroll bar track should be displayed.
     */
    protected boolean paintTrack = WebScrollBarStyle.paintTrack;

    /**
     * Component painter.
     */
    protected IScrollBarPainter painter;

    /**
     * Runtime variables.
     */
    protected Insets margin = null;
    protected Insets padding = null;

    /**
     * Returns an instance of the WebScrollBarUI for the specified component.
     * This tricky method is used by UIManager to create component UIs when needed.
     *
     * @param c component that will use UI instance
     * @return instance of the WebScrollBarUI
     */
    @SuppressWarnings ("UnusedParameters")
    public static ComponentUI createUI ( final JComponent c )
    {
        return new WebScrollBarUI ();
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

        // Enabled handling mark
        SwingUtils.setHandlesEnableStateMark ( scrollbar );

        // Applying skin
        StyleManager.installSkin ( scrollbar );
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
        StyleManager.uninstallSkin ( scrollbar );

        // Removing enabled handling mark
        SwingUtils.removeHandlesEnableStateMark ( scrollbar );

        // Uninstalling UI
        super.uninstallUI ( c );
    }

    @Override
    public StyleId getStyleId ()
    {
        return StyleManager.getStyleId ( scrollbar );
    }

    @Override
    public StyleId setStyleId ( final StyleId id )
    {
        return StyleManager.setStyleId ( scrollbar, id );
    }

    @Override
    public Shape provideShape ()
    {
        return PainterSupport.getShape ( scrollbar, painter );
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
     * Returns whether scroll bar arrow buttons should be displayed or not.
     *
     * @return true if scroll bar arrow buttons should be displayed, false otherwise
     */
    public boolean isPaintButtons ()
    {
        return paintButtons;
    }

    /**
     * Sets whether scroll bar arrow buttons should be displayed or not.
     *
     * @param paintButtons whether scroll bar arrow buttons should be displayed or not
     */
    public void setPaintButtons ( final boolean paintButtons )
    {
        this.paintButtons = paintButtons;
        scrollbar.revalidate ();
        scrollbar.repaint ();
    }

    /**
     * Returns whether scroll bar track should be displayed or not.
     *
     * @return true if scroll bar track should be displayed, false otherwise
     */
    public boolean isPaintTrack ()
    {
        return paintTrack;
    }

    /**
     * Sets whether scroll bar track should be displayed or not.
     *
     * @param paintTrack whether scroll bar track should be displayed or not
     */
    public void setPaintTrack ( final boolean paintTrack )
    {
        this.paintTrack = paintTrack;
        scrollbar.revalidate ();
        scrollbar.repaint ();
    }

    /**
     * Returns scroll bar painter.
     *
     * @return scroll bar painter
     */
    public Painter getPainter ()
    {
        return PainterSupport.getAdaptedPainter ( painter );
    }

    /**
     * Sets scroll bar painter.
     * Pass null to remove scroll bar painter.
     *
     * @param painter new scroll bar painter
     */
    public void setPainter ( final Painter painter )
    {
        PainterSupport.setPainter ( scrollbar, new DataRunnable<IScrollBarPainter> ()
        {
            @Override
            public void run ( final IScrollBarPainter newPainter )
            {
                WebScrollBarUI.this.painter = newPainter;
            }
        }, this.painter, painter, IScrollBarPainter.class, AdaptiveScrollBarPainter.class );
    }

    /**
     * Paints scroll bar decorations.
     * The whole painting process is delegated to installed painter class.
     *
     * @param g graphics context
     * @param c scroll bar component
     */
    @Override
    public void paint ( final Graphics g, final JComponent c )
    {
        if ( painter != null )
        {
            painter.setDragged ( isDragging );
            painter.setTrackBounds ( getTrackBounds () );
            painter.setThumbBounds ( getThumbBounds () );
            painter.paint ( ( Graphics2D ) g, SwingUtils.size ( c ), c, this );
        }
    }

    /**
     * Installs additional scroll bar components.
     */
    @Override
    protected void installComponents ()
    {
        // Decrease button
        decrButton = new WebButton ( StyleId.scrollbarDecreaseButton.at ( scrollbar ) )
        {
            @Override
            public Dimension getPreferredSize ()
            {
                // The best way (so far) to hide buttons without causing a serious mess in the code
                return painter != null && paintButtons ? super.getPreferredSize () : new Dimension ( 0, 0 );
            }
        };
        decrButton.setEnabled ( scrollbar.isEnabled () );
        scrollbar.add ( decrButton );

        // Increase button
        incrButton = new WebButton ( StyleId.scrollbarIncreaseButton.at ( scrollbar ) )
        {
            @Override
            public Dimension getPreferredSize ()
            {
                // The best way (so far) to hide buttons without causing a serious mess in the code
                return painter != null && paintButtons ? super.getPreferredSize () : new Dimension ( 0, 0 );
            }
        };
        incrButton.setEnabled ( scrollbar.isEnabled () );
        scrollbar.add ( incrButton );
    }

    @Override
    public Dimension getPreferredSize ( final JComponent c )
    {
        // Scroll bar preferred size
        final boolean ver = scrollbar.getOrientation () == Adjustable.VERTICAL;
        final Dimension ps = painter != null ? painter.getPreferredSize () : new Dimension ( ver ? 0 : 48, ver ? 48 : 0 );

        // Arrow button preferred sizes
        if ( painter != null && paintButtons && decrButton != null && incrButton != null )
        {
            final Dimension dps = decrButton.getPreferredSize ();
            final Dimension ips = incrButton.getPreferredSize ();
            if ( ver )
            {
                ps.width = Math.max ( ps.width, Math.max ( dps.width, ips.width ) );
                ps.height += dps.height + ips.height;
            }
            else
            {
                ps.width += dps.width + ips.width;
                ps.height = Math.max ( ps.height, Math.max ( dps.height, ips.height ) );
            }
        }

        return ps;
    }
}