package ssic;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings( "serial" )
public final class PaneScroller extends JScrollPane
{
    public PaneScroller()
    {
        initComponents();
    }

    private void initComponents()
    {
        final int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
        final int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

        setHorizontalScrollBarPolicy( h );
        setVerticalScrollBarPolicy( v );
    }
}
