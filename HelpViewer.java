package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

public class HelpViewer extends JFrame implements ActionListener
{
   private final File file;

   private JButton closeButton;
   private JButton printButton;
   private JEditorPane editor;

   public HelpViewer(File helpFile)
   {
      super(helpFile.getName().substring(0, helpFile.getName().indexOf(".html")));

      file = helpFile;

      try
      {
	 editor = new JEditorPane(helpFile.toURI().toURL());
      }
      catch(Exception e)
      {
	 editor = new JEditorPane();
      }

      closeButton = new JButton("Close");
      closeButton.addActionListener(this);
//      printButton = new JButton("Print...");
//      printButton.setEnabled(false);
      Box hb = Box.createHorizontalBox();
//      hb.add(printButton);
      hb.add(Box.createHorizontalGlue());
      hb.add(closeButton);

      this.getContentPane().setLayout(new BorderLayout());
      this.getContentPane().add(new JScrollPane(editor), BorderLayout.CENTER);
      this.getContentPane().add(hb, BorderLayout.SOUTH);
      this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.pack();
   }
   
   public void actionPerformed(ActionEvent e)
   {
      if(e.getSource() == closeButton)
      {
	 this.dispose();
      }
      else
      {
	 // TODO enable printing
	 // http://java.sun.com/docs/books/tutorial/uiswing/misc/printtext.html
      }
   }
}