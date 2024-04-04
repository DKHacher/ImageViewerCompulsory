package XML.Gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainController {
    @FXML
    private Label fileNameLbl;
    @FXML
    private Button handleNext, handlePrevious, handleLoad, handleStop, handlePlay, handlePause;
    @FXML
    private ImageView imageView;


    private List<File> imageFiles = new ArrayList<>();
    private int currentIndex = 0;
    private PauseTransition slideshow = new PauseTransition(Duration.seconds(2));

    // Constructor
    public MainController() {
        slideshow.setOnFinished(event -> showNextImage());
    }


    // FXML Buttons
    @FXML
    private void handleNext(ActionEvent event) {
        showNextImage();
    }

    @FXML
    private void handlePrevious(ActionEvent event) {
        if (currentIndex > 0) {
            currentIndex--;
        } else {
            currentIndex = imageFiles.size() - 1; // Loops it back to the last image :)
        }
        showImage();
    }

    @FXML
    private void handleLoad(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            imageFiles.clear();
            File[] files = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpeg") || name.toLowerCase().endsWith(".jpg"));
            if (files != null) {
                imageFiles.addAll(Arrays.asList(files));
                currentIndex = 0;
                showImage();
                slideshow.playFromStart();
            }
        }
    }

    @FXML
    private void handlePause(ActionEvent actionEvent) {
    }

    @FXML
    private void handlePlay(ActionEvent actionEvent) {
    }

    @FXML
    private void handleStop(ActionEvent actionEvent) {
    }


    // Image Showing Methods
    private void showImage() {
        if (imageFiles.isEmpty()) return;
        File currentFile = imageFiles.get(currentIndex);
        try {
            Image image = new Image(new FileInputStream(currentFile));
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(695);
            imageView.setFitHeight(436);
        } catch (FileNotFoundException e) {
            showAlert("No File Found", "File was not found");
            e.printStackTrace();
        }
        slideshow.playFromStart();
    }

    private void showNextImage() {
        if (currentIndex < imageFiles.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        showImage();
    }


    //Alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content);
        alert.setTitle(title);
        alert.showAndWait();
    }


}
