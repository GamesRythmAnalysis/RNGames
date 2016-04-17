package fr.utbm.rngames.gamepad;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class Test {
	public static void main(String[] args) {
		System.out.println(System.getProperty("java.library.path"));

		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

		/* Get a list of the controllers JInput knows about and can interact with */
		for (int i = 0; i < ca.length; i++) {
		    /* Get the name of the controller */
			System.out.println(ca[i].getName());

			/* Get the type of the controller, e.g. GAMEPAD, MOUSE, KEYBOARD, etc. */
			System.out.println("Type: " + ca[i].getType().toString());

            /* Get this controllers components (buttons and axis) */
			Component[] components = ca[i].getComponents();
			System.out.println("Component Count: " + components.length);
			for (int j = 0; j < components.length; j++) {
			    /* Get the components name */
				System.out.println("Component " + j + ": " + components[j].getName());

				/* Get it's identifier, E.g. BUTTON.PINKIE, AXIS.POV and KEY.Z, etc. */
				System.out.println("    Identifier: " + components[j].getIdentifier().getName());

				/* Display if this component is relative or absolute (change in position
				 * or it's location. Mice are normally relative giving the number of units
				 * it has been moved since last polled (do not confuse this with pixels on
				 * the screen), Joysticks are normally absolute, giving the amount it's being
				 * pushed forwards, regardless of where it was last poll.
				 */
				System.out.print("    ComponentType: ");
				if (components[j].isRelative()) {
					System.out.print("Relative");
				} else {
					System.out.print("Absolute");
				}
				if (components[j].isAnalog()) {
					System.out.println(" Analog");
				} else {
					System.out.println(" Digital");
				}
			}
		}

		/* This is how to poll controllers/components and get their values. */
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		Controller firstGamepad = null;
		for (int i = 0; i < controllers.length; i++) {
			if (controllers[i].getType() == Controller.Type.GAMEPAD) {
				// Found a gamepad
				firstGamepad = controllers[i];
				break;
			}
		}

		if (firstGamepad == null) {
			// Couldn't find a gamepad
			System.out.println("Found no gamepad");
			System.exit(0);
		}

		System.out.println("First gamepad is: " + firstGamepad.getName());

		/* Poll, you need to do this often, if you don't the buffers in the
		 * underlying OS might get full, and then information will be lost
		 */
		while (true) {
			firstGamepad.poll();
			Component[] components = firstGamepad.getComponents();
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < components.length; i++) {
				if (i > 0) {
					buffer.append(", ");
				}

				buffer.append(components[i].getName());
				buffer.append(": ");

				/* Get the value this component had last time it was polled (a few lines
				 * up from here), check this is an analog or digital component and display
				 * an appropriate value
				 */
				if (components[i].isAnalog()) {
					buffer.append(components[i].getPollData());
				} else {
					if (components[i].getPollData() == 1.0f) {
						buffer.append("On");
					} else {
						buffer.append("Off");
					}
				}
			}
			System.out.println(buffer.toString());

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
