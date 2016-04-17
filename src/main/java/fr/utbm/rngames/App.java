/**************************************************************************
 * RNGames, a software to record your inputs while playing.
 * Copyright (C) 2016  CORTIER Benoît, BOULMIER Jérôme
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *************************************************************************/

package fr.utbm.rngames;

import fr.utbm.rngames.controller.CloseEvent;
import fr.utbm.rngames.controller.CloseEventListener;
import fr.utbm.rngames.controller.MainWindowController;
import fr.utbm.rngames.event.EventDispatcher;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.NativeSystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends Application implements CloseEventListener {
	private static final Logger LOG = Logger.getLogger(App.class.getName());

	// folder where jinput libraries are going to be extracted.
	private static String TMP_LIBS_FOLDER = System.getProperty("java.io.tmpdir") + "/rngames-libs";

	private Stage primStage;
	private BorderPane rootLayout;

	public App() {
		EventDispatcher.getInstance().addListener(CloseEvent.class, this);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primStage = primaryStage;
		this.primStage.setTitle(org.arakhne.afc.vmutil.locale.Locale.getString("stage.title")); //$NON-NLS-1$

		initRootLayout();

		showMainWindow();

		primaryStage.setOnCloseRequest(
			we -> {
				EventDispatcher.getInstance().removeListener(CloseEvent.class, this);
				EventDispatcher.getInstance().fire(new CloseEvent());
			}
		);
	}

	/**
	 * @return Returns the main stage.
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
			loader.setResources(ResourceBundle.getBundle("fr.utbm.rngames.templates.RootLayout_i18n.RootLayout", //$NON-NLS-1$
					Locale.getDefault()));
			this.rootLayout = loader.load();

			// Show the scene containing the root layout.
			final Scene scene = new Scene(this.rootLayout);
			this.primStage.setScene(scene);

			this.primStage.show();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
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
			loader.setResources(ResourceBundle.getBundle("fr.utbm.rngames.templates.MainWindow_i18n.MainWindow", //$NON-NLS-1$
					Locale.getDefault()));
			final AnchorPane mainWindow = loader.load();
			mainWindow.getStylesheets().add(App.class.getResource("templates/MainWindow_i18n/MainWindow.css").toExternalForm()); //$NON-NLS-1$

			// Set person overview into the center of root layout.
			this.rootLayout.setCenter(mainWindow);

			// Give the controller access to the main app.
			final MainWindowController controller = loader.getController();
			controller.setApp(this);
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	public static void copyStreamTo(InputStream stream, File path) {
		try {
			Files.copy(stream, path.getAbsoluteFile().toPath());
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	public static void extractJInputLib() {
		NativeSystem.Family systemFamily = NativeSystem.getFamily();
		NativeSystem.Arch systemArchitecture = NativeSystem.getArchitecture();
		List<String> filesToExtract = new LinkedList<>();

		switch (systemFamily) {
			case LINUX:
				switch (systemArchitecture) {
					case x86:
						filesToExtract.add("libjinput-linux.so");
						break;
					case x86_64:
						filesToExtract.add("libjinput-linux64.so");
						break;
					default:
						break;
				}
				break;
			case WINDOWS:
				filesToExtract.add("jinput-wintab.dll");
				switch (systemArchitecture) {
					case x86:
						filesToExtract.add("jinput-raw.dll");
						filesToExtract.add("jinput-dx8.dll");
						break;
					case x86_64:
						filesToExtract.add("jinput-raw_64.dll");
						filesToExtract.add("jinput-dx8_64.dll");
						break;
					default:
						break;
				}
				break;
			case DARWIN:
				filesToExtract.add("libjinput-osx.jnilib");
				break;
			default:
				break;
		}

		File libDir = new File(TMP_LIBS_FOLDER);
		if (!libDir.exists()) {
			libDir.mkdir();
		}

		for (String filename : filesToExtract) {
			File file = new File(TMP_LIBS_FOLDER + "/" + filename);
			if (!file.exists()) {
				InputStream stream = net.java.games.input.Controller.class.getResourceAsStream("libs/" + filename);
				copyStreamTo(stream, file);
			}
		}
	}

	public static void addLibDir(String s) throws IOException {
		// Code taken from http://nicklothian.com/blog/2008/11/19/modify-javalibrarypath-at-runtime/
		try {
			// This enables the java.library.path to be modified at runtime
			// From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[]) field.get(null);
			for (int i = 0; i < paths.length; i++) {
				if (s.equals(paths[i])) {
					return;
				}
			}
			String[] tmp = new String[paths.length + 1];
			System.arraycopy(paths, 0, tmp, 0, paths.length);
			tmp[paths.length] = s;
			field.set(null, tmp);
			System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s);
		} catch (IllegalAccessException e) {
			throw new IOException("Failed to get permissions to set library path");
		} catch (NoSuchFieldException e) {
			throw new IOException("Failed to get field handle to set library path");
		}
	}

	public static void main(String[] args) {
		final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException exception) {
			LOG.severe(exception.getMessage());
		}

		extractJInputLib();
		try {
			addLibDir(TMP_LIBS_FOLDER);
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}

		launch(args);

		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	@Override
	public void handleCloseEvent() {
		this.primStage.close();
	}
}
