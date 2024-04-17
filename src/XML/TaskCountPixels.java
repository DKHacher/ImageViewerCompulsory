package XML;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class TaskCountPixels extends Task<long[]> {
    private Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    private Image image;


    @Override
    protected long[] call() throws Exception {
        long[] counts = {0, 0, 0, 0}; // red, green, blue, mixed
        PixelReader reader = image.getPixelReader();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = reader.getColor(x, y);
                double r = color.getRed();
                double g = color.getGreen();
                double b = color.getBlue();
                // Determine the dominant color
                if (r > g && r > b) counts[0]++;
                else if (g > r && g > b) counts[1]++;
                else if (b > r && b > g) counts[2]++;
                else counts[3]++;
            }
        }
        return counts;
    }
}
