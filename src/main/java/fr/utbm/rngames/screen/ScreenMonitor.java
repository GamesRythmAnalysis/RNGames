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

import fr.utbm.rngames.controller.CloseEvent;
import fr.utbm.rngames.controller.CloseEventListener;
import fr.utbm.rngames.event.EventDispatcher;
import javafx.stage.Screen;

import java.util.logging.Logger;

public class ScreenMonitor implements CloseEventListener, Runnable {
	private static final Logger LOG = Logger.getLogger(ScreenMonitor.class.getName());

	private boolean	running;

	@Override
	public void run() {
		EventDispatcher.getInstance().addListener(CloseEvent.class, this);

		double lastWidth = 0;
		double lastHeight = 0;
		double newWidth;
		double newHeight;

		this.running = true;
		while (this.running) {
			Screen screen = Screen.getPrimary();
			newWidth = screen.getBounds().getWidth();
			newHeight = screen.getBounds().getHeight();
			if (newWidth != lastWidth || newHeight != lastHeight) {
				EventDispatcher.getInstance().fire(new ScreenEvent(newWidth, newHeight));
				lastWidth = newWidth;
				lastHeight = newHeight;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.severe(e.getMessage());
			}
		}

		EventDispatcher.getInstance().removeListener(CloseEvent.class, this);
	}

	/**
	 * Stop the screen monitor.
	 */
	public void stop() {
		this.running = false;
	}

	public void handleCloseEvent() {
		this.running = false;
	}
}
