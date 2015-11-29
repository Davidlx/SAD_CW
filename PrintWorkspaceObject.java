package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class PrintWorkspaceObject extends WorkspaceObject
{
   PrintStatement print;
   PartSink sink;

   public PrintWorkspaceObject(Workspace w)
   {
      super(w);
      print = new PrintStatement();
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
	    print.setExpression(exp);
	 }
	 public void split()
	 {
	    print.setExpression(null);
	 }
      });
      sink.setToolTipText("Value to be displayed to the user on the screen");
      p.add(sink);
      return p;
   }

   PartType getPartType()
   {
      return PartType.STATEMENT_PART;
   }
   Object getPart()
   {
      return print;
   }

   String getWorkspaceObjectName()
   {
      return "Print";
   }

   void save(PrintWriter out)
   {
      out.println("<print>");
      WorkspaceObject c = sink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</print>");
   }
   static PrintWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("print"))
	 throw new ProgramLoadingException();

      PrintWorkspaceObject obj = new PrintWorkspaceObject(Workspace.getWorkspace());
      
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
      if(part == print)
	 return this;

      WorkspaceObject wo = null;
      wo = sink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
