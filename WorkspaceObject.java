package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.PrintWriter;

import org.w3c.dom.Element;

abstract class WorkspaceObject extends JPanel implements ActionListener
{
   private Point pt;		// access via getPoint()
   private boolean dragging = false;
   private int offx, offy;
   Workspace ws;
   boolean isCombined = false;
   PartSink combinedBy;
   private JPanel customPanel;
   private JPopupMenu popup;
   private static WorkspaceObject popedObject = null;
   private WorkspaceObject thisInstance;

   public WorkspaceObject(Workspace w, Object... extras)
   {
      ws = w;
      pt = new Point();
      thisInstance = this;

      earlyInit(extras);

      // build the generic GUI and add the custom parts
      setBackground(getBGColor());
      setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
						   BorderFactory.createLineBorder(Color.black)));
      setLayout(new BorderLayout());
      customPanel = createCustomPanel();
      customPanel.setBackground(getBGColor());
      add(customPanel, BorderLayout.CENTER);

      JPanel combinePanel = new JPanel();
      combinePanel.setBackground(getBGColor());
      combinePanel.setLayout(new BorderLayout());
      combinePanel.setBackground(getBGColor());

      java.net.URL url = getClass().getResource("/icons/combine-small.png");
      if(url == null)
      {
	 System.err.println("cannot find combine-small icon");
	 System.exit(-1);
      }
      JButton combineBut = new JButton(new ImageIcon(url));//"icons/combine-small.png"));//"Combine");
      Insets insetsZero = new Insets(0, 0, 0, 0);
      combineBut.setMargin(insetsZero);
      combineBut.setBackground(getBGColor());
      combineBut.setToolTipText("Combine this part inside another part");
      url = getClass().getResource("/icons/split-small.png");
      if(url == null)
      {
	 System.err.println("cannot find split-small icon");
	 System.exit(-1);
      }
      JButton splitBut = new JButton(new ImageIcon(url));//"icons/split-small.png"));//"Split");
      splitBut.setMargin(insetsZero);
      splitBut.setBackground(getBGColor());
      splitBut.setToolTipText("Remove this part from its current location");

      combinePanel.add(combineBut, BorderLayout.WEST);
      Box b = Box.createHorizontalBox();
      b.add(Box.createHorizontalGlue());
      b.add(new JLabel(getWorkspaceObjectName()));
      b.add(Box.createHorizontalGlue());
      combinePanel.add(b, BorderLayout.CENTER);
      combinePanel.add(splitBut, BorderLayout.EAST);
      add(combinePanel, BorderLayout.NORTH);
      
      // set behaviour of combine and split buttons
      combineBut.addActionListener(this);
      combineBut.setActionCommand("combine");
      splitBut.addActionListener(this);
      splitBut.setActionCommand("split");

      // right-click popup menu with delete part option
      popup = new JPopupMenu();
      JMenuItem item = new JMenuItem("Delete Part");
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    // first, do not allow deletion of combined components
	    // if combined, force the popup away
	    WorkspaceObject obj = popedObject;
	    if(obj.isCombined)
	    {
	       popup.setVisible(false);
	       JOptionPane.showMessageDialog(null,
					     "Cannot delete a part that is currently combined.\nPlease split out the part and then delete it.", "Error", JOptionPane.ERROR_MESSAGE);
	    }
	    else
	    {
	       if(popedObject == null)
	       {
		  System.err.println("Somehow we are deleting when popedObject == null??");
		  return;
	       }

	       ws.remove(popedObject);
	       popedObject.partDeleted(); // any extra deletion action the part may need (eg, remove algorithm from library)
	    }
	    Workspace.getWorkspace().repaint();
	    popedObject = null;
	 }
      });
      popup.add(item);
      item = new JMenuItem("Collapse/Expand");
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    WorkspaceObject obj = popedObject;
	    if(obj.isCustomPanelVisible())
	    {
	       obj.setCustomPanelVisible(false);
	    }
	    else
	    {
	       obj.setCustomPanelVisible(true);
	    }
	    invalidate();
	    Workspace.getWorkspace().repaint();
	    popedObject = null;
	 }
      });
      popup.add(item);

      // add the normal dragging around support
      addMouseListener(new MouseAdapter() {
	 public void mousePressed(MouseEvent e)
	 { 
	    if(maybeShowPopup(e))
	       return;
	    if(e.getButton() == MouseEvent.BUTTON1)
	    {
	       offx = e.getX();
	       offy = e.getY();
	       dragging = true;
	    }
	 }
	 public void mouseReleased(MouseEvent e)
	 {        
	    if(maybeShowPopup(e))
	       return;
	    if(e.getButton() == MouseEvent.BUTTON1)
	    {
	       dragging = false;
	    }
	 }
	 private boolean maybeShowPopup(MouseEvent e)
	 {
	    if (e.isPopupTrigger())
	    {
	       popedObject = thisInstance; // ugly but works
	       popup.show(e.getComponent(), e.getX(), e.getY());
	       return true;
	    }
	    return false;
	 }

	 public void mouseEntered(MouseEvent e)
	 {
	    JComponent comp = (JComponent)e.getSource();
	    comp.setBackground(Color.red);
	 }
	 public void mouseExited(MouseEvent e)
	 {
	    JComponent comp = (JComponent)e.getSource();
	    comp.setBackground(Color.white); // FIXME technically should be the proper BG colour, but only leaves 1 px white border.
	 }	 
      });
      addMouseMotionListener(new MouseMotionAdapter () {
	 public void mouseDragged(MouseEvent e)
	 {
	    if(dragging == true)
	    {
	       int dx, dy;
	       Point pnt = e.getPoint();
	       dx = pnt.x - offx;
	       dy = pnt.y - offy;
	       pt.translate(dx, dy);
	       if(pt.x < 0)  pt.setLocation(0, pt.y);
	       if(pt.y < 0)  pt.setLocation(pt.x, 0);
	       ws.invalidate(); // invalidate workspace somehow?? would rather use some parent thing than this
	       ws.getParent().validate(); // this gets JScrollPane to change it's scrollbars :)
	       ws.drag();
       }
	 }
      });
   }

   /** By default, we hide the entire custom panel.  Subclasses can override this to leave
       things like headers visible. */
   public void setCustomPanelVisible(boolean state)
   {
      customPanel.setVisible(state);
   }
   public boolean isCustomPanelVisible()
   {
      return customPanel.isVisible();
   }

   public void actionPerformed(ActionEvent e)
   {
      if(e.getActionCommand().equals("combine"))
	 combineActionPerformed(e);
      else if(e.getActionCommand().equals("split"))
	 splitActionPerformed(e);
      else
      {
	 System.err.println("unknown action command for common workspace buttons: " + e.getActionCommand());
	 System.exit(-1);
      }
   }

   public void combineActionPerformed(ActionEvent e)
   {
      if(!isCombined)
      {
	 if(PartSink.overallState == SinkState.NORMAL_STATE)
	 {
	    PartSink.partAvailable(this, getPartType());
	 }
	 else
	 {
	    PartSink.endPartAvailable();
	 }
      }
   }

   public void splitActionPerformed(ActionEvent e)
   {
      // if we are not combined, do nothing (actually, this should be disabled...)
      // if we are combined,
      //   remove from sink
      //   change sink back to button card
      //   place this part at a random location on the workspace
      
      if(isCombined)
      {
	 combinedBy.split();
    pt = ws.getFreePosition(this.preferredSize());
	 Workspace.getWorkspace().add(this, pt);
	 this.setVisible(true);
	 
	 isCombined = false;
	 combinedBy = null;
	 
	 Workspace.getWorkspace().invalidate(); // this invalidates all containing parts too
	 Workspace.getWorkspace().repaint();
      }
      else
      {
	 // do nothing
//	 System.out.println("split but not combined??");
      }
   }

   public boolean isOptimizedDrawingEnabled()
   {
      return false;
   }

   public void justCombined(PartSink s)
   {
      isCombined = true;
      combinedBy = s;
   }

   
   /** Gets called from the WorkspaceObject constructor very early.  Put any initialisation
       that your subclass needs to do before the WorkspaceObject constructor is run in here. */
   abstract void earlyInit(Object[] extras);
   /** Override this to create the components you wish to appear inside the part. */
   abstract JPanel createCustomPanel();

   /** Is the RAW part associated with this part a STATEMENT_PART or EXPRESSION_PART? */
   abstract PartType getPartType();

   /** Get the RAW class associated with this part.
       for this method you should use the result of the above method for casting. ugly but works quickly. */
   abstract Object getPart();

   /** Get the workspace object associated with this part.
       @return The workspace object associated with this part by checking the
       invoked-upon workspace object and any workspace objects it contains. null if not found. */
   abstract public WorkspaceObject getWorkspaceObjectForPart(Object part);

   /** What display name do you want on the top line? */
   abstract String getWorkspaceObjectName();

   /** For saving the program.  Should only save the cooker side since the RAW side
       will be automatically be recreated on reloading the cooker parts.
       This should recursively call on any parts contained as well. */
   abstract void save(PrintWriter out);
