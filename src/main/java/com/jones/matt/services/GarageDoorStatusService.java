package com.jones.matt.services;

import com.jones.matt.IterateThread;
import com.jones.matt.util.GPIOUtils;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.logging.Logger;

/**
 * Service to monitor the status of the garage door.
 *
 * It runs a thread and checks for the amount of time the door has been opened, triggering the closing of it after a
 * set amount of time.
 */
public class GarageDoorStatusService extends IterateThread
{
	private static Logger myLogger = Logger.getLogger("com.jones.GarageDoorStatusService");

	/**
	 * Pin to use for status
	 */
	private static final Pin kStatusPin = GPIOUtils.getPin(Integer.getInteger("GPIO.status.pin", 2));

	/**
	 * Time to wait before closing
	 */
	public static final long kAutoCloseDelay = Integer.getInteger("close.delay", 1000 * 60 * 10);//ms * seconds * minutes -> 10 Minutes

	/**
	 * Last time the door was detected open
	 */
	private long myOpenTime = -1;

	/**
	 * Pin to get our status from
	 */
	private GpioPinDigitalInput myStatusPin;

	private GarageDoorActionService myActionService;

	public GarageDoorStatusService()
	{
		GpioController aGPIOFactory = GpioFactory.getInstance();
		myStatusPin = aGPIOFactory.provisionDigitalInputPin(kStatusPin, PinPullResistance.PULL_DOWN);
		myStatusPin.addListener(new GpioPinListenerDigital()
		{
			/**
			 * Listen for status changes.  These can apparently trigger multiple times even
			 * when the status isn't really changing, so we use the open time as our gauge for "last"
			 * status, and don't set close status (or open) unless we're moving from the opposite
			 * state.
			 *
			 * @param theEvent
			 */
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent theEvent)
			{
				if(isGarageDoorOpen() && myOpenTime < 0)
				{
					myOpenTime = System.currentTimeMillis();
					myLogger.warning("Garage Door Opened.");
				}
				if(!isGarageDoorOpen() && myOpenTime != -1)
				{
					myOpenTime = -1;
					myLogger.warning("Garage Door Closed.");
				}
			}
		});
		if(isGarageDoorOpen())
		{
			myOpenTime = System.currentTimeMillis();
		}
		myLogger.warning("GarageDoorStatusService Startup:" + (isGarageDoorOpen() ? "Garage Door Opened." : "Garage Door Closed."));
		start();
	}

	public void resetOpenTime()
	{
		if(isGarageDoorOpen() && myOpenTime > 0)
		{
			myLogger.warning("Resetting open time");
			myOpenTime = System.currentTimeMillis();
		}
	}

	public boolean isGarageDoorOpen()
	{
		boolean anIsOpen = !myStatusPin.getState().isHigh();
		myLogger.info("Garage is " + (anIsOpen ? "Open" : "Closed"));
		return anIsOpen;
	}

	/**
	 * Check if we're open and if we've been opened too long.  If so, use the action service to close the door.
	 */
	@Override
	public void iterate()
	{
		if(myOpenTime > 0 && (System.currentTimeMillis() - myOpenTime) > kAutoCloseDelay)
		{
			myLogger.warning("Garage has been open too long, closing.");
			myOpenTime = System.currentTimeMillis();
			myActionService.closeDoor();
		}
	}

	public void setActionService(GarageDoorActionService theActionService)
	{
		myActionService = theActionService;
	}
}
