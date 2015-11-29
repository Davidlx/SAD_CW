package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class RandomIntegerWorkspaceObject extends WorkspaceObject
{
   RandomInteger randInt;
   PartSink sink;

   public RandomIntegerWorkspaceObject(Workspace w)
   {
      super(w);
      randInt = new RandomInteger();
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
	    randInt.setMax(exp);
	 }
	 public void split()
	 {
	    randInt.setMax(null);
	 }
      });
      sink.setToolTipText("Maximum possible value of the random number");
      p.add(sink);
      return p;
   }

   PartType getPartType()
   {
      return PartType.EXPRESSION_PART;
   }
   Object getPart()
   {
      return randInt;
   }

   String getWorkspaceObjectName()
   {
      return "Random Integer";
   }

   void save(PrintWriter out)
   {
      out.println("<randint>");
      WorkspaceObject c = sink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</randint>");
   }
   static RandomIntegerWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("randint"))
	 throw new ProgramLoadingException();

      RandomIntegerWorkspaceObject obj = new RandomIntegerWorkspaceObject(Workspace.getWorkspace());
      
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
      if(part == randInt)
	 return this;

      WorkspaceObject wo = null;
      wo = sink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
