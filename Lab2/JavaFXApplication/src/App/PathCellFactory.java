package App;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PathCellFactory implements Callback<TreeView<Path>, TreeCell<Path>> {

    @Override
    public TreeCell<Path> call(TreeView<Path> param)
    {
        return new PathCell();
    }
}

class PathCell extends TreeCell<Path> {
    private ContextMenu menu = new ContextMenu();
    
    public PathCell()
    {
        //String name = this.getItem().getFileName().toString();
        
        MenuItem addFileItem = new MenuItem("Add file");
        menu.getItems().add(addFileItem);
        
        addFileItem.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("NewFileGUI.fxml"));
                    Parent root = (Parent) loader.load();
                    NewFileGUIController controller = (NewFileGUIController) loader.getController();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Create directory");
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.showAndWait();
                    
                    if(controller.getToCreate())
                    {
                        Path newPath = Paths.get(getTreeItem().getValue().toString(), controller.getName());
                        if(controller.getIsDirectory())
                        {
                            Files.createDirectory(newPath);
                        }
                        else
                        {
                            Files.createFile(newPath);
                        }
                        TreeItem newFile = new TreeItem<>(newPath);
                        getTreeItem().getChildren().add(newFile);
                    }                        
                    
                } catch (IOException ex) {
                    Logger.getLogger(PathCell.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        
        
        
        MenuItem deleteFileItem = new MenuItem("Delete file");
        menu.getItems().add(deleteFileItem);
        
        deleteFileItem.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                
                try {
                    TreeItem<Path> selectedItem = getTreeItem();
                    deleteChildren(selectedItem);
                    selectedItem.getParent().getChildren().remove(selectedItem);
                } catch (IOException ex) {
                    Logger.getLogger(PathCell.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
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
    
    @Override
    protected void updateItem(Path file, boolean empty)
    {
        super.updateItem(file, empty);
        if (file != null) 
        {
            if(Files.isDirectory(file))
            {
                menu.getItems().get(0).setDisable(false);
                setContextMenu(menu);
            }
            else if(Files.isRegularFile(file))
            {
                menu.getItems().get(0).setDisable(true);
                setContextMenu(menu);
            }
            
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