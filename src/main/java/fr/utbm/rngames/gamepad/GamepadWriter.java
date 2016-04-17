package fr.utbm.rngames.gamepad;

import fr.utbm.rngames.event.EventDispatcher;
import org.arakhne.afc.vmutil.locale.Locale;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class GamepadWriter implements GamepadEventListener {
	private static final String CSV_SEPARATOR;

	static {
		CSV_SEPARATOR = Locale.getString("gamepad.csv.separator"); //$NON-NLS-1$
	}

	private static final Logger LOG = Logger.getLogger(GamepadWriter.class.getName());

	private final long startTime;
	private final URL fileLocation;
	private final BufferedWriter writer;

	public GamepadWriter(URL fileLocation) throws IOException {
		this.startTime = System.currentTimeMillis();
		this.fileLocation = fileLocation;
		File file = new File(getFileLocation().getPath());

		if (file.createNewFile()) {
			file.deleteOnExit();
		}

		this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
				StandardCharsets.UTF_8));

		this.writer.write(Locale.getString("gamepad.file.header"));
		this.writer.newLine();
	}

	private void register() {
		EventDispatcher.getInstance().addListener(GamepadEvent.class, this);
	}

	private void unregister() {
		EventDispatcher.getInstance().removeListener(GamepadEvent.class, this);
	}

	public final void start() {
		register();
	}

	public final void stop() {
		unregister();
		close();
	}

	public final URL getFileLocation() {
		return this.fileLocation;
	}

	@Override
	public void handleGamepadEvent(GamepadEvent e) {
		try {
			this.writer.write(generateGamepadEventText(e, System.currentTimeMillis()));
			this.writer.newLine();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	private String generateGamepadEventText(GamepadEvent e, long timestampMillis) {
		String eventName;
		if (e.isAnalog()) {
			eventName = Locale.getString("gamepad.event.analog.change"); //$NON-NLS-1$
		} else {
			eventName = Locale.getString("gamepad.event.button.change"); //$NON-NLS-1$
		}

		return eventName + CSV_SEPARATOR
				+ e.getComponentName() + CSV_SEPARATOR
				+ e.getValue() + CSV_SEPARATOR
				+ (timestampMillis - this.startTime);
	}

	private void close() {
		try {
			this.writer.close();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}
}
