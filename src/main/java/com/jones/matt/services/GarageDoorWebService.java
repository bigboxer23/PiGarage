package com.jones.matt.services;

import com.jones.matt.GarageDoorController;
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
public class GarageDoorWebService extends BaseService
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
	private static final String kClosePath = System.getProperty("close.path", "/Close");

	private static final String kOpenPath = System.getProperty("open.path", "/Open");

	private static final String kWeatherPath = System.getProperty("weather.path", "/Weather");

	public GarageDoorWebService(GarageDoorController theController) throws IOException
	{
		super(theController);
		myLogger.warning("Starting web service");
		HttpServer aServer = HttpServer.create(new InetSocketAddress(kPort), 0);
		aServer.createContext(kStatusPath, new StatusHandler());
		aServer.createContext(kClosePath, new CloseHandler());
		aServer.createContext(kOpenPath, new OpenHandler());
		aServer.createContext(kWeatherPath, new WeatherHandler());
		aServer.setExecutor(null); // creates a default executor
		aServer.start();
	}

	/**
	 * Check the status of the door
	 */
	private class StatusHandler extends BaseHandler
	{
		@Override
		public String getResponse()
		{
			myLogger.info("Checking status requested");
			return getController().getStatusService().isGarageDoorOpen() + "";
		}
	}

	private class WeatherHandler extends BaseHandler
	{
		@Override
		public String getResponse()
		{
			myLogger.info("Checking Weather requested");
			return "{temperature:" + getController().getWeatherService().getTemperature()
					+ ",humidity:" + getController().getWeatherService().getHumidity() + "}";
		}
	}

	/**
	 * Close the door
	 */
	private class CloseHandler extends BaseHandler
	{
		@Override
		public String getResponse()
		{
			myLogger.info("Closing door requested");
			getController().getActionService().closeDoor();
			return "\"Closing\"";
		}
	}

	private class OpenHandler extends BaseHandler
	{
		@Override
		public String getResponse()
		{
			myLogger.info("Opening door requested");
			getController().getActionService().openDoor();
			return "\"Opening\"";
		}
	}

	private abstract class BaseHandler implements HttpHandler
	{
		public abstract String getResponse();

		@Override
		public final void handle(HttpExchange theHttpExchange) throws IOException
		{
			String aResponse = getResponse();
			theHttpExchange.sendResponseHeaders(200, aResponse.length());
			OutputStream anOutputStream = theHttpExchange.getResponseBody();
			anOutputStream.write(aResponse.getBytes());
			anOutputStream.close();
		}
	}
}
