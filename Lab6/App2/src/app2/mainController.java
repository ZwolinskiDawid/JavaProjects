package app2;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ForkJoinPool;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.NumberStringConverter;

public class mainController implements Initializable {
    
    @FXML
    private TableView<ImageProcessingJob> imageProcessingJobTable;
    @FXML
    private TableColumn<ImageProcessingJob, String> imageNameColumn;
    @FXML
    private TableColumn<ImageProcessingJob, Double> progressColumn;
    @FXML
    private TableColumn<ImageProcessingJob, String> statusColumn;
    @FXML
    private Label sequentialTimeLabel;
    @FXML
    private Label parallelTimeLabel;
    @FXML
    private Label differentialLabel;
    @FXML
    private TextField numberOfThreadsTextField;

    private ObservableList<ImageProcessingJob> imageProcessingJobList;
    private DoubleProperty sequentialTime;
    private DoubleProperty parallelTime;
    private DoubleProperty differential;
    private SimpleStringProperty destinationDirectory;
    private IntegerProperty numberOfThreads;
    
    @FXML
    private void chooseFiles(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter =
            new FileChooser.ExtensionFilter("JPG images", "*.jpg");
        fileChooser.getExtensionFilters().add(filter);
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        for(File file : selectedFiles){
            this.imageProcessingJobList.add(new ImageProcessingJob(file));
        }
    }

    @FXML
    private void clearTable(ActionEvent event) {
        ArrayList<ImageProcessingJob> toRemove = new ArrayList<ImageProcessingJob>();
        for (ImageProcessingJob i : this.imageProcessingJobList){
            if(i.isDone()){
                toRemove.add(i);
            }
        }
        this.imageProcessingJobList.removeAll(toRemove);
    }

    @FXML
    private void startConversionSequential(ActionEvent event) {
        new Thread(() -> this.sequentialConversion()).start();
    }

    @FXML
    private void startConversionParallel(ActionEvent event) {
        ForkJoinPool pool = new ForkJoinPool(this.numberOfThreads.getValue()); 
        pool.submit(() -> this.parallelConversion());
    }
    
    private void sequentialConversion(){
        long start = System.currentTimeMillis();
        this.imageProcessingJobList.stream().forEach(
                ips -> ips.start(Paths.get(this.destinationDirectory.getValue()))
        );
        long end = System.currentTimeMillis(); 
        long duration = end - start;
        System.out.println(duration);
        Platform.runLater(() -> this.sequentialTime.set(duration));
    }
    
    private void parallelConversion(){
        long start = System.currentTimeMillis();
        this.imageProcessingJobList.parallelStream().forEach(
                ips -> ips.start(Paths.get(this.destinationDirectory.getValue()))
        );
        long end = System.currentTimeMillis(); 
        long duration = end - start;
        System.out.println(duration);
        Platform.runLater(() -> this.parallelTime.set(duration));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.imageProcessingJobList = FXCollections.observableArrayList();
        this.sequentialTime = new SimpleDoubleProperty(0);
        this.parallelTime = new SimpleDoubleProperty(0);
        this.differential = new SimpleDoubleProperty(0);
        this.destinationDirectory = new SimpleStringProperty("");
        this.numberOfThreads = new SimpleIntegerProperty(2);
        
        this.imageProcessingJobTable.setItems(imageProcessingJobList);
        Bindings.bindBidirectional(this.sequentialTimeLabel.textProperty(), 
                sequentialTime, new NumberStringConverter());
        Bindings.bindBidirectional(this.parallelTimeLabel.textProperty(), 
                parallelTime, new NumberStringConverter());
        this.differential.bind(sequentialTime.subtract(parallelTime));
        Bindings.bindBidirectional(this.differentialLabel.textProperty(), 
                differential, new NumberStringConverter());
        Bindings.bindBidirectional(this.numberOfThreadsTextField.textProperty(), 
                numberOfThreads, new NumberStringConverter());
        
        imageNameColumn.setCellValueFactory( //nazwa pliku
            p -> new SimpleStringProperty(p.getValue().getPath().getFileName().toString())
        );
        statusColumn.setCellValueFactory( //status przetwarzania
            p -> p.getValue().getStatusProperty()
        );
        progressColumn.setCellValueFactory( //postÄ™p przetwarzania
            p -> p.getValue().getProgressProperty().asObject()
        );
        progressColumn.setCellFactory( //wykorzystanie paska postÄ™pu
            ProgressBarTableCell.<ImageProcessingJob>forTableColumn()
        ); 
        
        this.destinationDirectory.set("C:\\Users\\Dawid\\Desktop\\gray");
    }      
    
}
