package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class GreaterOperatorWorkspaceObject extends WorkspaceObject
{
   GreaterOperator op;		// should never be changed after the creation or loading
   PartSink leftSink, rightSink;

   public GreaterOperatorWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      op = new GreaterOperator();
   }

   // 2 sinks, 1 for left operand, 1 for right operand
   JPanel createCustomPanel()
   {
      Box p = new Box(BoxLayout.LINE_AXIS);

      leftSink = createCommonExpressionSink(op, 0);
      p.add(leftSink);

      p.add(new JLabel("<html><h3><b>&gt;</b></h3></html>", SwingConstants.CENTER));

      rightSink = createCommonExpressionSink(op, 1);
      p.add(rightSink);

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
      return "Greater (>)";
   }

   void save(PrintWriter out)
   {
      out.println("<greater>");
      out.println("<loperand>");
      WorkspaceObject c = leftSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</loperand>");
      out.println("<roperand>");
      c = rightSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</roperand>");
      out.println("</greater>");
   }
   static GreaterOperatorWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("greater"))
	 throw new ProgramLoadingException();

      GreaterOperatorWorkspaceObject obj = new GreaterOperatorWorkspaceObject(Workspace.getWorkspace());
      Node lop = Workspace.getChildElementByName(node, "loperand");
      Node rop = Workspace.getChildElementByName(node, "roperand");

      if(lop != null)
      {
	 Node lopNode = Workspace.getNthChildElement(lop, 0);
	 if(lopNode != null)
	 {
	    WorkspaceObject lopObj = Workspace.dispatchLoad(lopNode);
	    if(lopObj != null)
	    {
	       obj.leftSink.progCombine(lopObj);
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
	       obj.rightSink.progCombine(ropObj);
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
      wo = leftSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;
      wo = rightSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
