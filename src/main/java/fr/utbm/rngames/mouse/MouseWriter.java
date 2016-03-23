package fr.utbm.rngames.mouse;

import org.arakhne.afc.vmutil.locale.Locale;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseWheelEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class MouseWriter extends MouseListener {
	private static final String CSV_SEPARATOR;

	static {
		CSV_SEPARATOR = Locale.getString("mouse.csv.separator"); //$NON-NLS-1$
	}

	private final Logger logger = Logger.getLogger(MouseWriter.class.getName());

	private final long startTime;
	private final URL fileLocation;
	private final BufferedWriter writer;

	public MouseWriter(URL fileLocation) throws IOException {
		this.startTime = System.currentTimeMillis();
		this.fileLocation = fileLocation;
		File file = new File(getFileLocation().getPath());

		if (file.createNewFile()) {
			file.deleteOnExit();
		}

		this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
				StandardCharsets.UTF_8));

		this.writer.write(Locale.getString("mouse.file.header"));
		this.writer.newLine();
	}

	@Override
	public void close() {
		try {
			this.writer.close();
		} catch (IOException exception) {
			this.logger.severe(exception.getMessage());
		}
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent evt) {
		// Unused
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent evt) {
		try {
			this.writer.write(generateMouseButtonEntry(Locale.getString("mouse.event.pressed"), evt));
			this.writer.newLine();
		} catch (IOException exception) {
			this.logger.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent evt) {
		try {
			this.writer.write(generateMouseButtonEntry(Locale.getString("mouse.event.released"), evt));
			this.writer.newLine();
		} catch (IOException exception) {
			this.logger.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent evt) {
		try {
			this.writer.write(generateMouseMoveEntry(evt));
			this.writer.newLine();
		} catch (IOException exception) {
			this.logger.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent evt) {
		// we simply consider mouse dragged events as mouse moved events.
		nativeMouseMoved(evt);
	}

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent evt) {
		try {
			this.writer.write(generateMouseButtonEntry(evt));
			this.writer.newLine();
		} catch (IOException exception) {
			this.logger.severe(exception.getMessage());
			// System.exit(-1);
		}
	}

	public final URL getFileLocation() {
		return this.fileLocation;
	}

	private String generateMouseButtonEntry(String eventType, NativeMouseEvent evt) {
		return "mouse " + evt.getButton() + " " +  eventType + CSV_SEPARATOR //$NON-NLS-1$ //$NON-NLS-2$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ (evt.getWhen() - this.startTime);
	}

	private String generateMouseButtonEntry(NativeMouseWheelEvent evt) {
		if (evt.getWheelRotation() > 0) {
			return "mouse scroll down" + CSV_SEPARATOR //$NON-NLS-1$
					+ evt.getX() + CSV_SEPARATOR
					+ evt.getY() + CSV_SEPARATOR
					+ (evt.getWhen() - this.startTime);
		}

		return "mouse scroll up" + CSV_SEPARATOR //$NON-NLS-1$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ (evt.getWhen() - this.startTime);
	}

	private String generateMouseMoveEntry(NativeMouseEvent evt) {
		return "mouse move" + CSV_SEPARATOR //$NON-NLS-1$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ (evt.getWhen() - this.startTime);
	}
}
