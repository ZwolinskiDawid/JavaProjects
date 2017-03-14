package app;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class mainController implements Initializable {
    private EntityManagerFactory emf;
    private EntityManager em;
    private ObservableList<Departments> departments;
    private ObservableList<String> depBox;
    private ObservableList<Students> students;
    
    @FXML private TableView<Students> table;
    @FXML private TableColumn nameColumn;
    @FXML private TableColumn sNameColumn;
    @FXML private TableColumn departmentColumn;
    @FXML private TextField nameField;
    @FXML private TextField sNameField;
    @FXML private ChoiceBox departmentBox;
    @FXML private TableView<Departments> depTable;
    @FXML private TableColumn depNameColumn;
    @FXML private TableColumn depSNameColumn;
    
    @FXML private void addAction(ActionEvent event) {
        this.em.getTransaction( ).begin( );
        
        Students ns = new Students();
        ns.setName(this.nameField.getText());
        ns.setSurname(this.sNameField.getText());
        int index = this.depBox.indexOf(this.departmentBox.getValue().toString());
        ns.setIdDep(this.departments.get(index));
        
        this.em.persist( ns );
        this.em.getTransaction( ).commit( );
        
        this.students.add(ns);
        this.table.setItems(this.students);
    }
    
    @FXML private void reportAction(ActionEvent event) {
        
        try {
            List<Object[]> results = this.em.createQuery(
                    "SELECT s.idDep.name, COUNT(s) "
                            + "FROM Students s "
                            + "GROUP BY s.idDep").getResultList();
            
            results.sort(new CountComparator());
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("report.fxml"));
            Parent root = (Parent) loader.load();
            ReportController controller = (ReportController) loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Report");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
            
            for (Object[] result : results)
            {
                String name = (String) result[0];
                int count = ((Number) result[1]).intValue();
                
                controller.setLabel(name, count);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.table.setEditable(true);
        
        this.emf = Persistence.createEntityManagerFactory("AppPU");
        this.em = emf.createEntityManager();
        
        this.departments = 
                FXCollections.observableArrayList(
                        em.createQuery("SELECT d FROM Departments d").getResultList());
        
        this.depBox = FXCollections.observableArrayList();
        for(Departments d : this.departments)
        {
            this.depBox.add(d.getShortname());
        }
        
        this.departmentBox.setItems(depBox);
        this.departmentBox.getSelectionModel().selectFirst();
        
        this.depNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.depNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Departments, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Departments, String> param) {
                return new SimpleStringProperty(param.getValue().getName());
            }
        });
        
        this.depSNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.depSNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Departments, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Departments, String> param) {
                return new SimpleStringProperty(param.getValue().getShortname());
            }
        });
        
        this.depTable.setItems(departments);
        this.depTable.getSelectionModel().selectFirst();
        
        this.students = 
                FXCollections.observableArrayList(
                        em.createQuery("SELECT s FROM Students s").getResultList());
        
        table.setRowFactory(new Callback<TableView<Students>, TableRow<Students>>() {
            @Override
            public TableRow<Students> call(TableView<Students> param) {
                
            final TableRow<Students> row = new TableRow<>();            
            final ContextMenu rowMenu = new ContextMenu();
            
            MenuItem removeItem = new MenuItem("Delete");
            removeItem.setOnAction(new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent event) {
                em.getTransaction( ).begin( );
                
                Students ds = param.getSelectionModel().getSelectedItem();
                
                em.remove( ds );
                students.remove(ds);
                
                em.getTransaction( ).commit( );
                table.setItems(students);                  
              }
            });
            rowMenu.getItems().addAll(removeItem);

            // only display context menu for non-null items:
            row.contextMenuProperty().bind(
              Bindings.when(Bindings.isNotNull(row.itemProperty()))
              .then(rowMenu)
              .otherwise((ContextMenu)null));
            return row;              
                
            }
        });
        
        this.nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.nameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Students, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Students, String> param) {
                return new SimpleStringProperty(param.getValue().getName());
            }
        });
        this.nameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Students, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Students, String> event) {
                em.getTransaction( ).begin( );
                event.getRowValue().setName(event.getNewValue());
                em.getTransaction( ).commit( );
            }
        });
        
        this.sNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.sNameColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Students, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Students, String> param) {
                return new SimpleStringProperty(param.getValue().getSurname());
            }
        });
        this.sNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Students, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Students, String> event) {
                em.getTransaction( ).begin( );
                event.getRowValue().setSurname(event.getNewValue());
                em.getTransaction( ).commit( );
            }
        });
        
        this.departmentColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(depBox));
        this.departmentColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Students, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Students, String> param) {
                return new SimpleStringProperty(param.getValue().getIdDep().getShortname());
            }
        });
        this.departmentColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Students, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Students, String> event) {
                em.getTransaction( ).begin( );
                int index = depBox.indexOf(event.getNewValue());
                event.getRowValue().setIdDep(departments.get(index));
                em.getTransaction( ).commit( );
                depTable.getSelectionModel().select(index);   
            }
        });

        this.table.setItems(this.students);
      
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                depTable.getSelectionModel().select(
                        ((Students)observable.getValue()).getIdDep().getIdDep()-1);
                
            }
        });
    }
    
}
