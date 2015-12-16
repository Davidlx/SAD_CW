package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class RepeatWorkspaceObject extends WorkspaceObject
{
   RepeatStatement repeatSmt;
   PartSink repeatSink, bodySink;

   public RepeatWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      repeatSmt = new RepeatStatement();
   }

   // if statement has 3 sinks, 1 for condition, 1 for true branch, 1 for false branch
   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());

      // condition sink
      repeatSink = new PartSink(PartType.EXPRESSION_PART);
      repeatSink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = repeatSink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    repeatSmt.setIteration(exp);
	 }
	 public void split()
	 {
	    repeatSmt.setIteration(null);
	 }
      });
      repeatSink.setToolTipText("<html>Number of exicution that requre the loop.<br>The loop body will be executed while this is true</html>");
      p.add(repeatSink, BorderLayout.NORTH);
      
      // body sink
      bodySink = new PartSink(PartType.STATEMENT_PART);
      bodySink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = bodySink.getContainedPart();
	    Object o = c.getPart();
	    Statement exp = (Statement)o;
	    repeatSmt.setBody(exp);
	 }
	 public void split()
	 {
	    repeatSmt.setBody(null);
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
      return repeatSmt;
   }

   String getWorkspaceObjectName()
   {
      return "Repeat";
   }

   void save(PrintWriter out)
   {
      out.println("<repeat>");
      out.println("<times>");
      WorkspaceObject c = repeatSink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</times>");
      out.println("<body>");
      c = bodySink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</body>");
      out.println("</repeat>");
   }
   static RepeatWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("repeat"))
	 throw new ProgramLoadingException();

      RepeatWorkspaceObject obj = new RepeatWorkspaceObject(Workspace.getWorkspace());
      Node cond = Workspace.getChildElementByName(node, "times");
      Node bBranch = Workspace.getChildElementByName(node, "body");

      if(cond != null)
      {
	 Node condNode = Workspace.getNthChildElement(cond, 0);
	 if(condNode != null)
	 {
	    WorkspaceObject condObj = Workspace.dispatchLoad(condNode);
	    if(condObj != null)
	    {
	       obj.repeatSink.progCombine(condObj);
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
      if(part == repeatSmt)
	 return this;

      WorkspaceObject wo = null;
      wo = repeatSink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;
      wo = bodySink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
