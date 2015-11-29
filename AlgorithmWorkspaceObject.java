package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class AlgorithmWorkspaceObject extends WorkspaceObject
{
   Algorithm alg;
   JTextField algNameGad;
   JTextArea commentArea;
   FormalArgsPanel argsPanel;
   PartSink sink;

   public AlgorithmWorkspaceObject(Workspace w)
   {
      super(w);
      Library.addAlgorithm(alg);
   }
   
   void earlyInit(Object[] extras)
   {
      alg = new Algorithm();
   }

   JPanel createCustomPanel()
   {
      JPanel topPanel = new JPanel();
      topPanel.setLayout(new BorderLayout());
      algNameGad = new JTextField(alg.getName());
//      algNameGad.setBackground(getBGColor());
      algNameGad.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    JTextField tf = (JTextField)e.getSource();
	    if(tf.getText() == null || tf.getText().equals(""))
	    {
	       JOptionPane.showMessageDialog(null, "Sorry, that is not a valid algorithm name",
					     "Invalid value", JOptionPane.ERROR_MESSAGE);
	       tf.setText(alg.getName());
	       tf.setBackground(Color.white);//SPR
	    }
	    else
	    {
	       alg.setName(tf.getText());
	       tf.transferFocus(); // SPR shift focus to something, anything else
	       tf.setBackground(Color.white);//SPR
	    }
	 }
      });
      // all SPR
      algNameGad.getDocument().addDocumentListener(new DocumentListener() {
	 public void insertUpdate(DocumentEvent e) { warning(); }
	 public void removeUpdate(DocumentEvent e) { warning(); }
	 public void changedUpdate(DocumentEvent e) { warning(); }
	 void warning()		// obviously, there is no way to get the JTextField from the DocumentEvent(!)
	 {
	    algNameGad.setBackground(Color.red);
	 }
      });
      algNameGad.setToolTipText("<html>Algorithm name.<br>No two algorithms in the same program should have the same name.</html>");

      // make the algorithm comment/description button and popup infrastructure
      final JButton commentButton;
      java.net.URL url = getClass().getResource("/icons/comment-small.png");
      if(url == null)
      {
	 System.err.println("Cannot find comment-small icon");
	 commentButton = new JButton("...");
      }
      else
      {
	 commentButton = new JButton(new ImageIcon(url));
      }
      commentButton.setMargin(new Insets(0, 0, 0, 0));
      commentButton.setBackground(getBGColor());
      final JPopupMenu commentMenu = new JPopupMenu();
      commentArea = new JTextArea(10, 20);
      commentArea.setLineWrap(true);
      commentArea.setWrapStyleWord(true);
      commentArea.getDocument().addDocumentListener(new DocumentListener() {
	 public void insertUpdate(DocumentEvent e) { saveComment(); }
	 public void removeUpdate(DocumentEvent e) { saveComment(); }
	 public void changedUpdate(DocumentEvent e) { saveComment(); }
	 private void saveComment()
	 {
//	    System.err.println("\"" + commentArea.getText() + "\"");
	    alg.setComment(commentArea.getText());
	 }
      });
      JScrollPane commentScroller = new JScrollPane(commentArea);
      
      commentMenu.add(commentScroller);
      commentButton.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    if(!commentMenu.isVisible())
	    {
	       commentMenu.show(commentButton, 0, 0);
	       commentArea.requestFocusInWindow();
	    }
	 }
      });


      JPanel nameCommentPanel = new JPanel(new BorderLayout());
      nameCommentPanel.add(algNameGad, BorderLayout.CENTER);
      nameCommentPanel.add(commentButton, BorderLayout.EAST);
      topPanel.add(nameCommentPanel, BorderLayout.NORTH);
      
      argsPanel = new FormalArgsPanel(alg);
      argsPanel.setBackground(getBGColor());
      topPanel.add(argsPanel, BorderLayout.CENTER);

      sink = new PartSink(PartType.STATEMENT_PART);
      sink.addCombineListener(new CombineListener() {
	 public void combined()
	 {
	    WorkspaceObject c = sink.getContainedPart();
	    Object o = c.getPart();
	    Statement smt = (Statement)o;
	    alg.setBody(smt);
	 }
	 public void split()
	 {
	    alg.setBody(null);
	 }
      });
      sink.setToolTipText("Body of the algorithm");

      JPanel wholePanel = new JPanel();
      wholePanel.setLayout(new BorderLayout());
      wholePanel.add(topPanel, BorderLayout.NORTH);
      wholePanel.add(sink, BorderLayout.CENTER);
      return wholePanel;
   }

   /** Override the default  version to leave the algorithm header visible. */
   public void setCustomPanelVisible(boolean state)
   {
      sink.setVisible(state);
   }
   public boolean isCustomPanelVisible()
   {
      return sink.isVisible();
   }

   PartType getPartType()
   {
      return PartType.STATEMENT_PART; // FIXME is this correct for algorithm?  or should there be a NONE_PART?
   }
   Object getPart()
   {
      return alg;
   }

   String getWorkspaceObjectName()
   {
      return "Algorithm";
   }

   void partDeleted()
   {
      Library.removeAlgorithm(alg);
   }

   // need to save name, formal args, and body
   void save(PrintWriter out)
   {
      out.println("<algorithm " +
		  "name=\"" + alg.getName() + "\" " +
		  "xpos=\"" + getPoint().x + "\" " +
		  "ypos=\"" + getPoint().y + "\" " +
		  ">");

      String cmt = commentArea.getText();
      if(cmt != null && cmt.length() != 0)
      {
	 out.println("<comment>" + protectSpecialCharacters(cmt) + "</comment>");
      }

      Vector<String> formalPs = alg.getFormalParameters();
      out.println("<formalparams count=\"" + formalPs.size() + "\">");
      for(String s : formalPs)
      {
	 out.println("<param name=\"" + s + "\"/>");
      }
      out.println("</formalparams>");

      out.println("<body>");
      WorkspaceObject c = sink.getContainedPart();
      if(c != null)
	 c.save(out);
      out.println("</body>");
      
      out.println("</algorithm>");
   }

   static AlgorithmWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("algorithm"))
	 throw new ProgramLoadingException();

      AlgorithmWorkspaceObject obj = new AlgorithmWorkspaceObject(Workspace.getWorkspace());

      Element el = (Element)node;
      // do alg name
      String an = el.getAttribute("name");
      obj.algNameGad.setText(an);
      obj.alg.setName(an);
      // load the part's position, if saved
      obj.loadPointDetails(el);

      NodeList children = el.getChildNodes();
      Node paramNode = null, bodyNode = null, commentNode = null;
      for(int i = 0; i < children.getLength(); i++)
      {
	 Node c = children.item(i);
	 if(c.getNodeType() == Node.ELEMENT_NODE)
	 {
	    if(c.getNodeName().equals("formalparams"))
	       paramNode = c;
	    else if(c.getNodeName().equals("body"))
	       bodyNode = c;
	    else if(c.getNodeName().equals("comment"))
	       commentNode = c;
	 }
      }
      if(paramNode == null || bodyNode == null)
	 throw new ProgramLoadingException();

      // if there is a comment then set it
      if(commentNode != null)
      {
	 String cmt = commentNode.getTextContent();
	 // FIXME unescape cmt
	 obj.alg.setComment(cmt);
	 obj.commentArea.setText(cmt);
      }

      // do formal parameters
      obj.argsPanel.loadFormalParameters(paramNode);
      
      // take the first ELEMENT node inside body (since there should be at most one anyway)
      Node realBodyNode = Workspace.getNthChildElement(bodyNode, 0);

      // we may or may not have saved with a body combined
      if(realBodyNode !=null)
      {
	 WorkspaceObject wsObj = Workspace.dispatchLoad(realBodyNode);
	 if(wsObj == null)
	    throw new ProgramLoadingException();
	 // alg body can either be a statement or an exprssion, so no need to check type here.
	 obj.sink.progCombine(wsObj);
      }

      obj.algNameGad.setBackground(Color.white);//SPR - needed to avoid going red on loading a file
      
      return obj;
   }


   public WorkspaceObject getWorkspaceObjectForPart(Object part)
   {
      if(part == alg)
	 return this;
      else
      {
	 if(sink != null && sink.getContainedPart() != null)
	    return sink.getContainedPart().getWorkspaceObjectForPart(part);
	 else
	    return null;
      }
   }


   /*
    * Returns the string where all non-ascii and <, &, > are encoded as numeric entities. I.e. "&lt;A &amp; B &gt;"
    */
   public static String protectSpecialCharacters(String originalUnprotectedString)
   {
      if (originalUnprotectedString == null)
      {
	 return null;
      }
      boolean anyCharactersProtected = false;
      
      StringBuffer stringBuffer = new StringBuffer();
      for (int i = 0; i < originalUnprotectedString.length(); i++)
      {
	 char ch = originalUnprotectedString.charAt(i);
	 
	 boolean controlCharacter = ch < 32;
	 boolean unicodeButNotAscii = ch > 126;
	 boolean characterWithSpecialMeaningInXML = ch == '<' || ch == '&' || ch == '>';
	 
	 if (characterWithSpecialMeaningInXML || unicodeButNotAscii || controlCharacter)
	 {
	    stringBuffer.append("&#" + (int) ch + ";");
	    anyCharactersProtected = true;
	 }
	 else
	 {
	    stringBuffer.append(ch);
	 }
      }
      if (anyCharactersProtected == false)
      {
	 return originalUnprotectedString;
      }
      
      return stringBuffer.toString();
   }

}
