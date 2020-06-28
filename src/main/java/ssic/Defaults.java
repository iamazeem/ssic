package ssic;

import java.awt.Color;

public final class Defaults
{
    public static final boolean CONSOLE_OUTPUT = false;
    public static final int STARTUP_DELAY_MS = 3000;

    public static final String SPLASH  = "Splash.png";

    public static final String WIN_ICON  = "Icon.png";
    public static final String WIN_TITLE = "SSIC 1.0.0 [Developed by: AZEEM]";
    public static final int WIN_WIDTH    = 800;
    public static final int WIN_HEIGHT   = 600;

    public static final String DEFAULT_STATUS = "Welcome to SSIC - A sequence alignment tool";
    public static final String NEW_LINE = System.getProperty( "line.separator" );

    public static final String ALIGNMENT_TYPE = "GLOBAL";

    public static final int DNA     = 0;
    public static final int PROTEIN = 1;

    public static final String[] PROTEIN_SCORING_MATRICES =
    {
        "PAM250",  "GONNET250",
        "BLOSUM30", "BLOSUM35",
        "BLOSUM40", "BLOSUM45",
        "BLOSUM50", "BLOSUM55",
        "BLOSUM60", "BLOSUM62", "BLOSUM65",
        "BLOSUM70", "BLOSUM75",
        "BLOSUM80", "BLOSUM85",
        "BLOSUM90", "BLOSUM100"
    };

    public static final String DNA_FIRST_GAP         = "16";
    public static final String DNA_EXTENDING_GAP     = "4";
    public static final String PROTEIN_FIRST_GAP     = "12";
    public static final String PROTEIN_EXTENDING_GAP = "2";

    public static final short  MIN_GAP = Short.MIN_VALUE;
    public static final short  MAX_GAP = Short.MAX_VALUE;

    public static final String DNA_REGEX     = "[^GATC^gatc]";
    public static final String PROTEIN_REGEX = "[^ARNDCQEGHILKMFPSTWYV^arndcqeghilkmfpstwyv]";

    public static final Color FG_MATRIX   = Color.WHITE;
    public static final Color BG_MATRIX   = Color.DARK_GRAY;
    public static final Color FG_DIAGONAL = Color.DARK_GRAY;
    public static final Color BG_DIAGONAL = Color.WHITE;

    public static final int SIMILARITY = 0;
    public static final int IDENTITY   = 1;
    public static final int MATRIX_ON  = 0;
    public static final int MATRIX_OFF = 1;

    public static final double DECIMAL_PLACES = 2;
    public static final double PLACES = Math.pow(10, DECIMAL_PLACES );
}
