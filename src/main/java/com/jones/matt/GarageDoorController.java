package com.jones.matt;

import com.jones.matt.services.GarageDoorActionService;
import com.jones.matt.services.GarageDoorMotionService;
import com.jones.matt.services.GarageDoorStatusService;
import com.jones.matt.services.GarageDoorWebService;

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

	public static void main(String[] theArgs) throws IOException
	{
		new GarageDoorController();
	}

	public GarageDoorController() throws IOException
	{
		setupLogger();
		GarageDoorMotionService aMotionService = new GarageDoorMotionService();
		GarageDoorStatusService aStatusService = new GarageDoorStatusService();
		GarageDoorActionService anActionService = new GarageDoorActionService();
		aMotionService.setStatusService(aStatusService);
		aStatusService.setActionService(anActionService);
		anActionService.setStatusService(aStatusService);
		new GarageDoorWebService(aStatusService, anActionService);

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
}
