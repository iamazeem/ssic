package ssic;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.datatransfer.*;
import java.beans.XMLEncoder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

@SuppressWarnings( "serial" )
public final class OutputPanel extends JPanel
{
    private PaneHeader          header;
    private PaneScroller        simScroller;
    private PaneScroller        idnScroller;
    private JPanel              pnlTableGroup;

    private JTable              simTable;
    private JTable              idnTable;
    private JLabel              statusBar;

    private final String        strExportTitle = "Export Settings";
    private JPanel              pnlExport;
    private JLabel              lblDelimiter;
    private JComboBox<String>   cmbDelimiters;
    private final String[]      strDelimeters = { "Tab", "Space", "Comma", "Semicolon" };
    private final String[]      strDelMapping = { "\t", " ", ",", ";" };
    private JButton             btnExport;

    private final String        strToggleTitle = "Toggle Similarity / Identity";
    private JPanel              pnlToggle;
    private JCheckBox           chkSimilarity;
    private JCheckBox           chkIdentity;

    private final String        strButtonsTitle = "Control Buttons";
    private JPanel              pnlButtons;
    private JButton             btnPrint;
    private JButton             btnCopy;

    public OutputPanel()
    {
        initComponents();
    }

    private void initComponents()
    {
        setLayout( new BorderLayout() );

        header        = new PaneHeader();
        simScroller   = new PaneScroller();
        idnScroller   = new PaneScroller();
        pnlTableGroup = new JPanel( new FlowLayout() );

        simScroller.setPreferredSize( new Dimension(778, 188) );
        idnScroller.setPreferredSize( new Dimension(778, 188) );

        simScroller.setBorder( GUI.titledBorder("Similarity Matrix") );
        idnScroller.setBorder( GUI.titledBorder("Identity Matrix") );

        DefaultTableModel model = GUI.getDefaultTableModel( 5, 5 );
        simTable = new JTable( model );
        idnTable = new JTable( model );

        Border tableBorder = BorderFactory.createLineBorder( Color.DARK_GRAY );
        simTable.setBorder( tableBorder );
        simTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        simTable.setRowSelectionAllowed( false );
        simTable.setCellSelectionEnabled( false );
        simTable.setColumnSelectionAllowed( false );
        GUI.renderTableCells( simTable );

        idnTable.setBorder( tableBorder );
        idnTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        idnTable.setRowSelectionAllowed( false );
        idnTable.setCellSelectionEnabled( false );
        idnTable.setColumnSelectionAllowed( false );
        GUI.renderTableCells( idnTable );

        statusBar = new JLabel( Defaults.DEFAULT_STATUS );
        statusBar.setFont( new Font("Courier New", Font.PLAIN, 14) );
        statusBar.setBorder( BorderFactory.createEmptyBorder(2, 5, 2, 2) );
        statusBar.setPreferredSize( new Dimension(794, 20) );

        populateExport();
        populateToggle();
        populateButtons();

        header.add( pnlExport );
        header.add( pnlToggle );
        header.add( pnlButtons );

        simScroller.getViewport().add( simTable );
        idnScroller.getViewport().add( idnTable );

        pnlTableGroup.add( simScroller );
        pnlTableGroup.add( idnScroller );

        add( header, BorderLayout.NORTH );
        add( pnlTableGroup, BorderLayout.CENTER );
        add( statusBar, BorderLayout.SOUTH );

        addHierarchyListener( new HierarchyListener()
        {
            @Override
            public void hierarchyChanged( HierarchyEvent he )
            {
                if( GUI.isUpdated() && AlignmentKernel.isUpdated() )
                {
                    GUI.setUpdate( false );

                    TableModel simModel, idnModel;
                    simModel = AlignmentKernel.getMatrixAsTableModel( Defaults.SIMILARITY );
                    idnModel = AlignmentKernel.getMatrixAsTableModel( Defaults.IDENTITY );

                    simTable.setModel( simModel );
                    idnTable.setModel( idnModel );
                    GUI.renderTableCells( simTable );
                    GUI.renderTableCells( idnTable );
                }
            }
        });

        simTable.addMouseListener( GUI.updateStatus(statusBar, "Similarity matrix: Double click on a paricular cell for pairwise details") );
        simTable.addMouseListener( viewAlignmentDetail() );

        idnTable.addMouseListener( GUI.updateStatus(statusBar, "Identity matrix: Double click on a paricular cell for pairwise details") );
        idnTable.addMouseListener( viewAlignmentDetail() );
    }

