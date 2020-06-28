package ssic;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings( "serial" )
public final class InputPanel extends JPanel
{
    private PaneHeader          header;
    private PaneScroller        scroller;
    private JTextArea           textarea;
    private JLabel              statusBar;

    private final String        strSeqTitle = "Sequence Details";
    private JPanel              pnlSequence;
    private JButton             btnLoadFile;
    private JRadioButton        btnDNA;
    private JRadioButton        btnProtein;
    private JComboBox<String>   cmbScoringMatrices;
    private JButton             btnSelectedMatrix;

    private final String        strGapsTitle = "Gaps Settings";
    private JPanel              pnlGaps;
    private JLabel              lblFirst;
    private JTextField          txtFirstGap;
    private JLabel              lblExtending;
    private JTextField          txtExtendingGap;

    private final String        strButtonsTitle = "Control Buttons";
    private JPanel              pnlButtons;
    private JButton             btnReset;
    private JButton             btnClear;
    private JButton             btnGenerate;

    public InputPanel()
    {
        initComponents();
    }

    private void initComponents()
    {
        setLayout( new BorderLayout() );

        textarea = new JTextArea();
        header   = new PaneHeader();
        scroller = new PaneScroller();

        textarea.setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5) );
        textarea.setFont( new Font("Courier New", Font.PLAIN, 14) );

        statusBar = new JLabel( Defaults.DEFAULT_STATUS );
        statusBar.setFont( new Font("Courier New", Font.PLAIN, 14) );
        statusBar.setBorder( BorderFactory.createEmptyBorder(2, 5, 2, 2) );
        statusBar.setPreferredSize( new Dimension(794, 20) );

        populateSequence();
        populateGaps();
        populateButtons();

        header.add( pnlSequence );
        header.add( pnlGaps );
        header.add( pnlButtons );

        scroller.getViewport().add( textarea );
        add( header, BorderLayout.NORTH );
        add( scroller, BorderLayout.CENTER );
        add( statusBar, BorderLayout.SOUTH );

        textarea.addMouseListener( GUI.updateStatus(statusBar, "Paste sequences in FASTA format or load from a text file") );
        textarea.addKeyListener( new KeyAdapter()
        {
            @Override
            public void keyTyped( KeyEvent ke )
            {
                char c = ke.getKeyChar();

                final boolean isAlphabet    = ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' );
                final boolean isGreaterThan = ( c == '>' );
                final boolean isEditingKey  = ( c == KeyEvent.VK_BACK_SPACE ) || ( c == KeyEvent.VK_DELETE );

                if ( !(isAlphabet || isGreaterThan || isEditingKey) )
                {
                    ke.consume();
                }
            }
        });
    }

    private void populateSequence()
    {
        pnlSequence = new JPanel( new FlowLayout() );
        pnlSequence.setPreferredSize( new Dimension(280, 118) );
        pnlSequence.setBorder( GUI.titledBorder(strSeqTitle) );

        final String strDNA = "DNA";
        btnDNA = new JRadioButton( strDNA );
        btnDNA.setActionCommand( strDNA );
        btnDNA.setMnemonic( KeyEvent.VK_D );
        btnDNA.setSelected( true );
        btnDNA.setPreferredSize( new Dimension(100, 35) );

        btnLoadFile = new JButton( "Load Text File" );
        btnLoadFile.setPreferredSize( new Dimension(135, 35) );
        btnLoadFile.setMnemonic( KeyEvent.VK_L );

        final String strProtein = "Protein";
        btnProtein = new JRadioButton( strProtein );
        btnProtein.setActionCommand( strProtein );
        btnProtein.setMnemonic( KeyEvent.VK_P );
        btnProtein.setPreferredSize( new Dimension(100, 35) );

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add( btnDNA );
        btnGroup.add( btnProtein );

        cmbScoringMatrices = new JComboBox<>( Defaults.PROTEIN_SCORING_MATRICES );
        cmbScoringMatrices.setPreferredSize( new Dimension(100, 25) );
        cmbScoringMatrices.setSelectedIndex( 0 );
        cmbScoringMatrices.setEnabled( false );

        btnSelectedMatrix = new JButton( "M" );
        btnSelectedMatrix.setPreferredSize( new Dimension(30, 25) );
        btnSelectedMatrix.setMargin( new Insets(0, 0, 0, 0) );
        btnSelectedMatrix.setMnemonic( KeyEvent.VK_M );
        btnSelectedMatrix.setEnabled( false );

        pnlSequence.add( btnDNA );
        pnlSequence.add( btnLoadFile );
        pnlSequence.add( btnProtein );
        pnlSequence.add( cmbScoringMatrices );
        pnlSequence.add( btnSelectedMatrix );

        btnLoadFile.addMouseListener( GUI.updateStatus(statusBar, "Load sequences from a text (.txt) file") );
        btnLoadFile.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                loadFile();
            }
        });

        btnDNA.addMouseListener( GUI.updateStatus(statusBar, "Input DNA sequences") );
        btnDNA.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                cmbScoringMatrices.setEnabled( false );
                btnSelectedMatrix.setEnabled( false );
                txtFirstGap.setText( Defaults.DNA_FIRST_GAP );
                txtExtendingGap.setText( Defaults.DNA_EXTENDING_GAP );
            }
        });

        btnProtein.addMouseListener( GUI.updateStatus(statusBar, "Input Protein sequences") );
        btnProtein.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                cmbScoringMatrices.setEnabled( true );
                btnSelectedMatrix.setEnabled( true );
                txtFirstGap.setText( Defaults.PROTEIN_FIRST_GAP );
                txtExtendingGap.setText( Defaults.PROTEIN_EXTENDING_GAP );
            }
        });

        cmbScoringMatrices.addMouseListener( GUI.updateStatus(statusBar, "Change the type of Scoring Matrix for protein sequences") );

        btnSelectedMatrix.addMouseListener( GUI.updateStatus(statusBar, "View the selected scoring matrix") );
        btnSelectedMatrix.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                final int dlgWidth      = 550;
                final int dlgHeight     = 420;
                final int matrixType    = getMatrixType();
                final String matrixName = getMatrixName();
                final String matrix     = AlignmentKernel.getProteinScoringMatrixAsString( matrixType );
                new Dialog( "Scoring Matrix: " + matrixName, matrix, dlgWidth, dlgHeight );
            }
        });
    }

    private void loadFile()
    {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle( "Load text file (FASTA format)" );
        fileChooser.setAcceptAllFileFilterUsed( false );
        fileChooser.addChoosableFileFilter( new FileNameExtensionFilter("Text Files (*.txt)", "txt") );
        int status = fileChooser.showOpenDialog( null );

        if( status == JFileChooser.APPROVE_OPTION )
        {
            File file = fileChooser.getSelectedFile();
            textarea.setText( "" );

            BufferedReader br = null;

            try
            {
                br = new BufferedReader( new FileReader(file) );
                String line;
                while( (line = br.readLine()) != null )
                {
                    if( !line.isEmpty() )
                    {
                        if( !line.trim().startsWith(">") )
                        {
                            line = cleanSequence( line );
                            textarea.append( line + "\n" );
                        }
                        else
                        {
                            textarea.append( "\n" + line + "\n" );
                        }
                    }
                }
                textarea.moveCaretPosition( 0 );
            }
            catch( IOException ioEx )
            {
                JOptionPane.showMessageDialog( null, "FATAL ERROR: Failure in I/O operation!\n" + ioEx );
            }
            finally
            {
                try
                {
                    if( br != null )
                    {
                        br.close();
                    }
                }
                catch( IOException ioEx )
                {
                    JOptionPane.showMessageDialog( null, "FATAL ERROR: " + ioEx );
                }
            }
        }
    }

    private String cleanSequence( final String line )
    {
        if( btnDNA.isSelected() )
        {
            return line.replaceAll(Defaults.DNA_REGEX, "" );
        }
        else
        {
            return line.replaceAll(Defaults.PROTEIN_REGEX, "" );
        }
    }

    private void populateGaps()
    {
        pnlGaps = new JPanel( new FlowLayout() );
        pnlGaps.setPreferredSize( new Dimension(280, 118) );
        pnlGaps.setBorder( GUI.titledBorder(strGapsTitle) );

        final String strFirst = "First Gap";
        lblFirst = new JLabel( strFirst );

        lblFirst.setPreferredSize( new Dimension(135, 35) );
        lblFirst.setDisplayedMnemonic( KeyEvent.VK_F );

        final String strExtending = "Extending Gap";
        lblExtending = new JLabel( strExtending );
        lblExtending.setPreferredSize( new Dimension(135, 35) );
        lblExtending.setDisplayedMnemonic( KeyEvent.VK_E );

        txtFirstGap = new JTextField( 5 );
        txtFirstGap.setHorizontalAlignment( JTextField.CENTER );

        txtExtendingGap = new JTextField( 5 );
        txtExtendingGap.setHorizontalAlignment( JTextField.CENTER );

        // set defaults
        btnDNA.doClick();

        lblFirst.setLabelFor( txtFirstGap );
        lblExtending.setLabelFor( txtExtendingGap );

        pnlGaps.add( lblFirst );
        pnlGaps.add( txtFirstGap );
        pnlGaps.add( lblExtending );
        pnlGaps.add( txtExtendingGap );

        lblFirst.addMouseListener( GUI.updateStatus(statusBar, "Set the value of First Gap") );
        txtFirstGap.addMouseListener( GUI.updateStatus(statusBar, "Set the value of First Gap") );
        txtFirstGap.addKeyListener( validateGaps() );

        lblExtending.addMouseListener( GUI.updateStatus(statusBar, "Set the value of Extending Gap") );
        txtExtendingGap.addMouseListener( GUI.updateStatus(statusBar, "Set the value of Extending Gap") );
        txtExtendingGap.addKeyListener( validateGaps() );
    }

    private KeyAdapter validateGaps()
    {
        return ( new KeyAdapter()
        {
            @Override
            public void keyTyped( KeyEvent ke )
            {
                char c = ke.getKeyChar();

                final boolean isDigit      = ( c >= '0' && c <= '9' );
                final boolean isMinus      = ( c == '-' );
                final boolean isEditingKey = ( c == KeyEvent.VK_BACK_SPACE ) || ( c == KeyEvent.VK_DELETE );

                if ( !(isDigit || isMinus || isEditingKey) )
                {
                    ke.consume();
                }
            }
        });
    }

    private void populateButtons()
    {
        pnlButtons = new JPanel( new FlowLayout() );
        pnlButtons.setPreferredSize( new Dimension(200, 118) );
        pnlButtons.setBorder( GUI.titledBorder(strButtonsTitle) );

        btnReset = new JButton( "Reset" );
        btnReset.setPreferredSize( new Dimension(80, 35) );
        btnReset.setMnemonic( KeyEvent.VK_R );

        btnClear = new JButton( "Clear" );
        btnClear.setPreferredSize( new Dimension(80, 35) );
        btnClear.setMnemonic( KeyEvent.VK_C );

        btnGenerate = new JButton( "Generate" );
        btnGenerate.setPreferredSize( new Dimension(164, 40) );
        btnGenerate.setMnemonic( KeyEvent.VK_G );

        pnlButtons.add( btnReset );
        pnlButtons.add( btnClear );
        pnlButtons.add( btnGenerate );

        btnReset.addMouseListener( GUI.updateStatus(statusBar, "Reset to default Sequence Type and Gaps Settings") );
        btnReset.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                btnDNA.doClick();
            }
        });

        btnClear.addMouseListener( GUI.updateStatus(statusBar, "Clear the sequence area") );
        btnClear.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                textarea.setText( "" );
            }
        });

        btnGenerate.addMouseListener( GUI.updateStatus(statusBar, "Generate similarity and identity matrices") );
        btnGenerate.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                generateMatrices();
            }
        });
    }

    private void generateMatrices()
    {
        if( textarea.getText().isEmpty() )
        {
            JOptionPane.showMessageDialog( null, "ERROR: No sequences for generating alignments!" );
            return;
        }

        final String sequences = textarea.getText();
        if( btnDNA.isSelected() )
        {
            AlignmentKernel.generateMatricesForDNA( sequences,
                                                    getFirstGap(),
                                                    getExtendingGap() );
        }
        else if( btnProtein.isSelected() )
        {
            AlignmentKernel.generateMatricesForProtein( sequences,
                                                        getFirstGap(),
                                                        getExtendingGap(),
                                                        getMatrixType() );
        }

        GUI.showOutputPanel();
    }

    private short getFirstGap()
    {
        short firstGap = 0;
        try
        {
            firstGap = Short.parseShort( txtFirstGap.getText() );
        }
        catch( NumberFormatException nfex )
        {
            JOptionPane.showMessageDialog( null, "ERROR: Invalid First Gap value!\nDetails: " + nfex );
        }
        return firstGap;
    }

    private short getExtendingGap()
    {
        short extendingGap = 0;
        try
        {
            extendingGap = Short.parseShort( txtExtendingGap.getText() );
        }
        catch( NumberFormatException nfex )
        {
            JOptionPane.showMessageDialog( null, "ERROR: Invalid Extending Gap value!\nDetails: " + nfex );
        }
        return extendingGap;
    }

    private int getMatrixType()
    {
        return cmbScoringMatrices.getSelectedIndex();
    }

    private String getMatrixName()
    {
        return cmbScoringMatrices.getSelectedItem().toString();
    }
}
