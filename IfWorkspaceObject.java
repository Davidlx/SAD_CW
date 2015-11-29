package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class IfWorkspaceObject extends WorkspaceObject
{
   IfStatement ifSmt;
   PartSink conditionSink, trueSink, falseSink;

   public IfWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      ifSmt = new IfStatement();
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
	    ifSmt.setCondition(exp);
	 }
	 public void split()
	 {
	    ifSmt.setCondition(null);
	 }
      });
      conditionSink.setToolTipText("Condition that is checked by this If statement");
      p.add(conditionSink, BorderLayout.NORTH);
      

      // true branch sink
      trueSink = new PartSink(PartType.STATEMENT_PART);
      trueSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = trueSink.getContainedPart();
	    Object o = c.getPart();
	    Statement exp = (Statement)o;
	    ifSmt.setTrueStatement(exp);
	 }
	 public void split()
	 {
	    ifSmt.setTrueStatement(null);
	 }
      });
      trueSink.setToolTipText("Part that is executed if the condition is true");

      // false branch sink
      falseSink = new PartSink(PartType.STATEMENT_PART);
      falseSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = falseSink.getContainedPart();
	    Object o = c.getPart();
	    Statement exp = (Statement)o;
	    ifSmt.setFalseStatement(exp);
	 }
	 public void split()
	 {
	    ifSmt.setFalseStatement(null);
	 }
      });
      falseSink.setToolTipText("Part that is executed if the condition is false");

      JPanel branchPanel = new JPanel();
      branchPanel.setLayout(new BorderLayout());
      branchPanel.add(trueSink, BorderLayout.WEST);
      branchPanel.add(Box.createHorizontalStrut(5), BorderLayout.CENTER);
      branchPanel.add(falseSink, BorderLayout.EAST);
      p.add(branchPanel, BorderLayout.CENTER);

      return p;
   }

   PartType getPartType()
   {
      return PartType.STATEMENT_PART;
   }
   Object getPart()
   {
      return ifSmt;
   }

   String getWorkspaceObjectName()
   {
      return "If";
   }

   void save(PrintWriter out)
   {
      out.println("<if>");
      out.println("<condition>");
      WorkspaceObject c = conditionSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</condition>");
      out.println("<tbranch>");
      c = trueSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</tbranch>");
      out.println("<fbranch>");
      c = falseSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</fbranch>");
      out.println("</if>");
   }
   static IfWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("if"))
	 throw new ProgramLoadingException();

      IfWorkspaceObject obj = new IfWorkspaceObject(Workspace.getWorkspace());
      Node cond = Workspace.getChildElementByName(node, "condition");
      Node tBranch = Workspace.getChildElementByName(node, "tbranch");
      Node fBranch = Workspace.getChildElementByName(node, "fbranch");

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

      if(tBranch != null)
      {
	 Node tBranchNode = Workspace.getNthChildElement(tBranch, 0);
	 if(tBranchNode != null)
	 {
	    WorkspaceObject tBranchObj = Workspace.dispatchLoad(tBranchNode);
	    if(tBranchObj != null)
	    {
	       obj.trueSink.progCombine(tBranchObj);
	    }
	 }
      }
      
      if(fBranch != null)
      {
	 Node fBranchNode = Workspace.getNthChildElement(fBranch, 0);
	 if(fBranchNode != null)
	 {
	    WorkspaceObject fBranchObj = Workspace.dispatchLoad(fBranchNode);
	    if(fBranchObj != null)
	    {
	       obj.falseSink.progCombine(fBranchObj);
	    }
	 }
      }

      return obj;

   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == ifSmt)
	 return this;

      WorkspaceObject wo = null;
      wo = conditionSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;
      wo = trueSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;
      wo = falseSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}