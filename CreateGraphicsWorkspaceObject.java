package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class CreateGraphicsWorkspaceObject extends WorkspaceObject
{
   CreateGraphicsOperator op;
   PartSink widthSink, heightSink;

   public CreateGraphicsWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      op = new CreateGraphicsOperator();
   }

   JPanel createCustomPanel()
   {
      Box p = new Box(BoxLayout.LINE_AXIS);
      
      // width operand sink
      widthSink = new PartSink(PartType.EXPRESSION_PART);
      widthSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = widthSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setWidthOperand(exp);
	 }
	 public void split()
	 {
	    op.setWidthOperand(null);
	 }
      });
      widthSink.setToolTipText("Width of the graphics area");
      p.add(widthSink);
     
      // height operand sink
      heightSink = new PartSink(PartType.EXPRESSION_PART);
      heightSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = heightSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setHeightOperand(exp);
	 }
	 public void split()
	 {
	    op.setHeightOperand(null);
	 }
      });
      heightSink.setToolTipText("Height of the graphics area");
      p.add(heightSink);

      JPanel retPanel = new JPanel();
      retPanel.setLayout(new BorderLayout());
      retPanel.add(p, BorderLayout.CENTER);
      return retPanel;
   }


   PartType getPartType()
   {
      return PartType.EXPRESSION_PART;
   }
   Object getPart()
   {
      return op;
   }

   String getWorkspaceObjectName()
   {
      return "Create Graphics Area (Graphics)";
   }

   void save(PrintWriter out)
   {
      out.println("<graphicscontext>");
      out.println("<width>");
      WorkspaceObject c = widthSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</width>");
      out.println("<height>");
      c = heightSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</height>");
      out.println("</graphicscontext>");
   }
   static CreateGraphicsWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("graphicscontext"))
	 throw new ProgramLoadingException();

      CreateGraphicsWorkspaceObject obj = new CreateGraphicsWorkspaceObject(Workspace.getWorkspace());
      Node lop = Workspace.getChildElementByName(node, "width");
      Node rop = Workspace.getChildElementByName(node, "height");

      if(lop != null)
      {
	 Node lopNode = Workspace.getNthChildElement(lop, 0);
	 if(lopNode != null)
	 {
	    WorkspaceObject lopObj = Workspace.dispatchLoad(lopNode);
	    if(lopObj != null)
	    {
	       obj.widthSink.progCombine(lopObj);
	    }
	 }
      }
      
      if(rop != null)
      {
	 Node ropNode = Workspace.getNthChildElement(rop, 0);
	 if(ropNode != null)
	 {
	    WorkspaceObject ropObj = Workspace.dispatchLoad(ropNode);
	    if(ropObj != null)
	    {
	       obj.heightSink.progCombine(ropObj);
	    }
	 }
      }

      return obj;
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == op)
	 return this;

      WorkspaceObject wo = null;
      wo = widthSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;
      wo = heightSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
