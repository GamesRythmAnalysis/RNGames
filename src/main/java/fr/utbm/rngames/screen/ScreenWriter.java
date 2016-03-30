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

package fr.utbm.rngames.screen;

import fr.utbm.rngames.event.EventDispatcher;
import fr.utbm.rngames.mouse.MouseWriter;
import org.arakhne.afc.vmutil.locale.Locale;
import org.jnativehook.GlobalScreen;
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

public class ScreenWriter implements ScreenEventListener {
	private static final String CSV_SEPARATOR;

	static {
		CSV_SEPARATOR = Locale.getString("screen.csv.separator"); //$NON-NLS-1$
	}

	private static final Logger LOG = Logger.getLogger(MouseWriter.class.getName());

	private final long startTime;
	private final URL fileLocation;
	private final BufferedWriter writer;

	public ScreenWriter(URL fileLocation) throws IOException {
		this.startTime = System.currentTimeMillis();
		this.fileLocation = fileLocation;
		File file = new File(getFileLocation().getPath());

		if (file.createNewFile()) {
			file.deleteOnExit();
		}

		this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),
				StandardCharsets.UTF_8));

		this.writer.write(Locale.getString("screen.file.header"));
		this.writer.newLine();
	}

	private void register() {
		EventDispatcher.getInstance().addListener(ScreenEvent.class, this);
	}

	private void unregister() {
		EventDispatcher.getInstance().removeListener(ScreenEvent.class, this);
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
	public void handleScreenResizeEvent(double newWidth, double newHeight) {
		try {
			this.writer.write(generateScreenResize(newWidth, newHeight, System.currentTimeMillis()));
			this.writer.newLine();
		} catch (IOException exception) {
			LOG.severe(exception.getMessage());
		}
	}

	private String generateScreenResize(double newWidth, double newHeight, long timestampMillis) {
		return Locale.getString("screen.event.resize") + CSV_SEPARATOR //$NON-NLS-1$
				+ newWidth + CSV_SEPARATOR
				+ newHeight + CSV_SEPARATOR
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
