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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ImportBillController implements Initializable {

	ArrayList<String> supplierNamesForAutoComplete = new ArrayList<String>();
	ArrayList<String> itemNamesForAutoComplete = new ArrayList<String>();

	ObservableList<Item> data = FXCollections.observableArrayList();

	@FXML
	private DatePicker billDate;

	@FXML
	private TextField distPhone;

	@FXML
	private TextField distName;

	@FXML
	private TableView<Item> table;

	@FXML
	private TableColumn<Item, String> barcodeColumn;

	@FXML
	private TableColumn<Item, String> itemColumn;

	@FXML
	private TableColumn<Item, Integer> quantityColumn;

	@FXML
	private TableColumn<Item, Double> buyPriceColumn;

	@FXML
	private TableColumn<Item, Double> sellPriceColumn;

	@FXML
	private TableColumn<Item, Item> TotalBuyPriceColumn;

	@FXML
	private TableColumn<Item, Double> TotalSellPriceColumn;

	@FXML
	private Label collectTotalBuyPriceLabel;

	@FXML
	private Label collectTotalSellPriceLabel;

	@FXML
	private Button saveButton;

	@FXML
	private Button cancelButton;

	@FXML
	private TextField barcodeReader;

	@FXML
	void buttonHandler(ActionEvent event) throws SQLException {

		if (event.getSource().toString().contains("saveButton")) {

			if (data.size() == 0) {
				// empty table
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("›« Ê—… ›«—€…");
				alert.setHeaderText(null);
				alert.setContentText("«·›« Ê—… ›«—€…° «·—Ã«¡  ⁄»∆… «·”·⁄ «Ê·«");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(new Image("/application/images/icon.png"));
				alert.showAndWait();

			}

			// Data Base Query
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad", "112233");
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			String q = "";

			// if name of supplier doesn't exists
			if (distName.getText().equals("")) {

				for (int i = 0; i < data.size(); i++) {
					/// we should make it here
					if (!data.get(i).getBarcode().equals("")) {
						q = "select * from stored where barcode ='" + data.get(i).getBarcode() + "'";
						ResultSet rs = statement.executeQuery(q);
						int count = 0;
						while (rs.next()) {
							count++;
						}
						if (count == 0) {

							q = "insert into stored values (" + data.get(i).getSellPrice() + ",'"
									+ data.get(i).getBarcode() + "','" + data.get(i).getName() + "',"
									+ data.get(i).getBuyPrice() + "," + data.get(i).getQuant() + ",TO_DATE('"
									+ data.get(i).getExpDate() + "','YYYY-MM-DD'))";
							statement.executeUpdate(q);
						}

						else {
							q = "update stored set quantity=quantity+" + data.get(i).getQuant() + "where barcode ='"
									+ data.get(i).getBarcode() + "'";
							statement.executeUpdate(q);
							q = "update stored set exp_date=TO_DATE('" + data.get(i).getExpDate()
									+ "','YYYY-MM-DD') where barcode = '" + data.get(i).getBarcode() + "'";
							statement.executeUpdate(q);

						}

					}

					else {

						int count = 0;
						int count2 = 0;
						int count3 = 0;
						q = "(select barcode from bought where bought.barcode between '0000000000000'and '0000000001000') union (select barcode from stored where stored.barcode between '0000000000000'and '0000000001000')";
						ResultSet rs = statement.executeQuery(q);
						String bar = "";
						if (!rs.next())
							bar = "0000000000000";
						else
							rs.previous();

						while (rs.next()) {
							bar = rs.getString("barcode");
						}
						boolean flag = false;
						char x = bar.charAt(bar.length() - 1);
						if (x != '9')
							x++;
						else {
							x = '0';
							flag = true;
						}
						char y = bar.charAt(bar.length() - 2);
						if (flag && y != '9') {
							y++;
							flag = false;
						} else if (y == '9') {
							y = '0';
							flag = true;
						}

						char z = bar.charAt(bar.length() - 3);
						if (flag)
							z++;

						String bar2 = "";
						for (int j = 0; j < bar.length() - 3; j++)
							bar2 += bar.charAt(j);

						bar2 += z;
						bar2 += y;
						bar2 += x;
						q = "select * from stored where name ='" + data.get(i).getName() + "'";
						rs = statement.executeQuery(q);
						while (rs.next()) {
							count++;
						}

						if (count != 0) {
							q = "update stored set quantity=quantity+" + data.get(i).getQuant() + " where name='"
									+ data.get(i).getName() + "'";
							statement.executeUpdate(q);
							q = "update stored set exp_date=TO_DATE('" + data.get(i).getExpDate()
									+ "','YYYY-MM-DD') where name = '" + data.get(i).getName() + "'";
							statement.executeUpdate(q);
						} else {
							q = "insert into stored values (" + data.get(i).getSellPrice() + ",'" + bar2 + "','"
									+ data.get(i).getName() + "'," + data.get(i).getBuyPrice() + ","
									+ data.get(i).getQuant() + ",TO_DATE('" + data.get(i).getExpDate()
									+ "','YYYY-MM-DD'))";
							statement.executeUpdate(q);

						}

					}

				}

			}
			// if name of supplier exists
			else {
				q = "select * from supplier where supplier_name ='" + distName.getText() + "'";
				ResultSet rs = statement.executeQuery(q);
				int count = 0;
				while (rs.next()) {
					count++;
				}
				if (count == 0) {
					q = "insert into supplier values('" + distName.getText() + "','" + distPhone.getText() + "')";
					statement.executeUpdate(q);
				}
				// till here i had insert the name of supplier if it doesnt Exist

				for (int i = 0; i < data.size(); i++) {

					if (!data.get(i).getBarcode().equals("")) {

						q = "select * from bought where barcode ='" + data.get(i).getBarcode() + "'";
						rs = statement.executeQuery(q);
						count = 0;
						while (rs.next()) {
							count++;
						}
						if (count == 0) {
							q = "insert into bought values(" + data.get(i).getSellPrice() + ",'"
									+ data.get(i).getBarcode() + "','" + data.get(i).getName() + "',"
									+ data.get(i).getBuyPrice() + ")";
							statement.executeUpdate(q);
							q = "insert into buying_bill values('" + data.get(i).getBarcode() + "',TO_DATE('"
									+ LocalDate.now() + "','YYYY-MM-DD')," + data.get(i).getQuant() + ",'"
									+ distName.getText() + "')";
							statement.executeUpdate(q);

							count = 0;
							q = "select * from stored where barcode='" + data.get(i).getBarcode() + "'";
							rs = statement.executeQuery(q);
							while (rs.next()) {
								count++;
							}

							if (count != 0) {
								q = "update stored set quantity=quantity+" + data.get(i).getQuant() + "where barcode='"
										+ data.get(i).getBarcode() + "'";
								statement.executeUpdate(q);
								q = "update stored set exp_date=TO_DATE('" + data.get(i).getExpDate()
										+ "','YYYY-MM-DD') where name = '" + data.get(i).getName() + "'";
								statement.executeUpdate(q);
							}

							else {
								q = "insert into stored values (" + data.get(i).getSellPrice() + ",'"
										+ data.get(i).getBarcode() + "','" + data.get(i).getName() + "',"
										+ data.get(i).getBuyPrice() + "," + data.get(i).getQuant() + ",TO_DATE('"
										+ data.get(i).getExpDate() + "','YYYY-MM-DD'))";
								statement.executeUpdate(q);

							}

						}

						else {
							q = "insert into buying_bill values('" + data.get(i).getBarcode() + "',TO_DATE('"
									+ LocalDate.now() + "','YYYY-MM-DD')," + data.get(i).getQuant() + ",'"
									+ distName.getText() + "')";
							statement.executeUpdate(q);

							if (count != 0) {
								q = "update stored set quantity=quantity+" + data.get(i).getQuant() + "where barcode='"
										+ data.get(i).getBarcode() + "'";
								statement.executeUpdate(q);
								q = "update stored set exp_date=TO_DATE('" + data.get(i).getExpDate()
										+ "','YYYY-MM-DD') where name = '" + data.get(i).getName() + "'";
								statement.executeUpdate(q);
							} else {
								q = "insert into stored values (" + data.get(i).getSellPrice() + ",'"
										+ data.get(i).getName() + "','" + data.get(i).getName() + "',"
										+ data.get(i).getBuyPrice() + "," + data.get(i).getQuant() + ",TO_DATE('"
										+ data.get(i).getExpDate() + "','YYYY-MM-DD'))";
								statement.executeUpdate(q);

							}

						}

					}

					else {
						// here if supplier exists but barcode doesn't
						int countb = 0;
						int count2 = 0;
						int count3 = 0;
						q = "(select barcode from bought where bought.barcode between '0000000000000'and '0000000001000') union (select barcode from stored where stored.barcode between '0000000000000'and '0000000001000')";
						ResultSet rss = statement.executeQuery(q);
						String bar = "";
						if (!rss.next())
							bar = "0000000000000";
						else
							rss.previous();
						while (rss.next()) {
							bar = rss.getString("barcode");
						}
						boolean flag = false;
						char x = bar.charAt(bar.length() - 1);
						if (x != '9')
							x++;
						else {
							x = '0';
							flag = true;
						}
						char y = bar.charAt(bar.length() - 2);
						if (flag && y != '9') {
							y++;
							flag = false;
						} else if (y == '9') {
							y = '0';
							flag = true;
						}

						char z = bar.charAt(bar.length() - 3);
						if (flag)
							z++;

						String bar2 = "";
						for (int j = 0; j < bar.length() - 3; j++)
							bar2 += bar.charAt(j);

						bar2 += z;
						bar2 += y;
						bar2 += x;
						q = "select * from stored where name ='" + data.get(i).getName() + "'";
						rss = statement.executeQuery(q);
						while (rss.next()) {
							countb++;
						}
						int countc = 0;
						q = "select * from bought where name='" + data.get(i).getName() + "'";
						rss = statement.executeQuery(q);
						while (rss.next()) {
							countc++;
						}

						if (countb != 0) {
							q = "update stored set quantity=quantity+" + data.get(i).getQuant() + " where name='"
									+ data.get(i).getName() + "'";
							statement.executeUpdate(q);
							q = "update stored set exp_date=TO_DATE('" + data.get(i).getExpDate()
									+ "','YYYY-MM-DD') where name = '" + data.get(i).getName() + "'";
							statement.executeUpdate(q);
							if (countc == 0) {
								q = "select barcode from stored where name='" + data.get(i).getName() + "'";
								rss = statement.executeQuery(q);
								rss.next();
								bar2 = rss.getString("barcode");
								q = "insert into buying_bill values('" + bar2 + "',TO_DATE('" + LocalDate.now()
										+ "','YYYY-MM-DD')," + data.get(i).getQuant() + ",'" + distName.getText()
										+ "')";
								statement.executeUpdate(q);
							} else {
								q = "select barcode from bought where name='" + data.get(i).getName() + "'";
								rss = statement.executeQuery(q);
								rss.next();
								bar2 = rss.getString("barcode");

								q = "insert into buying_bill values('" + bar2 + "',TO_DATE('" + LocalDate.now()
										+ "','YYYY-MM-DD')," + data.get(i).getQuant() + ",'" + distName.getText()
										+ "')";
								statement.executeUpdate(q);
							}

						} else {
							if (countc == 0) {
								q = "insert into stored values (" + data.get(i).getSellPrice() + ",'" + bar2 + "','"
										+ data.get(i).getName() + "'," + data.get(i).getBuyPrice() + ","
										+ data.get(i).getQuant() + ",TO_DATE('" + data.get(i).getExpDate()
										+ "','YYYY-MM-DD'))";
								statement.executeUpdate(q);
								q = "insert into buying_bill values('" + bar2 + "',TO_DATE('" + LocalDate.now()
										+ "','YYYY-MM-DD')," + data.get(i).getQuant() + ",'" + distName.getText()
										+ "')";
								statement.executeUpdate(q);
							} else {
								q = "select barcode from bought where name='" + data.get(i).getName() + "'";
								ResultSet rsm = statement.executeQuery(q);
								rsm.next();
								bar2 = rsm.getString("barcode");

								q = "insert into stored values (" + data.get(i).getSellPrice() + ",'" + bar2 + "','"
										+ data.get(i).getName() + "'," + data.get(i).getBuyPrice() + ","
										+ data.get(i).getQuant() + ",TO_DATE('" + data.get(i).getExpDate()
										+ "','YYYY-MM-DD'))";
								statement.executeUpdate(q);
								q = "insert into buying_bill values('" + bar2 + "',TO_DATE('" + LocalDate.now()
										+ "','YYYY-MM-DD')," + data.get(i).getQuant() + ",'" + distName.getText()
										+ "')";
								statement.executeUpdate(q);

							}

						}

						q = "select * from bought where name='" + data.get(i).getName() + "'";
						ResultSet rs3 = statement.executeQuery(q);
						int count4 = 0;
						while (rs3.next()) {
							count4++;
						}
						q = "select * from stored where name='" + data.get(i).getName() + "'";
						ResultSet rs4 = statement.executeQuery(q);
						int count5 = 0;
						while (rs4.next()) {
							count5++;
						}

						if (count4 == 0) {
							if (count5 == 0) {
								q = "insert into bought values(" + data.get(i).getSellPrice() + ",'" + bar2 + "','"
										+ data.get(i).getName() + "'," + data.get(i).getBuyPrice() + ")";
								statement.executeUpdate(q);
							} else {
								q = "select *from stored where name='" + data.get(i).getName() + "'";
								ResultSet rsss = statement.executeQuery(q);
								rsss.next();
								bar2 = rsss.getString("barcode");
								q = "insert into bought values(" + data.get(i).getSellPrice() + ",'" + bar2 + "','"
										+ data.get(i).getName() + "'," + data.get(i).getBuyPrice() + ")";
								statement.executeUpdate(q);
							}

						}

					}

				}

			}

			data.clear();
			table.refresh();

		}

		else {
			data.clear();
			table.refresh();
		}

	}

	@FXML
	void barcodeHandler(KeyEvent event) throws SQLException {
		String barcode = "", itemName = "";
		int quant = 0;
		double buyPrice = 0, sellPrice = 0, totalBP = 0, totalSP = 0;
		LocalDate expDate = null;
		boolean cancle = false;

		if (event.getSource().toString().contains("barcodeReader"))
			if (event.getCode().equals(KeyCode.ENTER)) {
				barcode = barcodeReader.getText();

				// Data Base Query
				DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
				Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad",
						"112233");
				Statement statement = connection.createStatement();
				String q = "";

				// barcode written
				if (!barcodeReader.getText().equals("")) {
					int count = 0;
					q = "select * from stored where barcode='" + barcodeReader.getText() + "'";
					ResultSet rs = statement.executeQuery(q);
					while (rs.next()) {
						count++;
					}

					if (count == 0) {
						// barcode is new
						Dialog<String> nameDialog = new Dialog<>();
						try {
							nameDialog.setTitle("«”„ «·”·⁄…");
							nameDialog.setHeaderText("«·—Ã«¡ «œŒ«· «”„ «·”·⁄…");

							Stage stage2 = (Stage) nameDialog.getDialogPane().getScene().getWindow();
							stage2.getIcons().add(new Image("/application/images/icon.png"));

							TextField nameTF = new TextField("");
							TextFields.bindAutoCompletion(nameTF, itemNamesForAutoComplete);
							nameDialog.getDialogPane().setContent(nameTF);

							ButtonType buttonTypeOk2 = new ButtonType(" √ﬂÌœ", ButtonData.OK_DONE);
							nameDialog.getDialogPane().getButtonTypes().add(buttonTypeOk2);

							nameDialog.setResultConverter(new Callback<ButtonType, String>() {
								@Override
								public String call(ButtonType b) {

									if (b == buttonTypeOk2) {
										return nameTF.getText();
									}

									return null;
								}
							});

							itemName = nameDialog.showAndWait().get();
							if (itemName.equals(""))
								throw new Exception();

						} catch (Exception e) {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("«”„ Œ«ÿ∆");
							alert.setHeaderText(null);
							alert.setContentText("«·—Ã«¡ ≈œŒ«· «”„ «·”·⁄…");
							Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image("/application/images/icon.png"));
							alert.showAndWait();
							cancle = true;

						}
						//////

						TextInputDialog dialog = new TextInputDialog();
//                       
						dialog = new TextInputDialog();
						dialog.setTitle("«·ﬂ„Ì…");
						dialog.setHeaderText("«·—Ã«¡ ≈œŒ«· «·ﬂ„Ì…");
						if (!cancle)
							try {
								quant = Integer.parseInt(dialog.showAndWait().get());
							} catch (Exception e) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("≈œŒ«· Œ«ÿ∆");
								alert.setHeaderText(null);
								alert.setContentText("«·—Ã«¡ ≈œŒ«· «·ﬂ„Ì…");
								Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
								stage.getIcons().add(new Image("/application/images/icon.png"));
								alert.showAndWait();
								cancle = true;
							}
//                       
						dialog = new TextInputDialog();
						dialog.setTitle("”⁄— «·‘—«¡");
						dialog.setHeaderText("«·—Ã«¡ ≈œŒ«· ”⁄— ‘—«¡ «·ÊÕœ…");
						if (!cancle)
							try {
								buyPrice = Double.parseDouble(dialog.showAndWait().get());
							} catch (Exception e) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("≈œŒ«· Œ«ÿ∆");
								alert.setHeaderText(null);
								alert.setContentText("«·—Ã«¡ ≈œŒ«· ”⁄— «·‘—«¡");
								Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
								stage.getIcons().add(new Image("/application/images/icon.png"));
								alert.showAndWait();
								cancle = true;
							}
//                       
						dialog = new TextInputDialog();
						dialog.setTitle("”⁄— «·»Ì⁄");
						dialog.setHeaderText("«·—Ã«¡ ≈œŒ«· ”⁄— »Ì⁄ «·ÊÕœ…");
						if (!cancle)
							try {
								sellPrice = Double.parseDouble(dialog.showAndWait().get());
							} catch (Exception e) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("≈œŒ«· Œ«ÿ∆");
								alert.setHeaderText(null);
								alert.setContentText("«·—Ã«¡ ≈œŒ«· ”⁄— «·»Ì⁄");
								Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
								stage.getIcons().add(new Image("/application/images/icon.png"));
								alert.showAndWait();
								cancle = true;
							}
						//
						// show Calender dialog
						Dialog<LocalDate> dateDialog = new Dialog<>();
						try {
							dateDialog.setTitle(" «—ÌŒ «‰ Â«¡ «·”·⁄…");
							dateDialog.setHeaderText("«·—Ã«¡ «œŒ«·  «—ÌŒ «‰ Â«¡ «·”·⁄…");
							Stage stage = (Stage) dateDialog.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image("/application/images/icon.png"));

							DatePicker datePicker = new DatePicker();
							dateDialog.getDialogPane().setContent(datePicker);

							ButtonType buttonTypeOk = new ButtonType(" √ﬂÌœ", ButtonData.OK_DONE);
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

							if (!cancle)
								expDate = dateDialog.showAndWait().get();
						} catch (Exception e) {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("≈œŒ«· Œ«ÿ∆");
							alert.setHeaderText(null);
							alert.setContentText("«·—Ã«¡ ≈œŒ«·  «—ÌŒ ≈‰ Â«¡ «·”·⁄…");
							Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image("/application/images/icon.png"));
							alert.showAndWait();
							cancle = true;
						}

					} else {
						rs = statement.executeQuery(q);

						TextInputDialog dialog = new TextInputDialog();
						//
						dialog = new TextInputDialog();
						dialog.setTitle("«·ﬂ„Ì…");
						dialog.setHeaderText("«·—Ã«¡ ≈œŒ«· «·ﬂ„Ì…");
						if (!cancle)
							try {
								quant = Integer.parseInt(dialog.showAndWait().get());
							} catch (Exception e) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("≈œŒ«· Œ«ÿ∆");
								alert.setHeaderText(null);
								alert.setContentText("«·—Ã«¡ ≈œŒ«· «·ﬂ„Ì…");
								Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
								stage.getIcons().add(new Image("/application/images/icon.png"));
								alert.showAndWait();
								cancle = true;
							}
						//
						// show Calender dialog
						Dialog<LocalDate> dateDialog = new Dialog<>();
						try {
							dateDialog.setTitle(" «—ÌŒ «‰ Â«¡ «·”·⁄…");
							dateDialog.setHeaderText("«·—Ã«¡ «œŒ«·  «—ÌŒ «‰ Â«¡ «·”·⁄…");
							Stage stage = (Stage) dateDialog.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image("/application/images/icon.png"));

							DatePicker datePicker = new DatePicker();
							dateDialog.getDialogPane().setContent(datePicker);

							ButtonType buttonTypeOk = new ButtonType(" √ﬂÌœ", ButtonData.OK_DONE);
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

							if (!cancle)
								expDate = dateDialog.showAndWait().get();
						} catch (Exception e) {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("≈œŒ«· Œ«ÿ∆");
							alert.setHeaderText(null);
							alert.setContentText("«·—Ã«¡ ≈œŒ«·  «—ÌŒ ≈‰ Â«¡ «·”·⁄…");
							Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image("/application/images/icon.png"));
							alert.showAndWait();
							cancle = true;
						}
						
						rs.next();
						sellPrice = rs.getDouble("sell_price");
						buyPrice = rs.getDouble("buy_price");
						itemName = rs.getString("Name");

					}
				}
				// barcode not written
				else {
					// ask for the name
					////////
					Dialog<String> nameDialog = new Dialog<>();
					try {
						nameDialog.setTitle("«”„ «·”·⁄…");
						nameDialog.setHeaderText("«·—Ã«¡ «œŒ«· «”„ «·”·⁄…");

						Stage stage2 = (Stage) nameDialog.getDialogPane().getScene().getWindow();
						stage2.getIcons().add(new Image("/application/images/icon.png"));

						TextField nameTF = new TextField("");
						TextFields.bindAutoCompletion(nameTF, itemNamesForAutoComplete);
						nameDialog.getDialogPane().setContent(nameTF);

						ButtonType buttonTypeOk2 = new ButtonType(" √ﬂÌœ", ButtonData.OK_DONE);
						nameDialog.getDialogPane().getButtonTypes().add(buttonTypeOk2);

						nameDialog.setResultConverter(new Callback<ButtonType, String>() {
							@Override
							public String call(ButtonType b) {

								if (b == buttonTypeOk2) {

									return nameTF.getText();
								}

								return null;
							}
						});

						itemName = nameDialog.showAndWait().get();
						if (itemName.equals(""))
							throw new Exception();

					} catch (Exception e) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("«”„ Œ«ÿ∆");
						alert.setHeaderText(null);
						alert.setContentText("«·—Ã«¡ ≈œŒ«· «”„ «·”·⁄…");
						Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
						stage.getIcons().add(new Image("/application/images/icon.png"));
						alert.showAndWait();
						cancle = true;

					}
					q = "select * from stored where name='" + itemName + "'";
					int count = 0;
					ResultSet rs = statement.executeQuery(q);
					while (rs.next()) {
						count++;
					}

					if (count != 0) {
						// ask for date & quant
						rs = statement.executeQuery(q);
						rs.next();
						sellPrice = rs.getDouble("sell_price");
						buyPrice = rs.getDouble("buy_price");
						TextInputDialog dialog = new TextInputDialog();

						//
						dialog = new TextInputDialog();
						dialog.setTitle("«·ﬂ„Ì…");
						dialog.setHeaderText("«·—Ã«¡ ≈œŒ«· «·ﬂ„Ì…");
						if (!cancle)
							try {
								quant = Integer.parseInt(dialog.showAndWait().get());
							} catch (Exception e) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("≈œŒ«· Œ«ÿ∆");
								alert.setHeaderText(null);
								alert.setContentText("«·—Ã«¡ ≈œŒ«· «·ﬂ„Ì…");
								Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
								stage.getIcons().add(new Image("/application/images/icon.png"));
								alert.showAndWait();
								cancle = true;
							}
						//
						// show Calender dialog
						Dialog<LocalDate> dateDialog = new Dialog<>();
						try {
							dateDialog.setTitle(" «—ÌŒ «‰ Â«¡ «·”·⁄…");
							dateDialog.setHeaderText("«·—Ã«¡ «œŒ«·  «—ÌŒ «‰ Â«¡ «·”·⁄…");
							Stage stage = (Stage) dateDialog.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image("/application/images/icon.png"));

							DatePicker datePicker = new DatePicker();
							dateDialog.getDialogPane().setContent(datePicker);

							ButtonType buttonTypeOk = new ButtonType(" √ﬂÌœ", ButtonData.OK_DONE);
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

							if (!cancle)
								expDate = dateDialog.showAndWait().get();
						} catch (Exception e) {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("≈œŒ«· Œ«ÿ∆");
							alert.setHeaderText(null);
							alert.setContentText("«·—Ã«¡ ≈œŒ«·  «—ÌŒ ≈‰ Â«¡ «·”·⁄…");
							Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image("/application/images/icon.png"));
							alert.showAndWait();
							cancle = true;
						}

					}

					else {
						// ask for everything

						TextInputDialog dialog = new TextInputDialog();

//                   
						dialog = new TextInputDialog();
						dialog.setTitle("«·ﬂ„Ì…");
						dialog.setHeaderText("«·—Ã«¡ ≈œŒ«· «·ﬂ„Ì…");
						if (!cancle)
							try {
								quant = Integer.parseInt(dialog.showAndWait().get());
							} catch (Exception e) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("≈œŒ«· Œ«ÿ∆");
								alert.setHeaderText(null);
								alert.setContentText("«·—Ã«¡ ≈œŒ«· «·ﬂ„Ì…");
								Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
								stage.getIcons().add(new Image("/application/images/icon.png"));
								alert.showAndWait();
								cancle = true;
							}
