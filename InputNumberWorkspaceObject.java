package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class InputNumberWorkspaceObject extends WorkspaceObject
{
   InputNumber input;
   PartSink sink;

   public InputNumberWorkspaceObject(Workspace w)
   {
      super(w);
      input = new InputNumber();
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
	    input.setPrompt(exp);
	 }
	 public void split()
	 {
	    input.setPrompt(null);
	 }
      });
      sink.setToolTipText("Message that will be displayed to the user when requesting input");
      p.add(sink);
      return p;
   }

   PartType getPartType()
   {
      return PartType.EXPRESSION_PART;
   }
   Object getPart()
   {
      return input;
   }

   String getWorkspaceObjectName()
   {
      return "Input Number";
   }

   void save(PrintWriter out)
   {
      out.println("<input>");
      WorkspaceObject c = sink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</input>");
   }
   static InputNumberWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("input"))
	 throw new ProgramLoadingException();

      InputNumberWorkspaceObject obj = new InputNumberWorkspaceObject(Workspace.getWorkspace());
      
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
      if(part == input)
	 return this;

      WorkspaceObject wo = null;
      wo = sink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
