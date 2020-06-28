package ssic;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public final class SSIC
{
    public static void main( String[] args )
    {
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch( ClassNotFoundException |
               InstantiationException |
               IllegalAccessException |
               UnsupportedLookAndFeelException ex )
        {
            System.out.println( ex );
            return;
        }

        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                new GUI();
            }
        });
    }
}
