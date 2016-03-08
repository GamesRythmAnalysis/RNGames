package fr.utbm.RNGames;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import fr.utbm.RNGames.controller.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

	private Stage primStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primStage = primaryStage;
		this.primStage.setTitle("RNGames"); //$NON-NLS-1$

		initRootLayout();

		showMainWindow();
	}

	/**
	 * Returns the main stage.
	 * @return
	 */
	public Stage getPrimaryStage() {
		return this.primStage;
	}

	/**
	 * Initializes the root layout.
	 */
	private void initRootLayout() {
		try {
			// Load root layout from fxml file.
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(App.class.getResource("templates/RootLayout_i18n/RootLayout_i18n.fxml")); //$NON-NLS-1$
			loader.setResources(ResourceBundle.getBundle("fr.utbm.RNGames.templates.RootLayout_i18n.RootLayout", Locale.getDefault())); //$NON-NLS-1$
			this.rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			final Scene scene = new Scene(this.rootLayout);
			this.primStage.setScene(scene);

			this.primStage.show();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the main window inside the root layout.
	 */
	private void showMainWindow() {
		try {
			// Load person overview.
			final FXMLLoader loader = new FXMLLoader();
			loader.setLocation(App.class.getResource("templates/MainWindow_i18n/MainWindow_i18n.fxml")); //$NON-NLS-1$
			loader.setResources(ResourceBundle.getBundle("fr.utbm.RNGames.templates.MainWindow_i18n.MainWindow", Locale.getDefault())); //$NON-NLS-1$
			final AnchorPane mainWindow = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			this.rootLayout.setCenter(mainWindow);

			// Give the controller access to the main app.
			final MainWindowController controller = loader.getController();
			controller.setApp(this);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
