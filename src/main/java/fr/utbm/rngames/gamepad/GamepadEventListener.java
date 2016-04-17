package fr.utbm.rngames.gamepad;

import java.util.EventListener;

public interface GamepadEventListener extends EventListener {
	void handleGamepadEvent(GamepadEvent e);
}
