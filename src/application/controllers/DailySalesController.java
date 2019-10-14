package application.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import application.SoldItems;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DailySalesController implements Initializable {

	@FXML
	private TableView<SoldItems> table;

	@FXML
	private TableColumn<SoldItems, String> itemColumn;

	@FXML
	private TableColumn<SoldItems, Integer> quantityColumn;

	@FXML
	private TableColumn<SoldItems, Double> TotalSellPriceColumn;

	@FXML
	private TableColumn<SoldItems, Double> profitColumn;

	@FXML
	private DatePicker date;

	@FXML
	private Label totalSellPrice;

	@FXML
	private Label totalProfit;

	ObservableList<SoldItems> data;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		date.setValue(LocalDate.now());
		data = FXCollections.observableArrayList();
		itemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quant"));
		TotalSellPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalsellPrice"));
		profitColumn.setCellValueFactory(new PropertyValueFactory<>("profit"));
		table.setItems(data);

		// Listen to date changes
		try {
			date.valueProperty().addListener((observable, oldDate, newDate) -> {
				showInTable(newDate);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		showInTable(LocalDate.now());

	}

	public void showInTable(LocalDate date) {
		try {
			data.clear();
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad","112233");
			Statement statement = connection.createStatement();
			String q = "select * from sold , selling_bill where sold.barcode=selling_bill.bar";
			ResultSet rs = statement.executeQuery(q);
			double totalprofit = 0;
			double sellprice = 0;

			while (rs.next()) {
        if(date.equals(rs.getDate("selling_date").toLocalDate())) {
            data.add(new SoldItems(rs.getString("name"),rs.getInt("quantity"),rs.getDouble("SELL_PRICE"),rs.getDate("selling_date").toLocalDate(),rs.getDouble("buy_price")));
            table.refresh();
        }
			}

        for(int i=0;i<data.size();i++) {
            totalprofit+=data.get(i).getProfit();
            sellprice+=data.get(i).getTotalSellPrice();
        }
        totalSellPrice.setText(sellprice+"");
        totalProfit.setText(totalprofit+"");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
	}
}
