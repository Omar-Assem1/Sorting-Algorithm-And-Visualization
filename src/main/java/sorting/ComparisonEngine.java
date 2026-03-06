package sorting;

import java.util.*;

public class ComparisonEngine {

    public static final List<Sorter> ALL_SORTERS = Arrays.asList(
            new SelectionSorter(),
            new InsertionSorter(),
            new BubbleSorter(),
            new MergeSorter(),
            new HeapSorter(),
            new QuickSorter()
    );

    public static class AggregatedResult {
        public final String algorithmName;
        public final int    arraySize;
        public final String arrayType;
        public final int    runs;
        public final double avgRuntimeMs;
        public final double minRuntimeMs;
        public final double maxRuntimeMs;
        public final long   comparisons;
        public final long   interchanges;

        AggregatedResult(String algo, int size, String type, int runs,
                         double avg, double min, double max, long cmp, long swp) {
            this.algorithmName = algo;
            this.arraySize     = size;
            this.arrayType     = type;
            this.runs          = runs;
            this.avgRuntimeMs  = avg;
            this.minRuntimeMs  = min;
            this.maxRuntimeMs  = max;
            this.comparisons   = cmp;
            this.interchanges  = swp;
        }
    }

    public static List<AggregatedResult> runAll(int[] source, String arrayType, int numRuns) {
        List<AggregatedResult> results = new ArrayList<>();
        for (Sorter sorter : ALL_SORTERS)
            results.add(runOne(sorter, source, arrayType, numRuns));
        return results;
    }

    public static AggregatedResult runOne(Sorter sorter, int[] source, String arrayType, int numRuns) {
        double min = Double.MAX_VALUE, max = 0, total = 0;
        long lastCmp = 0, lastSwp = 0;
        for (int r = 0; r < numRuns; r++) {
            SortResult sr = sorter.sort(source, arrayType);
            double ms = sr.getRuntimeMs();
            total += ms;
            if (ms < min) min = ms;
            if (ms > max) max = ms;
            lastCmp = sr.comparisons;
            lastSwp = sr.interchanges;
        }
        return new AggregatedResult(sorter.getName(), source.length, arrayType,
                numRuns, total / numRuns, min, max, lastCmp, lastSwp);
    }
}
