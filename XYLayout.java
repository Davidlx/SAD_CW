// TEMP COPIED FROM java-src REPOS FOR DEV SPEED!
// 20081124 scotek Updated a bit to try and work with JScrollPane

package org.scotek.util.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Vector;

public class XYLayout implements LayoutManager2
{
   Dimension d;
   Vector<Component> components = new Vector<Component>();
   Vector<Point> coords = new Vector<Point>();
	
   public void addLayoutComponent(String name, Component comp)
   {
      System.err.println("addLayoutComponent not done");
   }
	
   public void removeLayoutComponent(Component comp)
   {
      Enumeration e = components.elements();
      int i = -1;
      while(e.hasMoreElements())
      {
	 i++;
	 Component c = (Component)e.nextElement();
	 if(c == comp)
	 {
	    components.remove(i);
	    coords.remove(i);
	    return;
	 }
      }
   }
	
   public Dimension preferredLayoutSize(Container parent)
   {
//      return parent.getPreferredSize();
      Dimension min = minimumLayoutSize(parent);
      min.setSize(min.width + 500, min.height + 500);
      return min;
   }
	
   /** Calculate the minimum width as the the furthest right edge and minimum
    ** height as the lowest bottom edge TODO */
   public Dimension minimumLayoutSize(Container parent)
   {
//      return parent.getMinimumSize();
      int rightEdge = 0, lowestBottom = 0;

      for(Component c : components)
      {
	 Dimension d = c.getPreferredSize();
	 int re = ((int)c.getX()) + d.width;
	 int lb = ((int)c.getY()) + d.height;

	 if(re > rightEdge)
	    rightEdge = re;

	 if(lb > lowestBottom)
	    lowestBottom = lb;
      }

      return new Dimension(rightEdge, lowestBottom);
   }
	
   public void layoutContainer(Container parent)
   {
		
   }
	
   // LayoutManager2 methods now...
   public void addLayoutComponent(Component comp, Object constraints)
   {
      components.add(comp);
      if(constraints == null)
	 constraints = new Point(0,0);
      else
	 if(!(constraints instanceof Point))
	    System.err.println("addLayoutComponent: constraint is not a Point");
      coords.add((Point)constraints);
   }
	
   public Dimension maximumLayoutSize(Container target)
   {
      return target.getMaximumSize();
   }
	
   public float getLayoutAlignmentX(Container target)
   {
      return (float)0.5;
   }
	
   public float getLayoutAlignmentY(Container target)
   {
      return (float)0.5;
   }
	
   public void invalidateLayout(Container target)
   {
      Enumeration e = components.elements();
      int i = -1;
      while(e.hasMoreElements())
      {
	 i++;
	 Component c = (Component)e.nextElement();
	 Point p = (Point)coords.elementAt(i);
	 Dimension d = c.getPreferredSize();
	 c.setBounds((int)p.getX(), (int)p.getY(), d.width, d.height);
	 c.validate();
      }
   }
	

   // XYLayout specific methods
   public Point getPointForComponent(Component comp)
   {
      for(int i = 0; i < components.size(); i++)
	 if(components.elementAt(i) == comp)
	    return coords.elementAt(i);
      return null;		// FIXME should probably throw an exception
   }
}
