package fr.utbm.rngames.event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {
	private final Map<Class<? extends Event<?>>, List<?>> eventMap = new HashMap<>(10);

	EventDispatcher() {
	}

	private static class EventDispatcherHolder {
		static final EventDispatcher instance = new EventDispatcher();
	}

	public static EventDispatcher getInstance() {
		return EventDispatcherHolder.instance;
	}

	public <T extends EventListener> void listen(Class<? extends Event<T>> eventClass, T listener) {
		List<T> listeners = getListeners(eventClass);

		synchronized (this.eventMap) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}

	public <T extends EventListener> void removeListener(Class<? extends Event<T>> eventClass, T listener) {
		assert (this.eventMap.containsKey(eventClass));

		List<T> listeners = getListeners(eventClass);

		synchronized (this.eventMap) {
			listeners.remove(listener);
		}
	}

	public <T extends EventListener> void notify(Event<T> event) {
		@SuppressWarnings("unchecked")
		Class<Event<T>> eventClass = (Class<Event<T>>) event.getClass();

		getListeners(eventClass).forEach(event::notify);
	}

	private <T extends EventListener> List<T> getListeners(Class<? extends Event<T>> eventClass) {
		synchronized (this.eventMap) {
			@SuppressWarnings("unchecked")
			List<T> listeners = (List<T>) this.eventMap.get(eventClass);
			if (listeners == null) {
				listeners = new ArrayList<>();
				this.eventMap.put(eventClass, listeners);
			}

			return listeners;

		}
	}
}
