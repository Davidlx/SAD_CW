package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class TreeLeafWorkspaceObject extends WorkspaceObject
{
   TreeLeaf lit;

   public TreeLeafWorkspaceObject(Workspace ws)
   {
      super(ws);
   }

   public void earlyInit(Object[] extras)
   {
      lit = new TreeLeaf();
   }

   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(1, 1));
      p.add(new JLabel("Leaf", SwingConstants.CENTER));
      return p;
   }

   PartType getPartType()
   {
      return PartType.EXPRESSION_PART;
   }
   Object getPart()
   {
      return lit;
   }

   String getWorkspaceObjectName()
   {
      return "Leaf";
   }
   void save(PrintWriter out)
   {
      out.println("<treeleaf />");
   }
   static TreeLeafWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("treeleaf"))
	 throw new ProgramLoadingException();

      return new TreeLeafWorkspaceObject(Workspace.getWorkspace());
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == lit)
	 return this;
      else
	 return null;
   }
}
