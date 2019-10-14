package application;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	// define your offsets here
	private double xOffset = 0;
	private double yOffset = 0;

	@Override
	public void start(Stage primaryStage) {
		try {

			AnchorPane root = FXMLLoader.load(getClass().getResource("/application/fxml/MianFXML.fxml"));

			// grab your root here
			root.setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					xOffset = event.getSceneX();
					yOffset = event.getSceneY();
				}
			});

			// move around here
			root.setOnMouseDragged(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					primaryStage.setX(event.getScreenX() - xOffset);
					primaryStage.setY(event.getScreenY() - yOffset);
				}
			});

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.getIcons().add(new Image("/application/images/icon.png"));
			primaryStage.setTitle("SuperMarket DataBase");
			primaryStage.setResizable(false);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
