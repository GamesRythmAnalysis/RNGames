package fr.utbm.rngames.controller;

import fr.utbm.rngames.event.EventDispatcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.arakhne.afc.vmutil.locale.Locale;

public class RootLayoutController {
	/**
	 * Opens an about dialog.
	 */
	@SuppressWarnings("MethodMayBeStatic")
	@FXML
	private void handleAbout() {
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(Locale.getString("alert.about.title")); //$NON-NLS-1$
		alert.setHeaderText(Locale.getString("alert.about.header")); //$NON-NLS-1$
		alert.setContentText(Locale.getString("alert.about.content.text")); //$NON-NLS-1$

		alert.showAndWait();
	}

	/**
	 * Closes the application.
	 */
	@SuppressWarnings("MethodMayBeStatic")
	@FXML
	private void handleExit(ActionEvent event) {
		EventDispatcher.getInstance().notify(new CloseEvent());
	}

}
