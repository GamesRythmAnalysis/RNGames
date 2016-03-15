package fr.utbm.rngames.keyboard;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyListener;

public abstract class KeyboardListener implements NativeKeyListener {
	private void register() {
		GlobalScreen.addNativeKeyListener(this);
	}

	private void unregister() {
		GlobalScreen.removeNativeKeyListener(this);
	}

	public final void start() {
		register();
	}

	public final void stop() {
		unregister();
		close();
	}

	protected abstract void close();
}
