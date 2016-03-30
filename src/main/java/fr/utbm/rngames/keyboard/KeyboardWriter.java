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

package fr.utbm.rngames.keyboard;

import org.arakhne.afc.vmutil.locale.Locale;
import org.jnativehook.keyboard.NativeKeyEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class KeyboardWriter extends KeyboardListener {
	private static final Logger LOG;

	private static final String CSV_SEPARATOR;
	private static final String EVENT_NAME;
	private static final String PRESSED_EVENT;
	private static final String RELEASED_EVENT;

	static {
		CSV_SEPARATOR = Locale.getString("keyboard.csv.separator"); //$NON-NLS-1$
		LOG = Logger.getLogger(KeyboardWriter.class.getName());
		EVENT_NAME = Locale.getString("keyboard.event.name"); //$NON-NLS-1$
		PRESSED_EVENT = Locale.getString("keyboard.event.pressed"); //$NON-NLS-1$
		RELEASED_EVENT = Locale.getString("keyboard.event.released"); //$NON-NLS-1$
	}


	private final List<Integer> keysPressed = new ArrayList<>();
	private final long startTime;
	private final URL fileLocation;
	private final BufferedWriter writer;

	public KeyboardWriter(URL fileLocation) throws IOException {
		this.startTime = System.currentTimeMillis();
		this.fileLocation = fileLocation;
		File file = new File(this.fileLocation.getPath());

		if (file.createNewFile()) {
			file.deleteOnExit();
		}

		this.writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), StandardCharsets.UTF_8));

		this.writer.write(Locale.getString("keyboard.file.header"));
		this.writer.newLine();
	}

	@Override
	protected void close() {
		try {
			this.writer.close();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent evt) {
		if (!this.keysPressed.contains(new Integer(evt.getKeyCode()))) {
			this.keysPressed.add(new Integer(evt.getKeyCode()));
			try {
				this.writer.write(generateFileEntry(EVENT_NAME + " " + PRESSED_EVENT, evt)); //$NON-NLS-1$
				this.writer.newLine();
			} catch (IOException exception) {
				LOG.severe(exception.getMessage());
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent evt) {
		this.keysPressed.remove(new Integer(evt.getKeyCode()));
		try {
			this.writer.write(generateFileEntry(EVENT_NAME + " " + RELEASED_EVENT, evt)); //$NON-NLS-1$
			this.writer.newLine();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent evt) {
		// Unused
	}

	public URL getFileLocation() {
		return this.fileLocation;
	}

	private String generateFileEntry(String eventType, NativeKeyEvent event) {
		return eventType + CSV_SEPARATOR
				+ NativeKeyEvent.getKeyText(event.getKeyCode()) + CSV_SEPARATOR
				+ (event.getWhen() - this.startTime);
	}
}
