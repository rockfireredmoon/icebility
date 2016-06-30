package org.icebility;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	static ResourceBundle BUNDLE = ResourceBundle.getBundle(Main.class
			.getName());
	
	public final static Preferences PREFS = Preferences.userNodeForPackage(Main.class);

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle(BUNDLE.getString("title"));

		URL resource = Abilities.class.getResource(Abilities.class
				.getSimpleName() + ".fxml");
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(BUNDLE);
		Parent root = loader.load(resource.openStream());
		Abilities abilities = (Abilities) loader.getController();
		// root.getStylesheets().add(
		// controller.getResource(Client.class.getSimpleName() + ".css")
		// .toExternalForm());
		abilities.configure(primaryStage);;
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
