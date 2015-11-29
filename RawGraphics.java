/* RawGraphics.java
** Simple graphics support in the RAW language
** 20081209 scotek created
*/

package org.scotek.vpl;//.graphics;

//import org.scotek.vpl.*;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;


class RAWGraphics
{
   JFrame frame;
   JImageComponent imageComp;
   BufferedImage image;
   int width, height;
   Color currentColor = Color.black;
   Color currentBackground = Color.white;

   //* Create a graphics area of the given width and height
   public RAWGraphics(int w, int h)
   {
      width = w;
      height = h;

      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      GraphicsDevice gd = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gd.getDefaultConfiguration();
      image = gc.createCompatibleImage(width, height, Transparency.BITMASK);

      imageComp = new JImageComponent(image);

      clear();

      frame = new JFrame("Cooker graphics area");
      frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
      frame.getContentPane().setLayout(new GridLayout(1, 1));
      frame.getContentPane().add(imageComp);
      frame.setSize(width, height);
      frame.setVisible(true);
   }

   /** Draw a pixel in the current color to the gien coordinates.
    ** If the coordinates are out of bounds, this will silently do nothing. */
   public void drawPixel(int x, int y)
   {
      Graphics2D g = image.createGraphics();
      g.setColor(currentColor);
      g.drawLine(x, y, x, y);
   }

   public void drawString(String s, int x, int y)
   {
      Graphics2D g = image.createGraphics();
      g.setColor(currentColor);
      g.drawString(s, x, y);
   }

   public void drawOval(int x, int y, int wx, int wy)
   {
      Graphics2D g = image.createGraphics();
      g.setColor(currentColor);
      g.drawOval(x, y, wx, wy);
   }

   public void clear()
   {
      Graphics2D g = image.createGraphics();
      g.setColor(currentBackground);
      g.fillRect(0, 0, width, height);
   }

   public void repaint()
   {
      imageComp.repaint();
   }
}
class JImageComponent extends JComponent
{
   BufferedImage image;
   
   public JImageComponent(BufferedImage img)
   {
      image = img;
      setSize(image.getWidth(), image.getHeight());
   }

   public void paint(Graphics g)
   {
      super.paint(g);
      g.drawImage(image, 0, 0, null);
   }
}


class CreateGraphicsOperator implements Expression
{
   Expression widthExp, heightExp;

   public void setWidthOperand(Expression e)
   {
      widthExp = e;
   }

   public void setHeightOperand(Expression e)
   {
      heightExp = e;
   }

   public Object getValue()
   {
      Object wVal = widthExp.getValue();
      Object hVal = heightExp.getValue();

      if(wVal instanceof Integer && hVal instanceof Integer)
      {
	 Integer w = (Integer)wVal;
	 Integer h = (Integer)hVal;

	 return new RAWGraphics(w, h);
      }
      else
      {
	 throw new InvalidArgumentTypeException("Parameters to createGraphics must be integers");
      }
   }
}

class DrawPixelOperator extends Statement
{
   Expression contextExp, xExp, yExp;

   public void setContextOperand(Expression e)
   {
      contextExp = e;
   }
   public void setXOperand(Expression e)
   {
      xExp = e;
   }
   public void setYOperand(Expression e)
   {
      yExp = e;
   }

   public void performAction()
   {
      Object contextVal = contextExp.getValue();
      Object xVal = xExp.getValue();
      Object yVal = yExp.getValue();

      if(contextVal instanceof RAWGraphics)
      {
	 if(xVal instanceof Integer && yVal instanceof Integer)
	 {
	    RAWGraphics context = (RAWGraphics)contextVal;
	    Integer x = (Integer)xVal;
	    Integer y = (Integer)yVal;
	    
	    context.drawPixel(x, y);
	    context.repaint();
	 }
	 else
	 {
	    throw new InvalidArgumentTypeException("Second and thrid arguments to drawPixel must be integer coordinates");
	 }
      }
      else
      {
	 throw new InvalidArgumentTypeException("First argument to drawPixel must be the graphics context");
      }
      
      
   }
}

class DrawStringOperator extends Statement
{
   Expression contextExp, xExp, yExp, strExp;

   public void setContextOperand(Expression e)
   {
      contextExp = e;
   }
   public void setXOperand(Expression e)
   {
      xExp = e;
   }
   public void setYOperand(Expression e)
   {
      yExp = e;
   }
   public void setStringOperand(Expression e)
   {
      strExp = e;
   }

   public void performAction()
   {
      Object contextVal = contextExp.getValue();
      Object xVal = xExp.getValue();
      Object yVal = yExp.getValue();
      Object strVal = strExp.getValue();

      if(contextVal instanceof RAWGraphics)
      {
	 if(xVal instanceof Integer && yVal instanceof Integer)
	 {
	    if(strVal instanceof String)
	    {
	       RAWGraphics context = (RAWGraphics)contextVal;
	       Integer x = (Integer)xVal;
	       Integer y = (Integer)yVal;
	       String s = (String)strVal;
	       
	       context.drawString(s, x, y);
	       context.repaint();
	    }
	    else
	    {
	       throw new InvalidArgumentTypeException("Last argument to drawSting must be a string");
	    }
	 }
	 else
	 {
	    throw new InvalidArgumentTypeException("Second and third arguments to drawString must be integer coordinates");
	 }
      }
      else
      {
	 throw new InvalidArgumentTypeException("First argument to drawString must be the graphics context");
      }
      
      
   }
}

class DrawCircleOperator extends Statement
{
   Expression contextExp, xExp, yExp, diamExp;

   public void setContextOperand(Expression e)
   {
      contextExp = e;
   }
   public void setXOperand(Expression e)
   {
      xExp = e;
   }
   public void setYOperand(Expression e)
   {
      yExp = e;
   }
   public void setDiameterOperand(Expression e)
   {
      diamExp = e;
   }

   public void performAction()
   {
      Object contextVal = contextExp.getValue();
      Object xVal = xExp.getValue();
      Object yVal = yExp.getValue();
      Object diamVal = diamExp.getValue();

      if(contextVal instanceof RAWGraphics)
      {
	 if(xVal instanceof Integer && yVal instanceof Integer)
	 {
	    if(diamVal instanceof Integer)
	    {
	       RAWGraphics context = (RAWGraphics)contextVal;
	       Integer x = (Integer)xVal;
	       Integer y = (Integer)yVal;
	       Integer d = (Integer)diamVal;
	       
	       context.drawOval(x, y, d, d);
	       context.repaint();
	    }
	    else
	    {
	       throw new InvalidArgumentTypeException("Last argument to drawCircle must be a string");
	    }
	 }
	 else
	 {
	    throw new InvalidArgumentTypeException("Second and third arguments to drawCircle must be integer coordinates");
	 }
      }
      else
      {
	 throw new InvalidArgumentTypeException("First argument to drawCircle must be the graphics context");
      }
      
      
   }
}

class ClearGraphics extends Statement
{
   Expression contextExp;

   public void setContextOperand(Expression e)
   {
      contextExp = e;
   }

   public void performAction()
   {
      Object contextVal = contextExp.getValue();

      if(contextVal instanceof RAWGraphics)
      {
	 RAWGraphics context = (RAWGraphics)contextVal;
	       
	 context.clear();
	 context.repaint();
      }
      else
      {
	 throw new InvalidArgumentTypeException("First argument to clearGraphics must be the graphics context");
      }
   }
}
//class SetForegroundColor extends Statement
//{}