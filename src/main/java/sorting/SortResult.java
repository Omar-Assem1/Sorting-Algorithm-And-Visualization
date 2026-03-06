package sorting;

public class SortResult {
    public final String algorithmName;
    public final int    arraySize;
    public final String arrayType;
    public final long   runtimeNs;
    public final long   comparisons;
    public final long   interchanges;

    public SortResult(String algorithmName, int arraySize, String arrayType,
                      long runtimeNs, long comparisons, long interchanges) {
        this.algorithmName = algorithmName;
        this.arraySize     = arraySize;
        this.arrayType     = arrayType;
        this.runtimeNs     = runtimeNs;
        this.comparisons   = comparisons;
        this.interchanges  = interchanges;
    }

    public double getRuntimeMs() {
        return runtimeNs / 1_000_000.0;
    }
}
