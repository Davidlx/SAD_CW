package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class ListNilWorkspaceObject extends WorkspaceObject
{
   ListNil lit;

   public ListNilWorkspaceObject(Workspace ws)
   {
      super(ws);
   }

   public void earlyInit(Object[] extras)
   {
      lit = new ListNil();
   }

   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(1, 1));
      p.add(new JLabel("Nil", SwingConstants.CENTER));
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
      return "Empty List";
   }
   void save(PrintWriter out)
   {
      out.println("<nillist />");
   }
   static ListNilWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("nillist"))
	 throw new ProgramLoadingException();

      return new ListNilWorkspaceObject(Workspace.getWorkspace());
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == lit)
	 return this;
      else
	 return null;
   }
}
