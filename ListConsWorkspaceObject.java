package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class ListConsWorkspaceObject extends WorkspaceObject
{
   ListCons op;
   PartSink leftSink, rightSink;

   public ListConsWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      op = new ListCons();
   }

   JPanel createCustomPanel()
   {
      Box p = new Box(BoxLayout.LINE_AXIS);
      
      // left operand sink
      leftSink = new PartSink(PartType.EXPRESSION_PART);
      leftSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = leftSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setValueExpression(exp);
	 }
	 public void split()
	 {
	    op.setValueExpression(null);
	 }
      });
      leftSink.setToolTipText("Value that will be the first element of the new list.");
      p.add(leftSink);
     
      JLabel eqLab = new JLabel("<html><h3><b>&bull;</b></h3></html>", SwingConstants.CENTER);
      p.add(eqLab);

      // right operand sink
      rightSink = new PartSink(PartType.EXPRESSION_PART);
      rightSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = rightSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setListExpression(exp);
	 }
	 public void split()
	 {
	    op.setListExpression(null);
	 }
      });
      rightSink.setToolTipText("List that will be the rest of the new list.");
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
      return "Cons (List)";
   }

   void save(PrintWriter out)
   {
      out.println("<listcons>");
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
      out.println("</listcons>");
   }
   static ListConsWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("listcons"))
	 throw new ProgramLoadingException();

      ListConsWorkspaceObject obj = new ListConsWorkspaceObject(Workspace.getWorkspace());
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
