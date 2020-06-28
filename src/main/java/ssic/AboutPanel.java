package ssic;

import java.awt.*;
import javax.swing.*;

@SuppressWarnings( "serial" )
public final class AboutPanel extends JPanel
{
    private final String fontName = "Courier New";

    private JLabel lblTitle;
    private JLabel lblVersion;
    private JLabel lblIcon;
    private JLabel lblAuthor;
    private JLabel lblEmail;

    public AboutPanel()
    {
        initComponents();
    }

    private void initComponents()
    {
        setLayout( new FlowLayout() );

        final String title   = "Sequence Similarity and Identity Calculator (SSIC)";
        final String version = "Version: 1.0.0";
        final String author  = "Developed by: AZEEM";
        final String email   = "Email: azeem.sajid@gmail.com";

        final int CENTER = SwingConstants.CENTER;

        lblTitle   = new JLabel( title, CENTER );
        lblVersion = new JLabel( version, CENTER );
        lblIcon    = new JLabel( "", CENTER );
        lblAuthor  = new JLabel( author, CENTER );
        lblEmail   = new JLabel( email, CENTER );

        lblTitle.setPreferredSize( SIZE(792, 130) );
        lblTitle.setFont( FONT(22) );

        lblVersion.setPreferredSize( SIZE(792, 10) );
        lblVersion.setFont( FONT(18) );

        lblIcon.setPreferredSize( SIZE(792, 80) );
        lblIcon.setIcon( new ImageIcon(getClass().getClassLoader().getResource(Defaults.WIN_ICON)) );
        
        lblAuthor.setPreferredSize( SIZE(792, 200) );
        lblAuthor.setFont( FONT(22) );
        
        lblEmail.setPreferredSize( SIZE(792, 30) );
        lblEmail.setFont( FONT(20) );

        add( lblTitle );
        add( lblVersion );
        add( lblAuthor );
        add( lblEmail );
    }

    private Dimension SIZE( final int width, final int height )
    {
        return new Dimension(width, height);
    }

    private Font FONT( final int size )
    {
        return new Font(fontName, Font.BOLD, size);
    }
}
