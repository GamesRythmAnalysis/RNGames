package fr.utbm.rngames.gamepad;

import fr.utbm.rngames.controller.CloseEvent;
import fr.utbm.rngames.controller.CloseEventListener;
import fr.utbm.rngames.event.EventDispatcher;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import java.util.logging.Logger;

public class GamepadMonitor implements CloseEventListener, Runnable {
	private static final Logger LOG = Logger.getLogger(GamepadMonitor.class.getName());

	private static final float ANALOG_DEAD_ZONE = 0.16f;

	private boolean running;

	private static Controller FIRST_GAMEPAD;

	static {
		/* This is how to poll controllers/components and get their values. */
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		FIRST_GAMEPAD = null;
		for (Controller controller : controllers) {
			if (controller.getType() == Controller.Type.GAMEPAD) {
				// Found a gamepad
				FIRST_GAMEPAD = controller;
				break;
			}
		}
	}

	public static boolean isNoGamepadFound() {
		return FIRST_GAMEPAD == null;
	}

	@Override
	public void run() {
		EventDispatcher.getInstance().addListener(CloseEvent.class, this);

		if (FIRST_GAMEPAD == null) {
			// Couldn't find a gamepad
			LOG.warning("No gamepad found. The GamepadMonitor is not going to run.");
			this.running = false;
		} else {
			this.running = true;
		}

		while (this.running) {
			FIRST_GAMEPAD.poll();
			EventQueue queue = FIRST_GAMEPAD.getEventQueue();
			// Create an event object to pass down to get populated with the information.
			Event event = new Event();
			while (queue.getNextEvent(event)) {
				Component comp = event.getComponent();
				if (!comp.isAnalog() || Math.abs(event.getValue()) > ANALOG_DEAD_ZONE) {
					EventDispatcher.getInstance().fire(
							new GamepadEvent(comp.getName(),
									comp.isAnalog(),
									event.getValue())
					);
				}
			}

			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				LOG.severe(e.getMessage());
			}
		}

		EventDispatcher.getInstance().removeListener(CloseEvent.class, this);
	}

	/**
	 * Stop the gamepad monitor.
	 */
	public void stop() {
		this.running = false;
	}

	@Override
	public void handleCloseEvent() {
		stop();
	}
}
