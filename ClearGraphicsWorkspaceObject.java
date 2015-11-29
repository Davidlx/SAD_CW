package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class ClearGraphicsWorkspaceObject extends WorkspaceObject
{
   ClearGraphics op;
   PartSink contextSink;

   public ClearGraphicsWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      op = new ClearGraphics();
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
      contextSink.setToolTipText("Graphics area to clear.");
      p.add(contextSink);

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
      return "Clear (Graphics)";
   }

   void save(PrintWriter out)
   {
      out.println("<cleargraphics>");
      out.println("<context>");
      WorkspaceObject c = contextSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</context>");
      out.println("</cleargraphics>");
   }
   static ClearGraphicsWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("cleargraphics"))
	 throw new ProgramLoadingException();

      ClearGraphicsWorkspaceObject obj = new ClearGraphicsWorkspaceObject(Workspace.getWorkspace());
      Node cop = Workspace.getChildElementByName(node, "context");

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

      return null;
   }
}
