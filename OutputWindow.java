/* OutputWindow.java
** Display the output from the program in a "proper" window instead of the console.
** Taken and adapted from progtool project.
** Mainly got rid of displaying STDERR, didn't need threading model, etc.
** 20081214 scotek Adapted for RAW Cooker
** 20100605 scotek Added experimental RuntimeInspector display
*/

//package progtool;
package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.text.*;

public class OutputWindow extends OutputStream implements WindowListener
{
   private JFrame window;
   private JTabbedPane tabber;
   private JTextPane outPane;
   private Document outDoc;

   private PrintStream savedOut;
   private StringBuffer sb = new StringBuffer();

   private StackTraceInspector inspector;

   public OutputWindow(String title)
   {
      super();
      makeGUI(title);
   }

    private void makeGUI(String title)
    {
	tabber = new JTabbedPane();
	outPane = new JTextPane();
	outPane.setEditable(false);
	tabber.addTab("Standard Output", null,
		      new JScrollPane(outPane), "Output from the command");
	outDoc = outPane.getDocument();
//	errPane = new JTextPane();
//	errPane.setEditable(false);
//	tabber.addTab("Standard Error", null,
//		      new JScrollPane(errPane), "Output from the command");

	inspector = new StackTraceInspector();
	VarStack.addRuntimeInspector(inspector);

	window = new JFrame(title);
	window.getContentPane().setLayout(new BorderLayout());
	window.getContentPane().add(tabber, BorderLayout.CENTER);
	window.getContentPane().add(inspector, BorderLayout.NORTH);
	window.setSize(new Dimension(400, 400));
	window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	window.addWindowListener(this);
	window.setVisible(true);

    }

   public void startCapture()
   {
      savedOut = System.out;
      System.setOut(new PrintStream(this));
   }

   public void stopCapture()
   {
      System.setOut(savedOut);
   }

   public void setVisible(boolean s)
   {
      window.setVisible(s);
   }

   public void flush(){ }
    
   public void close(){ }

   public void write(int b) throws IOException
   {
      
      if (b == '\r')
	 return;
      
      if (b == '\n')
      {
	 try
	 {
	    outDoc.insertString(outDoc.getLength(), sb.toString(), null);
	    sb.setLength(0);
	 }
	 catch(BadLocationException ble)
	 {
	    System.err.println(ble);
	    ble.printStackTrace();
	 }
      }
      
      sb.append((char)b);
   }


   
   // WindowListener interface methods to remove the inspector when closing the window
   public void windowClosed(WindowEvent e)
   {
      // FIXME not enough! needs to be removed when the program ends otherwise the next
      // run will also be appended if the output window has been left open.
      VarStack.removeRuntimeInspector(inspector);
   }
   public void windowOpened(WindowEvent e) {}
   public void windowClosing(WindowEvent e) {}
   public void windowIconified(WindowEvent e) {}
   public void windowDeiconified(WindowEvent e) {}
   public void windowActivated(WindowEvent e) {}
   public void windowDeactivated(WindowEvent e) {}
   public void windowStateChanged(WindowEvent e) {}
   public void windowGainedFocus(WindowEvent e) {}
   public void windowLostFocus(WindowEvent e) {}
}
