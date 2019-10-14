package application.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.ResourceBundle;

import application.BoughtItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

public class ImportBillsController implements Initializable {

	ObservableList<BoughtItem> data = FXCollections.observableArrayList();

	
    @FXML
    private TableView<BoughtItem> table;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private TableColumn<?, ?> dateColumn;

    @FXML
    private TextField nameTF;

    @FXML
    private TextField phoneTF;

    @FXML
    private TextField dateTF;

    @FXML
    private Button viewBillButton;

    @FXML
    void buttonHandler(ActionEvent event) throws SQLException, JRException {
    	
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad","112233");

			BoughtItem m=table.getSelectionModel().getSelectedItem();
			
			JasperDesign jd=JRXmlLoader.load("JasperReport.jrxml");
			String q="select * from buying_bill,bought where buying_bill.bar=bought.barcode and supplier_name='"+m.getSupplierName()+"' and buying_date=TO_DATE('"+m.getBuyingDate()+"','YYYY-MM-DD')";
			JRDesignQuery newQuery=new JRDesignQuery();
			newQuery.setText(q);
			jd.setQuery(newQuery);
			
			JasperReport jr=JasperCompileManager.compileReport(jd);
//			HashMap param=new HashMap();
			HashMap<String, Object> param=new HashMap<>();
			param.put("name", m.getSupplierName());
			param.put("date", m.getBuyingDate().toString());
			JasperPrint jp=JasperFillManager.fillReport(jr, param,connection);
			JasperViewer.viewReport(jp,false);
		} catch (Exception e) {
			   Alert alert = new Alert(AlertType.WARNING);
			   alert.setTitle("·„  ﬁ„ »≈Œ Ì«— ›« Ê—…");
			   alert.setHeaderText(null);
			   alert.setContentText("«·—Ã«¡ ≈Œ Ì«— ›« Ê—… ·⁄—÷Â«");
			   Stage stage =(Stage) alert.getDialogPane().getScene().getWindow();
			   stage.getIcons().add(new Image("/application/images/icon.png"));
			   alert.showAndWait();
		}
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad","112233");

			Statement statement=connection.createStatement();
			String q="select distinct supplier_name,Buying_date  from buying_bill ";
			ResultSet rs=statement.executeQuery(q);
			while(rs.next()) {
				data.add(new BoughtItem("","",0,0,0,rs.getDate("Buying_date").toLocalDate(),rs.getString("Supplier_name")));
			}
			
			
			table.setItems(data);
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
			dateColumn.setCellValueFactory(new PropertyValueFactory<>("buyingDate"));
			
			table.getSelectionModel().selectedItemProperty().addListener(
		            (observable, oldValue, newValue) -> {//your code here

		            	nameTF.setText(newValue.getSupplierName());
		            	dateTF.setText(newValue.getBuyingDate()+"");
		            	String s="select * from supplier where supplier_name='"+newValue.getSupplierName()+"'";
		            	try {
							ResultSet rss=statement.executeQuery(s);							
               
//							while(
							rss.next();
							//) {
	                phoneTF.setText(rss.getString("phone_number"));
//          }			
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            	
		            	
		               }
		            
					);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
