package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class VarDeclarationWorkspaceObject extends WorkspaceObject
{
   VarDeclaration var;
   JTextField varNameGad;
   PartSink sink;

   public VarDeclarationWorkspaceObject(Workspace w)
   {
      super(w);
   }
   
   void earlyInit(Object[] extras)
   {
      var = new VarDeclaration();
   }

   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());
      varNameGad = new JTextField(var.getName());
      varNameGad.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    if(varNameGad.getText() == null || varNameGad.getText().equals(""))
	    {
	       JOptionPane.showMessageDialog(null, "Sorry, that is not a valid variable name",
					     "Invalid value", JOptionPane.ERROR_MESSAGE);
	       varNameGad.setText(var.getName());
	       varNameGad.setBackground(Color.white);//SPR
	    }
	    else
	    {
	       var.setName(varNameGad.getText());
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
	 void warning()//obviously, there is no way to get the JTextField from the DocumentEvent(!)
	 {
	    varNameGad.setBackground(Color.red);
	 }
      });
      varNameGad.setToolTipText("<html>The name of the variable you want to create.<br>This name must be different from all the other variable names in this algorithm.</html>");
      p.add(varNameGad, BorderLayout.NORTH);
      
      sink = new PartSink(PartType.EXPRESSION_PART);
      sink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = sink.getContainedPart();
	    Object o = c.getPart();
	    Expression exp = (Expression)o;
	    var.setValue(exp);
	 }
	 public void split()
	 {
	    var.setValue(null);
	 }
      });
      sink.setToolTipText("Initialisation value of the new variable");
      p.add(sink, BorderLayout.CENTER);

      return p;
   }

   PartType getPartType()
   {
      return PartType.STATEMENT_PART;
   }
   Object getPart()
   {
      return var;
   }

   String getWorkspaceObjectName()
   {
      return "Declaration";
   }

   void save(PrintWriter out)
   {
      out.print("<vardecl value=\"");
      String s = var.getName();
      if(s == null || s.equals(""))
	 out.print("");
      else
	 out.print(s);
      out.println("\">");

      WorkspaceObject o = sink.getContainedPart();
      if(o != null)
	 o.save(out);
      
      out.println("</vardecl>");
   }

   static VarDeclarationWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("vardecl"))
	 throw new ProgramLoadingException();

      Element el = (Element)node;
      String str = el.getAttribute("value");

      VarDeclarationWorkspaceObject obj = new VarDeclarationWorkspaceObject(Workspace.getWorkspace());
      obj.varNameGad.setText(str);
      obj.varNameGad.setBackground(Color.white);//SPR - load fix
      obj.var.setName(str);

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
      if(part == var)
	 return this;

      WorkspaceObject wo = null;
      wo = sink.getContainedPart().getWorkspaceObjectForPart(part);
      if(wo != null)
	 return wo;

      return null;
   }
}
