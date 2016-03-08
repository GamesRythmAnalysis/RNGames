package fr.utbm.RNGames.keyboard;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyListener;

public abstract class KeyboardListener implements NativeKeyListener {
	protected void register() {
		GlobalScreen.addNativeKeyListener(this);
	}

	protected void unregister() {
		GlobalScreen.removeNativeKeyListener(this);
	}

	public void start() {
		register();
	}

	public void stop() {
		unregister();
		close();
	}

	protected abstract void close();
}
