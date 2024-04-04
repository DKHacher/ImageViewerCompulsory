package XML.Gui;

import javafx.animation.FadeTransition;
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
    private Button handleNext, handlePrevious, handleLoad, handleStop, handlePlayPause;
    @FXML
    private ImageView imageView, togglePlayPause;


    private List<File> imageFiles = new ArrayList<>();
    private int currentIndex = 0;
    private PauseTransition slideshow = new PauseTransition(Duration.seconds(2));
    private boolean isSlideshowPlaying = false;

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
            }
        }
    }

    /*
    @FXML
    private void handlePause(ActionEvent actionEvent) {
        isSlideshowPlaying = false;
        slideshow.pause();
    }

    @FXML
    private void handlePlay(ActionEvent actionEvent) {
        if (imageFiles.size() > 0) { // Check if there are images loaded
            isSlideshowPlaying = true;
            slideshow.play();
        }
    }
    */

    @FXML
    private void handlePlayPause(ActionEvent actionEvent){
        if (imageFiles.isEmpty()) return;

        isSlideshowPlaying = !isSlideshowPlaying;

        if (isSlideshowPlaying) {
            slideshow.play();
            togglePlayPause.setImage(new Image(getClass().getResourceAsStream("/Images/pause.png")));
        } else {
            slideshow.pause();
            togglePlayPause.setImage(new Image(getClass().getResourceAsStream("/Images/play.png")));
        }
    }


    @FXML
    private void handleStop(ActionEvent actionEvent) {
        isSlideshowPlaying = false;
        slideshow.stop();
        currentIndex = 0;
        showImage();
        togglePlayPause.setImage(new Image(getClass().getResourceAsStream("/Images/play.png")));
    }


    // Image Showing Methods
    private void showImage() {
        if (imageFiles.isEmpty()) return;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), imageView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(event -> {
            File currentFile = imageFiles.get(currentIndex);
            try {
                Image image = new Image(new FileInputStream(currentFile));
                imageView.setImage(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(695);
                imageView.setFitHeight(436);

                // Extract the filename without the extension
                String fileNameWithoutExtension = currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.'));
                fileNameLbl.setText(fileNameWithoutExtension);

            } catch (FileNotFoundException e) {
                showAlert("No File Found", "File was not found");
                e.printStackTrace();
            }

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), imageView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();

        if (isSlideshowPlaying) {
            slideshow.playFromStart();
        }
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
