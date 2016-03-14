package fr.utbm.rngames.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import fr.utbm.rngames.Zipper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.Initializable;
import org.arakhne.afc.vmutil.locale.Locale;

import fr.utbm.rngames.App;
import fr.utbm.rngames.keyboard.KeyboardWriter;
import fr.utbm.rngames.mouse.MouseWriter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.DirectoryChooser;

public class MainWindowController implements Initializable {
	private final Logger logger = Logger.getLogger(MainWindowController.class.getName());

	private final List<URL> fileLocations = new ArrayList<>();

	private KeyboardWriter kWriter;
	private MouseWriter mWriter;
	private final BooleanProperty startDisabled = new SimpleBooleanProperty(false);

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
	private ToggleButton toggleButtonGamePad;

	@FXML
	private Button buttonStartRecording;

	@FXML
	private Button buttonStopRecording;

	// Reference to the main application
	private App app;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.textAreaSaveDirectory.disableProperty().bind(this.startDisabled);
		this.textAreaSaveDirectory.disableProperty().bind(this.startDisabled);
		this.textAreaRecordName.disableProperty().bind(this.startDisabled);
		this.buttonSelectDirectory.disableProperty().bind(this.startDisabled);
		this.toggleButtonGamePad.disableProperty().bind(this.startDisabled);
		this.toggleButtonKeyboard.disableProperty().bind(this.startDisabled);
		this.toggleButtonMouse.disableProperty().bind(this.startDisabled);
		this.buttonStartRecording.disableProperty().bind(this.startDisabled);
		this.buttonStopRecording.disableProperty().bind(this.startDisabled.not());
	}

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

		if (!this.textAreaSaveDirectory.getText().isEmpty()) {
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
	private void handleStartRecording() {
		if (!isReadyToRecord()) {
			return;
		}

		if (this.toggleButtonKeyboard.isSelected()) {
			try {
				this.fileLocations.add(new URL("file:///" + this.textAreaSaveDirectory.getText() + File.separator + Locale.getString(KeyboardWriter.class, "keyboard.file.name")));
				this.kWriter = new KeyboardWriter(this.fileLocations.get(this.fileLocations.size() - 1));
				this.kWriter.start();
			} catch (IOException exception) {
				this.logger.severe(exception.getMessage());
			}
		}

		if (this.toggleButtonMouse.isSelected()) {
			try {
				this.fileLocations.add(new URL("file:///" + this.textAreaSaveDirectory.getText() + File.separator + Locale.getString(MouseWriter.class, "mouse.file.name")));
				this.mWriter = new MouseWriter(this.fileLocations.get(this.fileLocations.size() - 1));
				this.mWriter.start();
			} catch (IOException exception) {
				this.logger.severe(exception.getMessage());
			}
		}

		this.startDisabled.set(false);
	}

	@FXML
	private void handleStopRecording() {
		this.startDisabled.set(true);

		try (Zipper zipper = new Zipper(new URL("file:///" + this.textAreaSaveDirectory.getText()
				+ File.separator
				+ this.textAreaRecordName.getText()))) {
			if (this.kWriter != null) {
				zipper.addFile(this.kWriter.getFileLocation());
				this.kWriter.stop();
			}

			if (this.mWriter != null) {
				zipper.addFile(this.mWriter.getFileLocation());
				this.mWriter.stop();
			}
		} catch (IOException exception) {
			this.logger.severe(exception.getMessage());
		}
	}

	/**
	 * Check if the recording can be started.
	 *
	 * @return true if the recording can be started.
	 */
	private boolean isReadyToRecord() {
		final List<String> errorMessages = new ArrayList<>();

		if (this.textAreaSaveDirectory.getText() == null
				|| this.textAreaSaveDirectory.getText().isEmpty()) {
			errorMessages.add(Locale.getString(MainWindowController.class, "error.no.save.directory")); //$NON-NLS-1$
		} else if (!new File(this.textAreaSaveDirectory.getText()).exists()) {
			errorMessages.add(Locale.getString(MainWindowController.class, "error.invalid.save.directory")); //$NON-NLS-1$
		}

		if (this.textAreaRecordName.getText().isEmpty()) {
			errorMessages.add(Locale.getString(MainWindowController.class, "error.no.record.name")); //$NON-NLS-1$
		}

		if (!this.toggleButtonGamePad.isSelected()
				&& !this.toggleButtonKeyboard.isSelected()
				&& !this.toggleButtonMouse.isSelected()) {
			errorMessages.add(Locale.getString(MainWindowController.class, "error.no.device")); //$NON-NLS-1$
		}

		// TODO: add check for record name already existing.
		if (errorMessages.isEmpty()) {
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
