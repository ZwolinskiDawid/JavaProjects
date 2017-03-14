package App;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Dawid
 */
public class NewFileGUIController implements Initializable {
    @FXML private TextField name;
    @FXML private RadioButton normalFile;
    @FXML private RadioButton directory;
    private String newFileName;
    private boolean isDirectory;
    private boolean toCreate;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.toCreate = false;
    }
    
    @FXML protected void addButtonAction(ActionEvent event) throws IOException
    {
        this.toCreate = true;
        this.newFileName = name.getText();
        if(normalFile.isSelected())
        {
            this.isDirectory = false;
        }
        else if(directory.isSelected())
        {
            this.isDirectory = true;
        }
        Stage stage = (Stage) name.getScene().getWindow();
        stage.close();
    }
    
    public boolean getToCreate()
    {
        return this.toCreate;
    }
    
    public String getName()
    {
        return this.newFileName;
    }
    
    public boolean getIsDirectory()
    {
        return this.isDirectory;
    }
}
