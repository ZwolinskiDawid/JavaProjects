package app;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import productBinding.ProductCatalog;
import java.io.File;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;
import productBinding.ObjectFactory;
import productBinding.PriceType;
import productBinding.ProductType;

public class mainController implements Initializable {
    private ProductCatalog catalog;
    private ObservableList<ProductType> products;
    
    @FXML private TableView<ProductType> table;
    @FXML private TableColumn nameColumn;
    @FXML private TableColumn priceColumn;
    @FXML private TableColumn currencyColumn;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField currentField;
        
    @FXML private void addAction(ActionEvent event) {
        PriceType newPrice = (new ObjectFactory()).createPriceType();
        BigInteger newValue = new BigInteger(this.priceField.textProperty().getValue());
        newPrice.setValue(newValue);
        newPrice.setCurrency(this.currentField.textProperty().getValue());
        
        ProductType newProduct = (new ObjectFactory()).createProductType();
        newProduct.setName(this.nameField.textProperty().getValue());
        newProduct.setPrice(newPrice);
        
        this.catalog.getProduct().add(newProduct);
        this.products = FXCollections.observableArrayList(this.catalog.getProduct());
        this.table.setItems(this.products);
    }
    
    @FXML private void loadAction(ActionEvent event) {
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File("products.xsd"));
            
            JAXBContext jc = JAXBContext.newInstance(ProductCatalog.class);
            
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setSchema(schema);
            
            this.catalog = (ProductCatalog) unmarshaller.unmarshal(new File("src\\app\\products.xml"));
            this.products = FXCollections.observableArrayList(this.catalog.getProduct());
            this.table.setItems(this.products);
            
        } catch (SAXException | JAXBException ex) {
            Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML private void saveAction(ActionEvent event) {
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File("products.xsd"));
            
            JAXBContext jc = JAXBContext.newInstance(ProductCatalog.class);
            
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setSchema(schema);
            
            marshaller.marshal(this.catalog, new File("src\\app\\products.xml"));
        } catch (SAXException | JAXBException ex) {
            Logger.getLogger(mainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML private void removeAction(ActionEvent event) {
        for(ProductType product: this.table.getSelectionModel().getSelectedItems())
        {
            this.catalog.getProduct().remove(product);
        }
        this.products = FXCollections.observableArrayList(this.catalog.getProduct());
        this.table.setItems(this.products);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {         
        this.catalog = (new ObjectFactory()).createProductCatalog();
        this.table.setEditable(true);
        this.table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        this.nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.nameColumn.setCellValueFactory(new Callback<CellDataFeatures<ProductType, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ProductType, String> param) {
                return new SimpleStringProperty(param.getValue().getName());
            }
        });
        this.nameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ProductType, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<ProductType, String> event) {
                event.getRowValue().setName(event.getNewValue());
            }
        });
        
        this.priceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.priceColumn.setCellValueFactory(new Callback<CellDataFeatures<ProductType, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ProductType, String> param) {
                return new SimpleStringProperty(param.getValue().getPrice().getValue().toString());
            }
        });
        this.priceColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ProductType, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<ProductType, String> event) {
                event.getRowValue().getPrice().setValue(new BigInteger(event.getNewValue()));
            }
        });
        
        this.currencyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.currencyColumn.setCellValueFactory(new Callback<CellDataFeatures<ProductType, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ProductType, String> param) {
                return new SimpleStringProperty(param.getValue().getPrice().getCurrency());
            }
        });
        this.currencyColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ProductType, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<ProductType, String> event) {
                event.getRowValue().getPrice().setCurrency(event.getNewValue());
            }
        });
    }    
}
