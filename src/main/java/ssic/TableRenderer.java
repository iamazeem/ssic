package ssic;

import java.awt.Font;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings( "serial" )
public final class TableRenderer extends DefaultTableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent( JTable table, Object value,
                                                    boolean isSelected,
                                                    boolean hasFocus,
                                                    int row, int column )
    {
        Component cellComponent = super.getTableCellRendererComponent( table, value,
                                                                       isSelected,
                                                                       hasFocus,
                                                                       row, column );
        setFont( new Font("Arial", Font.BOLD, 12) );
        if( column == 0 )
        {
            setHorizontalAlignment( SwingConstants.LEFT );
        }
        else
        {
            setHorizontalAlignment( SwingConstants.RIGHT );
        }

        if( row+1 > column )
        {
            cellComponent.setForeground( Defaults.FG_MATRIX );
            cellComponent.setBackground( Defaults.BG_MATRIX );
        }
        else
        {
            cellComponent.setForeground( Defaults.FG_DIAGONAL );
            cellComponent.setBackground( Defaults.BG_DIAGONAL );
        }
        return cellComponent;
    }
}
