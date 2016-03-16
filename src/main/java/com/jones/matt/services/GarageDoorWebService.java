package com.jones.matt.services;

import com.jones.matt.GarageDoorController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * REST service to allow checking status or closing the door remotely
 */
public class GarageDoorWebService extends BaseService
{
	/**
	 * port for server to run on
	 */
	private static final int kPort = Integer.getInteger("server.port", 80);

	private static final String kStatus2Path = System.getProperty("status.path", "/Status2");

	private static final String kDisableAutoClosePath = System.getProperty("status.path", "/DisableAutoClose");

	/**
	 * Path to close the door
	 */
	private static final String kClosePath = System.getProperty("close.path", "/Close");

	private static final String kOpenPath = System.getProperty("open.path", "/Open");

	public GarageDoorWebService(GarageDoorController theController) throws IOException
	{
		super(theController);
		myLogger.warning("Starting web service");
		HttpServer aServer = HttpServer.create(new InetSocketAddress(kPort), 0);
		aServer.createContext(kClosePath, new CloseHandler());
		aServer.createContext(kOpenPath, new OpenHandler());
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
			myLogger.config("Checking status requested");
			return "{\"temperature\":" + getController().getWeatherService().getTemperature()
					+ ",\"humidity\":" + getController().getWeatherService().getHumidity() +
					",\"door\":" + getController().getStatusService().isGarageDoorOpen() +
					",\"autoClose\":" + getController().getStatusService().getAutoCloseTimeRemaining() + "}";
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
			myLogger.warning("Closing door requested");
			getController().getActionService().closeDoor();
			return "\"Closing\"";
		}
	}

	private class OpenHandler extends BaseHandler
	{
		@Override
		public String getResponse()
		{
			myLogger.warning("Opening door requested");
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
			myLogger.config("BaseHandle " + this.getClass());
			String aResponse = getResponse();
			myLogger.config("BaseHandle response generated.");
			theHttpExchange.sendResponseHeaders(200, aResponse.length());
			OutputStream anOutputStream = theHttpExchange.getResponseBody();
			myLogger.config("BaseHandle output.");
			anOutputStream.write(aResponse.getBytes());
			myLogger.config("BaseHandle write done.");
			anOutputStream.close();
		}
	}
}
