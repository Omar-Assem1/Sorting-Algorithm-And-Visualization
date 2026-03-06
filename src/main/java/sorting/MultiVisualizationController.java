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

public class MultiVisualizationController {

    @FXML private ComboBox<String>  cmbType;
    @FXML private Spinner<Integer>  spnSize;
    @FXML private Slider            sldSpeed;
    @FXML private Button            btnLoad;
    @FXML private Button            btnPlay;
    @FXML private Button            btnStep;
    @FXML private Button            btnStop;
    @FXML private Canvas            canvas1;
    @FXML private Canvas            canvas2;
    @FXML private Canvas            canvas3;
    @FXML private Canvas            canvas4;
    @FXML private Canvas            canvas5;
    @FXML private Canvas            canvas6;
    @FXML private Label             lblProgress;

    private static final String[] ALGOS = {
        "Selection Sort","Insertion Sort","Bubble Sort",
        "Merge Sort","Heap Sort","Quick Sort"
    };

    private List<List<VisualizationSorter.Step>> allSteps;
    private Canvas[] canvases;
    private int     currentStep = 0;
    private File    chosenFile  = null;
    private AnimationTimer timer;
    private long    lastUpdate  = 0;

    @FXML
    public void initialize() {
        canvases = new Canvas[]{canvas1, canvas2, canvas3, canvas4, canvas5, canvas6};
        
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

        for (Canvas canvas : canvases) {
            StackPane parent = (StackPane) canvas.getParent();
            canvas.widthProperty().bind(parent.widthProperty());
            canvas.heightProperty().bind(parent.heightProperty());
            canvas.widthProperty().addListener((obs, oldVal, newVal) -> drawAllSteps());
            canvas.heightProperty().addListener((obs, oldVal, newVal) -> drawAllSteps());
        }

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

        allSteps = new ArrayList<>();
        for (String algo : ALGOS) {
            allSteps.add(VisualizationSorter.getSteps(algo, arr));
        }
        
        currentStep = 0;
        updateProgress();
        drawAllSteps();
        setButtonStates(true, false);
    }

    @FXML private void handlePlay() {
        if (allSteps == null) return;
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
        if (allSteps == null || allSteps.isEmpty()) {
            setButtonStates(false, false);
            return false;
        }
        
        int maxSteps = 0;
        for (List<VisualizationSorter.Step> steps : allSteps) {
            if (steps.size() > maxSteps) maxSteps = steps.size();
        }
        
        if (currentStep >= maxSteps - 1) {
            setButtonStates(false, false);
            return false;
        }
        
        currentStep++;
        updateProgress();
        drawAllSteps();
        
        if (currentStep >= maxSteps - 1) {
            setButtonStates(false, false);
            return false;
        }
        return true;
    }

    private void stopTimer() {
        if (timer != null) { timer.stop(); timer = null; }
        if (allSteps == null || allSteps.isEmpty()) {
            setButtonStates(false, false);
            return;
        }
        
        int maxSteps = 0;
        for (List<VisualizationSorter.Step> steps : allSteps) {
            if (steps.size() > maxSteps) maxSteps = steps.size();
        }
        
        boolean hasMore = currentStep < maxSteps - 1;
        setButtonStates(hasMore, false);
    }

    private void drawAllSteps() {
        if (allSteps == null || allSteps.isEmpty()) return;
        
        for (int i = 0; i < ALGOS.length && i < canvases.length; i++) {
            drawStep(canvases[i], allSteps.get(i), ALGOS[i]);
        }
    }

    private void drawStep(Canvas canvas, List<VisualizationSorter.Step> steps, String algoName) {
        if (steps == null || steps.isEmpty()) return;
        
        int stepIndex = Math.min(currentStep, steps.size() - 1);
        VisualizationSorter.Step step = steps.get(stepIndex);
        int[] arr = step.state;
        int n = arr.length;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.setFill(Color.rgb(15, 15, 25));
        gc.fillRect(0, 0, w, h);

        gc.setFill(Color.WHITE);
        gc.fillText(algoName, 5, 15);
        gc.fillText(String.format("Cmp: %,d  Swp: %,d", step.comparisons, step.interchanges), 5, h - 5);

        int maxVal = 1;
        for (int v : arr) if (v > maxVal) maxVal = v;

        double barW = w / n;
        double vizH = h - 30;
        boolean isDone = stepIndex == steps.size() - 1;

        for (int i = 0; i < n; i++) {
            double barH = ((double) arr[i] / maxVal) * vizH * 0.85;
            double x = i * barW;
            double y = h - barH - 20;

            if (isDone)                gc.setFill(Color.rgb(80, 220, 120));
            else if (i == step.indexA) gc.setFill(Color.rgb(255, 80, 80));
            else if (i == step.indexB) gc.setFill(Color.rgb(255, 210, 50));
            else                       gc.setFill(Color.rgb(80, 160, 255));

            gc.fillRect(x + 1, y, Math.max(1, barW - 2), barH);
        }
    }

    private void updateProgress() {
        if (allSteps == null || allSteps.isEmpty()) return;
        
        int maxSteps = 0;
        for (List<VisualizationSorter.Step> steps : allSteps) {
            if (steps.size() > maxSteps) maxSteps = steps.size();
        }
        
        lblProgress.setText(String.format("Step %d / %d", currentStep, maxSteps - 1));
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
