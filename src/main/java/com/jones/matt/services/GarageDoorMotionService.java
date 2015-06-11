package com.jones.matt.services;

import com.jones.matt.GarageDoorController;
import com.jones.matt.util.GPIOUtils;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * Sensor wired up to GPIO3 (pin 15), 5v (pin 2), grd (pin 6)
 * Listens for state change (meaning motion) and tells the status service to reset its auto
 * close timer (we're actively in the garage working on something)
 */
public class GarageDoorMotionService extends BaseService
{
	/**
	 * Pin to use for status
	 */
	private static final Pin kMotionPin = GPIOUtils.getPin(Integer.getInteger("GPIO.motion.pin", 3));

	/**
	 * Delay because the sensor bounces up and down so we don't want to reset a bunch of times
	 */
	private static final long kDelay = 5 * 1000;//5 seconds

	private GpioPinDigitalInput myStatusPin;

	/**
	 * Last time we've detected motion
	 */
	private long myLastTime = -1;

	public GarageDoorMotionService(GarageDoorController theController)
	{
		super(theController);
		GpioController aGPIOFactory = GpioFactory.getInstance();
		myStatusPin = aGPIOFactory.provisionDigitalInputPin(kMotionPin, PinPullResistance.PULL_DOWN);
		myStatusPin.addListener(new GpioPinListenerDigital()
		{
			/**
			 * Listen for status changes, inform the status service we're working so it won't auto
			 * close the door.
			 *
			 * @param theEvent
			 */
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent theEvent)
			{
				if (isMotionDetected() && (System.currentTimeMillis() - myLastTime) > kDelay)
				{
					myLastTime = System.currentTimeMillis();
					myLogger.warning("Motion detected!");
					getController().getStatusService().resetOpenTime();
				}
			}
		});
	}

	public boolean isMotionDetected()
	{
		return myStatusPin.getState().isHigh();
	}
}
