package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class LiteralStringWorkspaceObject extends WorkspaceObject
{
   LiteralString lit;

   public LiteralStringWorkspaceObject(Workspace w, String s)
   {
      super(w, s);
   }

   void earlyInit(Object[] extras)
   {
      if(extras.length ==1)
	 lit = new LiteralString((String)extras[0]);
      else
	 throw new RuntimeException("LiteralStringWorkspaceObject constructor not given a single String");
   }

   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(1, 1));
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
      return "Literal String";
   }

   void save(PrintWriter out)
   {
      String strValue = (String)lit.getValue();
//      strValue = strValue.replaceAll("\"", "\\\\\"");   Java escaped \" is not XML!
      strValue = strValue.replaceAll("\"", "&quot;");
      out.println("<literalstring value=\"" + strValue + "\" />");
   }
   static LiteralStringWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("literalstring"))
	 throw new ProgramLoadingException();

      Element el = (Element)node;
      String str = el.getAttribute("value");

      return new LiteralStringWorkspaceObject(Workspace.getWorkspace(), str);
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == lit)
	 return this;
      else
	 return null;
   }
}