//   abstract static WorkspaceObject load(Node node);  // CAN'T HAVE abstract AND static  GRRR.

   /** Override to do extra specific deletion actions.  Nothing happens by default. */
   void partDeleted()
   {
      // nothing extra required by default
   }

   // can override this to colour specially
   static Color expColor = new Color(150, 200, 150);
   static Color smtColor = new Color(170, 170, 255); //new Color(150, 150, 200);
   Color getBGColor()
   {
      if(!ws.useColor)
	 return Color.white;

      if(getPartType() == PartType.EXPRESSION_PART)
	 return expColor;
      else if(getPartType() == PartType.STATEMENT_PART)
	 return smtColor;
      else
	 return Color.white;
   } 

   /** Return the position of this workspace object. */
   public Point getPoint()
   {
      return pt;
   }

   /** Given the following Element, extract the saved xpos and ypos and move the part to that point.
    ** Used during loading to restore to save possition, if present. */
   protected void loadPointDetails(Element el)
   {
      String xpos = el.getAttribute("xpos");
      String ypos = el.getAttribute("ypos");
      if(xpos != null && !xpos.isEmpty() && ypos != null && !ypos.isEmpty())
      {
	 getPoint().setLocation(Integer.parseInt(xpos), Integer.parseInt(ypos));
      }
   }

   /** Create a PartSink which accepts an Expression and setOperand() it to the given index
    ** in the NaryOperandOperator when combined or split.  Used in the straight-forward
    ** WorkspaceObject subclasses. */
   static PartSink createCommonExpressionSink(final NaryOperandOperator ooop, final int operandIdx)
   {
      final PartSink sk = new PartSink(PartType.EXPRESSION_PART);
      sk.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = sk.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    ooop.setOperand(operandIdx, exp);
	 }
	 public void split()
	 {
	    ooop.setOperand(operandIdx, null);
	 }
      });
      return sk;
   }


}
