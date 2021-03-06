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

package com.alee.managers.style.skin.web.data.border;

import com.alee.managers.style.skin.web.data.decoration.IDecoration;
import com.alee.utils.GraphicsUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import javax.swing.*;
import java.awt.*;

/**
 * @author Mikle Garin
 */

@XStreamAlias ("LineBorder")
public class LineBorder<E extends JComponent, D extends IDecoration<E, D>, I extends LineBorder<E, D, I>> extends AbstractBorder<E, D, I>
{
    @Override
    public void paint ( final Graphics2D g2d, final Rectangle bounds, final E c, final D d, final Shape shape )
    {
        final float transparency = getTransparency ();
        if ( transparency > 0 )
        {
            final Stroke stroke = getStroke ();
            final Color color = getColor ();

            final Composite oc = GraphicsUtils.setupAlphaComposite ( g2d, transparency, transparency < 1f );
            final Stroke os = GraphicsUtils.setupStroke ( g2d, stroke, stroke != null );
            final Paint op = GraphicsUtils.setupPaint ( g2d, color, color != null );

            g2d.draw ( shape );

            GraphicsUtils.restorePaint ( g2d, op, color != null );
            GraphicsUtils.restoreStroke ( g2d, os, stroke != null );
            GraphicsUtils.restoreComposite ( g2d, oc, transparency < 1f );
        }
    }
}