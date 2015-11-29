package org.scotek.vpl;

import java.awt.*;
import javax.swing.*;
import java.lang.reflect.*;

// BUG? this stops flashing after an indeterminate amount of time if you leave the modal dialog open.  maybe dur to now running in the swing event thread?  replace with a swingworker thread that sleeps, publish() a colour, sleep, publish() a color, and have the process method do the setBackground()?  This is really difficult to reproduce when looking for it.
class BackgroundFlasher extends Thread
{
   private static final int SLEEP_TIME = 200;
   private WorkspaceObject target;
   private Color flashColor;
   private boolean stop = false;
   
   public BackgroundFlasher(WorkspaceObject wo, Color c)
   {
      target = wo;
      flashColor = c;
   }
   
   public void run()
   {
      try
      {
	 while(!stop)
	 {
	    if(target != null)
	    {
	       SwingUtilities.invokeAndWait(new Runnable() {
		  public void run()
		  {
		     target.setBackground(flashColor);
		  }
	       });
	    }
	    sleep(SLEEP_TIME);
	    if(target != null)
	    {
	       SwingUtilities.invokeAndWait(new Runnable() {
		  public void run()
		  {
		     target.setBackground(Color.white);
		  }
	       });
	    }
	    sleep(SLEEP_TIME);
	 }
      }
      catch(InvocationTargetException ite)
      {
	 // could not call the runnable for some reason so just give up
      }
      catch(InterruptedException e)
      {
//	 System.err.println("BackgroundFlasher stopped by exception: " + e);
//	 e.printStackTrace();
	 // this is the expected way to stop
      }
      finally
      {
	 if(target != null)
	    target.setBackground(Color.white);
      }
   }
   
   public void stopFlashing()
   {
      stop = true;
      interrupt();
   }
}
