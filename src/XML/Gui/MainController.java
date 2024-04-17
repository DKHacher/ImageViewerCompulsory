package XML.Gui;

import XML.TaskCountPixels;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController {
    @FXML
    private Label fileNameLbl, redLabel, blueLabel, greenLabel, mixedLabel;
    @FXML
    private ImageView imageView, togglePlayPause;


    private ExecutorService executor = Executors.newSingleThreadExecutor(); // For running background tasks
    private List<File> imageFiles = new ArrayList<>();
    private int currentIndex = 0;
    private PauseTransition slideshow = new PauseTransition(Duration.seconds(2));
    private boolean isSlideshowPlaying = false;

    // Constructor - Sets up the slideshow transition behaviour
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
        applyFadeTransition(imageView, 500, 1.0, 0.0, () -> loadImageAndStats(imageFiles.get(currentIndex)));
    }

    private void applyFadeTransition(ImageView imageView, int duration, double from, double to, Runnable onFinishedAction) {
        FadeTransition fade = new FadeTransition(Duration.millis(duration), imageView);
        fade.setFromValue(from);
        fade.setToValue(to);
        fade.setOnFinished(event -> {
            if (onFinishedAction != null) {
                onFinishedAction.run();
            }
        });
        fade.play();
    }

    private void showNextImage() {
        if (currentIndex < imageFiles.size() - 1) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        showImage();
    }

    // Load the image and update the UI with the pixel stats:
    private void loadImageAndStats(File imageFile) {
        try {
            Image image = new Image(new FileInputStream(imageFile));
            imageView.setImage(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(695);
            imageView.setFitHeight(436);

            String fileNameWithoutExtension = imageFile.getName().substring(0, imageFile.getName().lastIndexOf('.'));
            fileNameLbl.setText(fileNameWithoutExtension);

            countPixelStatistics(image);

            //Fade in after loading image and statistics
            applyFadeTransition(imageView, 500, 0.0, 1.0, null);
        } catch (FileNotFoundException e) {
            showAlert("No File Found", "File was not found");
            e.printStackTrace();
        }

        if (isSlideshowPlaying) {
            slideshow.playFromStart();
        }
    }

    // Counting pixel colors
    private void countPixelStatistics(Image image) {
        TaskCountPixels tcp = new TaskCountPixels();
        tcp.setImage(image);

        tcp.setOnSucceeded(e -> {
            long[] counts = tcp.getValue();
            redLabel.setText("Red: " + counts[0]);
            greenLabel.setText("Green: " + counts[1]);
            blueLabel.setText("Blue: " + counts[2]);
            mixedLabel.setText("Mixed: " + counts[3]);
        });

        tcp.setOnFailed(e -> {
            showAlert("Error", "Failed to process image statistics.");
        });

        //Execute task with single-thread executor to ensure no concurrent UI updates
        executor.execute(tcp);
    }



    //Alert
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
