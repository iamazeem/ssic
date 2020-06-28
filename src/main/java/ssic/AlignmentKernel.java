package ssic;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

import org.biojava3.alignment.*;
import org.biojava3.alignment.template.SequencePair;
import org.biojava3.alignment.template.SubstitutionMatrix;
import org.biojava3.core.exceptions.CompoundNotFoundError;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.compound.*;
import org.biojava3.core.sequence.io.FastaReaderHelper;

public final class AlignmentKernel
{
    private static String similarityMatrix = "";
    private static String identityMatrix   = "";
    private static JTable similarityMatrixTable;
    private static JTable identityMatrixTable;
    private static ArrayList<Alignment> alignmentList = null;

    public static boolean isUpdated()
    {
        return !( similarityMatrix.isEmpty() && identityMatrix.isEmpty() );
    }

    public static TableModel getMatrixAsTableModel( final int matrixType )
    {
        convertAndUpdateMatrix( matrixType );

        if( isSimilarity( matrixType ) )
        {
            return similarityMatrixTable.getModel();
        }
        else if( isIdentity( matrixType ) )
        {
            return identityMatrixTable.getModel();
        }
        
        return null;
    }

    private static void convertAndUpdateMatrix( final int matrixType )
    {
        String matrixAsString  = "";

        if( isSimilarity( matrixType ) )
        {
            matrixAsString = similarityMatrix;
        }
        else if( isIdentity( matrixType ) )
        {
            matrixAsString = identityMatrix;
        }

        final String rows[] = matrixAsString.split( "\n" );

        final int MAX_ROWS = rows.length;
        final int MAX_COLS = rows.length;
        TableModel model = GUI.getDefaultTableModel( MAX_ROWS+1, MAX_COLS+1 );

        for( int i = 0; i < MAX_ROWS; ++i )
        {
            final String cells[] = rows[i].split( "\t" );
            for( int j = 0; j < cells.length; ++j )
            {
                model.setValueAt( cells[j], i, j );
            }
        }

        for( int i = 0; i < MAX_COLS; ++i )
        {
            final String value = (String) model.getValueAt( i, 0 );
            model.setValueAt( value, MAX_ROWS, i+1 );
        }

        if( isSimilarity( matrixType ) )
        {
            similarityMatrixTable = new JTable( model );
        }
        else if( isIdentity( matrixType ) )
        {
            identityMatrixTable = new JTable( model );
        }
    }

    public static void generateMatricesForDNA( final String sequences,
                                               final short  firstGap,
                                               final short  extendingGap )
    {
        InputStream is = new ByteArrayInputStream( sequences.getBytes() );
        SimpleGapPenalty gaps = new SimpleGapPenalty( firstGap, extendingGap );

        try
        {
            LinkedHashMap<String, DNASequence> DNAMap;
            DNAMap = FastaReaderHelper.readFastaDNASequence( is );

            SubstitutionMatrix<NucleotideCompound> matrix;
            matrix = SubstitutionMatrixHelper.getNuc4_4();

            SequencePair<DNASequence, NucleotideCompound> pair;
            AmbiguityDNACompoundSet compoundSet;
            compoundSet = AmbiguityDNACompoundSet.getDNACompoundSet();
            double similarity, identity;

            String simMatrix = "";
            String idnMatrix = "";
            Alignment alignment;

            alignmentList = new ArrayList<>();

            for( Entry<String, DNASequence> seq_i : DNAMap.entrySet() )
            {
                simMatrix += seq_i.getValue().getOriginalHeader() + "\t";
                idnMatrix += seq_i.getValue().getOriginalHeader() + "\t";

                seq_i.getValue().setCompoundSet( compoundSet );

                for( Entry<String, DNASequence> seq_j : DNAMap.entrySet() )
                {
                    if( seq_i.getKey().equals( seq_j.getKey() ) )
                    {
                        simMatrix += "100.0%";
                        idnMatrix += "100.0%";
                        break;
                    }

                    pair = Alignments.getPairwiseAlignment( seq_i.getValue(),
                                                            seq_j.getValue(),
                                                            Alignments.PairwiseSequenceAlignerType.GLOBAL,
                                                            gaps,
                                                            matrix );

                    similarity = getSimilarity( pair.getNumSimilars(), pair.getLength() );
                    identity   = getIdentity( pair.getNumIdenticals(), pair.getLength() );

                    simMatrix += similarity + "%\t";
                    idnMatrix += identity   + "%\t";

                    alignment = new Alignment( pair.getQuery().getAccession().toString(),
                                                    pair.getTarget().getAccession().toString(),
                                                    similarity + "%",
                                                    identity + "%",
                                                    Defaults.ALIGNMENT_TYPE,
                                                    pair.toString() );
                    alignmentList.add( alignment );
                }
                simMatrix += "\n";
                idnMatrix += "\n";
            }

            similarityMatrix = simMatrix;
            identityMatrix   = idnMatrix;

            if( Defaults.CONSOLE_OUTPUT == true )
            {
                System.out.println( "Similarity\n" + simMatrix );
                System.out.println( "Identity\n"   + idnMatrix );

                for( int i = 0; i < alignmentList.size(); ++i )
                {
                    System.out.println( alignmentList.get( i ) );
                }
            }
        }
        catch( CompoundNotFoundError cnfe )
        {
            JOptionPane.showMessageDialog( null, "ERROR: Unrecognized sequence format! " +
                                                 "Input a valid FASTA sequence!\nDetails: " + cnfe );
        }
        catch( Exception ex )
        {
            JOptionPane.showMessageDialog( null, "ERROR: I/O failure!\nDetails: " + ex );
        }
    }

