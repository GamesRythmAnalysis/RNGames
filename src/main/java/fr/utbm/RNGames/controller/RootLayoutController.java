package fr.utbm.RNGames.controller;

import org.arakhne.afc.vmutil.locale.Locale;

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
		alert.setTitle(Locale.getString(RootLayoutController.class, "alert.about.title")); //$NON-NLS-1$
		alert.setHeaderText(Locale.getString(RootLayoutController.class, "alert.about.header")); //$NON-NLS-1$
		alert.setContentText(Locale.getString(RootLayoutController.class, "alert.about.content.text")); //$NON-NLS-1$

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
