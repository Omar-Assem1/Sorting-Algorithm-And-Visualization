package sorting;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ComparisonController {

    @FXML private Spinner<Integer> spnSize;
    @FXML private Spinner<Integer> spnRuns;
    @FXML private CheckBox chkRandom;
    @FXML private CheckBox chkSorted;
    @FXML private CheckBox chkInverse;
    @FXML private Button btnAddFiles;
    @FXML private Button btnClear;
    @FXML private Button btnRun;
    @FXML private Button btnExport;
    @FXML private ListView<String> fileListView;
    @FXML private TableView<ResultRow> resultsTable;
    @FXML private TableColumn<ResultRow, String> colAlgorithm;
    @FXML private TableColumn<ResultRow, Integer> colArraySize;
    @FXML private TableColumn<ResultRow, String> colArrayType;
    @FXML private TableColumn<ResultRow, Integer> colRuns;
    @FXML private TableColumn<ResultRow, String> colAvg;
    @FXML private TableColumn<ResultRow, String> colMin;
    @FXML private TableColumn<ResultRow, String> colMax;
    @FXML private TableColumn<ResultRow, String> colComparisons;
    @FXML private TableColumn<ResultRow, String> colInterchanges;
    @FXML private Label lblStatus;

    private ObservableList<String> fileList = FXCollections.observableArrayList();
    private List<File> inputFiles = new ArrayList<>();
    private ObservableList<ResultRow> results = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        fileListView.setItems(fileList);
        resultsTable.setItems(results);
        
        colAlgorithm.setCellValueFactory(new PropertyValueFactory<>("algorithmName"));
        colArraySize.setCellValueFactory(new PropertyValueFactory<>("arraySize"));
        colArrayType.setCellValueFactory(new PropertyValueFactory<>("arrayType"));
        colRuns.setCellValueFactory(new PropertyValueFactory<>("runs"));
        colAvg.setCellValueFactory(new PropertyValueFactory<>("avgRuntimeMs"));
        colMin.setCellValueFactory(new PropertyValueFactory<>("minRuntimeMs"));
        colMax.setCellValueFactory(new PropertyValueFactory<>("maxRuntimeMs"));
        colComparisons.setCellValueFactory(new PropertyValueFactory<>("comparisons"));
        colInterchanges.setCellValueFactory(new PropertyValueFactory<>("interchanges"));
        
        spnSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 10000, 1000, 100));
        spnRuns.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 3, 1));
    }

    @FXML
    private void handleAddFiles() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Input Files");
        List<File> files = fc.showOpenMultipleDialog(btnAddFiles.getScene().getWindow());
        if (files != null) {
            for (File f : files) {
                if (!inputFiles.contains(f)) {
                    inputFiles.add(f);
                    fileList.add(f.getName());
                }
            }
        }
    }

    @FXML
    private void handleClearFiles() {
        inputFiles.clear();
        fileList.clear();
    }

    @FXML
    private void handleRunComparison() {
        btnRun.setDisable(true);
        lblStatus.setText("Running...");
        results.clear();

        int size = spnSize.getValue();
        int runs = spnRuns.getValue();

        Task<List<ComparisonEngine.AggregatedResult>> task = new Task<>() {
            @Override
            protected List<ComparisonEngine.AggregatedResult> call() {
                List<ComparisonEngine.AggregatedResult> all = new ArrayList<>();
                
                if (chkRandom.isSelected())
                    all.addAll(ComparisonEngine.runAll(
                            ArrayFactory.generate(ArrayFactory.Type.RANDOM, size),
                            ArrayFactory.label(ArrayFactory.Type.RANDOM), runs));
                if (chkSorted.isSelected())
                    all.addAll(ComparisonEngine.runAll(
                            ArrayFactory.generate(ArrayFactory.Type.SORTED, size),
                            ArrayFactory.label(ArrayFactory.Type.SORTED), runs));
                if (chkInverse.isSelected())
                    all.addAll(ComparisonEngine.runAll(
                            ArrayFactory.generate(ArrayFactory.Type.INVERSELY_SORTED, size),
                            ArrayFactory.label(ArrayFactory.Type.INVERSELY_SORTED), runs));
                
                for (File f : inputFiles) {
                    try {
                        int[] arr = ArrayFactory.fromFile(f);
                        all.addAll(ComparisonEngine.runAll(arr, f.getName(), runs));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return all;
            }
        };

        task.setOnSucceeded(e -> {
            List<ComparisonEngine.AggregatedResult> resultList = task.getValue();
            for (ComparisonEngine.AggregatedResult r : resultList) {
                results.add(new ResultRow(r));
            }
            lblStatus.setText("Done – " + resultList.size() + " rows generated.");
            btnRun.setDisable(false);
        });

        task.setOnFailed(e -> {
            lblStatus.setText("Error: " + task.getException().getMessage());
            btnRun.setDisable(false);
        });

        new Thread(task).start();
    }

    @FXML
    private void handleExportCSV() {
        if (results.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Export Error");
            alert.setHeaderText(null);
            alert.setContentText("No data to export. Run a comparison first.");
            alert.showAndWait();
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Save CSV File");
        fc.setInitialFileName("sorting_results.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showSaveDialog(btnExport.getScene().getWindow());

        if (file != null) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("Algorithm,Array Size,Array Type,Runs,Avg (ms),Min (ms),Max (ms),Comparisons,Interchanges");
                
                for (ResultRow row : results) {
                    pw.printf("%s,%d,%s,%d,%s,%s,%s,%s,%s%n",
                            row.getAlgorithmName(),
                            row.getArraySize(),
                            row.getArrayType(),
                            row.getRuns(),
                            row.getAvgRuntimeMs().replace(",", ""),
                            row.getMinRuntimeMs().replace(",", ""),
                            row.getMaxRuntimeMs().replace(",", ""),
                            row.getComparisons().replace(",", ""),
                            row.getInterchanges().replace(",", ""));
                }
                
                lblStatus.setText("Exported to: " + file.getAbsolutePath());
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Success");
                alert.setHeaderText(null);
                alert.setContentText("Data exported successfully to:\n" + file.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception ex) {
                lblStatus.setText("Export failed: " + ex.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Export Error");
                alert.setHeaderText(null);
                alert.setContentText("Error exporting CSV: " + ex.getMessage());
                alert.showAndWait();
            }
        }
    }

    public static class ResultRow {
        private final String algorithmName;
        private final int arraySize;
        private final String arrayType;
        private final int runs;
        private final String avgRuntimeMs;
        private final String minRuntimeMs;
        private final String maxRuntimeMs;
        private final String comparisons;
        private final String interchanges;

        public ResultRow(ComparisonEngine.AggregatedResult r) {
            this.algorithmName = r.algorithmName;
            this.arraySize = r.arraySize;
            this.arrayType = r.arrayType;
            this.runs = r.runs;
            this.avgRuntimeMs = String.format("%.4f", r.avgRuntimeMs);
            this.minRuntimeMs = String.format("%.4f", r.minRuntimeMs);
            this.maxRuntimeMs = String.format("%.4f", r.maxRuntimeMs);
            this.comparisons = String.format("%,d", r.comparisons);
            this.interchanges = String.format("%,d", r.interchanges);
        }

        public String getAlgorithmName() { return algorithmName; }
        public int getArraySize() { return arraySize; }
        public String getArrayType() { return arrayType; }
        public int getRuns() { return runs; }
        public String getAvgRuntimeMs() { return avgRuntimeMs; }
        public String getMinRuntimeMs() { return minRuntimeMs; }
        public String getMaxRuntimeMs() { return maxRuntimeMs; }
        public String getComparisons() { return comparisons; }
        public String getInterchanges() { return interchanges; }
    }
}
