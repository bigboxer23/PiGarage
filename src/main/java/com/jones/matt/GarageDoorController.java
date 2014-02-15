package com.jones.matt;

import com.jones.matt.services.GarageDoorActionService;
import com.jones.matt.services.GarageDoorStatusService;
import com.jones.matt.services.GarageDoorWebService;

import java.io.IOException;

/**
 *
 */
public class GarageDoorController extends IterateThread
{
	public static void main(String[] theArgs) throws IOException
	{
		new GarageDoorController();
	}

	public GarageDoorController() throws IOException
	{
		GarageDoorStatusService aStatusService = new GarageDoorStatusService();
		GarageDoorActionService anActionService = new GarageDoorActionService();
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
}
