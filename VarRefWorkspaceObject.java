package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class VarRefWorkspaceObject extends WorkspaceObject
{
   VarRef varRef;
   JTextField varNameGad;

   public VarRefWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      varRef = new VarRef();
   }

   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(1, 1));
      varNameGad = new JTextField();
      varNameGad.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    String str = varNameGad.getText();
	    if(str == null || str.equals(""))
	    {
	       varRef.setName(null);
	       varNameGad.setBackground(Color.white); // SPR
	    }
	    else
	    {
	       varRef.setName(str);
	       varNameGad.transferFocus();//SPR
	       varNameGad.setBackground(Color.white);//SPR
	    }
	 }
      });
      // all SPR
      varNameGad.getDocument().addDocumentListener(new DocumentListener() {
	 public void insertUpdate(DocumentEvent e) { warning(); }
	 public void removeUpdate(DocumentEvent e) { warning(); }
	 public void changedUpdate(DocumentEvent e) { warning(); }
	 void warning()		// obviously, there is no way to get the JTextField from the DocumentEvent(!)
	 {
	    varNameGad.setBackground(Color.red);
	 }
      });
      varNameGad.setToolTipText("<html>Name of the variable whose value will be used for this part.<br>The variable must already exist by this point; it must either be a parameter name or a declared variable.");
      p.add(varNameGad);
      return p;
   }

   PartType getPartType()
   {
      return PartType.EXPRESSION_PART;
   }
   
   Object getPart()
   {
      return varRef;
   }

   String getWorkspaceObjectName()
   {
      return "VarRef";
   }

   void save(PrintWriter out)
   {
      out.print("<varref value=\"");
      String s = varRef.getName();
      if(s == null || s.equals(""))
	 out.print("");
      else
	 out.print(s);
      out.println("\" />");
   }
   static VarRefWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("varref"))
	 throw new ProgramLoadingException();

      Element el = (Element)node;
      String str = el.getAttribute("value");
      
      VarRefWorkspaceObject obj = new VarRefWorkspaceObject(Workspace.getWorkspace());
      obj.varNameGad.setText(str);
      obj.varNameGad.setBackground(Color.white); // SPR - load fix
      obj.varRef.setName(str);
      return obj;
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == varRef)
	 return this;
      else
	 return null;
   }
}
