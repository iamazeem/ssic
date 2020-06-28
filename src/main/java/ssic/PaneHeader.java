package ssic;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JPanel;

@SuppressWarnings( "serial" )
public final class PaneHeader extends JPanel
{
    public PaneHeader()
    {
        initComponents();
    }

    private void initComponents()
    {
        setPreferredSize( new Dimension(794, 130) );
        setLayout( new FlowLayout() );
    }
}
