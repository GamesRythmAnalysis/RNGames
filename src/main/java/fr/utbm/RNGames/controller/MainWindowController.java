package fr.utbm.RNGames.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import fr.utbm.RNGames.App;
import fr.utbm.RNGames.keyboard.KeyboardWriter;
import fr.utbm.RNGames.mouse.MouseWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.DirectoryChooser;

public class MainWindowController {
	private KeyboardWriter kWriter;
	private MouseWriter mWriter;

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
		chooser.setTitle("Select directory where records are saved"); //$NON-NLS-1$

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
	void handleStartRecording(@SuppressWarnings("unused") ActionEvent event) {
		if (!this.toggleButtonGamepad.isSelected()
				&& !this.toggleButtonKeyboard.isSelected()
				&& !this.toggleButtonMouse.isSelected()) {
			return;
		}

		if (this.toggleButtonKeyboard.isSelected()) {
			try {
				this.kWriter = new KeyboardWriter(new URL("file://" + this.textAreaSaveDirectory.getText() + "/keyboard.csv")); //$NON-NLS-1$ //$NON-NLS-2$
				this.kWriter.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (this.toggleButtonMouse.isSelected()) {
			try {
				this.mWriter = new MouseWriter(new URL("file://" + this.textAreaSaveDirectory.getText() + "/mouse.csv")); //$NON-NLS-1$ //$NON-NLS-2$
				this.mWriter.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	void handleStopRecording(@SuppressWarnings("unused") ActionEvent event) {
		this.textAreaSaveDirectory.disableProperty().set(false);
		this.textAreaRecordName.disableProperty().set(false);
		this.buttonSelectDirectory.disableProperty().set(false);
		this.toggleButtonGamepad.disableProperty().set(false);
		this.toggleButtonKeyboard.disableProperty().set(false);
		this.toggleButtonMouse.disableProperty().set(false);
		this.buttonStartRecording.disableProperty().set(false);
		this.buttonStopRecording.disableProperty().set(true);

		if (this.kWriter != null) {
			this.kWriter.stop();
		}

		if (this.mWriter != null) {
			this.mWriter.stop();
		}
	}

}
