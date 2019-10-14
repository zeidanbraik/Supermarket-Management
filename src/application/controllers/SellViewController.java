package application.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.textfield.TextFields;

import application.Item;
import javafx.application.Platform;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class SellViewController implements Initializable {

	@FXML
	private TextField itemNameTF;

	@FXML
	private TextField barcodeTF;

	@FXML
	private TableView<Item> tableView;

	@FXML
	private TableColumn<Item, String> barcodeColumn;

	@FXML
	private TableColumn<Item, String> itemColumn;

	@FXML
	private TableColumn<Item, Double> itemPriceColumn;

	@FXML
	private TableColumn<Item, Integer> quantityColumn;

	@FXML
	private TableColumn<Item, Double> totalPriceColumn;

	@FXML
	private Button cancelButton;

	@FXML
	private Button saveAndNewButton;

	@FXML
	private TextField totalPriceTF;

	ArrayList<String> data = new ArrayList<String>();

	ObservableList<Item> itemsData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad","112233");
			Statement statement = connection.createStatement();
			String q="delete from stored where quantity<=0";
			statement.executeUpdate(q);
			 q = "select name from stored";
			ResultSet rs = statement.executeQuery(q);

			while (rs.next()) {
				data.add(rs.getString("name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		TextFields.bindAutoCompletion(itemNameTF, data);
		Platform.runLater(() -> barcodeTF.requestFocus());

		barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
		itemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quant"));
		totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalSP"));
		tableView.setItems(itemsData);

	}

	@FXML
	void buttonHandler(ActionEvent event) {
		if (event.getSource().toString().contains("saveAndNewButton")) {
			for(int i=0;i<itemsData.size();i++) {
			}
			/// After the query clear the table
			if(itemsData.size() == 0) {
					//empty table
				   Alert alert = new Alert(AlertType.WARNING);
				   alert.setTitle("ÝÇÊæÑÉ ÝÇÑÛÉ");
				   alert.setHeaderText(null);
				   alert.setContentText("ÇáÝÇÊæÑÉ ÝÇÑÛÉ¡ ÇáÑÌÇÁ ÊÚÈÆÉ ÇáÓáÚ ÇæáÇð");
				   Stage stage =(Stage) alert.getDialogPane().getScene().getWindow();
				   stage.getIcons().add(new Image("/application/images/icon.png"));
				   alert.showAndWait();

				
				
			}
			
			for(int i=0;i<itemsData.size();i++) {
		
				//in case there is not 0 from this type
				if(itemsData.get(i).getQuant()!=0) {
					try {
						DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
					
						Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad","112233");
						Statement statement = connection.createStatement();
					String q = "update stored set quantity=quantity-"+ itemsData.get(i).getQuant()+" where name='"+itemsData.get(i).getName()+"'";
				    statement.executeUpdate(q);
				    
					q="select * from sold where name='"+itemsData.get(i).getName()+"'";
             ResultSet r= statement.executeQuery(q);
             
             int count=0;
             while(r.next()) {
            	 count++;}
             q="select * from selling_bill where bar='"+itemsData.get(i).getBarcode()+"' and selling_date=TO_DATE('"+LocalDate.now()+"','YYYY-MM-DD')" ;
              r= statement.executeQuery(q);
             
             int count2=0;
             while(r.next()) {
            	 count2++;}
             
             
             if(count==0) {
            	 if(count2==0) {
            	 //a.get(i).getName()+"','"+data.get(i).getBarcode()+"',"+data.get(i).getQuant()+","+data.get(i).getBuyPrice()+","+data.get(i).getSellPrice()+",TO_DATE('"+billDate.getValue()+"','YYYY-MM-DD'),'"
            q="insert into sold values ("+itemsData.get(i).getSellPrice()+",'"+itemsData.get(i).getBarcode()+"','"+itemsData.get(i).getName()+"',"+itemsData.get(i).getBuyPrice()+")";	 
            statement.executeUpdate(q);
           q="insert into selling_bill values ('"+itemsData.get(i).getBarcode()+"',TO_DATE('"+LocalDate.now()+"','YYYY-MM-DD'),"+ itemsData.get(i).getQuant()+")" ;
           statement.executeUpdate(q);
             
            	 }
            	 else {
            		  q="insert into sold values ("+itemsData.get(i).getSellPrice()+",'"+itemsData.get(i).getBarcode()+"','"+itemsData.get(i).getName()+"',"+itemsData.get(i).getBuyPrice()+")";	 
            	            statement.executeUpdate(q);
            	q="update selling_bill set quantity=quantity+"+ itemsData.get(i).getQuant()+"where bar="+itemsData.get(i).getBarcode();	 
            		 statement.executeUpdate(q);
            	 }
            	 
            	 
            	 }
             else {
            	 if(count2==0) {
                 q="insert into selling_bill values ('"+itemsData.get(i).getBarcode()+"',TO_DATE('"+LocalDate.now()+"','YYYY-MM-DD'),"+ itemsData.get(i).getQuant()+")" ; 
            statement.executeUpdate(q);
            	 }
            	 else {
            		 q="update selling_bill set quantity=quantity+"+ itemsData.get(i).getQuant()+" where bar="+itemsData.get(i).getBarcode();	 
            		 statement.executeUpdate(q);
            	 }
             }
					
					}
					catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
				
				
				
			}
			
			
			itemsData.clear();
			tableView.refresh();
			totalPriceTF.clear();
		}
		
		if (event.getSource().toString().contains("cancelButton")) {
			itemsData.clear();
			tableView.refresh();
			totalPriceTF.clear();
		}
	}

	@FXML
	void itemHandler(KeyEvent event) throws SQLException {

		if (event.getSource().toString().contains("barcodeTF"))
			if (event.getCode().equals(KeyCode.ENTER))
				try {
					showInTable(barcodeTF.getText());
				} catch (Exception e) {
					   Alert alert = new Alert(AlertType.WARNING);
					   alert.setTitle("ÇáÓáÚÉ ÛíÑ ãæÌæÏÉ");
					   alert.setHeaderText(null);
					   alert.setContentText("ÇáÓáÚÉ ÛíÑ ãÓÌáÉ ÈÇáãÎÒä¡ ÇáÑÌÇÁ ÊÎÒíäåÇ ÃæáÇð");
					   Stage stage =(Stage) alert.getDialogPane().getScene().getWindow();
					   stage.getIcons().add(new Image("/application/images/icon.png"));
					   alert.showAndWait();
					
				}

		if (event.getSource().toString().contains("itemNameTF")) {
			if (event.getCode().equals(KeyCode.ENTER)) {
				Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad","112233");
				Statement statement = connection.createStatement();
				String q = "";

				q = "select barcode from stored where name='" + itemNameTF.getText() + "'";
				ResultSet rs = statement.executeQuery(q);
				rs.next();
				try {
					showInTable(rs.getString("barcode"));
				} catch (Exception e) {
					   Alert alert = new Alert(AlertType.WARNING);
					   alert.setTitle("ÇáÓáÚÉ ÛíÑ ãæÌæÏÉ");
					   alert.setHeaderText(null);
					   alert.setContentText("ÇáÓáÚÉ ÛíÑ ãÓÌáÉ ÈÇáãÎÒä¡ ÇáÑÌÇÁ ÊÎÒíäåÇ ÃæáÇð");
					   Stage stage =(Stage) alert.getDialogPane().getScene().getWindow();
					   stage.getIcons().add(new Image("/application/images/icon.png"));
					   alert.showAndWait();
					   e.printStackTrace();
				}
			}

		}
	}

	public void showInTable(String barcode) throws SQLException {
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad","112233");
		Statement statement = connection.createStatement();
		String q = "";

		q = "select * from stored where barcode='" + barcode + "'";
		ResultSet rs = statement.executeQuery(q);
		rs.next();
		boolean exist = false;
		int i = 0;
		for (i = 0; i < itemsData.size(); i++) {

			if (itemsData.get(i).getBarcode().trim().equals(barcode.trim())) {
				exist = true;
				break;
			}
		}
		if (!exist)
			itemsData.add(new Item(rs.getString("barcode"), rs.getString("name"), 1, rs.getDouble("buy_price"), rs.getDouble("sell_price"),
					null, 0, rs.getDouble("sell_price")));
		else
			itemsData.get(i).update();

		tableView.refresh();
		barcodeTF.clear();
		itemNameTF.clear();
		
		double sum=0;
		for(i=0;i<itemsData.size();i++) {
			sum+=itemsData.get(i).getTotalSP();
		}
		totalPriceTF.setText(sum+"");
		

	}

}
