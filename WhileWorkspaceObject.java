package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class WhileWorkspaceObject extends WorkspaceObject
{
   WhileStatement whileSmt;
   PartSink conditionSink, bodySink;

   public WhileWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      whileSmt = new WhileStatement();
   }

   // if statement has 3 sinks, 1 for condition, 1 for true branch, 1 for false branch
   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());

      // condition sink
      conditionSink = new PartSink(PartType.EXPRESSION_PART);
      conditionSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = conditionSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    whileSmt.setCondition(exp);
	 }
	 public void split()
	 {
	    whileSmt.setCondition(null);
	 }
      });
      conditionSink.setToolTipText("<html>Condition that controls the loop.<br>The loop body will be executed while this is true</html>");
      p.add(conditionSink, BorderLayout.NORTH);
      
      // body sink
      bodySink = new PartSink(PartType.STATEMENT_PART);
      bodySink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = bodySink.getContainedPart();
	    Object o = c.getPart();
	    Statement exp = (Statement)o;
	    whileSmt.setBody(exp);
	 }
	 public void split()
	 {
	    whileSmt.setBody(null);
	 }
      });
      bodySink.setToolTipText("The body of the loop");
      p.add(bodySink, BorderLayout.CENTER);

      return p;
   }

   PartType getPartType()
   {
      return PartType.STATEMENT_PART;
   }
   Object getPart()
   {
      return whileSmt;
   }

   String getWorkspaceObjectName()
   {
      return "While";
   }

   void save(PrintWriter out)
   {
      out.println("<while>");
      out.println("<condition>");
      WorkspaceObject c = conditionSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</condition>");
      out.println("<body>");
      c = bodySink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</body>");
      out.println("</while>");
   }
   static WhileWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("while"))
	 throw new ProgramLoadingException();

      WhileWorkspaceObject obj = new WhileWorkspaceObject(Workspace.getWorkspace());
      Node cond = Workspace.getChildElementByName(node, "condition");
      Node bBranch = Workspace.getChildElementByName(node, "body");

      if(cond != null)
      {
	 Node condNode = Workspace.getNthChildElement(cond, 0);
	 if(condNode != null)
	 {
	    WorkspaceObject condObj = Workspace.dispatchLoad(condNode);
	    if(condObj != null)
	    {
	       obj.conditionSink.progCombine(condObj);
	    }
	 }
      }

      if(bBranch != null)
      {
	 Node bBranchNode = Workspace.getNthChildElement(bBranch, 0);
	 if(bBranchNode != null)
	 {
	    WorkspaceObject bBranchObj = Workspace.dispatchLoad(bBranchNode);
	    if(bBranchObj != null)
	    {
	       obj.bodySink.progCombine(bBranchObj);
	    }
	 }
      }

      return obj;
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == whileSmt)
	 return this;

      WorkspaceObject wo = null;
      wo = conditionSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;
      wo = bodySink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
