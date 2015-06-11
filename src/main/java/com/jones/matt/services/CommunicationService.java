package com.jones.matt.services;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.jones.matt.GarageDoorController;

import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

/**
 * Service to communicate status changes back to a central hub.  If hub url
 * isn't defined, does nothing.
 */
public class CommunicationService extends BaseService
{
	private static String kHubUrl = System.getProperty("HubURL");//, "http://192.168.0.7:8080/Lights/S/Notification/Garage");

	public CommunicationService(GarageDoorController theController)
	{
		super(theController);
	}

	public void garageDoorOpened()
	{
		doAction("Opened");
	}

	public void garageDoorClosed()
	{
		//doAction("Closed");
	}

	private void doAction(String theAction)
	{
		if (kHubUrl == null)
		{
			return;
		}
		try
		{
			URLConnection aConnection = new URL(kHubUrl + "/" + theAction).openConnection();
			new String(ByteStreams.toByteArray(aConnection.getInputStream()), Charsets.UTF_8);
		}
		catch (Throwable e)
		{
			myLogger.log(Level.WARNING, "GarageController: ", e);
		}
	}
}
