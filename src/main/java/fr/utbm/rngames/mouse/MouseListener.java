package fr.utbm.rngames.mouse;

import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;
import org.jnativehook.mouse.NativeMouseWheelListener;

public abstract class MouseListener implements NativeMouseListener, NativeMouseMotionListener, NativeMouseWheelListener {

	protected void register() {
		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);
		GlobalScreen.addNativeMouseWheelListener(this);
	}

	protected void unregister() {
		GlobalScreen.removeNativeMouseListener(this);
		GlobalScreen.removeNativeMouseMotionListener(this);
		GlobalScreen.removeNativeMouseWheelListener(this);
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
