package com.jones.matt.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
/**
 *
 */
public class GarageDoorWebService
{
	private static final int kPort = Integer.getInteger("server.port", 80);

	private static final String kStatusPath = System.getProperty("status.path", "/Status");

	private static final String kClosePath = System.getProperty("status.path", "/Close");

	private GarageDoorActionService myActionService;

	private GarageDoorStatusService myStatusService;

	public GarageDoorWebService(GarageDoorStatusService theStatusService, GarageDoorActionService theDoorActionService) throws IOException
	{
		myStatusService = theStatusService;
		myActionService = theDoorActionService;
		HttpServer aServer = HttpServer.create(new InetSocketAddress(kPort), 0);
		aServer.createContext(kStatusPath, new StatusHandler());
		aServer.createContext(kClosePath, new CloseHandler());
		aServer.setExecutor(null); // creates a default executor
		aServer.start();
	}

	private class StatusHandler implements HttpHandler
	{
		public void handle(HttpExchange theExchange) throws IOException
		{
			String aResponse = myStatusService.isGarageDoorOpen() + "";
			theExchange.sendResponseHeaders(200, aResponse.length());
			OutputStream anOutputStream = theExchange.getResponseBody();
			anOutputStream.write(aResponse.getBytes());
			anOutputStream.close();
		}
	}

	private class CloseHandler implements HttpHandler
	{
		public void handle(HttpExchange theExchange) throws IOException
		{
			myActionService.closeDoor();
			String aResponse = "Closing";
			theExchange.sendResponseHeaders(200, aResponse.length());
			OutputStream anOutputStream = theExchange.getResponseBody();
			anOutputStream.write(aResponse.getBytes());
			anOutputStream.close();
		}
	}
}
