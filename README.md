# Sorting Algorithm Analyser — JavaFX

A JavaFX desktop app to compare and visualise 6 sorting algorithms.

---

## Requirements

- **Java 11–21** (Java 17 or 21 recommended)
- **Internet connection** on first run (Gradle downloads JavaFX automatically)

---

## How to Run (3 steps)

### Linux / macOS
```bash
# Step 1 — enter the project folder
cd SortingFX

# Step 2 — make gradlew executable
chmod +x gradlew

# Step 3 — run (downloads dependencies automatically on first run)
./gradlew run
```

### Windows
```cmd
cd SortingFX
gradlew.bat run
```

### If you don't have Gradle wrapper (gradlew missing):
```bash
# Install Gradle manually then run:
gradle run
```

Or open the project in **IntelliJ IDEA**:
1. File → Open → select the `SortingFX` folder
2. IntelliJ detects the `build.gradle` automatically
3. In Gradle panel (right side) → Tasks → application → **run**
4. Make sure **Gradle JVM** is set to Java 17 or 21:
   File → Settings → Build Tools → Gradle → Gradle JVM

---

## IntelliJ Setup (Important)

If you see "JavaFX runtime components are missing":
1. **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
2. Set **Gradle JVM** to Java 17 or Java 21 (NOT Java 24/25)
3. Click OK and re-sync

---

## Features

### Tab 1 — Sorting Comparison
- Choose array size (up to 10,000)
- Choose number of runs for timing accuracy
- Select Random / Sorted / Inversely Sorted arrays
- Load custom integer files (comma-separated .txt)
- Results table: Avg/Min/Max runtime, Comparisons, Interchanges
- Export results to CSV

### Tab 2 — Sorting Visualisation
- Animated bar chart (up to 100 elements)
- Step-by-step or auto-play with speed control
- Colour coding: 🔵 unsorted · 🔴 compare A · 🟡 compare B · 🟢 done
- Live comparison and interchange counters

---

## Algorithms

| Algorithm      | Best      | Average    | Worst      |
|----------------|-----------|------------|------------|
| Selection Sort | O(n²)     | O(n²)      | O(n²)      |
| Insertion Sort | O(n)      | O(n²)      | O(n²)      |
| Bubble Sort    | O(n)      | O(n²)      | O(n²)      |
| Merge Sort     | O(n log n)| O(n log n) | O(n log n) |
| Heap Sort      | O(n log n)| O(n log n) | O(n log n) |
| Quick Sort     | O(n log n)| O(n log n) | O(n²)      |

Quick Sort uses **median-of-three** pivot selection.

---

## Project Structure

```
SortingFX/
├── src/main/java/sorting/
│   ├── SortingApp.java           ← Entry point
│   ├── Sorter.java               ← Abstract base (counters)
│   ├── Algorithms.java           ← All 6 sort implementations
│   ├── VisualizationSorter.java  ← Step-recording versions
│   ├── ArrayFactory.java         ← Array generation + file loading
│   ├── ComparisonEngine.java     ← Multi-run aggregation
│   ├── ComparisonController.java ← Tab 1 logic
│   ├── VisualizationController.java ← Tab 2 logic
│   ├── MainController.java       ← Tab pane controller
│   └── SortResult.java           ← Data holder
├── src/main/resources/sorting/
│   ├── MainView.fxml
│   ├── ComparisonView.fxml
│   ├── VisualizationView.fxml
│   └── styles.css
├── build.gradle
├── settings.gradle
└── gradle/wrapper/
    └── gradle-wrapper.properties
```
