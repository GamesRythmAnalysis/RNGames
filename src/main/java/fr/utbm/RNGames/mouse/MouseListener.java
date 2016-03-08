package fr.utbm.RNGames.mouse;

import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;
import org.jnativehook.mouse.NativeMouseWheelListener;

public interface MouseListener extends NativeMouseListener, NativeMouseMotionListener, NativeMouseWheelListener {

	default void register() {
		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);
		GlobalScreen.addNativeMouseWheelListener(this);
	}

	default void unregister() {
		GlobalScreen.removeNativeMouseListener(this);
		GlobalScreen.removeNativeMouseMotionListener(this);
		GlobalScreen.removeNativeMouseWheelListener(this);
	}
}
