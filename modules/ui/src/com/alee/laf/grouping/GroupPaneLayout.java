package com.alee.laf.grouping;

import com.alee.extended.layout.AbstractLayoutManager;
import com.alee.utils.general.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Layout designed specifically for usage within {@link com.alee.laf.grouping.GroupPane} container.
 * It constructs a grid of components to be visually grouped and knows how to retrieve components at specific cells.
 *
 * @author Mikle Garin
 */

public class GroupPaneLayout extends AbstractLayoutManager implements SwingConstants
{
    /**
     * Components placement order orientation.
     */
    private int orientation;

    /**
     * Amount of columns used to place components.
     */
    private int columns;

    /**
     * Amount of rows used to place components.
     */
    private int rows;

    /**
     * Constructs default layout.
     */
    public GroupPaneLayout ()
    {
        this ( HORIZONTAL, Integer.MAX_VALUE, 1 );
    }

    /**
     * Constructs layout with the specified amount of rows and columns.
     *
     * @param orientation components placement order orientation
     */
    public GroupPaneLayout ( final int orientation )
    {
        this ( orientation, Integer.MAX_VALUE, 1 );
    }

    /**
     * Constructs layout with the specified amount of rows and columns.
     *
     * @param columns amount of columns used to place components
     * @param rows    amount of rows used to place components
     */
    public GroupPaneLayout ( final int columns, final int rows )
    {
        this ( HORIZONTAL, columns, rows );
    }

    /**
     * Constructs layout with the specified amount of rows and columns.
     *
     * @param orientation components placement order orientation
     * @param columns     amount of columns used to place components
     * @param rows        amount of rows used to place components
     */
    public GroupPaneLayout ( final int orientation, final int columns, final int rows )
    {
        super ();
        setOrientation ( orientation );
        setColumns ( columns );
        setRows ( rows );
    }

    /**
     * Returns components placement order orientation.
     *
     * @return components placement order orientation
     */
    public int getOrientation ()
    {
        return orientation;
    }

    /**
     * Sets components placement order orientation.
     *
     * @param orientation components placement order orientation
     */
    public void setOrientation ( final int orientation )
    {
        this.orientation = orientation;
    }

    /**
     * Returns amount of columns used to place components.
     *
     * @return amount of columns used to place components
     */
    public int getColumns ()
    {
        return columns;
    }

    /**
     * Sets amount of columns used to place components.
     *
     * @param columns amount of columns to place components
     */
    public void setColumns ( final int columns )
    {
        this.columns = columns;
    }

    /**
     * Returns amount of rows used to place components.
     *
     * @return amount of rows used to place components
     */
    public int getRows ()
    {
        return rows;
    }

    /**
     * Sets amount of rows used to place components.
     *
     * @param rows amount of rows to place components
     */
    public void setRows ( final int rows )
    {
        this.rows = rows;
    }

    @Override
    public void layoutContainer ( final Container parent )
    {
        // Retrieving actual grid size
        final GridSize gridSize = getActualGridSize ( parent );

        // Calculating children preferred sizes
        final Pair<int[], int[]> sizes = calculateSizes ( parent, gridSize );

        // Laying out components
        // To do that we will simply iterate through the whole grid
        // Some cells we will iterate through won't have components, we will simply skip those
        final Insets border = parent.getInsets ();
        int y = border.top;
        for ( int row = 0; row < gridSize.rows; row++ )
        {
            int x = border.left;
            for ( int column = 0; column < gridSize.columns; column++ )
            {
                // Converting grid point to component index
                final int index = pointToIndex ( parent, column, row, gridSize );

                // Retrieving cell component if it exists
                final Component component = parent.getComponent ( index );
                if ( component != null )
                {
                    // Updating its bounds
                    component.setBounds ( x, y, sizes.key[ column ], sizes.value[ row ] );
                }

                // Move forward into grid
                x += sizes.key[ column ];
            }

            // Move forward into grid
            y += sizes.value[ row ];
        }
    }