//                       
						dialog = new TextInputDialog();
						dialog.setTitle("”⁄— «·‘—«¡");
						dialog.setHeaderText("«·—Ã«¡ ≈œŒ«· ”⁄— ‘—«¡ «·ÊÕœ…");
						if (!cancle)
							try {
								buyPrice = Double.parseDouble(dialog.showAndWait().get());
							} catch (Exception e) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("≈œŒ«· Œ«ÿ∆");
								alert.setHeaderText(null);
								alert.setContentText("«·—Ã«¡ ≈œŒ«· ”⁄— «·‘—«¡");
								Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
								stage.getIcons().add(new Image("/application/images/icon.png"));
								alert.showAndWait();
								cancle = true;
							}
//                       
						dialog = new TextInputDialog();
						dialog.setTitle("”⁄— «·»Ì⁄");
						dialog.setHeaderText("«·—Ã«¡ ≈œŒ«· ”⁄— »Ì⁄ «·ÊÕœ…");
						if (!cancle)
							try {
								sellPrice = Double.parseDouble(dialog.showAndWait().get());
							} catch (Exception e) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("≈œŒ«· Œ«ÿ∆");
								alert.setHeaderText(null);
								alert.setContentText("«·—Ã«¡ ≈œŒ«· ”⁄— «·»Ì⁄");
								Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
								stage.getIcons().add(new Image("/application/images/icon.png"));
								alert.showAndWait();
								cancle = true;
							}
						//

						// show Calender dialog
						Dialog<LocalDate> dateDialog = new Dialog<>();
						try {
							dateDialog.setTitle(" «—ÌŒ «‰ Â«¡ «·”·⁄…");
							dateDialog.setHeaderText("«·—Ã«¡ «œŒ«·  «—ÌŒ «‰ Â«¡ «·”·⁄…");
							Stage stage = (Stage) dateDialog.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image("/application/images/icon.png"));

							DatePicker datePicker = new DatePicker();
							dateDialog.getDialogPane().setContent(datePicker);

							ButtonType buttonTypeOk = new ButtonType(" √ﬂÌœ", ButtonData.OK_DONE);
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

							if (!cancle)
								expDate = dateDialog.showAndWait().get();
						} catch (Exception e) {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("≈œŒ«· Œ«ÿ∆");
							alert.setHeaderText(null);
							alert.setContentText("«·—Ã«¡ ≈œŒ«·  «—ÌŒ ≈‰ Â«¡ «·”·⁄…");
							Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
							stage.getIcons().add(new Image("/application/images/icon.png"));
							alert.showAndWait();
							cancle = true;
						}

					}
				}

				if (!cancle)
					data.add(new Item(barcode, itemName, quant, buyPrice, sellPrice, expDate, buyPrice * quant,
							sellPrice * quant));

				table.refresh();
				barcodeReader.clear();

			}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(() -> barcodeReader.requestFocus());
		billDate.setValue(LocalDate.now());

		table.setItems(data);

		barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));
		itemColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quant"));
		buyPriceColumn.setCellValueFactory(new PropertyValueFactory<>("buyPrice"));
		sellPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
		TotalBuyPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalBP"));
		TotalSellPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalSP"));

		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad", "112233");
			Statement statement = connection.createStatement();
			String q = "select supplier_name from supplier";
			ResultSet rs = statement.executeQuery(q);
			while (rs.next()) {
				supplierNamesForAutoComplete.add(rs.getString("supplier_name"));
			}
			TextFields.bindAutoCompletion(distName, supplierNamesForAutoComplete);

			q = "select name from stored";
			rs = statement.executeQuery(q);
			while (rs.next()) {
				itemNamesForAutoComplete.add(rs.getString("name"));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}