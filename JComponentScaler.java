/* JComponentScaler.java
** Zoom in and out of a component
** 20090102 scotek created
*/

package org.scotek.util.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class JComponentScaler extends JComponent
{
   JComponent child;
   int zoomLevel = 100;		// 100 = 100%

   //* @param child Component to be scaled.
   public JComponentScaler(JComponent c)
   {
      setDoubleBuffered(false);
      child = c;
      child.setDoubleBuffered(false);
      child.setVisible(true);
      Dimension d = child.getPreferredSize();
      System.err.println("Setting child to 0, 0, " + d.width + ", " + d.height);
      child.setBounds(0, 0, d.width, d.height);
      updateDimensions();
   }

   public void paint(Graphics g)
   {      
      System.err.println("painting...");

      super.paint(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.scale(zoomLevel / 100.0, zoomLevel / 100.0);
      
      System.err.println("painting child...");
      child.paint(g);
      System.err.println("...done");
   }
   
   public Dimension getPreferredSize()
   {
      System.err.println("getpreferredsize...");
      Dimension d = child.getPreferredSize();
      System.err.println("...done");
      return new Dimension((int)(d.width * (zoomLevel / 100.0)), (int)(d.height * (zoomLevel / 100.0)));
   }

   public Dimension getMaximumSize()
   {
      System.err.println("getMaximumsize...");
      Dimension d = child.getPreferredSize();
      System.err.println("...done");
      return new Dimension((int)(d.width * (zoomLevel / 100.0)), (int)(d.height * (zoomLevel / 100.0)));
   }

   public int getZoomLevel()
   {
      return zoomLevel;
   }

   public void setZoomLevel(int newZoom)
   {
      System.err.println("setZoomLevel...");
      zoomLevel = newZoom;
      updateDimensions();
      invalidate();
      revalidate();
      repaint();
      System.err.println("...done");
   }

   private void updateDimensions()
   {
      System.err.println("updateDimensions...");
      Dimension d = child.getPreferredSize();
//      child.setBounds(0, 0, d.width, d.height);
      setPreferredSize(new Dimension((int)(d.width * (zoomLevel / 100.0)), (int)(d.height * (zoomLevel / 100.0))));

      setBounds(0, 0, d.width, d.height);
      System.err.println("...done");
   }

//    public static void main(String[] argv)
//    {
//       JFrame frame = new JFrame("JComponentScaler test");

//       JLabel cld = new JLabel("<html><h1>Hello World!</h1></html>");
      
//       final JComponentScaler scaler = new JComponentScaler(cld);

//       JPanel ctrlPanel = new JPanel();
//       ctrlPanel.setLayout(new GridLayout(2, 1));
//       JButton zoomInButton = new JButton("Zoom In");
//       ctrlPanel.add(zoomInButton);
//       zoomInButton.addActionListener(new ActionListener() {
// 	 public void actionPerformed(ActionEvent e)
// 	 {
// 	    scaler.setZoomLevel(scaler.getZoomLevel() + 10);
// 	 }
//       });
//       JButton zoomOutButton = new JButton("Zoom Out");
//       ctrlPanel.add(zoomOutButton);
//       zoomOutButton.addActionListener(new ActionListener() {
// 	 public void actionPerformed(ActionEvent e)
// 	 {
// 	    scaler.setZoomLevel(scaler.getZoomLevel() - 10);
// 	 }
//       });

//       JPanel panel = new JPanel();
//       panel.setLayout(new BorderLayout());

//       panel.add(scaler, BorderLayout.CENTER);
//       panel.add(ctrlPanel, BorderLayout.EAST);

//       frame.getContentPane().add(panel);

//       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//       frame.setSize(800, 600);
//       frame.setVisible(true);
//    }

   public static void main(String[] argv)
   {
      JFrame frame = new JFrame("JComponentScaler test");

      JLabel cld = new JLabel(new ImageIcon("image.png"));
      
      final JComponentScaler scaler = new JComponentScaler(cld);

      JPanel ctrlPanel = new JPanel();
      ctrlPanel.setLayout(new GridLayout(2, 1));
      JButton zoomInButton = new JButton("Zoom In");
      ctrlPanel.add(zoomInButton);
      zoomInButton.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    scaler.setZoomLevel(scaler.getZoomLevel() + 10);
	 }
      });
      JButton zoomOutButton = new JButton("Zoom Out");
      ctrlPanel.add(zoomOutButton);
      zoomOutButton.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    scaler.setZoomLevel(scaler.getZoomLevel() - 10);
	 }
      });

      final JSlider slider = new JSlider(10, 200, 100);
      slider.setLabelTable(slider.createStandardLabels(25, 25));
      slider.setPaintLabels(true);
      slider.addChangeListener(new ChangeListener() {
	 public void stateChanged(ChangeEvent e)
	 {
	    scaler.setZoomLevel(slider.getValue());
	 }
      });

      JPanel panel = new JPanel();
      panel.setLayout(new BorderLayout());

      JScrollPane scrollPane = new JScrollPane(scaler);
      panel.add(scrollPane, BorderLayout.CENTER);
      panel.add(ctrlPanel, BorderLayout.EAST);
      panel.add(slider, BorderLayout.SOUTH);

      frame.getContentPane().add(panel);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(800, 600);
      frame.setVisible(true);
   }
}