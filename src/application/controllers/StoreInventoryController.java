package application.controllers;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

import application.StoredItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class StoreInventoryController implements Initializable {

	@FXML
	private TableView<StoredItem> table;

	@FXML
	private TableColumn<StoredItem, String> barcodeColumn;

	@FXML
	private TableColumn<StoredItem, String> itemColumn;

	@FXML
	private TableColumn<StoredItem, Integer> quantityColumn;

	@FXML
	private TableColumn<StoredItem, Double> buyPriceColumn;

	@FXML
	private TableColumn<StoredItem, Double> sellPriceColumn;

	@FXML
	private TableColumn<StoredItem, Double> totalItemProfitColumn;

	@FXML
	private TableColumn<StoredItem, String> expiryDateColumn;

	@FXML
	private Label totalBuyPrice;

	@FXML
	private Label totalSellPrice;

	@FXML
	private Label totalProfit;

	@FXML
	private Button updateExpiryDate;

	ObservableList<StoredItem> data;

	@FXML
	void buttonHandler(ActionEvent event) {

		LocalDate date;

		String barcode;
		try {
			// show Calender dialog
			Dialog<LocalDate> dateDialog = new Dialog<>();
			dateDialog.setTitle(" ÕœÌÀ  «—ÌŒ «·≈‰ Â«¡");
			dateDialog.setHeaderText("«·—Ã«¡ ≈œŒ«· «· «—ÌŒ «·ÃœÌœ");

			Stage stage = (Stage) dateDialog.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image("/application/images/icon.png"));

			DatePicker datePicker = new DatePicker();
			dateDialog.getDialogPane().setContent(datePicker);

			ButtonType buttonTypeOk = new ButtonType(" ÕœÌÀ «· «—ÌŒ", ButtonData.OK_DONE);
			dateDialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

			dateDialog.setResultConverter(new Callback<ButtonType, LocalDate>() {
				@Override
				public LocalDate call(ButtonType b) {

					if (b == buttonTypeOk) {

						return datePicker.getValue();
					}

					return null;
				}
			});

			date = dateDialog.showAndWait().get();
			table.getSelectionModel().getSelectedItem().setExpDate(date);
			barcode = table.getSelectionModel().getSelectedItem().getBarcode();

			try {
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad",
						"112233");
				Statement statement = connection.createStatement();
				statement.executeUpdate("update stored set exp_date=TO_DATE('" + date
						+ "','YYYY-MM-DD') where barcode='" + barcode + "'");

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				table.refresh();
			}

		} catch (Exception e) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle(" «—ÌŒ Œ«ÿ∆");
			alert.setHeaderText(null);
			alert.setContentText("·„  ﬁ„ »≈œŒ«·  «—ÌŒ° «·—Ã«¡ ≈œŒ«·  «—ÌŒ ≈‰ Â«¡ ··”·⁄…");
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(new Image("/application/images/icon.png"));
			alert.showAndWait();
		}

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		int count = 0;

		data = FXCollections.observableArrayList();

		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad",
					"112233");
			Statement statement = connection.createStatement();
			String q = "delete from stored where quantity<=0";
			statement.executeUpdate(q);
			q = "select * from stored";
			ResultSet rs = statement.executeQuery(q);

			while (rs.next()) {
				data.add(new StoredItem(rs.getString("barcode"), rs.getString("name"), rs.getDouble("buy_price"),
						rs.getDouble("sell_price"), rs.getDate("exp_date").toLocalDate(), rs.getInt("quantity")));
				count++;
			}

			table.setItems(data);
			barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
			itemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quant"));
			buyPriceColumn.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
			sellPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
			totalItemProfitColumn.setCellValueFactory(new PropertyValueFactory<>("totalProfit"));
			expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expDate"));

			makeTableEditable();

			calculateTotals(count);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void calculateTotals(int count) {
		double totalBuyPrice = 0;
		double totalSellPrice = 0;
		double totalProfit = 0;

		for (int i = 0; i < count; i++) {
			totalBuyPrice += buyPriceColumn.getCellObservableValue(i).getValue()
					* quantityColumn.getCellObservableValue(i).getValue();
			totalSellPrice += sellPriceColumn.getCellObservableValue(i).getValue()
					* quantityColumn.getCellObservableValue(i).getValue();
			totalProfit += totalItemProfitColumn.getCellObservableValue(i).getValue();
		}

		this.totalBuyPrice.setText(totalBuyPrice + "");
		this.totalSellPrice.setText(totalSellPrice + "");
		this.totalProfit.setText(new DecimalFormat("###.#").format(totalProfit) + "");
	}

	public void makeTableEditable() {

		barcodeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		barcodeColumn.setOnEditCommit(e -> {
			e.getTableView().getItems().get(e.getTablePosition().getRow()).setBarcode(e.getNewValue());
			try {
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad",
						"112233");
				Statement statement = connection.createStatement();
				statement.executeUpdate(
						"update stored set barcode='" + e.getNewValue() + "' where barcode='" + e.getOldValue() + "'");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				table.refresh();
				calculateTotals(data.size());
			}

		});

		itemColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		itemColumn.setOnEditCommit(e -> {
			e.getTableView().getItems().get(e.getTablePosition().getRow()).setName(e.getNewValue());
			String barcode = e.getTableView().getItems().get(e.getTablePosition().getRow()).getBarcode();
			try {
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad",
						"112233");
				Statement statement = connection.createStatement();
				statement.executeUpdate(
						"update stored set name='" + e.getNewValue() + "' where barcode='" + barcode + "'");

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				table.refresh();
				calculateTotals(data.size());
			}
		});

