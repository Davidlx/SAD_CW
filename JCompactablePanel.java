package org.scotek.util.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javax.swing.border.*;
import java.awt.image.*;

public class JCompactablePanel extends JPanel implements ActionListener
{
//   static ImageIcon hideIcon;// = new ImageIcon("hide.gif");
//   static ImageIcon showIcon;// = new ImageIcon("show.gif");
   BufferedImage hideImg, showImg;
   ImageBorder bdr;

    boolean isHidden = true;
    JPanel mainPanel = new JPanel();
    JButton button = new JButton();

    JCompactablePanel()
    {
	this("", null);
    }

    public JCompactablePanel(String headerString, JComponent mainComp)
    {
	super(new BorderLayout());

//	if(hideIcon == null)
//	   hideIcon = new ImageIcon(Class.class.getResource("/icons/hide.png"));
//	if(showIcon == null)
//	   showIcon = new ImageIcon(Class.class.getResource("/icons/show.png"));

	button.setText(headerString);
//	button.setIcon(hideIcon);
	try
	{
//	   java.net.URL ul = getClass().getResource("/icons/hide.png");
	   hideImg = javax.imageio.ImageIO.read(getClass().getResource("/icons/hide.png"));
	   showImg = javax.imageio.ImageIO.read(getClass().getResource("/icons/show.png"));
//	   ImageBorder ib = new ImageBorder(button, javax.imageio.ImageIO.read(ul));
	   bdr = new ImageBorder(button, hideImg);
	   button.setBorder(BorderFactory.createCompoundBorder(button.getBorder(), bdr));
	}
	catch(Exception e)
	{
	   System.err.println("imageborder problem");
	}

	
	button.addActionListener(this);

	JPanel panelb = new JPanel(new BorderLayout());
	panelb.setBorder(BorderFactory.createCompoundBorder(panelb.getBorder(), BorderFactory.createMatteBorder(0, 10, 0, 0, Color.gray)));
	panelb.add(mainComp, BorderLayout.CENTER);

	mainPanel.add(panelb);//mainComp);
	mainPanel.setBorder(BorderFactory.createLoweredBevelBorder());
	mainPanel.setVisible(false);

	this.add(button, BorderLayout.NORTH);
	this.add(mainPanel, BorderLayout.CENTER);
    }

    public JCompactablePanel(JComponent headerComp, JComponent mainComp)
    {
	super(new BorderLayout());
	
	mainPanel.add(mainComp);
	mainPanel.setVisible(false);
	
	this.add(headerComp, BorderLayout.NORTH);
	this.add(mainPanel, BorderLayout.CENTER);
    }


    // public due to interface requirements
    public void actionPerformed(ActionEvent e)
    {
	if(isHidden)
	    setContentVisible(true);
	else
	    setContentVisible(false);
    }

    public void setContentVisible(boolean show)
    {
	if(show)
	{
//	    button.setIcon(showIcon);
	   bdr.setImage(showImg);
	    mainPanel.setVisible(true);
	    isHidden = false;
	}
	else
	{
//	    button.setIcon(hideIcon);
	   bdr.setImage(hideImg);
	    mainPanel.setVisible(false);
	    isHidden = true;
	}
	this.revalidate();
    }

    public boolean isContentVisisble()
    {
	return !isHidden;
    }

    public static void main(String[] argv)
    {
	JFrame frame = new JFrame();
	Box hbox = Box.createHorizontalBox();
	for(int i = 0; i < 3; i++)
	{
	    Box vbox = Box.createVerticalBox();
	    for(int j = 0; j < 6; j++)
	    {
		JCompactablePanel p
		    = new JCompactablePanel("View Image",
					    new JLabel(new ImageIcon("pic.gif")));
		vbox.add(p);
	    }
	    hbox.add(vbox);
	}
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add(new JScrollPane(hbox), BorderLayout.CENTER);
	frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    System.exit(0);
		}});
	frame.pack();
	frame.show();
    }
    
}

class ImageBorder implements Border
{
   BufferedImage img;
   JComponent comp;
   
   public ImageBorder(JComponent c, BufferedImage i)
   {
      comp = c;
      img = i;
   }

   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
   {
      int yPos = (comp.getPreferredSize().height - img.getHeight())/2;
      ((Graphics2D) g).drawRenderedImage(img, java.awt.geom.AffineTransform.getTranslateInstance(0,yPos));
   }

   public Insets getBorderInsets(Component c)
   {
      return new Insets(0,img.getWidth(),0,0);
   }
   
   public boolean isBorderOpaque()
   {
      return true;
   }

   public void setImage(BufferedImage im)
   {
      img = im;
   }
}
