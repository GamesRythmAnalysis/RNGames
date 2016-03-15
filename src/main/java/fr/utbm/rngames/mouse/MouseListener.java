package fr.utbm.rngames.mouse;

import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;
import org.jnativehook.mouse.NativeMouseWheelListener;

public abstract class MouseListener implements NativeMouseListener, NativeMouseMotionListener, NativeMouseWheelListener {

	private void register() {
		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);
		GlobalScreen.addNativeMouseWheelListener(this);
	}

	private void unregister() {
		GlobalScreen.removeNativeMouseListener(this);
		GlobalScreen.removeNativeMouseMotionListener(this);
		GlobalScreen.removeNativeMouseWheelListener(this);
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
