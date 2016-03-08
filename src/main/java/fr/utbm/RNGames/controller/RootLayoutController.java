package fr.utbm.RNGames.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

@SuppressWarnings("static-method")
public class RootLayoutController {

	/**
	 * Opens an about dialog.
	 */
	@FXML
	private void handleAbout() {
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("RNGames");
		alert.setHeaderText("About");
		alert.setContentText("Authors: Jérôme BOULMIER & Benoît CORTIER\n"
				+ "Website: http://nononono.no");

		alert.showAndWait();
	}

	/**
	 * Closes the application.
	 */
	@FXML
	private void handleExit() {
		System.exit(0);
	}

}
