package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

// TODO we need to detect if the chosen alg changes name after filling this in!
class CallWorkspaceObject extends WorkspaceObject
{
   static boolean algorithmRunning = false;
   Call call;
   JTextField algNameGad;
   JButton runBut;
   ActualArgsPanel actualArgs;

   public CallWorkspaceObject(Workspace w)
   {
      super(w);
   }

   void earlyInit(Object[] extras)
   {
      call = new Call();
   }

   // We need a "RUN" button which is displayed when not combined, and hidden otherwise.
   // so override combineActionPerformed/splitActionPerformed and use some inside knowledge :/
   JPanel createCustomPanel()
   {
      JPanel topPanel = new JPanel();
      topPanel.setLayout(new BorderLayout());
      
      algNameGad = new JTextField();
      algNameGad.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    if(algNameGad.getText() == null || algNameGad.getText().equals("")
	       || Library.getAlgorithm(algNameGad.getText()) == null)
	    {
	       JOptionPane.showMessageDialog(null, "Sorry, that is not a valid algorithm name",
					     "Invalid value", JOptionPane.ERROR_MESSAGE);
	       algNameGad.setText(call.getAlgorithmName());
	       algNameGad.setBackground(Color.white);//SPR
	    }
	    else
	    {
	       call.setAlgorithmName(algNameGad.getText());
	       algNameGad.transferFocus();//SPR
	       algNameGad.setBackground(Color.white);//SPR
	       // update actualArgs panel to display appropriate number of expression sinks
	       actualArgs.setActualParameterCount(Library.getAlgorithm(algNameGad.getText()).getFormalParameterCount());
	       Workspace.getWorkspace().invalidate(); // to make the args display change correctly.
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
      algNameGad.setToolTipText("Name of the algorithm that will be called");
      topPanel.add(algNameGad, BorderLayout.NORTH);

      actualArgs = new ActualArgsPanel(call);
      topPanel.add(actualArgs, BorderLayout.CENTER);

      runBut = new JButton("Run algorithm");
//      if(Workspace.getWorkspace().useColor)
//	 runBut.setBackground(getBGColor());
      runBut.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    String name = algNameGad.getText();
	    if(name == null || Library.getAlgorithm(name) == null)
	    {
	      JOptionPane.showMessageDialog(null, "Sorry, that is not a valid algorithm name",
					     "Invalid value", JOptionPane.ERROR_MESSAGE);
	      return;
	    }

	    // Actually run the program.
	    if(algorithmRunning)
	    {
	       System.err.println("Cannot run algorithm, another algorithm is already running!");
	       return;
	    }
	    algorithmRunning = true;
	    Call.abortProgram = false;

	    // construct the abort dialog (first, so we can use the variables in the thread below
	    final JDialog abortDialog = new JDialog((Window)null, "Program running...",
						    Dialog.ModalityType.DOCUMENT_MODAL);
	    final JButton abortButton = new JButton("Click to abort program");
	    abortButton.addActionListener(new ActionListener() {
	       public void actionPerformed(ActionEvent e)
	       {
		  Call.abortProgram = true;
		  // TODO would be nice if we could wait on the exception propagating below
		  abortDialog.dispose();
	       }
	    });
	    abortDialog.getContentPane().setLayout(new BorderLayout());
	    abortDialog.getContentPane().add(abortButton);
	    abortDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    abortDialog.pack();
	    abortDialog.setLocationRelativeTo(null);

	    // build the program thread
	    Thread programThread = new Thread() {
	       public void run()
	       {
		  OutputWindow outWin = new OutputWindow("Program Output");
		  try
		  {
		     outWin.startCapture();
		     System.out.println("START OF PROGRAM.");
		     Object res = call.getValue();
		     abortDialog.dispose(); // cancel the abort dialog
		     JOptionPane.showMessageDialog(null, "" + res,
						   "Program return value", JOptionPane.INFORMATION_MESSAGE);
		  }
		  catch(ArithmeticException ae)
		  {
		     JOptionPane.showMessageDialog(null, "The program has crashed because of an arithmetic error.\nUsually this means you have devided by zero!",
						   "Error", JOptionPane.ERROR_MESSAGE);
		  }
		  catch(NotEnoughArgumentsException neae)
		  {
		     BackgroundFlasher targetThread = null;
		     Object errLoc = neae.getErrorLocation();
		     if(errLoc != null)
		     {
			targetThread = createBGFFromException(errLoc);
			targetThread.start();
		     }

		     JOptionPane.showMessageDialog(null, "The program has crashed because you did not give values in all the places that require them.\nDid you forget to give an algorithm or operator a value for a parameter?\n\n" + neae.getMessage(),
						   "Error", JOptionPane.ERROR_MESSAGE);

		     if(targetThread != null)
		     {
			targetThread.stopFlashing();
		     }
		  }
		  catch(UndefinedVariableException uve)
		  {
		     JOptionPane.showMessageDialog(null, "The program has tried to access a variable via a variable reference which is not valid.\nThis is either because the variable name is wrong, or the variable is not accessible from the place you tried to access it from.",
						   "Error", JOptionPane.ERROR_MESSAGE);
		  }
		  catch(UndefinedAlgorithmException uae)
		  {
		     JOptionPane.showMessageDialog(null, "The program has tried to call an algorithm which does exist.\nThis may be because you have changed an algorithm's name after you created a call to that algorithm.", "Error", JOptionPane.ERROR_MESSAGE);
		  }
		  catch(InvalidArgumentTypeException iate)
		  {
		     BackgroundFlasher targetThread = createBGFFromException(iate.getErrorLocation());
		     targetThread.start();
		     
		     String extra = "";
		     if(iate.getMessage() != null)
			extra = iate.getMessage();
		     JOptionPane.showMessageDialog(null, "The program crashed because a function recieved a value as a parameter which was not permitted:\n\n" + extra,
						   "Error", JOptionPane.ERROR_MESSAGE);
		     
		     targetThread.stopFlashing();
		  }
		  catch(ExecutionAbortedException eae)
		  {
		     // the user has aborted the execution of the program while it is running
		     JOptionPane.showMessageDialog(null, "The execution of the program has been aborted by the user.", "Execution Aborted", JOptionPane.INFORMATION_MESSAGE);
		  }
		  catch(StackOverflowError soe) // THIS WORKING DEPENDS ENTIRELY ON WHICH JVM IS USED AND THE SITUATION
		  {
		     JOptionPane.showMessageDialog(null, "The program has crashed because it has made too may recusive calls without a return.\nRe-design your program to avoid so many un-returned recursive calls.\n\nNote that Cooker may behave unpredicatably after this error and you may have to quit and restart.", "Error", JOptionPane.ERROR_MESSAGE);
		  }
		  catch(Exception allExp)
		  {
		     // TODO want to put stack trace inside a jtextarea but will need a custom dialog for this.
		     System.err.println("General exception: \n" + Workspace.prettyStackTrace(allExp));
		     JOptionPane.showMessageDialog(null, "A serious error happened in your program which caused it to crash.\nUnfortunately, I don't have a more useful error message for you.\nJust double-check everything in your program.\n\nIf you can repeat this error, please inform one of the instructors so we can provide a better error message.\nTechnical information about the error has been printed to the console for the instructor; you can ignore this information.\n",
						   "Error", JOptionPane.ERROR_MESSAGE);
		  }
		  finally
		  {
		     System.out.println("END OF PROGRAM.");
		     outWin.stopCapture();
		     abortDialog.dispose(); // just in case we get here via exceptions
		     algorithmRunning = false;
		  }
	       }
	    };
	    try
	    {
	       programThread.start();
	       abortDialog.setVisible(true);
	       abortDialog.toFront();
	    }
	    catch(Exception handlerExp)
	    {
	       System.err.println("algorithm thread handler exception:" + handlerExp);
	       handlerExp.printStackTrace();
	    }
	 }
      });
      JPanel wholePanel = new JPanel();
      wholePanel.setLayout(new BorderLayout());
      wholePanel.add(topPanel, BorderLayout.NORTH);
      wholePanel.add(runBut, BorderLayout.CENTER);
      return wholePanel;
   }

   private BackgroundFlasher createBGFFromException(Object errorLoc)
   {
      WorkspaceObject target = null;
      BackgroundFlasher targetThread = null;
      int count = Workspace.getWorkspace().getComponentCount();
      for(int i = 0; i < count; i++)
      {
	 WorkspaceObject wsObj = (WorkspaceObject)Workspace.getWorkspace().getComponent(i);
	 target = wsObj.getWorkspaceObjectForPart(errorLoc);
	 if(target != null)
	    break;
      }
//      assert target != null;
      return new BackgroundFlasher(target, Color.green);
   }

   // We need a "RUN" button which is displayed when not combined, and hidden otherwise.
   // so override combineActionPerformed/splitActionPerformed and use some inside knowledge :/
   public void combineActionPerformed(ActionEvent e)
   {
      super.combineActionPerformed(e);
      if(!isCombined)
      {
	 if(PartSink.overallState == SinkState.NORMAL_STATE)
	    setRunVisible(true);
	 else
	    setRunVisible(false);
      }
   }
   public void splitActionPerformed(ActionEvent e)
   {
      super.splitActionPerformed(e);
      runBut.setVisible(true);
   }
   
   public void setRunVisible(boolean s)
   {
      runBut.setVisible(s);
   }


   PartType getPartType()
   {
      return PartType.BOTH_PART;
   }
   Object getPart()
   {
      return call;
   }

   String getWorkspaceObjectName()
   {
      return "Call";
   }

   // it may have an alg name in it, and then it may have actual arguments
   void save(PrintWriter out)
   {
      String n = call.getAlgorithmName();
      if(n != null)
      {
	 out.println("<call name=\"" + n + "\" " + 
		     "xpos=\"" + getPoint().x + "\" " +
		     "ypos=\"" + getPoint().y + "\" " +
		     ">");
	 WorkspaceObject[] objs = actualArgs.getActualParameterWorkspaceObjects();
	 out.println("<actualparams count=\"" + objs.length + "\">");
	 for(WorkspaceObject o : objs)
	 {
	    out.println("<actualparam>");
	    if(o != null)
	       o.save(out);
	    out.println("</actualparam>");
	 }
	 out.println("</actualparams>");
      }
      else
      {
	 out.println("<call " +
		     "xpos=\"" + getPoint().x + "\" " +
		     "ypos=\"" + getPoint().y + "\" " +
		     ">");
      }

      out.println("</call>");
   }

   // We cannot guarantee that we can load Call parts as we load everything else because
   // if it has an algorithm name in it, that algorithm part needs to exist before creating
   // the Call part so the automatic actual parameter sinks work.
   // This obviously becomes a problem in recursive or mutally-recursive situations.
   // Therefore, when we see a Call part for the first time we add an empty Call part as normal
   // and also put that entire program subtree in a waiting queue.
   // Then when the rest of the program is finished loading
   // we go back to the unpopulated Call part and fill it's details in.
   static Vector<CallWorkspaceObject> loadingQueueCalls = new Vector<CallWorkspaceObject>();
   static Vector<Node> loadingQueueNodes = new Vector<Node>();
   static CallWorkspaceObject load(Node node) throws ProgramLoadingException
   {
      if(!node.getNodeName().equals("call"))
	 throw new ProgramLoadingException();

      CallWorkspaceObject obj = new CallWorkspaceObject(Workspace.getWorkspace());
      obj.setRunVisible(false);
      // load the part's position, if saved
      obj.loadPointDetails((Element)node);

      loadingQueueCalls.add(obj);
      loadingQueueNodes.add(node);

      return obj;
   }
   static void clearLoadingQueue()
   {
      loadingQueueCalls.clear();
      loadingQueueNodes.clear();
   }
   static void processLoadingQueue() throws ProgramLoadingException
   {
      // need to keep trying to do this until we are sure the queues are empty.
      // this is because call objects' args may themselves be call objects, which
      // get added to this queue during queue processing.
      while(loadingQueueCalls.size() != 0)
      {
	 CallWorkspaceObject callObj = loadingQueueCalls.remove(0);
	 Node nodeObj = loadingQueueNodes.remove(0);
	 processLoadingQueueElement(callObj, nodeObj);
      }
   }
   static void processLoadingQueueElement(CallWorkspaceObject callPart, Node root) throws ProgramLoadingException
   {
      // look to see if there is a name stored as an attribute, if not stop here
      Element el = (Element)root;
      String name = el.getAttribute("name");
      if(name == null || name.equals(""))
	 return;

      // otherwise set the algorithm name
      callPart.algNameGad.setText(name);
      callPart.call.setAlgorithmName(name);


      // load the actual args tags one by one
      Element actualParamsContainer = (Element)Workspace.getChildElementByName(root, "actualparams");
      if(actualParamsContainer == null)
	 throw new ProgramLoadingException();

      int numParams = Integer.parseInt(actualParamsContainer.getAttribute("count"));

      callPart.actualArgs.setActualParameterCount(numParams);

      for(int i = 0; i < numParams; i++)
      {
	 Element paramEl = (Element)Workspace.getNthChildElement(actualParamsContainer, i);
	 if(paramEl == null || !paramEl.getNodeName().equals("actualparam"))
	    throw new ProgramLoadingException();

	 Element subEl = (Element)Workspace.getNthChildElement(paramEl, 0);
	 if(subEl != null)
	 {
	    WorkspaceObject subObj = Workspace.dispatchLoad(subEl);
	    if(subObj != null)
	    {
	       callPart.actualArgs.getSink(i).progCombine(subObj);
	    }
	 }
      }

      Workspace.getWorkspace().invalidate(); // to make the args display change correctly.
      callPart.algNameGad.setBackground(Color.white);//SPR - load fix
   }

   Color getBGColor()
   {
      if(Workspace.getWorkspace().useColor)
	 return expColor;
      else
	 return Color.white;
   }

   public WorkspaceObject getWorkspaceObjectForPart(Object part)  //EEH
   {
      if(part == call)
	 return this;

      WorkspaceObject[] objs = actualArgs.getActualParameterWorkspaceObjects();
      for(WorkspaceObject o : objs)
      {
	 WorkspaceObject res = o.getWorkspaceObjectForPart(part);
	 if(res != null)
	    return res;
      }
      return null;
   }
}

