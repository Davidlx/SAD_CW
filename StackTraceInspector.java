/** StackTraceInspector.java
    A RuntimeInspector that just prints a trace of all
    the inspector activity.

    20100608 scotek Moved from OutputWindow to dedicated class.
*/

package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StackTraceInspector extends JPanel implements RuntimeInspector
{
   private JTextArea inspectArea;
   private int pushCount = 0;
   
   // does NOT register with the varstack itself, should be done by creating code
   public StackTraceInspector()
   {
      inspectArea = new JTextArea(10, 80);
      this.setLayout(new GridLayout(1, 1));
      this.add(new JScrollPane(inspectArea));
   }
   
   // RuntimeInspector interface methods
   public void variableValueChanged(final String varName)
   {
      // forcing this evaluation here shouldn't cause anything bad to happen?
      invokeAndWaitIgnoreException(new Runnable() {
	 public void run()
	 {
	    inspectArea.append(indent() + "Variable " + varName + " now " +
			       VarStack.getCurrentFrame().getValue(varName) + "\n");
	 }
      });
   }
   public void variableAdded(final String varName)
   {
      invokeAndWaitIgnoreException(new Runnable() {
	 public void run()
	 {
	    inspectArea.append(indent() + "Variable Added: " + varName + " with value " +
			       VarStack.getCurrentFrame().getValue(varName) + "\n");
	 }
      });
   }
   public void stackPushed(final String methName)
   {
      invokeAndWaitIgnoreException(new Runnable() {
	 public void run()
	 {
	    inspectArea.append(indent() + "Stack Pushed (" + methName  + ")\n");
	    pushCount++;
	 }
      });
   }
   public void stackPopped(final Object returnValue)
   {
      invokeAndWaitIgnoreException(new Runnable() {
	 public void run()
	 {
	    pushCount--;
	    inspectArea.append(indent() + "Stack Popped (" + returnValue + ")\n");
	 }
      });
   }
   private void invokeAndWaitIgnoreException(Runnable r)
   {
      try
      {
	 SwingUtilities.invokeAndWait(r);
      }
      catch(Exception e) {}
   }
   private String indent()
   {
      StringBuilder b = new StringBuilder();
      for(int i = 0 ; i < pushCount; i++)
	 b.append("   ");
      return b.toString();
   }
   
}