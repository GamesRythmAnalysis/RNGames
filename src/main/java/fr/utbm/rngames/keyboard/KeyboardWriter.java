package fr.utbm.rngames.keyboard;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.jnativehook.keyboard.NativeKeyEvent;

public class KeyboardWriter extends KeyboardListener {
	private final static String CSV_SEPARATOR;

	static {
		CSV_SEPARATOR = Locale.getString(KeyboardWriter.class, "keyboard.csv.separator"); //$NON-NLS-1$
	}

	private final Logger log = Logger.getLogger(KeyboardWriter.class.getName());

	private final Writer writer;

	public KeyboardWriter(URL fileLocation) throws IOException {
		this.writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileLocation.getPath()), StandardCharsets.UTF_8));

		this.writer.write(Locale.getString(KeyboardWriter.class, "keyboard.file.header")); //$NON-NLS-1$
	}

	@Override
	protected void close() {
		try {
			this.writer.close();
		} catch (IOException exception) {
			this.log.severe(exception.getMessage());
		}
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent evt) {
		try {
			this.writer.write(generateFileEntry("Key Down", evt)); //$NON-NLS-1$
		} catch (IOException exception) {
			this.log.severe(exception.getMessage());
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent evt) {
		try {
			this.writer.write(generateFileEntry("Key Up", evt)); //$NON-NLS-1$
		} catch (IOException exception) {
			this.log.severe(exception.getMessage());
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent evt) {
		// Unused
	}

	private static String generateFileEntry(String eventType, NativeKeyEvent event) {
		return eventType + CSV_SEPARATOR
				+ NativeKeyEvent.getKeyText(event.getKeyCode()) + CSV_SEPARATOR
				+ event.getWhen() + "\n"; //$NON-NLS-1$
	}

}
