package com.jones.matt;

/**
 * This class spawns a thread and uses it to call the iterate method over and over
 * delaying by the amount specified by the iterate delay variable.  Default is two seconds.
 */
public abstract class IterateThread implements Runnable
{

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
			iterate();
			try
			{
				Thread.sleep(myIterateDelay);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
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
