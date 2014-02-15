package com.jones.matt.services;

import com.jones.matt.IterateThread;
import com.jones.matt.events.GarageDoorCloseHandler;
import com.jones.matt.events.GarageDoorOpenHandler;
import com.jones.matt.events.HasGarageDoorCloseEvents;
import com.jones.matt.events.HasGarageDoorOpenEvents;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GarageDoorStatusService extends IterateThread
{
	public static final long kAutoCloseDelay = 1000 * 60 * 10;//ms * seconds * minutes -> 10 Minutes

	private long myOpenTime = -1;

	private GpioPinDigitalInput myStatusPin;

	private GarageDoorActionService myActionService;

	public GarageDoorStatusService()
	{
		GpioController aGPIOFactory = GpioFactory.getInstance();
		myStatusPin = aGPIOFactory.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
		myStatusPin.addListener(new GpioPinListenerDigital()
		{
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent theEvent)
			{
				myOpenTime = isGarageDoorOpen() ? System.currentTimeMillis() : -1;
				System.out.println("GarageDoorStatusService:" + (isGarageDoorOpen() ? "Garage Door Opened." : "Garage Door Closed."));
			}
		});
		if(isGarageDoorOpen())
		{
			myOpenTime = System.currentTimeMillis();
		}
		System.out.println("GarageDoorStatusService Startup:" + (isGarageDoorOpen() ? "Garage Door Opened." : "Garage Door Closed."));
		start();
	}

	public boolean isGarageDoorOpen()
	{
		return !myStatusPin.getState().isHigh();
	}

	@Override
	public void iterate()
	{
		if(myOpenTime > 0 && (System.currentTimeMillis() - myOpenTime) > kAutoCloseDelay)
		{
			myOpenTime = System.currentTimeMillis();
			myActionService.closeDoor();
		}
	}

	public void setActionService(GarageDoorActionService theActionService)
	{
		myActionService = theActionService;
	}
}
