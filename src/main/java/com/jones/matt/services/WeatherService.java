package com.jones.matt.services;

import com.jones.matt.GarageDoorController;
import com.jones.matt.sensors.DHT22Sensor;

/**
 * Service to get temperature and humidity from a connected sensor
 */
public class WeatherService extends BaseService
{
	private static final int kSensorPin = Integer.getInteger("GPIO.temp.pin", 10);

	private DHT22Sensor mySensor;

	public WeatherService(GarageDoorController theController)
	{
		super(theController);
		mySensor = new DHT22Sensor(kSensorPin);
	}

	public float getTemperature()
	{
		return mySensor.getTemperature();
	}

	public float getHumidity()
	{
		return mySensor.getHumidity();
	}
}
