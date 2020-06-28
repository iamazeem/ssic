package ssic;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

@SuppressWarnings( "serial" )
public final class Splash extends JDialog
{
    private final Timer timer;

    public Splash( final int milliseconds )
    {
        timer = new Timer( milliseconds, new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                setVisible( false );
                dispose();
            }
        });

        setAlwaysOnTop( true );
        setUndecorated( true );
        setSize( new Dimension(800, 600) );
        setLocationRelativeTo( null );
        setBackground( new Color(0, 0, 0, 0) );
        setLayout( new FlowLayout() );
        JLabel lblImage = new JLabel();
        lblImage.setPreferredSize( new Dimension(512, 512) );
        lblImage.setIcon( 
            new ImageIcon(getClass().getClassLoader().getResource(Defaults.SPLASH )) 
        );
        getContentPane().add( lblImage );
        setVisible( true );

        timer.start();
    }
}
