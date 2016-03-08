package fr.utbm.RNGames.mouse;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseWheelEvent;

import fr.utbm.RNGames.keyboard.KeyboardWriter;

public class MouseWriter extends MouseListener {
	private final static String CSV_SEPARATOR;

	static {
		CSV_SEPARATOR = Locale.getString(MouseWriter.class, "mouse.csv.separator"); //$NON-NLS-1$
	}

	private final Logger log = Logger.getLogger(MouseWriter.class.getName());

	private final Writer writer;

	public MouseWriter(URL fileLocation) throws IOException {
		this.writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileLocation.getPath()), StandardCharsets.UTF_8));

		this.writer.write(Locale.getString(KeyboardWriter.class, "mouse.file.header")); //$NON-NLS-1$
	}

	@Override
	public void close() {
		try {
			this.writer.close();
		} catch (IOException e) {
			this.log.severe(e.getMessage());
		}
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent evt) {
		// Unused
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent evt) {
		try {
			this.writer.write(generateMouseButtonEntry("down", evt)); //$NON-NLS-1$
		} catch (IOException exception) {
			this.log.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent evt) {
		try {
			this.writer.write(generateMouseButtonEntry("up", evt)); //$NON-NLS-1$
		} catch (IOException exception) {
			this.log.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent evt) {
		try {
			this.writer.write(generateMouseMoveEntry(evt));
		} catch (IOException exception) {
			this.log.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent evt) {
		// Unused in this project
	}

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent evt) {
		try {
			this.writer.write(generateMouseButtonEntry(evt));
		} catch (IOException exception) {
			this.log.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	private static String generateMouseButtonEntry(String eventType, NativeMouseEvent evt) {
		return "mouse " + evt.getButton() + " " +  eventType + CSV_SEPARATOR //$NON-NLS-1$ //$NON-NLS-2$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ evt.getWhen();
	}

	private static String generateMouseButtonEntry(NativeMouseWheelEvent evt) {
		if (evt.getWheelRotation() > 0) {
			return "mouse scroll down" + CSV_SEPARATOR //$NON-NLS-1$
					+ evt.getX() + CSV_SEPARATOR
					+ evt.getY() + CSV_SEPARATOR
					+ evt.getWhen()
					+ "\n"; //$NON-NLS-1$
		}

		return "mouse scroll up" + CSV_SEPARATOR //$NON-NLS-1$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ evt.getWhen()
				+ "\n"; //$NON-NLS-1$
	}

	private static String generateMouseMoveEntry(NativeMouseEvent evt) {
		return "mouse move" + CSV_SEPARATOR //$NON-NLS-1$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ evt.getWhen()
				+ "\n"; //$NON-NLS-1$
	}
}
