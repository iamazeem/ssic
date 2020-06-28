package ssic;

public final class Alignment
{
    private final String querySequence;
    private final String targetSequence;
    private final String similarity;
    private final String identity;
    private final String alignmentType;
    private final String alignmentAsString;

    public Alignment( final String _querySequence,
                      final String _targetSequence,
                      final String _similarity,
                      final String _identity,
                      final String _alignmentType,
                      final String _alignmentAsString )
    {
        querySequence     = _querySequence;
        targetSequence    = _targetSequence;
        similarity        = _similarity;
        identity          = _identity;
        alignmentType     = _alignmentType;
        alignmentAsString = _alignmentAsString;
    }

    public final String getTitle()
    {
        String s;
        s  = getQuerySequenceName()  + " vs. " + getTargetSequenceName();
        s += ", Similarity: " + getSimilarity();
        s += ", Identity: "   + getIdentity();
        s += ", Type: "       + getAlignmentType();
        return s;
    }

    public final String getQuerySequenceName()
    {
        return querySequence;
    }

    public final String getTargetSequenceName()
    {
        return targetSequence;
    }

    public final String getSimilarity()
    {
        return similarity;
    }

    public final String getIdentity()
    {
        return identity;
    }

    public final String getAlignmentType()
    {
        return alignmentType;
    }

    public final String getAlignmentAsString()
    {
        return alignmentAsString;
    }

    @Override
    public final String toString()
    {
        String s;
        s  = getQuerySequenceName() + " vs. " + getTargetSequenceName() + "\n\n";
        s += "Similarity: "         + getSimilarity()                   + "\n";
        s += "Identity  : "         + getIdentity()                     + "\n";
        s += "Align Type: "         + getAlignmentType()                + "\n\n";
        s += "Alignment :\n\n"      + getAlignmentAsString()            + "\n";
        return s;
    }
}
