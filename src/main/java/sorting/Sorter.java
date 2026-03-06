package sorting;

public abstract class Sorter {

    protected long comparisons;
    protected long interchanges;

    public abstract String getName();

    public SortResult sort(int[] original, String arrayType) {
        int[] arr    = original.clone();
        comparisons  = 0;
        interchanges = 0;
        long start   = System.nanoTime();
        doSort(arr);
        long elapsed = System.nanoTime() - start;
        return new SortResult(getName(), arr.length, arrayType, elapsed, comparisons, interchanges);
    }

    protected abstract void doSort(int[] arr);

    protected boolean less(int a, int b) {
        comparisons++;
        return a < b;
    }

    protected boolean lessOrEqual(int a, int b) {
        comparisons++;
        return a <= b;
    }

    protected void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i]  = arr[j];
        arr[j]  = tmp;
        interchanges++;
    }
}
