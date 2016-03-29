/**************************************************************************
 * EventDispatcher, to link event senders and event listeners.
 * Copyright (C) 2016  BOULMIER Jérôme
 *************************************************************************/

package fr.utbm.rngames.event;

public interface Event<T> {
	void notify(T listener);
}
