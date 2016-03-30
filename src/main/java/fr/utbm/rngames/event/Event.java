/**************************************************************************
 * EventDispatcher, to link event senders and event listeners.
 * Copyright (C) 2016  BOULMIER Jérôme
 *************************************************************************/

package fr.utbm.rngames.event;

import java.util.EventListener;

public interface Event<T extends EventListener> {
	void notify(T listener);
}
