package fr.utbm.RNGames.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.arakhne.afc.vmutil.locale.Locale;

import fr.utbm.RNGames.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.DirectoryChooser;

public class MainWindowController {

	@FXML
	private TextField textAreaSaveDirectory;

	@FXML
	private Button buttonSelectDirectory;

	@FXML
	private TextField textAreaRecordName;

	@FXML
	private ToggleButton toggleButtonKeyboard;

	@FXML
	private ToggleButton toggleButtonMouse;

	@FXML
	private ToggleButton toggleButtonGamepad;

	@FXML
	private Button buttonStartRecording;

	@FXML
	private Button buttonStopRecording;

	// Reference to the main application
	private App app;

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param app
	 */
	public void setApp(App app) {
		this.app = app;
	}

	/**
	 * Open a DirectoryChooser window.
	 */
	@FXML
	private void handleSelectFolder() {
		final DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle(Locale.getString(MainWindowController.class, "directory.chooser.title")); //$NON-NLS-1$

		if (!this.textAreaSaveDirectory.getText().equals("")) { //$NON-NLS-1$
			final File defaultDirectory = new File(this.textAreaSaveDirectory.getText());
			if (defaultDirectory.exists()) {
				chooser.setInitialDirectory(defaultDirectory);
			}
		}

		final File selectedDirectory = chooser.showDialog(this.app.getPrimaryStage());

		if (selectedDirectory != null) {
			this.textAreaSaveDirectory.setText(selectedDirectory.toString());
		}
	}

	@FXML
	private void handleStartRecording(@SuppressWarnings("unused") ActionEvent event) {
		if (!isReadyToRecord()) {
			return;
		}

		this.textAreaSaveDirectory.disableProperty().set(true);
		this.textAreaRecordName.disableProperty().set(true);
		this.buttonSelectDirectory.disableProperty().set(true);
		this.toggleButtonGamepad.disableProperty().set(true);
		this.toggleButtonKeyboard.disableProperty().set(true);
		this.toggleButtonMouse.disableProperty().set(true);
		this.buttonStartRecording.disableProperty().set(true);
		this.buttonStopRecording.disableProperty().set(false);
	}

	@FXML
	private void handleStopRecording(@SuppressWarnings("unused") ActionEvent event) {
		this.textAreaSaveDirectory.disableProperty().set(false);
		this.textAreaRecordName.disableProperty().set(false);
		this.buttonSelectDirectory.disableProperty().set(false);
		this.toggleButtonGamepad.disableProperty().set(false);
		this.toggleButtonKeyboard.disableProperty().set(false);
		this.toggleButtonMouse.disableProperty().set(false);
		this.buttonStartRecording.disableProperty().set(false);
		this.buttonStopRecording.disableProperty().set(true);
	}

	/**
	 * Check if the recording can be started.
	 *
	 * @return true if the recording can be started.
	 */
	private boolean isReadyToRecord() {
		final List<String> errorMessages = new ArrayList<>();

		if (this.textAreaSaveDirectory.getText() == null
				|| this.textAreaSaveDirectory.getText().length() == 0) {
			errorMessages.add(Locale.getString(MainWindowController.class, "error.no.save.directory")); //$NON-NLS-1$
		} else if (!new File(this.textAreaSaveDirectory.getText()).exists()) {
			errorMessages.add(Locale.getString(MainWindowController.class, "error.invalid.save.directory")); //$NON-NLS-1$
		}

		if (this.textAreaRecordName.getText() == null
				|| this.textAreaRecordName.getText().length() == 0) {
			errorMessages.add(Locale.getString(MainWindowController.class, "error.no.record.name")); //$NON-NLS-1$
		}

		if (!this.toggleButtonGamepad.isSelected()
				&& !this.toggleButtonKeyboard.isSelected()
				&& !this.toggleButtonMouse.isSelected()) {
			errorMessages.add(Locale.getString(MainWindowController.class, "error.no.device")); //$NON-NLS-1$
		}

		if (errorMessages.size() == 0) {
			return true;
		}

		// Show the error message.
		final Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(this.app.getPrimaryStage());
		alert.setTitle(Locale.getString(MainWindowController.class, "alert.error.title")); //$NON-NLS-1$
		alert.setHeaderText(Locale.getString(MainWindowController.class, "alert.error.header")); //$NON-NLS-1$
		alert.setContentText(String.join("\n", errorMessages)); //$NON-NLS-1$);

		alert.showAndWait();

		return false;
	}

}
