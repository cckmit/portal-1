package ru.protei.portal.core.service.template.htmldiff;

public class MatchOptions {

    /**
     * Match granularity, defines how many words are joined into single block
     */
    private int blockSize;
    private double repeatingWordsAccuracy;
    private boolean ignoreWhitespaceDifferences;

    public MatchOptions(int blockSize, double repeatingWordsAccuracy, boolean ignoreWhitespaceDifferences) {
        this.blockSize = blockSize;
        this.repeatingWordsAccuracy = repeatingWordsAccuracy;
        this.ignoreWhitespaceDifferences = ignoreWhitespaceDifferences;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public double getRepeatingWordsAccuracy() {
        return repeatingWordsAccuracy;
    }

    public void setRepeatingWordsAccuracy(double repeatingWordsAccuracy) {
        this.repeatingWordsAccuracy = repeatingWordsAccuracy;
    }

    public boolean isIgnoreWhitespaceDifferences() {
        return ignoreWhitespaceDifferences;
    }

    public void setIgnoreWhitespaceDifferences(boolean ignoreWhitespaceDifferences) {
        this.ignoreWhitespaceDifferences = ignoreWhitespaceDifferences;
    }
}
