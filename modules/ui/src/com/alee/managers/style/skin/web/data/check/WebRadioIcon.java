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

package com.alee.managers.style.skin.web.data.check;

import com.alee.managers.style.skin.web.data.DecorationState;
import com.alee.managers.style.skin.web.data.decoration.IDecoration;
import com.alee.utils.GraphicsUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * @author Mikle Garin
 */

@XStreamAlias ( "RadioIcon" )
public class WebRadioIcon<E extends JRadioButton, D extends IDecoration<E, D>, I extends WebRadioIcon<E, D, I>>
        extends AbstractCheckStateIcon<E, D, I>
{
    @XStreamAsAttribute
    protected Color leftColor;

    @XStreamAsAttribute
    protected Color rightColor;

    @Override
    public String getId ()
    {
        return DecorationState.selected;
    }

    @Override
    public void paint ( final Graphics2D g2d, final Rectangle bounds, final E c, final D d, final Rectangle content )
    {
        final int x = content.x + 2;
        final int y = content.y + 2;
        final int w = content.width - 4;
        final int h = content.height - 4;
        final Ellipse2D.Double shape = new Ellipse2D.Double ( x, y, w, h );

        final GradientPaint paint = new GradientPaint ( x, 0, leftColor, x + w, 0, rightColor );
        final Paint op = GraphicsUtils.setupPaint ( g2d, paint );

        g2d.fill ( shape );

        GraphicsUtils.restorePaint ( g2d, op );
    }

    @Override
    public I merge ( final I icon )
    {
        if ( icon.leftColor != null )
        {
            leftColor = icon.leftColor;
        }
        if ( icon.rightColor != null )
        {
            rightColor = icon.rightColor;
        }
        return ( I ) this;
    }
}