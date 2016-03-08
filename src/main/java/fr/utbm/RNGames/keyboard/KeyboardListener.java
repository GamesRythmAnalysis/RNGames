package fr.utbm.RNGames.keyboard;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyListener;

public interface KeyboardListener extends NativeKeyListener {
	default void register() {
		GlobalScreen.addNativeKeyListener(this);
	}

	default void unregister() {
		GlobalScreen.removeNativeKeyListener(this);
	}
}
