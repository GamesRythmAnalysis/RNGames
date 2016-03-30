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
	private static final Logger LOG;

	private static final String CSV_SEPARATOR;
	private static final String EVENT_NAME;
	private static final String PRESSED_EVENT;
	private static final String RELEASED_EVENT;
	private static final String SCROLLED_EVENT;
	private static final String MOVED_EVENT;

	static {
		CSV_SEPARATOR = Locale.getString("mouse.csv.separator"); //$NON-NLS-1$
		LOG = Logger.getLogger(MouseWriter.class.getName());
		EVENT_NAME = Locale.getString("mouse.event.name"); //$NON-NLS-1$
		PRESSED_EVENT = Locale.getString("mouse.event.pressed"); //$NON-NLS-1$
		RELEASED_EVENT = Locale.getString("mouse.event.released"); //$NON-NLS-1$
		SCROLLED_EVENT = Locale.getString("mouse.event.scrolled"); //$NON-NLS-1$
		MOVED_EVENT = Locale.getString("mouse.event.moved"); //$NON-NLS-1$
	}

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

		this.writer.write(Locale.getString("mouse.file.header")); //$NON-NLS-1$
		this.writer.newLine();
	}

	@Override
	public void close() {
		try {
			this.writer.close();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent evt) {
		// Unused
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent evt) {
		try {
			this.writer.write(generateMouseButtonEntry(PRESSED_EVENT, evt));
			this.writer.newLine();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent evt) {
		try {
			this.writer.write(generateMouseButtonEntry(RELEASED_EVENT, evt));
			this.writer.newLine();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent evt) {
		try {
			this.writer.write(generateMouseMoveEntry(evt));
			this.writer.newLine();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
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
			LOG.severe(exception.getMessage());
		}
	}

	public final URL getFileLocation() {
		return this.fileLocation;
	}

	private String generateMouseButtonEntry(String eventType, NativeMouseEvent evt) {
		return EVENT_NAME + " " + evt.getButton() + " " + eventType + CSV_SEPARATOR //$NON-NLS-1$ //$NON-NLS-2$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ (evt.getWhen() - this.startTime);
	}

	private String generateMouseButtonEntry(NativeMouseWheelEvent evt) {
		if (evt.getWheelRotation() > 0) {
			return EVENT_NAME + " " + SCROLLED_EVENT + " " + PRESSED_EVENT + CSV_SEPARATOR //$NON-NLS-1$
					+ evt.getX() + CSV_SEPARATOR
					+ evt.getY() + CSV_SEPARATOR
					+ (evt.getWhen() - this.startTime);
		}

		return EVENT_NAME + " " + SCROLLED_EVENT + " " + RELEASED_EVENT + CSV_SEPARATOR //$NON-NLS-1$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ (evt.getWhen() - this.startTime);
	}

	private String generateMouseMoveEntry(NativeMouseEvent evt) {
		return EVENT_NAME + " " + MOVED_EVENT + CSV_SEPARATOR //$NON-NLS-1$
				+ evt.getX() + CSV_SEPARATOR
				+ evt.getY() + CSV_SEPARATOR
				+ (evt.getWhen() - this.startTime);
	}
}
