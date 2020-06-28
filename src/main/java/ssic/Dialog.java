package ssic;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.*;

@SuppressWarnings( "serial" )
final class Dialog extends JDialog
{
    private JScrollPane scrPane;
    private JTextArea   txtContent;

    public Dialog( final String matrixName,
                   final String matrix,
                   final int    width,
                   final int    height )
    {
        initComponents( matrixName, matrix, width, height );
    }

    public void initComponents( final String matrixName,
                                final String matrix,
                                final int    width,
                                final int    height )
    {
        setTitle( matrixName );
        setSize( new Dimension(width, height) );
        getContentPane().setLayout( new BorderLayout() );
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        setLocationRelativeTo( this.rootPane );
        setResizable( false );
        setModal( true );

        txtContent = new JTextArea( matrix );
        txtContent.setEditable( false );
        txtContent.setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5) );
        txtContent.setFont( new Font("Courier New", Font.PLAIN, 12) );

        scrPane = new JScrollPane( txtContent );

        add( scrPane );
        setVisible( true );
    }
}
