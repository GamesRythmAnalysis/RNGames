package fr.utbm.RNGames.mouse;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseWheelEvent;

public class MouseWriter implements MouseListener {
	private final static String CSV_SEPARATOR;

	static {
		CSV_SEPARATOR = Locale.getString(MouseWriter.class, "mouse.csv.separator"); //$NON-NLS-1$
	}

	private final Logger log = Logger.getLogger(MouseWriter.class.getName());

	private final Path fileLocation;

	public MouseWriter(URI fileLocation) throws IOException {
		this.fileLocation = Paths.get(fileLocation);

		Files.write(this.fileLocation, Locale.getString(MouseWriter.class, "mouse.file.header").getBytes(StandardCharsets.UTF_8), //$NON-NLS-1$
				StandardOpenOption.CREATE);
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent evt) {
		// Unused
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent evt) {
		try {
			Files.write(this.fileLocation, generateMouseButtonEntry("down", evt).getBytes(StandardCharsets.UTF_8), //$NON-NLS-1$
					StandardOpenOption.APPEND);
		} catch (IOException exception) {
			this.log.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent evt) {
		try {
			Files.write(this.fileLocation, generateMouseButtonEntry("up", evt).getBytes(StandardCharsets.UTF_8), //$NON-NLS-1$
					StandardOpenOption.APPEND);
		} catch (IOException exception) {
			this.log.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent evt) {
		try {
			Files.write(this.fileLocation, generateMouseMoveEntry(evt).getBytes(StandardCharsets.UTF_8),
					StandardOpenOption.APPEND);
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
			Files.write(this.fileLocation, generateMouseButtonEntry(evt).getBytes(StandardCharsets.UTF_8),
					StandardOpenOption.APPEND);
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
					+ evt.getWhen();
		}

		return "mouse scroll up" + CSV_SEPARATOR //$NON-NLS-1$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ evt.getWhen();
	}

	private static String generateMouseMoveEntry(NativeMouseEvent evt) {
		return "mouse move" + CSV_SEPARATOR //$NON-NLS-1$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ evt.getWhen();
	}
}
