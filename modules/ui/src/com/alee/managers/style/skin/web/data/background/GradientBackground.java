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

package com.alee.managers.style.skin.web.data.background;

import com.alee.managers.style.skin.web.data.DecorationDataUtils;
import com.alee.managers.style.skin.web.data.decoration.IDecoration;
import com.alee.utils.CollectionUtils;
import com.alee.utils.GraphicsUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * @author Mikle Garin
 */

@XStreamAlias ( "GradientBackground" )
public class GradientBackground<E extends JComponent, D extends IDecoration<E, D>, I extends GradientBackground<E, D, I>>
        extends AbstractBackground<E, D, I>
{
    /**
     * Gradient type.
     */
    @XStreamAsAttribute
    protected GradientType type;

    /**
     * Bounds width/height percentage representing gradient start point.
     */
    @XStreamAsAttribute
    protected Point2D.Float from;

    /**
     * Bounds width/height percentage representing gradient end point.
     */
    @XStreamAsAttribute
    protected Point2D.Float to;

    /**
     * Gradient colors.
     * Must always be provided to properly render separator.
     */
    @XStreamImplicit ( itemFieldName = "color" )
    protected List<GradientColor> colors;

    @Override
    public void paint ( final Graphics2D g2d, final Rectangle bounds, final E c, final D d, final Shape shape )
    {
        final float transparency = getTransparency ();
        if ( transparency > 0 )
        {
            final Composite oc = GraphicsUtils.setupAlphaComposite ( g2d, transparency, transparency < 1f );
            final Rectangle b = shape.getBounds ();
            final int x1 = ( int ) Math.round ( b.x + b.width * from.getX () );
            final int y1 = ( int ) Math.round ( b.y + b.height * from.getY () );
            final int x2 = ( int ) Math.round ( b.x + b.width * to.getX () );
            final int y2 = ( int ) Math.round ( b.y + b.height * to.getY () );
            final Paint paint = DecorationDataUtils.getPaint ( type, colors, x1, y1, x2, y2 );
            final Paint op = GraphicsUtils.setupPaint ( g2d, paint );
            g2d.fill ( shape );
            GraphicsUtils.restorePaint ( g2d, op );
            GraphicsUtils.restoreComposite ( g2d, oc, transparency < 1f );
        }
    }

    @Override
    public I merge ( final I background )
    {
        super.merge ( background );
        if ( background.type != null )
        {
            type = background.type;
        }
        if ( background.from != null )
        {
            from = background.from;
        }
        if ( background.to != null )
        {
            to = background.to;
        }
        if ( background.colors != null )
        {
            colors = CollectionUtils.copy ( background.colors );
        }
        return ( I ) this;
    }
}