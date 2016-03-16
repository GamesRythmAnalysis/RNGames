package fr.utbm.rngames.controller;

import fr.utbm.rngames.event.Event;

public class CloseEvent implements Event<CloseEventListener> {
	public CloseEvent() {
	}

	@Override
	public void notify(CloseEventListener listener) {
		listener.handleCloseEvent();
	}
}
