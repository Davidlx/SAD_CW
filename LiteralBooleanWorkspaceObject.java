package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class LiteralBooleanWorkspaceObject extends WorkspaceObject
{
   LiteralBoolean lit;

   public LiteralBooleanWorkspaceObject(Workspace w, boolean v)
   {
      super(w, v);
   }

   void earlyInit(Object[] extras)
   {
      if(extras.length == 1)
	 lit = new LiteralBoolean((Boolean)extras[0]);
      else
	 throw new RuntimeException("LiteralBooleanWorkspaceObject constructor not given a single boolean");
   }

   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(1,1));
      p.add(new JLabel("" + lit.getValue(), SwingConstants.CENTER));
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
      return "Boolean Literal";
   }

   void save(PrintWriter out)
   {
      out.println("<literalboolean value=\"" + lit.getValue() + "\" />");
   }
   static LiteralBooleanWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("literalboolean"))
	 throw new ProgramLoadingException();

      Element el = (Element)node;
      String str = el.getAttribute("value");
      
      return new LiteralBooleanWorkspaceObject(Workspace.getWorkspace(), Boolean.parseBoolean(str));
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == lit)
	 return this;
      else
	 return null;
   }
}
