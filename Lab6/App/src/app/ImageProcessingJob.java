package app;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javax.imageio.ImageIO;

/**
 *
 * @author Zaki
 */
public class ImageProcessingJob {
    private Path imageName;
    private SimpleStringProperty status;
    private DoubleProperty progress;
    
    public ImageProcessingJob(File file){
        this.imageName = file.toPath();
        this.status = new SimpleStringProperty("Waiting ...");
        this.progress = new SimpleDoubleProperty(0);
    }

    public Path getPath() {
        return this.imageName;
    }

    public SimpleStringProperty getStatusProperty() {
        return this.status;
    }

    public DoubleProperty getProgressProperty() {
        return this.progress;
    }
    
    public void setProgressProperty(double value, double maxValue){
        this.progress.set(value/maxValue);
    }
    
    public void setProgressProperty(double value){
        this.progress.set(value);
    }
    
    public void setStatusProperty(String status){
        this.status.set(status);
    }
    
    public boolean isDone(){
        return this.getProgressProperty().getValue() == 1;
    }
    
    public void start(Path destinationDirectory){
        if(this.isDone())
            return;
        try {
            final BufferedImage original = ImageIO.read(this.imageName.toFile());

            BufferedImage grayscale = new BufferedImage(
            original.getWidth(), original.getHeight(), original.getType());
            
            this.setStatusProperty("Sending ...");
            for (int i = 0; i < original.getWidth(); i++) {
                
                for (int j = 0; j < original.getHeight(); j++) {

                    int red = new Color(original.getRGB(i, j)).getRed();
                    int green = new Color(original.getRGB(i, j)).getGreen();
                    int blue = new Color(original.getRGB(i, j)).getBlue();

                    int luminosity = (int) (0.21*red + 0.71*green + 0.07*blue);

                    int newPixel =
                        new Color(luminosity, luminosity, luminosity).getRGB();

                    grayscale.setRGB(i, j, newPixel);
                }
                double progress = (1.0 + i) / original.getWidth();
                Platform.runLater(() -> this.setProgressProperty(progress));
            }
            this.setProgressProperty(original.getWidth(), original.getWidth());
            this.setStatusProperty("Done !");
            Path outputPath =
                Paths.get(destinationDirectory.toString(), this.getPath().getFileName().toString());
            ImageIO.write(grayscale, "jpg", outputPath.toFile());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
