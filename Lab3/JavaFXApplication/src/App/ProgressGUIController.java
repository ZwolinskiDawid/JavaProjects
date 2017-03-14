package App;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class ProgressGUIController implements Initializable {
    @FXML private ProgressBar progressBar;
    @FXML private Label label;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public Label getLabel()
    {
        return label;
    }
    
    public ProgressBar getProgressBar()
    {
        return progressBar;
    }
    
    @FXML protected void closeAction(ActionEvent event) throws IOException
    {
        Stage stage = (Stage) label.getScene().getWindow();
        stage.close();
    }
    
}
