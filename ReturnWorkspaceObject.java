package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class ReturnWorkspaceObject extends WorkspaceObject
{
   ReturnStatement retSmt;
   PartSink sink;

   public ReturnWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      retSmt = new ReturnStatement();
   }

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
	    retSmt.setReturnValue(exp);
	 }
	 public void split()
	 {
	    retSmt.setReturnValue(null);
	 }
      });
      sink.setToolTipText("Value to return from this algorithm");
      p.add(sink);
      return p;
   }

   PartType getPartType()
   {
      return PartType.STATEMENT_PART;
   }
   Object getPart()
   {
      return retSmt;
   }

   String getWorkspaceObjectName()
   {
      return "Return";
   }

   void save(PrintWriter out)
   {
      out.println("<return>");
      WorkspaceObject c = sink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</return>");
   }
   static ReturnWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("return"))
	 throw new ProgramLoadingException();

      ReturnWorkspaceObject obj = new ReturnWorkspaceObject(Workspace.getWorkspace());
      Node n = Workspace.getNthChildElement(node, 0);
      if(n != null)
      {
	 WorkspaceObject wsObj = Workspace.dispatchLoad(n);
	 if(wsObj != null)
	 {
	    obj.sink.progCombine(wsObj);
	 }
      }

      return obj;
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == retSmt)
	 return this;

      WorkspaceObject wo = null;
      if(sink.getContainedPart() == null)
	 return null;
      wo = sink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
