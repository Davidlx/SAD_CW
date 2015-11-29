package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class StatementSequenceWorkspaceObject extends WorkspaceObject implements CombineListener
{
   StatementSequenceWorkspaceObject instance; // for listeners to use, since the superclass already implements ActionListener
   StatementSequence smtSeq;
   JPanel panel;
   GroupLayout layout;
   GroupLayout.ParallelGroup hGroup;
   GroupLayout.SequentialGroup vGroup;

   public StatementSequenceWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      smtSeq = new StatementSequence();
      instance = this;
   }

   JPanel createCustomPanel()
   {
      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());
      p.add(createRightPanel(), BorderLayout.EAST);

      panel = new JPanel();
      layout = new GroupLayout(panel);
      panel.setLayout(layout);
      hGroup = layout.createParallelGroup();
      vGroup = layout.createSequentialGroup();
      
      PartSink ps = new PartSink(PartType.STATEMENT_PART);
      ps.addCombineListener(this);

      hGroup.addComponent(ps);
      layout.setHorizontalGroup(hGroup);

      vGroup.addComponent(ps);
      layout.setVerticalGroup(vGroup);

      p.add(panel, BorderLayout.CENTER);
      return p;
   }

   JPanel createRightPanel()
   {
      JPanel ctrlPanel = new JPanel();
      JButton addButton = new JButton("+");
      addButton.setMargin(new Insets(0, 0, 0, 0));
      addButton.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    PartSink sk = new PartSink(PartType.STATEMENT_PART);
	    sk.addCombineListener(instance);
	    panel.setLayout(null); // ugly, but cannot do both lines below simultaneously!
	    hGroup.addComponent(sk);
	    vGroup.addComponent(sk);
	    panel.setLayout(layout); // ugly
	    instance.revalidate();
	    instance.repaint();
	    Workspace.getWorkspace().revalidate(); // fix BUG107; was not resizing until move.
	    updateUnderlyingObject();
	 }
      });
      JButton remButton = new JButton("-");
      remButton.setMargin(new Insets(0, 0, 0, 0));
      remButton.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    int count = panel.getComponentCount();
	    if(count > 1)
	    {
	       // check that there is nothing inside the last sink
	       PartSink sk = (PartSink)panel.getComponent(count - 1);
	       if(sk.getContainedPart() != null)
	       {
		  JOptionPane.showMessageDialog(null, "Please split the last part out of the statement sequence before reducing it's length.", "Error", JOptionPane.ERROR_MESSAGE);
		  return;
	       }

	       panel.remove(count - 1); // GroupLayout.removeLayoutComponent implies this will just do the right thing?

	       instance.revalidate();
	       instance.repaint();
	       updateUnderlyingObject();
	    }
	 }
      });
      ctrlPanel.setLayout(new GridLayout(2, 1));
      ctrlPanel.add(addButton);
      ctrlPanel.add(remButton);
      return ctrlPanel;
   }

   public void combined()
   {
      updateUnderlyingObject();
   }
   public void split()
   {
      updateUnderlyingObject();
   }

   void updateUnderlyingObject()
   {
      Vector<Statement> smts = new Vector<Statement>();
      int count = panel.getComponentCount();

      for(int i = 0; i < count; i++)
      {
	 PartSink s = (PartSink)panel.getComponent(i);
	 WorkspaceObject o = s.getContainedPart();
	 if(o == null)
	 {
	    smts.add(null);
	 }
	 else
	 {
	    Statement smt = (Statement)o.getPart();
	    smts.add(smt);
	 }
      }
      smtSeq.setAllStatements(smts);
   }
   
   PartType getPartType()
   {
      return PartType.STATEMENT_PART;
   }
   Object getPart()
   {
      return smtSeq;
   }

   String getWorkspaceObjectName()
   {
      return "Statement Sequence";
   }

   void save(PrintWriter out)
   {
      Statement[] sms = new Statement[panel.getComponentCount()];

      out.println("<statementsequence count=\"" + sms.length + "\">");
      for(int i = 0; i < sms.length; i++)
      {
	 PartSink sink = (PartSink)panel.getComponent(i);
	 WorkspaceObject o = sink.getContainedPart();
	 out.println("<smt>");
	 if(o != null)
	    o.save(out);
	 out.println("</smt>");
      }
      out.println("</statementsequence>");
   }
   static StatementSequenceWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("statementsequence"))
	 throw new ProgramLoadingException();

      Element el = (Element)node;
      int numSmts = Integer.parseInt(el.getAttribute("count"));

      StatementSequenceWorkspaceObject sseq = new StatementSequenceWorkspaceObject(Workspace.getWorkspace());

      for(int i = 0; i < numSmts; i++)
      {
	 PartSink sk;
	 if(i == 0)		// because first sink is pre-created
	 {
	    sk = (PartSink)sseq.panel.getComponent(0);
	 }
	 else
	 {
	    sk = new PartSink(PartType.STATEMENT_PART);
	    sk.addCombineListener(sseq);
	    sseq.hGroup.addComponent(sk); // this works ok when the component is not visible/added.
	    sseq.vGroup.addComponent(sk);
	 }

	 Element smtEl = (Element)Workspace.getNthChildElement(el, i);
	 if(smtEl == null || !smtEl.getNodeName().equals("smt"))
	    throw new ProgramLoadingException();

	 Element realEl = (Element)Workspace.getNthChildElement(smtEl, 0);
	 if(realEl != null)
	 {
	    WorkspaceObject realObj = Workspace.dispatchLoad(realEl);
	    if(realObj != null)
	    {
	       sk.progCombine(realObj);
	    }
	 }
      }
      
      sseq.revalidate();
      sseq.repaint();
      sseq.updateUnderlyingObject();
      return sseq;
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == smtSeq)
	 return this;

      int count = panel.getComponentCount();
      for(int i = 0; i < count; i++)
      {
	 PartSink sk = (PartSink)panel.getComponent(i);
	 WorkspaceObject o = sk.getContainedPart();
	 WorkspaceObject targ = o.getWorkspaceObjectForPart(part);
	 if(targ != null)
	    return targ;
      }

      return null;
   }
}