    @Override
    public Dimension preferredLayoutSize ( final Container parent )
    {
        // Retrieving actual grid size
        final GridSize gridSize = getActualGridSize ( parent );

        // Calculating children preferred sizes
        final Pair<int[], int[]> sizes = calculateSizes ( parent, gridSize );

        // Calculating preferred size
        final Dimension ps = new Dimension ( 0, 0 );
        for ( final Integer columnWith : sizes.key )
        {
            ps.width += columnWith;
        }
        for ( final Integer rowHeight : sizes.value )
        {
            ps.height += rowHeight;
        }
        final Insets border = parent.getInsets ();
        ps.width += border.left + border.right;
        ps.height += border.top + border.bottom;

        return ps;
    }

    /**
     * Returns actual grid size according to container components amount.
     * Actual grid size is very important for all calculations as it defines the final size of the grid.
     * <p/>
     * For example: Layout settings are set to have 5 columns and 5 rows which in total requires 25 components to fill-in the grid.
     * Though there might not be enough components provided to fill the grid, in that case the actual grid size might be less.
     *
     * @param parent group pane
     * @return actual grid size according to container components amount
     */
    public GridSize getActualGridSize ( final Container parent )
    {
        final int count = parent.getComponentCount ();
        if ( orientation == HORIZONTAL )
        {
            return new GridSize ( Math.min ( count, columns ), ( count - 1 ) / columns + 1 );
        }
        else
        {
            return new GridSize ( ( count - 1 ) / rows + 1, Math.min ( count, rows ) );
        }
    }

    /**
     * Returns component at the specified cell.
     *
     * @param parent group pane
     * @param column component column
     * @param row    component row
     * @return component at the specified cell
     */
    public Component getComponentAt ( final Container parent, final int column, final int row )
    {
        final GridSize gridSize = getActualGridSize ( parent );
        final int index = pointToIndex ( parent, column, row, gridSize );
        final int count = parent.getComponentCount ();
        return index < count ? parent.getComponent ( index ) : null;
    }

    /**
     * Returns grid column in which component under the specified index is placed.
     *
     * @param parent   group pane
     * @param index    component index
     * @param gridSize actual grid size
     * @return grid column in which component under the specified index is placed
     */
    public int indexToColumn ( final Container parent, final int index, final GridSize gridSize )
    {
        final boolean ltr = parent.getComponentOrientation ().isLeftToRight ();
        final int column = orientation == HORIZONTAL ? index % columns : index / rows;
        return ltr ? column : gridSize.columns - 1 - column;
    }

    /**
     * Returns grid row in which component under the specified index is placed.
     *
     * @param index component index
     * @return grid row in which component under the specified index is placed
     */
    public int indexToRow ( final int index )
    {
        return orientation == HORIZONTAL ? index / columns : index % rows;
    }

    /**
     * Returns index of the component placed in the specified grid cell or {@code null} if cell is empty.
     *
     * @param parent   group pane
     * @param column   grid column index
     * @param row      grid row index
     * @param gridSize actual grid size
     * @return index of the component placed in the specified grid cell or {@code null} if cell is empty
     */
    public int pointToIndex ( final Container parent, final int column, final int row, final GridSize gridSize )
    {
        final boolean ltr = parent.getComponentOrientation ().isLeftToRight ();
        final int c = ltr ? column : gridSize.columns - 1 - column;
        return orientation == HORIZONTAL ? row * columns + c : c * rows + row;
    }

    /**
     * Returns column and row sizes.
     *
     * @param parent   group pane
     * @param gridSize actual grid size
     * @return column and row sizes
     */
    protected Pair<int[], int[]> calculateSizes ( final Container parent, final GridSize gridSize )
    {
        final int count = parent.getComponentCount ();

        // Retrieving component preferred sizes
        final List<Dimension> ps = new ArrayList<Dimension> ( count );
        for ( int i = 0; i < count; i++ )
        {
            ps.add ( parent.getComponent ( i ).getPreferredSize () );
        }

        // Calculating max column widths and row heights
        final int[] columnWidths = new int[ gridSize.columns ];
        final int[] rowHeights = new int[ gridSize.rows ];
        for ( int i = 0; i < count; i++ )
        {
            final int col = indexToColumn ( parent, i, gridSize );
            columnWidths[ col ] = Math.max ( columnWidths[ col ], ps.get ( i ).width );

            final int row = indexToRow ( i );
            rowHeights[ row ] = Math.max ( rowHeights[ row ], ps.get ( i ).height );
        }
        return new Pair<int[], int[]> ( columnWidths, rowHeights );
    }
}