package com.jones.matt.services;

import com.jones.matt.GarageDoorController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * REST service to allow checking status or closing the door remotely
 */
public class GarageDoorWebService extends BaseService
{
	/**
	 * port for server to run on
	 */
	private static final int kPort = Integer.getInteger("server.port", 80);

	/**
	 * Path to check status
	 */
	private static final String kStatusPath = System.getProperty("status.path", "/Status");

	private static final String kStatus2Path = System.getProperty("status.path", "/Status2");

	private static final String kDisableAutoClosePath = System.getProperty("status.path", "/DisableAutoClose");

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
		aServer.createContext(kStatus2Path, new Status2Handler());
		aServer.createContext(kDisableAutoClosePath, new AutoCloseHandler());
		aServer.setExecutor(null); // creates a default executor
		aServer.start();
	}

	private class AutoCloseHandler extends BaseHandler
	{
		@Override
		public String getResponse()
		{
			getController().getStatusService().disableAutoClose();
			return "DisablingAutoClose";
		}
	}

	private class Status2Handler extends BaseHandler
	{
		@Override
		public String getResponse()
		{
			myLogger.info("Checking status requested");
			return "{\"temperature\":" + getController().getWeatherService().getTemperature()
					+ ",\"humidity\":" + getController().getWeatherService().getHumidity() +
					",\"door\":" + getController().getStatusService().isGarageDoorOpen() + "}";
		}
	}

	/**
	 * Check the status of the door
	 */
	@Deprecated
	private class StatusHandler extends BaseHandler
	{
		@Override
		public String getResponse()
		{
			myLogger.info("Checking status requested");
			return getController().getStatusService().isGarageDoorOpen() + "";
		}
	}

	@Deprecated
	private class WeatherHandler extends BaseHandler
	{
		@Override
		public String getResponse()
		{
			myLogger.info("Checking Weather requested");
			return "{\"temperature\":" + getController().getWeatherService().getTemperature() + ",\"humidity\":" + getController().getWeatherService().getHumidity() + "}";
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
			String aResponse = generateCallback(theHttpExchange, getResponse());
			theHttpExchange.sendResponseHeaders(200, aResponse.length());
			OutputStream anOutputStream = theHttpExchange.getResponseBody();
			anOutputStream.write(aResponse.getBytes());
			anOutputStream.close();
		}
	}

	private String generateCallback(HttpExchange theHttpExchange, String theResponse)
	{
		Map<String, String> aParams = queryToMap(theHttpExchange.getRequestURI().getQuery());
		if (aParams.containsKey("callback"))
		{
			theResponse = aParams.get("callback") + "(" + theResponse + ");";
		}
		return theResponse;
	}

	private Map<String, String> queryToMap(String theQuery)
	{
		Map<String, String> aResult = new HashMap<>();
		if (theQuery != null)
		{
			for (String aParam : theQuery.split("&"))
			{
				String aPair[] = aParam.split("=");
				if (aPair.length > 1)
				{
					aResult.put(aPair[0], aPair[1]);
				}
				else
				{
					aResult.put(aPair[0], "");
				}
			}
		}
		return aResult;
	}
}
