package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class DrawStringWorkspaceObject extends WorkspaceObject
{
   DrawStringOperator op;
   PartSink contextSink, xSink, ySink, strSink;

   public DrawStringWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      op = new DrawStringOperator();
   }

   JPanel createCustomPanel()
   {
      Box p = new Box(BoxLayout.LINE_AXIS);
      
      // graphics context sink
      contextSink = new PartSink(PartType.EXPRESSION_PART);
      contextSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = contextSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setContextOperand(exp);
	 }
	 public void split()
	 {
	    op.setContextOperand(null);
	 }
      });
      contextSink.setToolTipText("Graphics area to draw in");
      p.add(contextSink);

      // x operand sink
      xSink = new PartSink(PartType.EXPRESSION_PART);
      xSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = xSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setXOperand(exp);
	 }
	 public void split()
	 {
	    op.setXOperand(null);
	 }
      });
      xSink.setToolTipText("X coordinate");
      p.add(xSink);
     
      // y operand sink
      ySink = new PartSink(PartType.EXPRESSION_PART);
      ySink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = ySink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setYOperand(exp);
	 }
	 public void split()
	 {
	    op.setYOperand(null);
	 }
      });
      ySink.setToolTipText("Y coordinate");
      p.add(ySink);

      // string operand sink
      strSink = new PartSink(PartType.EXPRESSION_PART);
      strSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = strSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    op.setStringOperand(exp);
	 }
	 public void split()
	 {
	    op.setStringOperand(null);
	 }
      });
      strSink.setToolTipText("Text string to draw");
      p.add(strSink);

      JPanel retPanel = new JPanel();
      retPanel.setLayout(new BorderLayout());
      retPanel.add(p, BorderLayout.CENTER);
      return retPanel;
   }


   PartType getPartType()
   {
      return PartType.STATEMENT_PART;
   }
   Object getPart()
   {
      return op;
   }

   String getWorkspaceObjectName()
   {
      return "Draw String (Graphics)";
   }

   void save(PrintWriter out)
   {
      out.println("<drawstring>");
      out.println("<context>");
      WorkspaceObject c = contextSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</context>");
      out.println("<xcoord>");
      c = xSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</xcoord>");
      out.println("<ycoord>");
      c = ySink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</ycoord>");
      out.println("<str>");
      c = strSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</str>");
      out.println("</drawstring>");
   }
   static DrawStringWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("drawstring"))
	 throw new ProgramLoadingException();

      DrawStringWorkspaceObject obj = new DrawStringWorkspaceObject(Workspace.getWorkspace());
      Node cop = Workspace.getChildElementByName(node, "context");
      Node lop = Workspace.getChildElementByName(node, "xcoord");
      Node rop = Workspace.getChildElementByName(node, "ycoord");
      Node sop = Workspace.getChildElementByName(node, "str");

      if(cop != null)
      {
	 Node copNode = Workspace.getNthChildElement(cop, 0);
	 if(copNode != null)
	 {
	    WorkspaceObject copObj = Workspace.dispatchLoad(copNode);
	    if(copObj != null)
	    {
	       obj.contextSink.progCombine(copObj);
	    }
	 }
      }

      if(lop != null)
      {
	 Node lopNode = Workspace.getNthChildElement(lop, 0);
	 if(lopNode != null)
	 {
	    WorkspaceObject lopObj = Workspace.dispatchLoad(lopNode);
	    if(lopObj != null)
	    {
	       obj.xSink.progCombine(lopObj);
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
	       obj.ySink.progCombine(ropObj);
	    }
	 }
      }

      if(sop != null)
      {
	 Node sopNode = Workspace.getNthChildElement(sop, 0);
	 if(sopNode != null)
	 {
	    WorkspaceObject sopObj = Workspace.dispatchLoad(sopNode);
	    if(sopObj != null)
	    {
	       obj.ySink.progCombine(sopObj);
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
      wo = contextSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;
      wo = xSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;
      wo = ySink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;
      wo = strSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
