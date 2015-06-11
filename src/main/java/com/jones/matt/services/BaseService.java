package com.jones.matt.services;

import com.jones.matt.GarageDoorController;

import java.util.logging.Logger;

/**
 * wrap get/set of controller
 */
public abstract class BaseService
{
	protected static Logger myLogger = Logger.getLogger("com.jones.BaseService");

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
