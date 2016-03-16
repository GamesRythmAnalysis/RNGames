package fr.utbm.rngames.event;

public interface Event<T> {
	void notify(T listener);
}
