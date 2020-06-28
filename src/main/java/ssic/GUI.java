package ssic;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings( "serial" )
public final class GUI extends JFrame
{
    private static Pane     mainPane = null;
    private static Boolean  updateMatrices = false;

    public GUI()
    {
        new Splash( Defaults.STARTUP_DELAY_MS );

        initComponents();
    }

    private void initComponents()
    {
        setTitle( Defaults.WIN_TITLE );
        getContentPane().setLayout( new GridLayout(1, 1) );

        ImageIcon icon = new ImageIcon( getClass().getClassLoader().getResource(Defaults.WIN_ICON) );
        setIconImage( icon.getImage() );

        mainPane = new Pane();
        getContentPane().add( mainPane );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setSize( Defaults.WIN_WIDTH, Defaults.WIN_HEIGHT );
        setLocationRelativeTo( null );
        setResizable( false );
        setVisible( true );
    }

    public static void showOutputPanel()
    {
        setUpdate( true );
        mainPane.setSelectedIndex( 1 );
    }

    public static final Border titledBorder( final String title )
    {
        return BorderFactory.createTitledBorder( " " + title + " " );
    }

    public static MouseAdapter updateStatus( final JLabel statusBar,
                                             final String status )
    {
        return ( new MouseAdapter()
        {
            @Override
            public void mouseEntered( MouseEvent me )
            {
                statusBar.setText( status );
            }

            @Override
            public void mouseExited( MouseEvent me )
            {
                statusBar.setText(Defaults.DEFAULT_STATUS );
            }
        });
    }

    public static DefaultTableModel getDefaultTableModel( final int rows,
                                                          final int cols )
    {
        DefaultTableModel model = new DefaultTableModel(rows, cols)
        {
            @Override
            public boolean isCellEditable( int row, int column )
            {
                return false;
            }
        };
        return model;
    }

    public static void renderTableCells( JTable table )
    {
        TableRenderer renderer = new TableRenderer();
        for( int i = 0; i < table.getColumnCount(); i++ )
        {
            table.getColumnModel().getColumn(i).setCellRenderer( renderer );
        }
    }

    public static void setUpdate( final Boolean status )
    {
        updateMatrices = status;
    }

    public static Boolean isUpdated()
    {
        return updateMatrices;
    }
}
