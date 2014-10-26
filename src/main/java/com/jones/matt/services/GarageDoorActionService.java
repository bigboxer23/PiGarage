package com.jones.matt.services;

import com.jones.matt.GarageDoorController;
import com.jones.matt.util.GPIOUtils;
import com.pi4j.io.gpio.*;

import java.util.logging.Logger;

/**
 * Service to open or close the garage door by interfacing with Raspberry Pi's GPIO and
 * a relay wired to the door opener
 */
public class GarageDoorActionService extends BaseService
{
	private static Logger myLogger = Logger.getLogger("com.jones.GarageDoorActionService");

	private GpioPinDigitalOutput myPinTrigger;

	/**
	 * Pin to use for triggering actions
	 */
	private static final Pin kActionPin = GPIOUtils.getPin(Integer.getInteger("GPIO.action.pin", 7));

	/**
	 * The delay between the "press" and the "let go"
	 */
	public static final int kTriggerDelay = Integer.getInteger("triggerDelay", 400);

	public GarageDoorActionService(GarageDoorController theController)
	{
		super(theController);
		myPinTrigger = GpioFactory.getInstance().provisionDigitalOutputPin(kActionPin, "MyActionPin", PinState.HIGH);
	}

	/**
	 * Close the door if it is open
	 */
	public void closeDoor()
	{
		if(getController().getStatusService().isGarageDoorOpen())
		{
			myLogger.config("Closing the door.");
			doDoorAction();
		}
	}

	/**
	 * Open the door if it is already closed
	 */
	public void openDoor()
	{
		if(!getController().getStatusService().isGarageDoorOpen())
		{
			myLogger.config("Opening the door.");
			doDoorAction();
		}
	}

	/**
	 * Trigger the opener to toggle the current state.  There
	 * is no status check with this method.
	 */
	private void doDoorAction()
	{
			myPinTrigger.low();
			try
			{
				Thread.sleep(kTriggerDelay);
			}
			catch (InterruptedException e){}
			myPinTrigger.high();
	}
}
