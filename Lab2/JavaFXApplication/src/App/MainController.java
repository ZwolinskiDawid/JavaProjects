package App;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML; 
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.DirectoryChooser;

public class MainController implements Initializable {
    @FXML private TreeView<Path> treeView;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        treeView.setCellFactory(new PathCellFactory());
    }
    
    @FXML protected void changeRootButtonAction(ActionEvent event) throws IOException
    {
        DirectoryChooser dc = new DirectoryChooser();
        File file = dc.showDialog(null);
        
        Path path = Paths.get(file.getAbsolutePath());  
        TreeItem<Path> root = new TreeItem<>(path);
        treeView.setRoot(root);
        
        getChild(root);        
    }
    
    @FXML protected void deleteButtonAction(ActionEvent event) throws IOException
    {
        TreeItem<Path> selectedItem = treeView.getSelectionModel().getSelectedItem();
        deleteChildren(selectedItem);
        selectedItem.getParent().getChildren().remove(selectedItem);                    
    }
    
    private void deleteChildren(TreeItem<Path> selectedItem) throws IOException
    {
        if(Files.isDirectory(selectedItem.getValue()))
        {
            for(TreeItem<Path> Child: selectedItem.getChildren())
            {
                deleteChildren(Child);
            }
        }
        Files.delete(selectedItem.getValue());
    }
    
    private void getChild(TreeItem<Path> node) throws IOException
    {
        Path file = node.getValue();
        
        if(Files.isDirectory(file))
        {
            DirectoryStream<Path> ds = Files.newDirectoryStream(file);
            for (Path path : ds)
                {
                    TreeItem<Path> child = new TreeItem<>(path);
                    node.getChildren().add(child);
                    getChild(child);
                } 
        }
    }        
}

