package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.text.*;

/** Provides a static method to display a modal dialog for the user to input data with a numeric pad, in the style of JOptionPane's methods. */
class JNumericPadDialog
{
   /** Displays a modal dialog for the user to input data with a numeric pad.
      Returns the selected value as a String or null on cancel/close. */
   public static String showDialog(Component parentComponent, String title, String message)
   {
      JPanel innerPanel = new JPanel(new BorderLayout());
      JNumericPad pad = new JNumericPad();
      final JTextField field = new JTextField();
      innerPanel.add(pad, BorderLayout.CENTER);
      innerPanel.add(field, BorderLayout.SOUTH);
      JNumericPadDriver driver = new JNumericPadDriver(pad, field);

      JOptionPane pane = new JOptionPane(message);
      pane.setMessage(innerPanel);
      pane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
      
      JDialog dialog = pane.createDialog(parentComponent, title);
      // switch focus to start with the text field, for ease typing
      dialog.addWindowListener(new WindowAdapter() {
	 public void windowOpened(WindowEvent we)
	 {
	    final javax.swing.Timer timer = new javax.swing.Timer(0, new ActionListener() {
	       private int counter = 0;
	       public void actionPerformed(ActionEvent ae)
	       {
		  boolean b = field.requestFocusInWindow();
		  if(b == true || counter++ >= 10)
		  {
		     javax.swing.Timer tim = (javax.swing.Timer)ae.getSource();
		     tim.stop();
		  }
	       }
	    });
	    timer.setCoalesce(true);
	    timer.setDelay(10);
	    timer.start();

	    // This approach doesn't work because the components aren't always ready when
	    // the window is.
	    // SwingUtilities.invokeLater(new Runnable() {
	    //    public void run()
	    //    {
	    // 	  System.out.println(field.requestFocusInWindow());
	    //    }
	    // });

	    // This approach obviously doesn't work a it all happens in the EDT.
	    // for(int i = 0; i < 50; i++)
	    // {
	    //    boolean possible = field.requestFocusInWindow();
	    //    if(possible)
	    // 	  break;
	    //    try
	    //    {
	    // 	  Thread.sleep(5);
	    //    }
	    //    catch(InterruptedException ie) { break; } // if we can't do it, give up
	    //    System.out.println("trying again...");
	    // }
	 }
      });

      dialog.show();
      Object selectedValue = pane.getValue();
      if(selectedValue == null)
	 return null;
      if((Integer)selectedValue == JOptionPane.OK_OPTION)
      {
	 return field.getText();
      }
      else
      {
	 return null;
      }
   }
}

class NumericPadEvent extends EventObject
{
   public static final int DOT_EVENT = -1;
   public static final int BACKSPACE_EVENT = -2;

   private final int value;

   public NumericPadEvent(JNumericPad src, int v)
   {
      super(src);
      value = v;
   }

   public boolean isDotEvent()
   {
      return value == DOT_EVENT;
   }

   public boolean isBackspaceEvent()
   {
      return value == BACKSPACE_EVENT;
   }

   public boolean isNumberEvent()
   {
      return value >= 0 && value < 10;
   }

   public int getValue()
   {
      return value;
   }
}

interface NumericPadListener
{
   public void buttonClicked(NumericPadEvent e);
}

/*
  1  2  3
  4  5  6
  7  8  9
  0  .  <-
 */
class JNumericPad extends JPanel implements ActionListener
{
   private JButton[] numberButtons = new JButton[10];
   private JButton dotButton;
   private JButton backspaceButton;

   private Vector<NumericPadListener> listeners = new Vector<NumericPadListener>();

   public JNumericPad()
   {
      super(new GridLayout(4, 3));

      // create the buttons
      for(int i = 0; i < numberButtons.length; i++)
      {
	 JButton b = new JButton("" + i);
	 b.setActionCommand("" + i);
	 b.addActionListener(this);
	 numberButtons[i] = b;
      }
      dotButton = new JButton(".");
      dotButton.addActionListener(this);
      backspaceButton = new JButton("<-");
      backspaceButton.addActionListener(this);

      // add them to the layout in the correct order
      for(int i = 1; i < numberButtons.length; i++)
      {
	 this.add(numberButtons[i]);
      }
      this.add(numberButtons[0]);
      this.add(dotButton);
      this.add(backspaceButton);
   }


   public void addNumericPadListener(NumericPadListener l)
   {
      listeners.add(l);
   }

   private void fireListeners(NumericPadEvent e)
   {
      for(NumericPadListener l : listeners)
	 l.buttonClicked(e);
   }


   public void actionPerformed(ActionEvent e)
   {
      if(e.getSource() == dotButton)
	 fireListeners(new NumericPadEvent(this, NumericPadEvent.DOT_EVENT));
      else if(e.getSource() == backspaceButton)
	 fireListeners(new NumericPadEvent(this, NumericPadEvent.BACKSPACE_EVENT));
      else
      {
	 String ac = e.getActionCommand();
	 NumericPadEvent ev = new NumericPadEvent(this, ac.charAt(0) - '0');
	 fireListeners(ev);
      }
   }






   public static void main(String[] args)
   {
      JFrame frame = new JFrame("JNumericPad test");
      frame.getContentPane().setLayout(new BorderLayout());
      
      JNumericPad pad = new JNumericPad();
      pad.addNumericPadListener(new NumericPadListener() {
	 public void buttonClicked(NumericPadEvent e)
	 {
	    if(e.isDotEvent())
	       System.out.println("DOT");
	    else if(e.isBackspaceEvent())
	       System.out.println("BACKSPACE");
	    else
	       System.out.println(e.getValue());
	 }
      });

      JTextField textfield = new JTextField();
      JNumericPadDriver driver = new JNumericPadDriver(pad, textfield);

      frame.getContentPane().add(textfield, BorderLayout.NORTH);
      frame.getContentPane().add(pad, BorderLayout.CENTER);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
   }
}



/** This object takes references to a JNumericPad and a JTextComponent and propagates input
    from the former to the later.  Direct input into the textfield is still allowed.  Care
    should be taken with this as numeric pad input will be input at the current caret location.
    Errors with backspaces "before" the start of the input will be silently ignored.
    TODO optional int/real/ipaddr validation.
*/
class JNumericPadDriver implements NumericPadListener
{
   private JNumericPad pad;
   private JTextComponent field;

   public JNumericPadDriver(JNumericPad n, JTextComponent f)
   {
      pad = n;
      field = f;

      pad.addNumericPadListener(this);
   }

   public void buttonClicked(NumericPadEvent e)
   {
      try
      {
	 Document d = field.getDocument();

	 if(e.isDotEvent())
	 {
	    d.insertString(d.getLength(), ".", null);
	 }
	 else if(e.isBackspaceEvent())
	 {
	    Caret car = field.getCaret();
	    int pos = car.getDot();
	    if(pos > 0)
	    {
	       d.remove(pos - 1, 1); // removes the char AFTER the caret
	    }

	 }
	 else
	 {
	    d.insertString(d.getLength(), "" + e.getValue(), null);
	 }
      }
      catch(BadLocationException ble)  { } // ignore
   }
   
}
