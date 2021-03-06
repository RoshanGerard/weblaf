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

package com.alee.demo.content.data.list;

import com.alee.demo.api.*;
import com.alee.extended.list.FileListViewType;
import com.alee.extended.list.WebFileList;
import com.alee.managers.style.StyleId;
import com.alee.utils.CollectionUtils;
import com.alee.utils.FileUtils;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author Mikle Garin
 */

public class WebFileListExample extends AbstractExample
{
    @Override
    public String getId ()
    {
        return "webfilelist";
    }

    @Override
    protected String getStyleFileName ()
    {
        return "filelist";
    }

    @Override
    public FeatureType getFeatureType ()
    {
        return FeatureType.extended;
    }

    @Override
    protected List<Preview> createPreviews ()
    {
        final BasicList icons = new BasicList ( StyleId.filelist, FileListViewType.icons );
        final BasicList tiles = new BasicList ( StyleId.filelist, FileListViewType.tiles );
        final ScrollableList scrollable = new ScrollableList ( StyleId.filelist );
        final EditableList editable = new EditableList ( StyleId.filelist );
        return CollectionUtils.<Preview>asList ( icons, tiles, scrollable, editable );
    }

    /**
     * Basic file list preview.
     */
    protected class BasicList extends AbstractStylePreview
    {
        /**
         * List type.
         */
        private final FileListViewType type;

        /**
         * Constructs new style preview.
         *
         * @param styleId preview style ID
         * @param type    list type
         */
        public BasicList ( final StyleId styleId, final FileListViewType type )
        {
            super ( WebFileListExample.this, type.toString (), FeatureState.updated, styleId );
            this.type = type;
        }

        @Override
        protected List<? extends JComponent> createPreviewElements ( final StyleId containerStyleId )
        {
            final int size = type == FileListViewType.icons ? 6 : 4;
            final int cols = type == FileListViewType.icons ? 3 : 2;
            final File[] files = FileUtils.getUserHome ().listFiles ();
            final File[] model = files.length > size ? Arrays.copyOfRange ( files, 0, size ) : files;
            final WebFileList list = new WebFileList ( getStyleId (), model );
            list.setFileListViewType ( type );
            list.setPreferredColumnCount ( cols );
            list.setPreferredRowCount ( 2 );
            return CollectionUtils.asList ( list );
        }
    }

    /**
     * Scrollable file list preview.
     */
    protected class ScrollableList extends AbstractStylePreview
    {
        /**
         * Constructs new style preview.
         *
         * @param styleId preview style ID
         */
        public ScrollableList ( final StyleId styleId )
        {
            super ( WebFileListExample.this, "scrollable", FeatureState.updated, styleId );
        }

        @Override
        protected List<? extends JComponent> createPreviewElements ( final StyleId containerStyleId )
        {
            final WebFileList list = new WebFileList ( getStyleId (), FileUtils.getUserHome () );
            list.setFileListViewType ( FileListViewType.icons );
            list.setPreferredColumnCount ( 3 );
            list.setPreferredRowCount ( 2 );
            return CollectionUtils.asList ( list.createScrollView () );
        }
    }

    /**
     * Editable file list preview.
     */
    protected class EditableList extends AbstractStylePreview
    {
        /**
         * Constructs new style preview.
         *
         * @param styleId preview style ID
         */
        public EditableList ( final StyleId styleId )
        {
            super ( WebFileListExample.this, "editable", FeatureState.updated, styleId );
        }

        @Override
        protected List<? extends JComponent> createPreviewElements ( final StyleId containerStyleId )
        {
            final WebFileList list = new WebFileList ( getStyleId (), FileUtils.getUserHome () );
            list.setFileListViewType ( FileListViewType.tiles );
            list.setPreferredColumnCount ( 2 );
            list.setPreferredRowCount ( 2 );
            list.setEditable ( true );
            return CollectionUtils.asList ( list.createScrollView () );
        }
    }
}