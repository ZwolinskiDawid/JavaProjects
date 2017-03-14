package app;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ReportController implements Initializable {
    private int number;
    
    @FXML private Label label1;
    @FXML private Label label2;
    @FXML private Label label3;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.number = 1;
    }
    
    void setLabel(String name, int count)
    {
        if(this.number == 1)
        {
            this.label1.setText(name + ":   " + count);
        }
        else if(this.number == 2)
        {
            this.label2.setText(name + ":   " + count);
        }
        else
        {
            this.label3.setText(name + ":   " + count);
        }
        
        this.number += 1;
    }
    
}
