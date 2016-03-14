package fr.utbm.rngames.controller;

import org.arakhne.afc.vmutil.locale.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class RootLayoutController {

	/**
	 * Opens an about dialog.
	 */
	@SuppressWarnings("MethodMayBeStatic")
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
	@SuppressWarnings("MethodMayBeStatic")
	@FXML
	private void handleExit(ActionEvent event) {
		Node node = (Node) event.getSource();
		Stage stage = (Stage) node.getScene().getWindow();

		stage.close();
	}

}
