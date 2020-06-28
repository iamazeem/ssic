package ssic;

import javax.swing.JTabbedPane;
import java.awt.event.KeyEvent;

@SuppressWarnings( "serial" )
public final class Pane extends JTabbedPane
{
    public Pane()
    {
        initComponents();
    }

    private void initComponents()
    {
        InputPanel  inputPanel  = new InputPanel();
        OutputPanel outputPanel = new OutputPanel();
        AboutPanel  aboutPanel  = new AboutPanel();

        addTab( "Input Panel",  inputPanel  );
        addTab( "Output Panel", outputPanel );
        addTab( "About",        aboutPanel  );

        setMnemonicAt( 0, KeyEvent.VK_I );
        setMnemonicAt( 1, KeyEvent.VK_O );
        setMnemonicAt( 2, KeyEvent.VK_A );
    }
}
