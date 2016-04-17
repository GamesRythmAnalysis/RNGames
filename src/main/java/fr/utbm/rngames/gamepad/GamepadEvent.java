package fr.utbm.rngames.gamepad;

import fr.utbm.rngames.event.Event;

public class GamepadEvent implements Event<GamepadEventListener> {
	private final String componentName;

	private final boolean isAnalog;

	private final float value;

	public GamepadEvent(String componentName, boolean isAnalog, float value) {
		this.componentName = componentName;
		this.isAnalog = isAnalog;
		this.value = value;
	}

	public boolean isAnalog() {
		return this.isAnalog;
	}

	public float getValue() {
		return this.value;
	}

	public String getComponentName() {
		return this.componentName;
	}

	@Override
	public void notify(GamepadEventListener listener) {
		listener.handleGamepadEvent(this);
	}
}
