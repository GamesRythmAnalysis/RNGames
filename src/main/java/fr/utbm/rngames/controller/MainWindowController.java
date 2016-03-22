package fr.utbm.rngames.controller;

import fr.utbm.rngames.App;
import fr.utbm.rngames.Zipper;
import fr.utbm.rngames.event.EventDispatcher;
import fr.utbm.rngames.keyboard.KeyboardWriter;
import fr.utbm.rngames.mouse.MouseWriter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.DirectoryChooser;
import org.arakhne.afc.vmutil.locale.Locale;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class MainWindowController implements Initializable, CloseEventListener {

	private final Logger logger = Logger.getLogger(MainWindowController.class.getName());

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

	// current date + record name
	private String fullRecordName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.textAreaSaveDirectory.disableProperty().bind(this.startDisabled);
		this.textAreaSaveDirectory.disableProperty().bind(this.startDisabled);
		this.textAreaRecordName.disableProperty().bind(this.startDisabled);
		this.buttonSelectDirectory.disableProperty().bind(this.startDisabled);
		//this.toggleButtonGamePad.disableProperty().bind(this.startDisabled);
		this.toggleButtonGamePad.disableProperty().set(true); // FIXME: enable once it works.
		this.toggleButtonKeyboard.disableProperty().bind(this.startDisabled);
		this.toggleButtonMouse.disableProperty().bind(this.startDisabled);
		this.buttonStartRecording.disableProperty().bind(this.startDisabled);
		this.buttonStopRecording.disableProperty().bind(this.startDisabled.not());

		EventDispatcher.getInstance().addListener(CloseEvent.class, this);
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param app - application
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

		chooser.setTitle(Locale.getString("directory.chooser.title")); //$NON-NLS-1$

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
				this.kWriter = new KeyboardWriter(new URL("file:///" + System.getProperty("java.io.tmpdir")
						+ File.separator
						+ Locale.getString(KeyboardWriter.class, "keyboard.file.name")));

				this.kWriter.start();
			} catch (IOException exception) {
				this.logger.severe(exception.getMessage());
			}
		}

		if (this.toggleButtonMouse.isSelected()) {
			try {
				this.mWriter = new MouseWriter(new URL("file:///" + System.getProperty("java.io.tmpdir")
						+ File.separator
						+ Locale.getString(MouseWriter.class, "mouse.file.name")));

				this.mWriter.start();
			} catch (IOException exception) {
				this.logger.severe(exception.getMessage());
			}
		}

		this.startDisabled.set(true);
	}

	@FXML
	private void handleStopRecording() {
		this.startDisabled.set(false);
		stopAndZip();
	}

	@Override
	public void handleCloseEvent() {
		if (this.startDisabled.get()) {
			stopAndZip();
		}
	}

	/**
	 * Helper method to stop recording and zip data.
	 */
	private void stopAndZip() {
		try (Zipper zipper = new Zipper(new URL("file:///" + this.textAreaSaveDirectory.getText()
				+ File.separator
				+ this.fullRecordName
				+ Zipper.EXTENSION_NAME))) {
			if (this.kWriter != null) {
				this.kWriter.stop();
				zipper.addFile(this.kWriter.getFileLocation(),
						this.fullRecordName
						+ ".K"
						+ ".csv");
			}

			if (this.mWriter != null) {
				this.mWriter.stop();
				zipper.addFile(this.mWriter.getFileLocation(),
						this.fullRecordName
						+ ".M"
						+ ".csv");
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
			errorMessages.add(Locale.getString("error.no.save.directory")); //$NON-NLS-1$
		} else if (!new File(this.textAreaSaveDirectory.getText()).exists()) {
			errorMessages.add(Locale.getString("error.invalid.save.directory")); //$NON-NLS-1$
		}

		if (this.textAreaRecordName.getText().isEmpty()) {
			errorMessages.add(Locale.getString("error.no.record.name")); //$NON-NLS-1$
		}

		if (!this.toggleButtonGamePad.isSelected()
				&& !this.toggleButtonKeyboard.isSelected()
				&& !this.toggleButtonMouse.isSelected()) {
			errorMessages.add(Locale.getString("error.no.device")); //$NON-NLS-1$
		}

		if (!errorMessages.isEmpty()) {
			// Show the error message.
			final Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(this.app.getPrimaryStage());
			alert.setTitle(Locale.getString("alert.error.title")); //$NON-NLS-1$
			alert.setHeaderText(Locale.getString("alert.error.header")); //$NON-NLS-1$
			alert.setContentText(String.join("\n", errorMessages)); //$NON-NLS-1$);

			alert.showAndWait();

			return false;
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd"); // TODO: externalize
		String currentDate = dateFormat.format(new Date());
		this.fullRecordName = currentDate + "." + this.textAreaRecordName.getText();
		if (new File(this.textAreaSaveDirectory.getText() + File.separator
				+ this.fullRecordName + Zipper.EXTENSION_NAME).exists()) {
			// Show the confirmation message.
			final Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.getButtonTypes().set(0, ButtonType.YES);
			alert.getButtonTypes().set(1, ButtonType.NO);
			alert.initOwner(this.app.getPrimaryStage());
			alert.setTitle(Locale.getString("alert.record.already.existing.title")); //$NON-NLS-1$
			alert.setHeaderText(Locale.getString("alert.record.already.existing.header")); //$NON-NLS-1$
			alert.setContentText(Locale.getString("alert.record.already.existing.content")); //$NON-NLS-1$);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.NO) {
				return false;
			}
		}

		return true;
	}

}
