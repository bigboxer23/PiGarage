package com.jones.matt.services;

import com.jones.matt.GarageDoorController;

/**
 * wrap get/set of controller
 */
public abstract class BaseService
{
	private GarageDoorController myController;

	public BaseService(GarageDoorController theController)
	{
		myController = theController;
	}

	public GarageDoorController getController()
	{
		return myController;
	}
}
