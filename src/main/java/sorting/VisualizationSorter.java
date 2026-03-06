package sorting;

import java.util.ArrayList;
import java.util.List;

public class VisualizationSorter {

    public static class Step {
        public final int[]  state;
        public final int    indexA;
        public final int    indexB;
        public final long   comparisons;
        public final long   interchanges;
        public final String annotation;

        Step(int[] arr, int a, int b, long cmp, long swp, String note) {
            this.state        = arr.clone();
            this.indexA       = a;
            this.indexB       = b;
            this.comparisons  = cmp;
            this.interchanges = swp;
            this.annotation   = note;
        }
    }

    // shared counters (single-threaded use only)
    private static long cmp, swp;

    private static void reset()               { cmp = 0; swp = 0; }
    private static boolean less(int a, int b) { cmp++; return a < b; }
    private static boolean leq(int a, int b)  { cmp++; return a <= b; }
    private static void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t; swp++;
    }

    public static List<Step> getSteps(String algorithm, int[] source) {
        int[] arr = source.clone();
        switch (algorithm) {
            case "Selection Sort":  return selectionSteps(arr);
            case "Insertion Sort":  return insertionSteps(arr);
            case "Bubble Sort":     return bubbleSteps(arr);
            case "Merge Sort":      return mergeSteps(arr);
            case "Heap Sort":       return heapSteps(arr);
            case "Quick Sort":      return quickSteps(arr);
            default:                return new ArrayList<>();
        }
    }

    // ── Selection Sort ────────────────────────────────────────────────────────
    private static List<Step> selectionSteps(int[] arr) {
        reset();
        List<Step> steps = new ArrayList<>();
        int n = arr.length;
        steps.add(new Step(arr, -1, -1, cmp, swp, "Initial"));
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (less(arr[j], arr[minIdx])) minIdx = j;
                steps.add(new Step(arr, j, minIdx, cmp, swp, "Compare"));
            }
            if (minIdx != i) { swap(arr, i, minIdx); steps.add(new Step(arr, i, minIdx, cmp, swp, "Swap")); }
        }
        steps.add(new Step(arr, -1, -1, cmp, swp, "Done"));
        return steps;
    }

    // ── Insertion Sort ────────────────────────────────────────────────────────
    private static List<Step> insertionSteps(int[] arr) {
        reset();
        List<Step> steps = new ArrayList<>();
        int n = arr.length;
        steps.add(new Step(arr, -1, -1, cmp, swp, "Initial"));
        for (int i = 1; i < n; i++) {
            int key = arr[i], j = i - 1;
            while (j >= 0 && less(key, arr[j])) {
                arr[j + 1] = arr[j]; swp++;
                steps.add(new Step(arr, j, j + 1, cmp, swp, "Shift"));
                j--;
            }
            arr[j + 1] = key;
            steps.add(new Step(arr, j + 1, i, cmp, swp, "Insert"));
        }
        steps.add(new Step(arr, -1, -1, cmp, swp, "Done"));
        return steps;
    }

    // ── Bubble Sort ───────────────────────────────────────────────────────────
    private static List<Step> bubbleSteps(int[] arr) {
        reset();
        List<Step> steps = new ArrayList<>();
        int n = arr.length;
        steps.add(new Step(arr, -1, -1, cmp, swp, "Initial"));
        for (int i = 0; i < n - 1; i++) {
            boolean moved = false;
            for (int j = 0; j < n - i - 1; j++) {
                steps.add(new Step(arr, j, j + 1, cmp, swp, "Compare"));
                if (less(arr[j + 1], arr[j])) {
                    swap(arr, j, j + 1); moved = true;
                    steps.add(new Step(arr, j, j + 1, cmp, swp, "Swap"));
                }
            }
            if (!moved) break;
        }
        steps.add(new Step(arr, -1, -1, cmp, swp, "Done"));
        return steps;
    }

    // ── Merge Sort ────────────────────────────────────────────────────────────
    private static List<Step> mergeSteps(int[] arr) {
        reset();
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(arr, -1, -1, cmp, swp, "Initial"));
        mergeSortViz(arr, 0, arr.length - 1, steps);
        steps.add(new Step(arr, -1, -1, cmp, swp, "Done"));
        return steps;
    }

    private static void mergeSortViz(int[] arr, int l, int r, List<Step> steps) {
        if (l >= r) return;
        int m = l + (r - l) / 2;
        mergeSortViz(arr, l, m, steps);
        mergeSortViz(arr, m + 1, r, steps);
        mergeViz(arr, l, m, r, steps);
    }

    private static void mergeViz(int[] arr, int l, int m, int r, List<Step> steps) {
        int n1 = m - l + 1, n2 = r - m;
        int[] L = new int[n1], R = new int[n2];
        System.arraycopy(arr, l, L, 0, n1);
        System.arraycopy(arr, m + 1, R, 0, n2);
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            if (leq(L[i], R[j])) arr[k++] = L[i++];
            else { arr[k++] = R[j++]; swp++; }
            steps.add(new Step(arr, k - 1, -1, cmp, swp, "Merge"));
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
        steps.add(new Step(arr, l, r, cmp, swp, "Merged"));
    }

    // ── Heap Sort ─────────────────────────────────────────────────────────────
    private static List<Step> heapSteps(int[] arr) {
        reset();
        List<Step> steps = new ArrayList<>();
        int n = arr.length;
        steps.add(new Step(arr, -1, -1, cmp, swp, "Initial"));
        for (int i = n / 2 - 1; i >= 0; i--) heapifyViz(arr, n, i, steps);
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);
            steps.add(new Step(arr, 0, i, cmp, swp, "ExtractMax"));
            heapifyViz(arr, i, 0, steps);
        }
        steps.add(new Step(arr, -1, -1, cmp, swp, "Done"));
        return steps;
    }

    private static void heapifyViz(int[] arr, int n, int i, List<Step> steps) {
        int largest = i, l = 2 * i + 1, r = 2 * i + 2;
        if (l < n && less(arr[largest], arr[l])) largest = l;
        if (r < n && less(arr[largest], arr[r])) largest = r;
        if (largest != i) {
            swap(arr, i, largest);
            steps.add(new Step(arr, i, largest, cmp, swp, "Heapify"));
            heapifyViz(arr, n, largest, steps);
        }
    }

    // ── Quick Sort ────────────────────────────────────────────────────────────
    private static List<Step> quickSteps(int[] arr) {
        reset();
        List<Step> steps = new ArrayList<>();
        steps.add(new Step(arr, -1, -1, cmp, swp, "Initial"));
        quickSortViz(arr, 0, arr.length - 1, steps);
        steps.add(new Step(arr, -1, -1, cmp, swp, "Done"));
        return steps;
    }

    private static void quickSortViz(int[] arr, int low, int high, List<Step> steps) {
        if (low < high) {
            int pi = partitionViz(arr, low, high, steps);
            quickSortViz(arr, low, pi - 1, steps);
            quickSortViz(arr, pi + 1, high, steps);
        }
    }

    private static int partitionViz(int[] arr, int low, int high, List<Step> steps) {
        int mid = low + (high - low) / 2;
        if (less(arr[mid], arr[low]))  swap(arr, low, mid);
        if (less(arr[high], arr[low])) swap(arr, low, high);
        if (less(arr[mid], arr[high])) swap(arr, mid, high);
        int pivot = arr[high], i = low - 1;
        for (int j = low; j < high; j++) {
            steps.add(new Step(arr, j, high, cmp, swp, "Compare"));
            if (leq(arr[j], pivot)) {
                i++; swap(arr, i, j);
                steps.add(new Step(arr, i, j, cmp, swp, "Swap"));
            }
        }
        swap(arr, i + 1, high);
        steps.add(new Step(arr, i + 1, high, cmp, swp, "PlacePivot"));
        return i + 1;
    }
}
