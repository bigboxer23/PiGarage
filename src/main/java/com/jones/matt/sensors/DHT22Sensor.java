package com.jones.matt.sensors;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Wrapper around the adafruit driver to return temp and humidity from
 * a DHT22 Sensor
 */
public class DHT22Sensor
{
	private final static String kTemp = "Temp =";
	private final static String kHumidity = "Hum =";
	private static final long kPollingInterval = 3000;

	private final int myPin;
	private String myLastValue;
	private long myLastCheck;

	/**
	 *
	 * @param thePin the GPIO pin number to use
	 */
	public DHT22Sensor(int thePin)
	{
		myPin = thePin;
	}

	public synchronized float getHumidity()
	{
		checkForUpdates();
		return parseHumidity(myLastValue);
	}

	private void checkForUpdates()
	{
		long aNow = System.currentTimeMillis();
		if (aNow - myLastCheck > kPollingInterval)
		{
			String aValues = readValues();
			if (aValues.indexOf('%') > 0)
			{
				myLastValue = aValues;
				myLastCheck = aNow;
			}
		}
	}

	public synchronized float getTemperature()
	{
		checkForUpdates();
		return parseTemperature(myLastValue);
	}

	private float parseTemperature(String theValue)
	{
		if (theValue == null)
		{
			return Float.MIN_VALUE;
		}
		float aCelsius = Float.parseFloat(theValue.substring(theValue.indexOf(kTemp) + kTemp.length(), theValue.indexOf('*')));
		return (aCelsius * 1.8000f) + 32.00f;
	}

	private float parseHumidity(String theValue)
	{
		if (theValue == null)
		{
			return Float.MIN_VALUE;
		}
		return Float.parseFloat(theValue.substring(theValue.indexOf(kHumidity) + kHumidity.length(), theValue.indexOf('%')));
	}

	private String readValues()
	{
		String aResult = "";
		try
		{
			Process aProcess = Runtime.getRuntime().exec(String.format("Adafruit_DHT 22 %d", myPin));
			BufferedReader aReader = new BufferedReader(new InputStreamReader(aProcess.getInputStream()));
			String aLine = null;
			while ((aLine = aReader.readLine()) != null)
			{
				aResult += aLine;
			}
		}
		catch (Exception theException)
		{
			System.err.println(String.format("Could not read the DHT22 sensor at pin %d", myPin));
			theException.printStackTrace();
			return null;
		}
		return aResult;
	}
}
