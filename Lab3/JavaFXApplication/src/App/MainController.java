package App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.util.Callback;

public class MainController implements Initializable {
    @FXML private TreeView<Path> treeView;
    @FXML private TextField server;
    @FXML private TextField port;
    
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
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        getChild(root);        
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
    
    private class PathCellFactory implements Callback<TreeView<Path>, TreeCell<Path>> {
       
        @Override
        public TreeCell<Path> call(TreeView<Path> param)
        {
            return new PathCell(server, port, treeView);
        }
    }
}

class ListOfProgress {
    
    private List<Progress> listOfProgress;

    ListOfProgress() {
        this.listOfProgress = new ArrayList<>();
    }
    
    public void addToList(Progress progress)
    {
        listOfProgress.add(progress);
    }
    
    public void removeFromList(Progress progress)
    {
        listOfProgress.remove(progress);
    }
    
    public int getQuantity()
    {
        int quantity = 0;
        for(int i=0;i<listOfProgress.size();i++)
        {
            quantity += listOfProgress.get(i).getQuantity();
        }
        return quantity;
    }
    
    public int getAvailable()
    {
        int available = 0;
        for(int i=0;i<listOfProgress.size();i++)
        {
            available += listOfProgress.get(i).getAvailable();
        }
        return available;
    }
    
}

class Progress {
    
    private int quantity;
    private int available;
    
    Progress(int quantity, int available)
    {
        this.quantity = quantity;
        this.available = available;
    }

    public int getQuantity()
    {
        return this.quantity;
    }
    
    public int getAvailable()
    {
        return this.available;
    }
    
    public void addQuantity(int quantity)
    {
        this.quantity += quantity;
    }
    
    public void addAvailable(int available)
    {
        this.available += available;
    } 
    
    public void minusQuantity(int quantity)
    {
        this.quantity -= quantity;
    }
    
    public void minusAvailable(int available)
    {
        this.available -= available;
    } 
    
    
}

