package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController implements Initializable {

	public int offTime = 0;

	@FXML
	private Button close;

	@FXML
	private Button sellViewButton;

	@FXML
	private Button dailySalesButton;

	@FXML
	private Button storeInventoryButton;

	@FXML
	private Button importBillButton;

	@FXML
	private Button importBillsButton;

	@FXML
	private Button backupButton;

	@FXML
	private StackPane stack;

	@FXML
	private AnchorPane loginForm;

	@FXML
	private TextField username;

	@FXML
	private Button login;

	@FXML
	private PasswordField password;

	@FXML
	private VBox menu;

	@FXML
	private Button signOutButton;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		menu.translateXProperty().set(200);

		Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				offTime++;
				if (offTime == 900) {
					Platform.runLater(() -> {
						signOut();
						offTime = 0;
					});

				}
			}
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();

	}

	@FXML
	void buttonHandler(ActionEvent event) throws IOException {
		
		offTime=0;

		if (event.getSource().toString().contains("sellViewButton")) {
			stack.getChildren().clear();
			stack.getChildren()
					.add((AnchorPane) FXMLLoader.load(getClass().getResource("/application/fxml/SellView.fxml")));
		} else if (event.getSource().toString().contains("dailySalesButton")) {
			stack.getChildren().clear();
			stack.getChildren()
					.add((AnchorPane) FXMLLoader.load(getClass().getResource("/application/fxml/DailySales.fxml")));
		} else if (event.getSource().toString().contains("storeInventoryButton")) {
			stack.getChildren().clear();
			stack.getChildren()
					.add((AnchorPane) FXMLLoader.load(getClass().getResource("/application/fxml/StoreInventory.fxml")));
		} else if (event.getSource().toString().contains("importBillButton")) {
			stack.getChildren().clear();
			stack.getChildren()
					.add((AnchorPane) FXMLLoader.load(getClass().getResource("/application/fxml/ImportBill.fxml")));
		} else if (event.getSource().toString().contains("importBillsButton")) {
			stack.getChildren().clear();
			stack.getChildren()
					.add((AnchorPane) FXMLLoader.load(getClass().getResource("/application/fxml/ImportBills.fxml")));
		} else if (event.getSource().toString().contains("backupButton")) {

		} else if (event.getSource().toString().contains("login")) {
//			Username and Password
			   if(username.getText().toString().equalsIgnoreCase("Apache") && password.getText().toString().equals("22023202")) {
			Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5),
					new KeyValue(menu.translateXProperty(), 0, Interpolator.EASE_IN)));
			stack.getChildren().remove(loginForm);
			timeline.play();

			try {
				checkDates();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			   } else {
			   Alert alert = new Alert(AlertType.WARNING);
			   alert.setTitle("»Ì«‰«  œŒÊ· Œ«ÿ∆…");
			   alert.setHeaderText(null);
			   alert.setContentText("«·—Ã«¡ «· √ﬂœ „‰ «”„ «·„” Œœ„ √Ê ﬂ·„… «·„—Ê—");
			   Stage stage =(Stage) alert.getDialogPane().getScene().getWindow();
			   stage.getIcons().add(new Image("/application/images/icon.png"));

			   alert.showAndWait();
		   }

		} else if (event.getSource().toString().contains("signOutButton")) {
			signOut();
		}

		else if (event.getSource().toString().contains("close")) {
			System.exit(0);
		}
	}

	@FXML
	void onMuseMoved(MouseEvent event) {
		offTime = 0;
	}

	public void signOut() {
		this.stack.getChildren().clear();
		this.menu.translateXProperty().set(200);
		this.password.clear();
		this.stack.getChildren().add(loginForm);
	}

	public void closeHandler(javafx.scene.input.MouseEvent event) {
		System.exit(0);
	}

	public void checkDates() throws SQLException {

		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "ahmad", "112233");
		Statement statement = connection.createStatement();
		String q = "select name,exp_date from stored";
		ResultSet rs = statement.executeQuery(q);

		LocalDate now = LocalDate.now();
		int diff, diff2;

		while (rs.next()) {
			diff = Period.between(now, rs.getDate("exp_date").toLocalDate()).getMonths();
			diff2 = Period.between(now, rs.getDate("exp_date").toLocalDate()).getYears();
			if (diff < 1 && diff2 < 1) {
				Notifications notifications = Notifications.create().title(" ‰»ÌÂ ≈ﬁ —«»  «—ÌŒ ≈‰ Â«¡ ”·⁄…")
						.text(" «—ÌŒ «‰ Â«¡ " + rs.getString("name") + "\n" + rs.getDate("exp_date").toLocalDate())
						.graphic(null).position(Pos.BOTTOM_RIGHT).darkStyle().hideAfter(Duration.INDEFINITE);

				notifications.showWarning();

			}

		}

	}

}
