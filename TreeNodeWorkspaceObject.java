package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class TreeNodeWorkspaceObject extends WorkspaceObject
{
   TreeNode op;
   PartSink leftSink, rightSink, valueSink;

   public TreeNodeWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      op = new TreeNode();
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
	    op.setLeftExpression(exp);
	 }
	 public void split()
	 {
	    op.setLeftExpression(null);
	 }
      });
      leftSink.setToolTipText("Value that will be the left subtree of the new tree.");
      p.add(leftSink);
     
      JLabel eqLab = new JLabel("<html><h3><b>&bull;</b></h3></html>", SwingConstants.CENTER);
      p.add(eqLab);

      // value sink
      valueSink = new PartSink(PartType.EXPRESSION_PART);
      valueSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = valueSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setValueExpression(exp);
	 }
	 public void split()
	 {
	    op.setValueExpression(null);
	 }
      });
      valueSink.setToolTipText("Value that will be the root value of the new tree.");
      p.add(valueSink);

      JLabel eqLab2 = new JLabel("<html><h3><b>&bull;</b></h3></html>", SwingConstants.CENTER);
      p.add(eqLab2);

      // right operand sink
      rightSink = new PartSink(PartType.EXPRESSION_PART);
      rightSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = rightSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setRightExpression(exp);
	 }
	 public void split()
	 {
	    op.setRightExpression(null);
	 }
      });
      rightSink.setToolTipText("Value that will be the right subtree of the new tree.");
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
      return "Node (Tree)";
   }

   void save(PrintWriter out)
   {
      out.println("<treenode>");
      out.println("<ltree>");
      WorkspaceObject c = leftSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</ltree>");
      out.println("<value>");
      c = valueSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</value>");
      out.println("<rtree>");
      c = rightSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</rtree>");
      out.println("</treenode>");
   }
   static TreeNodeWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("treenode"))
	 throw new ProgramLoadingException();

      TreeNodeWorkspaceObject obj = new TreeNodeWorkspaceObject(Workspace.getWorkspace());
      Node lop = Workspace.getChildElementByName(node, "ltree");
      Node vop = Workspace.getChildElementByName(node, "value");
      Node rop = Workspace.getChildElementByName(node, "rtree");

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

      if(vop != null)
      {
	 Node vopNode = Workspace.getNthChildElement(vop, 0);
	 if(vopNode != null)
	 {
	    WorkspaceObject vopObj = Workspace.dispatchLoad(vopNode);
	    if(vopObj != null)
	    {
	       obj.valueSink.progCombine(vopObj);
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
      wo = valueSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