    private MouseAdapter viewAlignmentDetail()
    {
        return new MouseAdapter()
        {
            @Override
            public void mousePressed( MouseEvent me )
            {
                final int nRows = simTable.getRowCount();
                final int row  = simTable.rowAtPoint( me.getPoint() );
                final int col  = simTable.columnAtPoint( me.getPoint() );
                final boolean isUpdated     = AlignmentKernel.isUpdated();
                final boolean isDoubleClick = ( me.getClickCount() == 2 );
                final boolean isValidCell   = ( col <= row && col != 0 && row != nRows-1 );

                if( isUpdated && isDoubleClick && isValidCell )
                {
                    int index = 0;
                    for( int i = 1; i < row; ++i )
                    {
                        index += i;
                    }
                    index += col;

                    final String title  = AlignmentKernel.getTitle( index-1 );
                    final String align  = AlignmentKernel.getAlignmentDetails( index-1 );
                    final int dlgWidth  = 600;
                    final int dlgHeight = 250;

                    new Dialog( "Alignment Viewer - " + title, align, dlgWidth, dlgHeight );
                }
            }
        };
    }

    private void populateExport()
    {
        pnlExport = new JPanel( new FlowLayout() );
        pnlExport.setPreferredSize( new Dimension(280, 118) );
        pnlExport.setBorder( GUI.titledBorder(strExportTitle) );

        lblDelimiter = new JLabel( "Set Delimiter" );
        lblDelimiter.setPreferredSize( new Dimension(120, 25) );
        lblDelimiter.setDisplayedMnemonic( KeyEvent.VK_M );

        cmbDelimiters = new JComboBox<>( strDelimeters );
        cmbDelimiters.setPreferredSize( new Dimension(120, 25) );
        cmbDelimiters.setSelectedIndex( 0 );

        btnExport = new JButton( "Start Exporting" );
        btnExport.setPreferredSize( new Dimension(244, 50) );
        btnExport.setMnemonic( KeyEvent.VK_X );

        lblDelimiter.setLabelFor( cmbDelimiters );
        pnlExport.add( lblDelimiter );
        pnlExport.add( cmbDelimiters );
        pnlExport.add( btnExport );

        lblDelimiter.addMouseListener( GUI.updateStatus(statusBar, "Set delimiter to separate the matrix values") );
        cmbDelimiters.addMouseListener( GUI.updateStatus(statusBar, "Set delimiter to separate the matrix values") );

        btnExport.addMouseListener( GUI.updateStatus(statusBar, "Export matrices as txt/xml/xls/xlsx/csv formats") );
        btnExport.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                exportAlignments();
            }
        });
    }

    private void exportAlignments()
    {
        final String matricesData = readMatrices( getDelimiter() );
        if( !AlignmentKernel.isUpdated() )
        {
            JOptionPane.showMessageDialog( null, "ERROR: No matrix available to export!" );
            return;
        }
        else if( matricesData.isEmpty() )
        {
            JOptionPane.showMessageDialog( null, "ERROR: No matrix is selected!" );
            return;
        }

        final String[][] extensions =
        {
            { "Text Files (*.txt)", "txt" },
            { "XML Document (*.xml)", "xml" },
            { "Microsoft Excel 2000/2003 (*.xls)", "xls" },
            { "Microsoft Excel 2007/2010 (*.xlsx)", "xlsx" },
            { "Comma Separated Values (CSV) Files (*.csv)", "csv" }
        };

        JFileChooser fileChooser = new JFileChooser()
        {
            @Override
            public void approveSelection()
            {
                File file = getSelectedFile();
                String filename = file.getPath();
                String ext = ((FileNameExtensionFilter) getFileFilter()).getExtensions()[0];
                filename = addFileExtension( filename, ext );
                file = new File(filename);

                if( file.exists() )
                {
                    int confirm = JOptionPane.showConfirmDialog
                    (
                        null, "File already exists! Do you want to overwrite?", "Overwrite?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
                    );
                    if ( confirm != JOptionPane.YES_OPTION )
                    {
                        return;
                    }
                }
                super.approveSelection();
            }
        };

        fileChooser.setAcceptAllFileFilterUsed( false );
        fileChooser.setDialogTitle( "Export" );

        for ( final String[] extension : extensions )
        {
            final String fileType = extension[0];
            final String fileExtn = extension[1];
            final FileNameExtensionFilter filter = new FileNameExtensionFilter( fileType, fileExtn );
            fileChooser.addChoosableFileFilter( filter );
        }

        final int status = fileChooser.showSaveDialog( null );
        if( status == JFileChooser.APPROVE_OPTION )
        {
            File file = fileChooser.getSelectedFile();
            String filename = file.getPath();
            String ext = ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
            filename = addFileExtension( filename, ext );

            if( ext.equals("xml") )
            {
                writeToXMLFile( filename );
                return;
            }

            String delimiter = null;

            switch( ext )
            {
                case "txt":  delimiter = getDelimiter();
                break;

                case "xls":
                case "xlsx": delimiter = "\t";
                break;

                case "csv":  delimiter = ",";
                break;
            }
            writeToFile( filename, delimiter );
        }
    }

    private String addFileExtension( final String filename, final String ext )
    {
        if( filename.lastIndexOf('.') == -1 )
        {
            return filename + "." + ext;
        }

        return filename;
    }

    private String getDelimiter()
    {
        final int index = cmbDelimiters.getSelectedIndex();
        final String delimiter = strDelMapping[index];
        return delimiter;
    }

    private void writeToXMLFile( final String filename )
    {
        try
        {
            XMLEncoder encoder = new XMLEncoder( new FileOutputStream(filename) );

            if( chkSimilarity.isSelected() )
            {
                encoder.writeObject( "Similarity Matrix" );
                writeTableAsXML( simTable, encoder );
            }

            if( chkIdentity.isSelected() )
            {
                encoder.writeObject( "Identity Matrix" );
                writeTableAsXML( idnTable, encoder );
            }

            encoder.close();
        }
        catch( FileNotFoundException fnfe )
        {
        }
    }

    private void writeTableAsXML( final JTable table, XMLEncoder encoder )
    {
        final String delimiter = "\t";
        final TableModel tableModel = table.getModel();
        final int MAX_ROWS = tableModel.getRowCount();
        final int MAX_COLS = tableModel.getColumnCount();

        for( int i = 0; i < MAX_ROWS; i++ )
        {
            String tableData = "";
            for( int j = 0; j < MAX_COLS; j++ )
            {
                String cellValue = "";
                if( tableModel.getValueAt(i, j) != null )
                {
                    cellValue = tableModel.getValueAt(i, j).toString();
                }
                tableData += cellValue;
                tableData += ( j < MAX_COLS-1 ? delimiter : Defaults.NEW_LINE );
            }
            encoder.writeObject( tableData );
        }
    }

    private void writeToFile( final String filename, final String delimiter )
    {
        final String tableData = readMatrices( delimiter );
        if( tableData.isEmpty() )
        {
            JOptionPane.showMessageDialog( null, "ERROR: No matrix is selected!" );
            return;
        }

        BufferedWriter bw = null;
        try
        {
            bw = new BufferedWriter( new FileWriter(filename) );
            bw.write( tableData );
            bw.flush();
        }
        catch( IOException ioEx )
        {
            JOptionPane.showMessageDialog( null, "FATAL ERROR: " + ioEx );
        }
        finally
        {
            try
            {
                if( bw != null )
                {
                    bw.close();
                }
            }
            catch( IOException ioEx )
            {
                JOptionPane.showMessageDialog( null, "FATAL ERROR: " + ioEx );
            }
        }
    }

    private String readMatrices( String delimiter )
    {
        final String newline = Defaults.NEW_LINE;
        String tableData = "";
        if( chkSimilarity.isSelected() && chkIdentity.isSelected() )
        {
            tableData += "Similarity Matrix" + newline;
            tableData += readTable( simTable, delimiter );
            tableData += newline + newline;
            tableData += "Identity Matrix" + newline;
            tableData += readTable( simTable, delimiter );
        }
        else if( chkSimilarity.isSelected() )
        {
            tableData += "Similarity Matrix" + newline;
            tableData += readTable( simTable, delimiter );
        }
        else if( chkIdentity.isSelected() )
        {
            tableData += "Identity Matrix" + newline;
            tableData += readTable( simTable, delimiter );
        }
        return tableData;
    }

    private String readTable( JTable table, String delimiter )
    {
        final TableModel tableModel = table.getModel();
        final int MAX_ROWS = tableModel.getRowCount();
        final int MAX_COLS = tableModel.getColumnCount();

        String tableData = "";

        for( int i = 0; i < MAX_ROWS; i++ )
        {
            for( int j = 0; j < MAX_COLS; j++ )
            {
                String cellValue = "";
                if( tableModel.getValueAt(i, j) != null )
                {
                    cellValue = tableModel.getValueAt(i, j).toString();
                }
                tableData += cellValue;
                tableData += ( j < MAX_COLS-1 ? delimiter : Defaults.NEW_LINE );
            }
        }
        return tableData;
    }

    private void populateToggle()
    {
        pnlToggle = new JPanel( new FlowLayout() );
        pnlToggle.setPreferredSize( new Dimension(280, 118) );
        pnlToggle.setBorder( GUI.titledBorder(strToggleTitle) );

        chkSimilarity = new JCheckBox( "Similarity Matrix (on / off)" );
        chkSimilarity.setPreferredSize( new Dimension(235, 35) );
        chkSimilarity.setMnemonic( KeyEvent.VK_S );
        chkSimilarity.setSelected( true );

        chkIdentity = new JCheckBox( "Identity Matrix (on / off)" );
        chkIdentity.setPreferredSize( new Dimension(235, 35) );
        chkIdentity.setMnemonic( KeyEvent.VK_D );
        chkIdentity.setSelected( true );

        pnlToggle.add( chkSimilarity );
        pnlToggle.add( chkIdentity );

        chkSimilarity.addMouseListener( GUI.updateStatus(statusBar, "Toggle similarity matrix on/off") );
        chkSimilarity.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                if( ((AbstractButton) ae.getSource()).isSelected() )
                {
                    toggleMatrices( Defaults.SIMILARITY,
                                    Defaults.MATRIX_ON );
                }
                else
                {
                    toggleMatrices( Defaults.SIMILARITY,
                                    Defaults.MATRIX_OFF );
                }
            }
        });

        chkIdentity.addMouseListener( GUI.updateStatus(statusBar, "Toggle identity matrix on/off") );
        chkIdentity.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                if( ((AbstractButton) ae.getSource()).isSelected() )
                {
                    toggleMatrices( Defaults.IDENTITY,
                                    Defaults.MATRIX_ON );
                }
                else
                {
                    toggleMatrices( Defaults.IDENTITY,
                                    Defaults.MATRIX_OFF );
                }
            }
        });
    }

    private void toggleMatrices( final int matrixType, final int toggleFlag )
    {
        final boolean isToggleOn   = ( toggleFlag == Defaults.MATRIX_ON );
        final boolean isToggleOff  = ( toggleFlag == Defaults.MATRIX_OFF );
        final boolean isSimilarity = ( matrixType == Defaults.SIMILARITY );
        final boolean isIdentity   = ( matrixType == Defaults.IDENTITY );

        if( isToggleOn && isSimilarity )
        {
            simScroller.setVisible( true );
        }
        else if( isToggleOn && isIdentity )
        {
            idnScroller.setVisible( true );
        }

        if( isToggleOff && isSimilarity )
        {
            simScroller.setVisible( false );
        }
        else if( isToggleOff && isIdentity )
        {
            idnScroller.setVisible( false );
        }
    }

    private void populateButtons()
    {
        pnlButtons = new JPanel( new FlowLayout() );
        pnlButtons.setPreferredSize( new Dimension(200, 118) );
        pnlButtons.setBorder( GUI.titledBorder(strButtonsTitle) );

        btnCopy = new JButton( "Copy to clipboard" );
        btnCopy.setPreferredSize( new Dimension(164, 35) );
        btnCopy.setMnemonic( KeyEvent.VK_C );

        btnPrint = new JButton( "Print" );
        btnPrint.setPreferredSize( new Dimension(164, 40) );
        btnPrint.setMnemonic( KeyEvent.VK_P );

        pnlButtons.add( btnCopy );
        pnlButtons.add( btnPrint );

        btnCopy.addMouseListener( GUI.updateStatus(statusBar, "Copy similarity / identity matrices to system clipboard") );
        btnCopy.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                copyAlignments();
            }
        });

        btnPrint.addMouseListener( GUI.updateStatus(statusBar, "Print similarity / identity matrices") );
        btnPrint.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                printMatrices();
            }
        });
    }

    private void copyAlignments()
    {
        final String matricesData = readMatrices( getDelimiter() );
        if( !matricesData.isEmpty() )
        {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Clipboard clipboard = toolkit.getSystemClipboard();
            StringSelection text = new StringSelection( matricesData );
            clipboard.setContents( text , null );
        }
    }

    private void printMatrices()
    {
        final String matricesData = readMatrices( "\t" );
        if( matricesData.isEmpty() )
        {
            JOptionPane.showMessageDialog( null, "ERROR: No matrix is selected!" );
            return;
        }

        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable( new Printable()
        {
            @Override
            public int print( Graphics g, PageFormat pageFormat, int pageIndex )
            {
                if( pageIndex != 0 )
                {
                    return NO_SUCH_PAGE;
                }

                pageFormat.setOrientation( PageFormat.LANDSCAPE );
                Graphics2D g2 = (Graphics2D) g;
                g2.setFont( new Font("Courier New", Font.BOLD, 16) );
                g2.setPaint( Color.BLACK );
                g2.drawString( matricesData, 100, 100 );
                return PAGE_EXISTS;
            }
        });

        if( pj.printDialog() )
        {
            try
            {
                pj.print();
            }
            catch( PrinterException pEx )
            {
                JOptionPane.showMessageDialog( null, "FATAL ERROR: " + pEx );
            }
        }
    }
}
