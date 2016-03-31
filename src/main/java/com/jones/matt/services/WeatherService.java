package com.jones.matt.services;

import com.jones.matt.GarageDoorController;
import com.jones.matt.sensors.DHT22Sensor;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Service to get temperature and humidity from a connected sensor
 */
public class WeatherService extends BaseService
{
	private static final int kSensorPin = Integer.getInteger("GPIO.temp.pin", 10);

	private DHT22Sensor mySensor;

	private float myCachedTemperature = -1;

	private float myCachedHumidity = -1;

	private long myLastUpdate;

	public WeatherService(GarageDoorController theController)
	{
		super(theController);
		mySensor = new DHT22Sensor(kSensorPin);
		new Timer().scheduleAtFixedRate(new Task(), 0, 5000);
	}

	public float getTemperature()
	{
		myLogger.config("Last update: " + myLastUpdate);
		return myCachedTemperature;
	}

	public float getHumidity()
	{
		myLogger.config("Last update: " + myLastUpdate);
		return myCachedHumidity;
	}

	private class Task extends TimerTask
	{
		@Override
		public void run()
		{
			myLogger.config("Getting new sensorData...");
			myCachedTemperature = mySensor.getTemperature();
			myCachedHumidity = mySensor.getHumidity();
			myLastUpdate = System.currentTimeMillis();
		}
	}
}
