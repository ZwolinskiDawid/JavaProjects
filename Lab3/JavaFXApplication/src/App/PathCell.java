package App;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PathCell extends TreeCell<Path> {
    private ContextMenu menu = new ContextMenu();
    private TreeView<Path> treeView;

    PathCell(TextField server, TextField port, TreeView<Path> t) {
        treeView = t;
        MenuItem uploadFileItem = new MenuItem("Upload file");
        menu.getItems().add(uploadFileItem);
        
        uploadFileItem.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                
                Path selectedFile = getTreeItem().getValue();                
                String serverName = server.textProperty().get();
                int PortNumber = Integer.parseInt(port.textProperty().get());                
                
                if (!serverName.isEmpty() && PortNumber != 0)
                {   
                    for( TreeItem<Path> item : treeView.getSelectionModel().getSelectedItems()){
                        selectedFile = item.getValue();
                        try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProgressGUI.fxml"));
                        Parent root = (Parent) loader.load();
                        ProgressGUIController controller = (ProgressGUIController) loader.getController();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Sending " + selectedFile.getFileName().toString());
                        //stage.initModality(Modality.WINDOW_MODAL);
                        stage.show();
                        
                        SendWorker sender = new SendWorker(selectedFile, serverName, PortNumber, controller);
                        controller.getLabel().textProperty().bind(sender.messageProperty());
                        controller.getProgressBar().progressProperty().bind(sender.progressProperty());
                        Thread thread = new Thread(sender);
                        thread.setDaemon(true);
                        thread.start();
                        } catch (IOException ex) {
                            Logger.getLogger(PathCell.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                }
                
            }
        });
    }
    
    @Override
    protected void updateItem(Path file, boolean empty)
    {
        super.updateItem(file, empty);
        if (file != null) 
        {
            if(Files.isDirectory(file))
            {
                menu.getItems().get(0).setDisable(true);  
            }
            else if(Files.isRegularFile(file))
            {
                menu.getItems().get(0).setDisable(false);
            }
            setContextMenu(menu);
            
            String atributes = "";
            
            try {
                DosFileAttributes attr = Files.readAttributes(file, DosFileAttributes.class);
                atributes += (attr.isReadOnly() ? "r" : "-");
                atributes += (attr.isArchive() ? "a" : "-");
                atributes += (attr.isHidden() ? "h" : "-");
                atributes += (attr.isSystem() ? "s" : "-");
                
                setText(file.getFileName().toString() + " " + atributes);
            } catch (IOException ex) {
                Logger.getLogger(PathCell.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            setText(null);
        }
    }
}