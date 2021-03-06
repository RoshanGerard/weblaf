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
import com.alee.utils.MergeUtils;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import javax.swing.*;
import java.awt.*;

/**
 * @author Mikle Garin
 */

public abstract class AbstractBorder<E extends JComponent, D extends IDecoration<E, D>, I extends AbstractBorder<E, D, I>>
        implements IBorder<E, D, I>
{
    /**
     * Default border ID.
     */
    private static final String defaultId = "border";

    /**
     * Default border color.
     */
    private static final Color defaultColor = new Color ( 210, 210, 210 );

    /**
     * Border ID.
     */
    @XStreamAsAttribute
    protected String id;

    /**
     * Shade transparency.
     */
    @XStreamAsAttribute
    protected Float transparency;

    /**
     * Shade width.
     */
    @XStreamAsAttribute
    protected Stroke stroke;

    /**
     * Shade color.
     */
    @XStreamAsAttribute
    protected Color color;

    @Override
    public String getId ()
    {
        return id != null ? id : defaultId;
    }

    @Override
    public float getTransparency ()
    {
        return transparency != null ? transparency : 1f;
    }

    @Override
    public Stroke getStroke ()
    {
        return stroke;
    }

    @Override
    public Color getColor ()
    {
        return color != null ? color : defaultColor;
    }

    @Override
    public I merge ( final I border )
    {
        if ( border.transparency != null )
        {
            transparency = border.transparency;
        }
        if ( border.stroke != null )
        {
            stroke = border.stroke;
        }
        if ( border.color != null )
        {
            color = border.color;
        }
        return ( I ) this;
    }

    @Override
    public I clone ()
    {
        return ( I ) MergeUtils.cloneByFieldsSafely ( this );
    }
}