    public static void generateMatricesForProtein( final String sequences,
                                                   final short  firstGap,
                                                   final short  extendingGap,
                                                   final int    matrixType )
    {
        InputStream is = new ByteArrayInputStream( sequences.getBytes() );
        SimpleGapPenalty gaps = new SimpleGapPenalty( firstGap, extendingGap );

        try
        {
            LinkedHashMap<String, ProteinSequence> ProteinMap;
            ProteinMap = FastaReaderHelper.readFastaProteinSequence( is );

            SequencePair<ProteinSequence, AminoAcidCompound> pair;
            SubstitutionMatrix<AminoAcidCompound> matrix;
            matrix = getProteinScoringMatrix( matrixType );
            double similarity, identity;

            String simMatrix = "";
            String idnMatrix = "";
            Alignment alignment;

            alignmentList = new ArrayList<>();

            for( Entry<String, ProteinSequence> seq_i : ProteinMap.entrySet() )
            {
                simMatrix += seq_i.getValue().getOriginalHeader() + "\t";
                idnMatrix += seq_i.getValue().getOriginalHeader() + "\t";

                for( Entry<String, ProteinSequence> seq_j : ProteinMap.entrySet() )
                {
                    if( seq_i.getKey().equals( seq_j.getKey() ) )
                    {
                        simMatrix += "100.0%";
                        idnMatrix += "100.0%";
                        break;
                    }

                    pair = Alignments.getPairwiseAlignment( seq_i.getValue(),
                                                            seq_j.getValue(),
                                                            Alignments.PairwiseSequenceAlignerType.GLOBAL,
                                                            gaps,
                                                            matrix );

                    similarity = getSimilarity( pair.getNumSimilars(), pair.getLength() );
                    identity   = getIdentity( pair.getNumIdenticals(), pair.getLength() );

                    simMatrix += similarity + "%\t";
                    idnMatrix += identity   + "%\t";

                    alignment = new Alignment( pair.getQuery().getAccession().toString(),
                                                    pair.getTarget().getAccession().toString(),
                                                    similarity + "%",
                                                    identity + "%",
                                                    Defaults.ALIGNMENT_TYPE,
                                                    pair.toString() );
                    alignmentList.add( alignment );
                }
                simMatrix += "\n";
                idnMatrix += "\n";
            }

            similarityMatrix = simMatrix;
            identityMatrix   = idnMatrix;

            if( Defaults.CONSOLE_OUTPUT == true )
            {
                System.out.println( "Similarity\n" + simMatrix );
                System.out.println( "Identity\n"   + idnMatrix );

                for( int i = 0; i < alignmentList.size(); ++i )
                {
                    System.out.println( alignmentList.get( i ) );
                }
            }
        }
        catch( CompoundNotFoundError cnfe )
        {
            JOptionPane.showMessageDialog( null, "ERROR: Unrecognized sequence format! Input a valid FASTA sequence!"
                                                    + "\nDetails: " + cnfe );
        }
        catch( Exception ex )
        {
            JOptionPane.showMessageDialog( null, "ERROR: I/O failure!\nDetails: " + ex );
        }
    }

    public static final String getTitle( final int index )
    {
        return alignmentList.get( index ).getTitle();
    }

    public static final String getAlignmentDetails( final int index )
    {
        return alignmentList.get( index ).toString();
    }

    public static final String getAlignmentAsString( final int index )
    {
        return alignmentList.get( index ).getAlignmentAsString();
    }

    private static boolean isSimilarity( final int matrixType )
    {
        return ( matrixType == Defaults.SIMILARITY );
    }

    private static boolean isIdentity( final int matrixType )
    {
        return ( matrixType == Defaults.IDENTITY );
    }

    private static double getSimilarity( final int similars, final int length )
    {
        return roundOff( 100.0 * similars / length );
    }

    private static double getIdentity( final int identicals, final int length )
    {
        return roundOff( 100.0 * identicals / length );
    }

    private static double roundOff( final double value )
    {
        return Math.round( value * Defaults.PLACES ) / Defaults.PLACES;
    }

    private static SubstitutionMatrix<AminoAcidCompound> getProteinScoringMatrix( final int matrixType )
    {
        switch( matrixType )
        {
            case  0: return SubstitutionMatrixHelper.getPAM250();
            case  1: return SubstitutionMatrixHelper.getGonnet250();
            case  2: return SubstitutionMatrixHelper.getBlosum30();
            case  3: return SubstitutionMatrixHelper.getBlosum35();
            case  4: return SubstitutionMatrixHelper.getBlosum40();
            case  5: return SubstitutionMatrixHelper.getBlosum45();
            case  6: return SubstitutionMatrixHelper.getBlosum50();
            case  7: return SubstitutionMatrixHelper.getBlosum55();
            case  8: return SubstitutionMatrixHelper.getBlosum60();
            case  9: return SubstitutionMatrixHelper.getBlosum62();
            case 10: return SubstitutionMatrixHelper.getBlosum65();
            case 11: return SubstitutionMatrixHelper.getBlosum70();
            case 12: return SubstitutionMatrixHelper.getBlosum75();
            case 13: return SubstitutionMatrixHelper.getBlosum80();
            case 14: return SubstitutionMatrixHelper.getBlosum85();
            case 15: return SubstitutionMatrixHelper.getBlosum90();
            case 16: return SubstitutionMatrixHelper.getBlosum100();
        }
        
        return null;
    }

    public static String getProteinScoringMatrixAsString( final int matrixType )
    {
        return getProteinScoringMatrix( matrixType ).getMatrixAsString();
    }
}
