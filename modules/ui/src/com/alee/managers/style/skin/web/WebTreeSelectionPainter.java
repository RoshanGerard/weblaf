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

package com.alee.managers.style.skin.web;

import com.alee.laf.tree.ITreeNodePainter;
import com.alee.laf.tree.WebTreeUI;
import com.alee.managers.style.skin.web.data.decoration.IDecoration;

import javax.swing.*;

/**
 * Simple tree selection painter based on {@link com.alee.managers.style.skin.web.AbstractDecorationPainter}.
 * It is used within {@link com.alee.managers.style.skin.web.WebTreePainter} to paint cells selection.
 *
 * @author Mikle Garin
 */

public class WebTreeSelectionPainter<E extends JTree, U extends WebTreeUI, D extends IDecoration<E, D>>
        extends AbstractSectionDecorationPainter<E, U, D> implements ITreeNodePainter<E, U>
{
    /**
     * Painted row index.
     */
    protected int row;

    @Override
    public void prepareToPaint ( final int row )
    {
        this.row = row;
    }

    @Override
    protected boolean isFocused ()
    {
        return false;
    }
}