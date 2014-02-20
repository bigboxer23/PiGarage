package com.jones.matt.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * REST service to allow checking status or closing the door remotely
 */
public class GarageDoorWebService
{
	private static Logger myLogger = Logger.getLogger("com.jones.GarageDoorWebService");

	/**
	 * port for server to run on
	 */
	private static final int kPort = Integer.getInteger("server.port", 80);

	/**
	 * Path to check status
	 */
	private static final String kStatusPath = System.getProperty("status.path", "/Status");

	/**
	 * Path to close the door
	 */
	private static final String kClosePath = System.getProperty("status.path", "/Close");

	private GarageDoorActionService myActionService;

	private GarageDoorStatusService myStatusService;

	public GarageDoorWebService(GarageDoorStatusService theStatusService, GarageDoorActionService theDoorActionService) throws IOException
	{
		myLogger.warning("Starting web service");
		myStatusService = theStatusService;
		myActionService = theDoorActionService;
		HttpServer aServer = HttpServer.create(new InetSocketAddress(kPort), 0);
		aServer.createContext(kStatusPath, new StatusHandler());
		aServer.createContext(kClosePath, new CloseHandler());
		aServer.setExecutor(null); // creates a default executor
		aServer.start();
	}

	/**
	 * Check the status of the door
	 */
	private class StatusHandler implements HttpHandler
	{
		public void handle(HttpExchange theExchange) throws IOException
		{
			myLogger.info("Checking status requested");
			String aResponse = myStatusService.isGarageDoorOpen() + "";
			theExchange.sendResponseHeaders(200, aResponse.length());
			OutputStream anOutputStream = theExchange.getResponseBody();
			anOutputStream.write(aResponse.getBytes());
			anOutputStream.close();
		}
	}

	/**
	 * Close the door
	 */
	private class CloseHandler implements HttpHandler
	{
		public void handle(HttpExchange theExchange) throws IOException
		{
			myLogger.info("Closing door requested");
			myActionService.closeDoor();
			String aResponse = "Closing";
			theExchange.sendResponseHeaders(200, aResponse.length());
			OutputStream anOutputStream = theExchange.getResponseBody();
			anOutputStream.write(aResponse.getBytes());
			anOutputStream.close();
		}
	}
}