//		quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter() {
			@Override
			public Integer fromString(String value) {
				try {
					return super.fromString(value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return -1;
				}
			}
		}));

		quantityColumn.setOnEditCommit(e -> {
			if (e.getNewValue() == null || e.getNewValue() < 0) {
				table.refresh();
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("≈œŒ«· Œ«ÿ∆");
				alert.setHeaderText(null);
				alert.setContentText("«·—Ã«¡ ≈œŒ«· «·ﬂ„Ì… »‘ﬂ· ’ÕÌÕ");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image("/application/images/icon.png"));
				alert.showAndWait();

			}

			else {
				try {
					e.getTableView().getItems().get(e.getTablePosition().getRow()).setQuant(e.getNewValue());
					String barcode = e.getTableView().getItems().get(e.getTablePosition().getRow()).getBarcode();
					DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
					Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl",
							"ahmad", "112233");
					Statement statement = connection.createStatement();
					statement.executeUpdate(
							"update stored set quantity='" + e.getNewValue() + "' where barcode='" + barcode + "'");

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					table.refresh();
					calculateTotals(data.size());
				}

			}
		});

		buyPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter() {
			@Override
			public Double fromString(String value) {
				try {
					return super.fromString(value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return Double.NaN;
				}
			}
		}));
		buyPriceColumn.setOnEditCommit(e -> {
			if (e.getNewValue() == null || e.getNewValue().isNaN() || e.getNewValue() < 0) {
				table.refresh();
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("≈œŒ«· Œ«ÿ∆");
				alert.setHeaderText(null);
				alert.setContentText("«·—Ã«¡ ≈œŒ«· ”⁄— «·‘—«¡ »‘ﬂ· ’ÕÌÕ");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image("/application/images/icon.png"));
				alert.showAndWait();

			} else {
				e.getTableView().getItems().get(e.getTablePosition().getRow()).setBuyPrice(e.getNewValue());
				String barcode = e.getTableView().getItems().get(e.getTablePosition().getRow()).getBarcode();
				try {
					DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
					Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl",
							"ahmad", "112233");
					Statement statement = connection.createStatement();
					statement.executeUpdate(
							"update stored set buy_price='" + e.getNewValue() + "' where barcode='" + barcode + "'");

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					table.refresh();
					calculateTotals(data.size());
				}

			}
		});

		sellPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter() {
			@Override
			public Double fromString(String value) {
				try {
					return super.fromString(value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return Double.NaN;
				}
			}
		}));
		sellPriceColumn.setOnEditCommit(e -> {
			if (e.getNewValue() == null || e.getNewValue().isNaN() || e.getNewValue() < 0) {
				table.refresh();
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("≈œŒ«· Œ«ÿ∆");
				alert.setHeaderText(null);
				alert.setContentText("«·—Ã«¡ ≈œŒ«· ”⁄— «·»Ì⁄ »‘ﬂ· ’ÕÌÕ");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image("/application/images/icon.png"));
				alert.showAndWait();

			} else {
				e.getTableView().getItems().get(e.getTablePosition().getRow()).setSellPrice(e.getNewValue());
				String barcode = e.getTableView().getItems().get(e.getTablePosition().getRow()).getBarcode();
				try {
					DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
					Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl",
							"ahmad", "112233");
					Statement statement = connection.createStatement();
					statement.executeUpdate(
							"update stored set sell_price='" + e.getNewValue() + "' where barcode='" + barcode + "'");

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					table.refresh();
					calculateTotals(data.size());
				}

			}
		});

		table.setEditable(true);
	}

}
