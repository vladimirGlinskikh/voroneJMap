package panel;

import algorithms.PathFinder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import entity.Node;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import parser.DataParser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller implements Initializable, ChangeListener<Object> {

    @FXML
    private JFXButton initializeBtn, startAStarBtn, startBfsBtn;

    @FXML
    private JFXComboBox<String> nodeListComboBox;

    @FXML
    private JFXComboBox<String> nodeListComboBox1;

    @FXML
    private JFXSlider delaySlideBar, graphFontSizeSlideBar;

    private final ExecutorService threadPool = Executors.newWorkStealingPool();
    private PathFinder pathFinder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        delaySlideBar.valueProperty().addListener(this);
        graphFontSizeSlideBar.valueProperty().addListener(this);
        disableButtons(true);
    }

    public void initializeGraphPane() {
        initializeBtn.setDisable(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Выберите файл, который содержит данные графика");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            DataParser.DATASET = selectedFile.getPath();
        } else {
            initializeBtn.setDisable(false);
            return;
        }

        try {
            threadPool.execute(() -> {
                pathFinder = new PathFinder();
                delaySlideBar.setDisable(false);
                graphFontSizeSlideBar.setDisable(false);
                List<Node> nodeList = pathFinder.initializeGraph();
                setComboBoxValues(nodeList, nodeListComboBox);
                setComboBoxValues(nodeList, nodeListComboBox1);

                disableButtons(false);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAStarAlgorithm() {
        String startNode = nodeListComboBox.getValue();
        String endNode = nodeListComboBox1.getValue();


        if (startNode == null)
            return;

        disableButtons(true);

        try {
            threadPool.execute(() -> {
                pathFinder.runAStar(startNode, endNode);
                disableButtons(false);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startBreadthFirstAlgorithm() {
        String startNode = nodeListComboBox.getValue();
        String endNode = nodeListComboBox1.getValue();

        if (startNode == null)
            return;

        disableButtons(true);

        try {
            threadPool.execute(() -> {
                pathFinder.runBreadthFirst(startNode, endNode);
                disableButtons(false);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setComboBoxValues(List<Node> nodes, JFXComboBox<String> comboBox) {
        comboBox.getItems().clear();
        for (Node node : nodes) {
            comboBox.getItems().add(node.getName());
        }
    }

    private void disableButtons(boolean status) {
        startAStarBtn.setDisable(status);
        startBfsBtn.setDisable(status);
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        int delay = (int) delaySlideBar.getValue();
        pathFinder.setVisualizationDelay(delay);

        int fontSize = (int) graphFontSizeSlideBar.getValue();
        pathFinder.setGraphFontSize(fontSize);
    }
}
