package com.jones.matt;

import com.jones.matt.services.*;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Main class, creates services for status, action, and web, then iterates doing nothing
 * to keep everything alive.
 */
public class GarageDoorController extends IterateThread
{
	private static Logger myLogger = Logger.getLogger("com.jones");

	private GarageDoorMotionService myMotionService;

	private GarageDoorStatusService myStatusService;

	private GarageDoorActionService myActionService;

	private GarageDoorWebService myWebService;

	private WeatherService myWeatherService;

	private CommunicationService myCommunicationService;

	public static void main(String[] theArgs) throws IOException
	{
		new GarageDoorController();
	}

	private GarageDoorController() throws IOException
	{
		setupLogger();
		myMotionService = new GarageDoorMotionService(this);
		myStatusService = new GarageDoorStatusService(this);
		myActionService = new GarageDoorActionService(this);
		myWebService = new GarageDoorWebService(this);
		myCommunicationService = new CommunicationService(this);
		myWeatherService = new WeatherService(this);

		start();
	}

	@Override
	public void iterate()
	{
		//Do nothing, just keeping everything alive
	}

	private void setupLogger() throws IOException
	{
		FileHandler aHandler = new FileHandler(System.getProperty("log.location", "/home/pi/garage/logs/piGarage.log"), true);
		aHandler.setFormatter(new SimpleFormatter());
		myLogger.addHandler(aHandler);
		myLogger.setLevel(Level.parse(System.getProperty("log.level", "WARNING")));
	}

	public GarageDoorMotionService getMotionService()
	{
		return myMotionService;
	}

	public GarageDoorStatusService getStatusService()
	{
		return myStatusService;
	}

	public GarageDoorActionService getActionService()
	{
		return myActionService;
	}

	public GarageDoorWebService getWebService()
	{
		return myWebService;
	}

	public WeatherService getWeatherService()
	{
		return myWeatherService;
	}

	public CommunicationService getCommunicationService()
	{
		return myCommunicationService;
	}
}
