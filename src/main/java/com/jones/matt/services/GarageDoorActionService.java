package com.jones.matt.services;

import com.jones.matt.events.GarageDoorCloseEvent;
import com.jones.matt.events.GarageDoorCloseHandler;
import com.jones.matt.events.GarageDoorOpenEvent;
import com.jones.matt.events.GarageDoorOpenHandler;

/**
 *
 */
public class GarageDoorActionService
{
	private GarageDoorStatusService myStatusService;


	public void closeDoor()
	{
		if(myStatusService.isGarageDoorOpen())
		{
			System.out.println("Closing the door.");
		}
	}

	public void setStatusService(GarageDoorStatusService theStatusService)
	{
		myStatusService = theStatusService;
	}
}
