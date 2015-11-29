package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class TreeValueWorkspaceObject extends WorkspaceObject
{
   TreeValue op;
   PartSink sink;

   public TreeValueWorkspaceObject(Workspace w)
   {
      super(w);
      op = new TreeValue();
   }

   void earlyInit(Object[] extras) {}

   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(1,1));
      sink = new PartSink(PartType.EXPRESSION_PART);
      sink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = sink.getContainedPart();
	    Object o = c.getPart(); // we know this is an expression because of our sink restriction
	    Expression exp = (Expression)o;
	    op.setExpression(exp);
	 }
	 public void split()
	 {
	    op.setExpression(null);
	 }
      });
      sink.setToolTipText("<html>Tree to take the root value of.<br>This tree must not be empty.</html>");
      p.add(sink);
      return p;
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
      return "Value (Tree)";
   }

   void save(PrintWriter out)
   {
      out.println("<treevalue>");
      WorkspaceObject c = sink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</treevalue>");
   }
   static TreeValueWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("treevalue"))
	 throw new ProgramLoadingException();

      TreeValueWorkspaceObject obj = new TreeValueWorkspaceObject(Workspace.getWorkspace());
      
      Node subNode = Workspace.getNthChildElement(node, 0);
      if(subNode != null)
      {
	 WorkspaceObject wsObj = Workspace.dispatchLoad(subNode);
	 if(wsObj != null)
	 {
	    obj.sink.progCombine(wsObj);
	 }
      }
      return obj;
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == op)
	 return this;

      WorkspaceObject wo = null;
      wo = sink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
