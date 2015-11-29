package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class FormalArgsPanel extends JPanel implements ActionListener
{
   Algorithm alg;
   
   JPanel argPanel;
   JButton addButton;
   JButton remButton;
   JPanel ctrlPanel;
   GridLayout layout;
   
   public FormalArgsPanel(Algorithm a)
   {
      alg = a;

      setLayout(new BorderLayout());
      
      addButton = new JButton("+");
      if(Workspace.getWorkspace().useColor)
	 addButton.setBackground(WorkspaceObject.smtColor);
      addButton.setMargin(new Insets(0, 0, 0, 0));
      addButton.setActionCommand("+");
      addButton.addActionListener(this);

      remButton = new JButton("-");
      if(Workspace.getWorkspace().useColor)
	 remButton.setBackground(WorkspaceObject.smtColor);
      remButton.setMargin(new Insets(0, 0, 0, 0));
      remButton.setActionCommand("-");
      remButton.addActionListener(this);

      ctrlPanel = new JPanel();
      if(Workspace.getWorkspace().useColor)
	 ctrlPanel.setBackground(WorkspaceObject.smtColor);
      ctrlPanel.setLayout(new GridLayout(2, 1));
      ctrlPanel.add(addButton);
      ctrlPanel.add(remButton);
      add(ctrlPanel, BorderLayout.EAST);
      
      argPanel = new JPanel();
      layout = new GridLayout();
      argPanel.setLayout(layout);
      add(argPanel, BorderLayout.CENTER);
   }

/* Don't know why this doesn't work, but no time to work it out.
   public void setBackground(Color c)
   {
      super.setBackground(c);
      addButton.setBackground(c);
      remButton.setBackground(c);
      ctrlPanel.setBackground(c);
      }*/

   public void actionPerformed(ActionEvent e)
   {
      if(e.getActionCommand().equals("+"))
      {
	 layout.setColumns(layout.getColumns() + 1);
	 final JTextField txt = new JTextField(8); // allow space for 8 characters by default
	 txt.addActionListener(this);
	 txt.getDocument().addDocumentListener(new DocumentListener() {
	    public void insertUpdate(DocumentEvent e) { warning(); }
	    public void removeUpdate(DocumentEvent e) { warning(); }
	    public void changedUpdate(DocumentEvent e) { warning(); }
	    void warning()		// obviously, there is no way to get the JTextField from the DocumentEvent(!)
	    {
	       txt.setBackground(Color.red);
	    }
	 });
	 argPanel.add(txt);
	 revalidate();		// changed from validate() revalidate() + repaint();
	 repaint();
      }
      else if(e.getActionCommand().equals("-"))
      {
	 int count = argPanel.getComponentCount();
	 if(count > 0)
	 {
	    argPanel.remove(count - 1);
	    layout.setColumns(layout.getColumns() - 1);
	    validate();
	    argPanel.repaint();
	 }
      }
      // in every case, fall through and update the algorithm.
      // get all the values from the components
      // mass set all the formal args
      String[] strs = new String[argPanel.getComponentCount()];
      for(int i = 0; i < strs.length; i++)
      {
	 JTextField tf = (JTextField)argPanel.getComponent(i);
	 strs[i] = tf.getText();
	 tf.setBackground(Color.white);//SPR
      }
      alg.setFormalParameters(strs);
   }

   void loadFormalParameters(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("formalparams"))
	 throw new ProgramLoadingException();
      
      Element el = (Element)node;
      int pCount = Integer.parseInt(el.getAttribute("count"));
      
      layout.setColumns(pCount);
      for(int i = 0; i < pCount; i++)
	 argPanel.add(new JTextField());

      // get each of the param nodes and fill in the textfields
      NodeList possList = el.getChildNodes();
      if(possList.getLength() < pCount)
	 throw new ProgramLoadingException();

      int pIndex = 0;
      for(int i = 0; i < possList.getLength(); i++)
      {
	 Node n = possList.item(i);
	 if(n.getNodeType() == Node.ELEMENT_NODE
	    && n.getNodeName().equals("param"))
	 {
	    Element pEl = (Element)n;
	    String attr = pEl.getAttribute("name");
	    JTextField tf = (JTextField)argPanel.getComponent(pIndex);
	    tf.setText(attr);
	    pIndex++;
	 }
      }
      if(pIndex != pCount)
	 throw new ProgramLoadingException();

      // update the alg stuff (c&p from above)
      String[] strs = new String[argPanel.getComponentCount()];
      for(int i = 0; i < strs.length; i++)
      {
	 JTextField tf = (JTextField)argPanel.getComponent(i);
	 strs[i] = tf.getText();
      }
      alg.setFormalParameters(strs);
   }
}
