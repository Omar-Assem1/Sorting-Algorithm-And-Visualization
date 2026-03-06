package sorting;

class SelectionSorter extends Sorter {
    @Override public String getName() { return "Selection Sort"; }
    @Override protected void doSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++)
                if (less(arr[j], arr[minIdx])) minIdx = j;
            if (minIdx != i) swap(arr, i, minIdx);
        }
    }
}

class InsertionSorter extends Sorter {
    @Override public String getName() { return "Insertion Sort"; }
    @Override protected void doSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i], j = i - 1;
            while (j >= 0 && less(key, arr[j])) {
                arr[j + 1] = arr[j];
                interchanges++;
                j--;
            }
            arr[j + 1] = key;
        }
    }
}

class BubbleSorter extends Sorter {
    @Override public String getName() { return "Bubble Sort"; }
    @Override protected void doSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (less(arr[j + 1], arr[j])) { swap(arr, j, j + 1); swapped = true; }
            }
            if (!swapped) break;
        }
    }
}

class MergeSorter extends Sorter {
    @Override public String getName() { return "Merge Sort"; }
    @Override protected void doSort(int[] arr) { mergeSort(arr, 0, arr.length - 1); }
    private void mergeSort(int[] arr, int l, int r) {
        if (l >= r) return;
        int m = l + (r - l) / 2;
        mergeSort(arr, l, m);
        mergeSort(arr, m + 1, r);
        merge(arr, l, m, r);
    }
    private void merge(int[] arr, int l, int m, int r) {
        int n1 = m - l + 1, n2 = r - m;
        int[] L = new int[n1], R = new int[n2];
        System.arraycopy(arr, l, L, 0, n1);
        System.arraycopy(arr, m + 1, R, 0, n2);
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            if (lessOrEqual(L[i], R[j])) arr[k++] = L[i++];
            else { arr[k++] = R[j++]; interchanges++; }
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }
}

class HeapSorter extends Sorter {
    @Override public String getName() { return "Heap Sort"; }
    @Override protected void doSort(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapify(arr, n, i);
        for (int i = n - 1; i > 0; i--) { swap(arr, 0, i); heapify(arr, i, 0); }
    }
    private void heapify(int[] arr, int n, int i) {
        int largest = i, l = 2 * i + 1, r = 2 * i + 2;
        if (l < n && less(arr[largest], arr[l])) largest = l;
        if (r < n && less(arr[largest], arr[r])) largest = r;
        if (largest != i) { swap(arr, i, largest); heapify(arr, n, largest); }
    }
}

class QuickSorter extends Sorter {
    @Override public String getName() { return "Quick Sort"; }
    @Override protected void doSort(int[] arr) { quickSort(arr, 0, arr.length - 1); }
    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }
    private int partition(int[] arr, int low, int high) {
        int mid = low + (high - low) / 2;
        if (less(arr[mid], arr[low]))  swap(arr, low, mid);
        if (less(arr[high], arr[low])) swap(arr, low, high);
        if (less(arr[mid], arr[high])) swap(arr, mid, high);
        int pivot = arr[high], i = low - 1;
        for (int j = low; j < high; j++)
            if (lessOrEqual(arr[j], pivot)) { i++; swap(arr, i, j); }
        swap(arr, i + 1, high);
        return i + 1;
    }
}
