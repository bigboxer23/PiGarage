package com.jones.matt.services;

import com.jones.matt.events.GarageDoorCloseEvent;
import com.jones.matt.events.GarageDoorCloseHandler;
import com.jones.matt.events.GarageDoorOpenEvent;
import com.jones.matt.events.GarageDoorOpenHandler;
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

	public void closeDoor()
	{
		if(myStatusService.isGarageDoorOpen())
		{
			myLogger.warning("Closing the door.");
			myPinTrigger.low();
			try
			{
				Thread.sleep(kTriggerDelay);
			}
			catch (InterruptedException e){}
			myPinTrigger.high();
		}
	}

	public void setStatusService(GarageDoorStatusService theStatusService)
	{
		myStatusService = theStatusService;
	}
}
