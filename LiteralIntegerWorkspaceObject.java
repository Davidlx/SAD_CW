package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

// These are immutable, since they are literals.  If you want to change the value, create another part.
class LiteralIntegerWorkspaceObject extends WorkspaceObject
{
   LiteralInteger lit;

   public LiteralIntegerWorkspaceObject(Workspace w, int v)
   {
      super(w, v);
   }

   void earlyInit(Object[] extras)
   {
      if(extras.length == 1)
	 lit = new LiteralInteger((Integer)extras[0]);
      else
	 throw new RuntimeException("LiteralIntegerWorkspaceObject constructor not given a single integer");
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
      return "Integer Literal";
   }

   void save(PrintWriter out)
   {
      out.println("<literalinteger value=\"" + lit.getValue() + "\" />");
   }
   static LiteralIntegerWorkspaceObject load(Node node) throws ProgramLoadingException 
   {
      if(!node.getNodeName().equals("literalinteger"))
	 throw new ProgramLoadingException();

      Element el = (Element)node;
      String str = el.getAttribute("value");
      
      return new LiteralIntegerWorkspaceObject(Workspace.getWorkspace(), Integer.parseInt(str));
   }
   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == lit)
	 return this;
      else
	 return null;
   }
}
