package sorting;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VisualizationController {

    @FXML private ComboBox<String>  cmbAlgo;
    @FXML private ComboBox<String>  cmbType;
    @FXML private Spinner<Integer>  spnSize;
    @FXML private Slider            sldSpeed;
    @FXML private Button            btnLoad;
    @FXML private Button            btnPlay;
    @FXML private Button            btnStep;
    @FXML private Button            btnStop;
    @FXML private Canvas            vizCanvas;
    @FXML private Label             lblProgress;
    @FXML private Label             lblStats;

    private static final String[] ALGOS = {
        "Selection Sort","Insertion Sort","Bubble Sort",
        "Merge Sort","Heap Sort","Quick Sort"
    };

    private List<VisualizationSorter.Step> steps;
    private int     currentStep = 0;
    private File    chosenFile  = null;
    private AnimationTimer timer;
    private long    lastUpdate  = 0;

    @FXML
    public void initialize() {
        cmbAlgo.setItems(FXCollections.observableArrayList(ALGOS));
        cmbAlgo.getSelectionModel().selectFirst();
        cmbType.setItems(FXCollections.observableArrayList("Random","Sorted","Inversely Sorted","From File..."));
        cmbType.getSelectionModel().selectFirst();
        spnSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 1000, 30, 5));
        sldSpeed.setMin(1); sldSpeed.setMax(300); sldSpeed.setValue(60);

        cmbType.setOnAction(e -> {
            if ("From File...".equals(cmbType.getValue())) {
                FileChooser fc = new FileChooser();
                File f = fc.showOpenDialog(cmbType.getScene().getWindow());
                if (f != null) chosenFile = f;
                else cmbType.getSelectionModel().select(0);
            }
        });

        StackPane parent = (StackPane) vizCanvas.getParent();
        vizCanvas.widthProperty().bind(parent.widthProperty());
        vizCanvas.heightProperty().bind(parent.heightProperty());
        vizCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawStep());
        vizCanvas.heightProperty().addListener((obs, oldVal, newVal) -> drawStep());

        setButtonStates(false, false);
    }

    @FXML private void handleLoad() {
        stopTimer();
        int size = Math.min(spnSize.getValue(), 1000);
        int[] arr;
        try {
            switch (cmbType.getValue()) {
                case "Sorted":           arr = ArrayFactory.generate(ArrayFactory.Type.SORTED, size); break;
                case "Inversely Sorted": arr = ArrayFactory.generate(ArrayFactory.Type.INVERSELY_SORTED, size); break;
                case "From File...":
                    if (chosenFile == null) { showAlert("No file selected."); return; }
                    int[] raw = ArrayFactory.fromFile(chosenFile);
                    arr = raw.length > 1000 ? java.util.Arrays.copyOf(raw, 1000) : raw;
                    break;
                default:                 arr = ArrayFactory.generate(ArrayFactory.Type.RANDOM, size);
            }
        } catch (Exception ex) { showAlert("Error: " + ex.getMessage()); return; }

        steps = VisualizationSorter.getSteps(cmbAlgo.getValue(), arr);
        currentStep = 0;
        updateProgress();
        drawStep();
        setButtonStates(true, false);
    }

    @FXML private void handlePlay() {
        if (steps == null) return;
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                long delay = (long)(1_000_000_000.0 / sldSpeed.getValue());
                if (now - lastUpdate >= delay) {
                    lastUpdate = now;
                    if (!advance()) stopTimer();
                }
            }
        };
        lastUpdate = System.nanoTime();
        timer.start();
        setButtonStates(true, true);
    }

    @FXML private void handleStep() { advance(); }

    @FXML private void handleStop() { stopTimer(); }

    private boolean advance() {
        if (steps == null || currentStep >= steps.size() - 1) {
            setButtonStates(false, false);
            return false;
        }
        currentStep++;
        updateProgress();
        drawStep();
        if (currentStep >= steps.size() - 1) {
            setButtonStates(false, false);
            return false;
        }
        return true;
    }

    private void stopTimer() {
        if (timer != null) { timer.stop(); timer = null; }
        boolean hasMore = steps != null && currentStep < steps.size() - 1;
        setButtonStates(hasMore, false);
    }

    private void drawStep() {
        if (steps == null || steps.isEmpty()) return;
        VisualizationSorter.Step step = steps.get(currentStep);
        int[] arr = step.state;
        int n = arr.length;

        GraphicsContext gc = vizCanvas.getGraphicsContext2D();
        double w = vizCanvas.getWidth();
        double h = vizCanvas.getHeight();

        // background
        gc.setFill(Color.rgb(15, 15, 25));
        gc.fillRect(0, 0, w, h);

        int maxVal = 1;
        for (int v : arr) if (v > maxVal) maxVal = v;

        double barW = w / n;
        double vizH = h;
        boolean isDone = currentStep == steps.size() - 1;

        for (int i = 0; i < n; i++) {
            double barH = ((double) arr[i] / maxVal) * vizH * 0.92;
            double x = i * barW;
            double y = vizH - barH;

            if (isDone)                gc.setFill(Color.rgb(80, 220, 120));
            else if (i == step.indexA) gc.setFill(Color.rgb(255, 80, 80));
            else if (i == step.indexB) gc.setFill(Color.rgb(255, 210, 50));
            else                       gc.setFill(Color.rgb(80, 160, 255));

            gc.fillRect(x + 1, y, Math.max(1, barW - 2), barH);
        }

        lblStats.setText(String.format("Comparisons: %,d   |   Interchanges: %,d   |   Action: %s",
                step.comparisons, step.interchanges, step.annotation));
    }

    private void updateProgress() {
        if (steps == null) return;
        lblProgress.setText(String.format("Step %d / %d", currentStep, steps.size() - 1));
    }

    private void setButtonStates(boolean canPlay, boolean isPlaying) {
        btnPlay.setDisable(!canPlay || isPlaying);
        btnStep.setDisable(!canPlay || isPlaying);
        btnStop.setDisable(!isPlaying);
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setContentText(msg);
        a.showAndWait();
    }
}
