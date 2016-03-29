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

package fr.utbm.rngames.controller;

import fr.utbm.rngames.event.EventDispatcher;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.arakhne.afc.vmutil.locale.Locale;

public class RootLayoutController {
	/**
	 * Opens an about dialog.
	 */
	@SuppressWarnings("MethodMayBeStatic")
	@FXML
	private void handleAbout() {
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(Locale.getString("alert.about.title")); //$NON-NLS-1$
		alert.setHeaderText(Locale.getString("alert.about.header")); //$NON-NLS-1$
		alert.setContentText(Locale.getString("alert.about.content.text")); //$NON-NLS-1$

		alert.showAndWait();
	}

	/**
	 * Closes the application.
	 */
	@SuppressWarnings("MethodMayBeStatic")
	@FXML
	private void handleExit() {
		EventDispatcher.getInstance().fire(new CloseEvent());
	}

}
