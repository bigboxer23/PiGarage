package com.jones.matt.services;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.util.logging.Logger;

/**
 * Service to open or close the garage door by interfacing with Raspberry Pi's GPIO and
 * a relay wired to the door opener
 */
public class GarageDoorActionService
{
	private static Logger myLogger = Logger.getLogger("com.jones.GarageDoorActionService");

	private GarageDoorStatusService myStatusService;

	private GpioPinDigitalOutput myPinTrigger;

	/**
	 * The delay between the "press" and the "let go"
	 */
	public static final int kTriggerDelay = Integer.getInteger("triggerDelay", 400);

	public GarageDoorActionService()
	{
		myPinTrigger = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.GPIO_07, "MyLED", PinState.HIGH);
	}

	/**
	 * Close the door if it is open
	 */
	public void closeDoor()
	{
		if(myStatusService.isGarageDoorOpen())
		{
			myLogger.warning("Closing the door.");
			doDoorAction();
		}
	}

	/**
	 * Open the door if it is already closed
	 */
	public void openDoor()
	{
		if(!myStatusService.isGarageDoorOpen())
		{
			myLogger.warning("Opening the door.");
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
	public void setStatusService(GarageDoorStatusService theStatusService)
	{
		myStatusService = theStatusService;
	}
}
