package com.jones.matt;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class spawns a thread and uses it to call the iterate method over and over
 * delaying by the amount specified by the iterate delay variable.  Default is two seconds.
 */
public abstract class IterateThread implements Runnable
{
	private static Logger myLogger = Logger.getLogger("com.jones.IterateThread");

	/**
	 * Default delay
	 */
	private long myIterateDelay = 2000;

	private boolean myIsStarted = false;

	@Override
	public void run()
	{
		while(myIsStarted)
		{
			try
			{
				iterate();
				Thread.sleep(myIterateDelay);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			} catch (Exception e)
			{
				myLogger.log(Level.WARNING, "IterateThread:", e);
			}
		}
	}

	/**
	 * This method gets called over and over again
	 */
	public abstract void iterate();

	/**
	 * Start iterating
	 */
	public void start()
	{
		myIsStarted = true;
		new Thread(this).start();
	}

	/**
	 * Set the delay to iterate with (in ms).
	 *
	 * @param theIterateDelay
	 */
	public void setIterateDelay(long theIterateDelay)
	{
		myIterateDelay = theIterateDelay;
	}
}
