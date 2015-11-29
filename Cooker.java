package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import org.scotek.util.swing.*;

import java.util.*;
import java.lang.ref.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.util.zip.*;

import com.sun.scenario.scenegraph.*;

public class Cooker extends JFrame
{
   public static int printDPI = 72;
   public static boolean useZoomer = false;

   Workspace workspace;
   JScrollPane scroller;
   JSGPanel scenePanel;
   SGComponent sceneComp;
   SGTransform.Scale zoomNode;
   JSlider zoomSlider;
   javax.swing.Timer scrollerTimer;

   public Cooker()
   {
      super("Cooker");

      SwingUtilities.invokeLater(new Runnable() {
	 public void run()
	 {
	    if(useZoomer)
	    {
	       // with lightweight menus the highlight/mouse position coordination is lost
	       // at non-100% zoom scales.
	       JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	    }

	    setJMenuBar(createMenus());
      
	    setLayout(new BorderLayout());
	    getContentPane().add(createPartsPanel(), BorderLayout.EAST);
	    
	    workspace = new Workspace();

	    if(useZoomer)
	    {
	       sceneComp = new SGComponent();
	       sceneComp.setComponent(workspace);
	       sceneComp.setID("workspace wrapper");
	       
	       zoomNode = SGTransform.createScale(1.0f, 1.0f, null);
	       zoomNode.setChild(sceneComp);
	       
	       scenePanel = new JSGPanel();
	       scenePanel.setScene(zoomNode);
	       
	       scroller = new JScrollPane(scenePanel);
	       getContentPane().add(scroller, BorderLayout.CENTER);
	    }
	    else
	    {
	       scroller = new JScrollPane(workspace);
	       getContentPane().add(scroller, BorderLayout.CENTER);
	    }
	    
	    if(useZoomer)
	    {
	       zoomSlider = new JSlider(10, 200, 100);
//	       zoomSlider.setLabelTable(zoomSlider.createStandardLabels(25, 25));
	       zoomSlider.setPaintLabels(true);
	       zoomSlider.setMajorTickSpacing(10);
	       zoomSlider.setPaintTicks(true);
	       zoomSlider.addChangeListener(new ChangeListener() {
		  public void stateChanged(ChangeEvent e)
		  {
		     double val = zoomSlider.getValue() / 100.0;
		     zoomNode.setScale(val, val);
		  }
	       });
	       getContentPane().add(zoomSlider, BorderLayout.SOUTH);
	    }
	    
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    addWindowListener(new WindowAdapter() {
	       public void windowClosing(WindowEvent e)
	       {
		  if(maybeQuit())
		     System.exit(0);
	       }
	    });
	    
	 }
      });
      
   }

   boolean maybeQuit()
   {
      // TODO check if file needs to be saved
      int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to continue?\nAny unsaved work will be lost.", "Unsaved Work?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
      if(answer != JOptionPane.YES_OPTION)
	 return false;
      else
	 return true;
   }

   JMenuBar createMenus()
   {
      JMenuBar bar = new JMenuBar();
      JMenu menu = new JMenu("Project");
      bar.add(menu);
      JMenuItem item = new JMenuItem("New");
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    if(maybeQuit())
	       Workspace.clearWorkspace();
	 }
      });
      menu.add(item);
      
      item = new JMenuItem("Load...");
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    if(!maybeQuit())
	       return;

	    try
	    {
	       JFileChooser chooser = new JFileChooser();
	       FileNameExtensionFilter filter = new FileNameExtensionFilter("Cooker programs (*.ckz, *.ckr)", "ckz", "ckr");
	       chooser.setFileFilter(filter);
	       chooser.setCurrentDirectory(null); // home dir
	       int returnVal = chooser.showOpenDialog(null);
	       if(returnVal == JFileChooser.APPROVE_OPTION)
	       {
		  File file = chooser.getSelectedFile();
	       
		  DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		  Document doc;
		  if(file.getName().endsWith(".ckz"))
		  {
		     FileInputStream stream = new FileInputStream(file);
		     ZipInputStream zip = new ZipInputStream(stream);
		     ZipEntry entry = zip.getNextEntry();
		     if(!entry.getName().equals("program.ckr"))
		     {
			System.err.println("compressed program.ckr not found");
			return;
		     }
		     doc = docBuild.parse(zip);
		     zip.close();
		  }
		  else
		  {
		     doc = docBuild.parse(file);
		  }
	       
		  // check that the first is a <workspace> tag
		  NodeList list = doc.getChildNodes();
		  Node n = list.item(0);
		  if(list.getLength() != 1 || !list.item(0).getNodeName().equals("workspace"))
		  {
		     System.err.println("first tag is not <workspace>, it is " + doc.getNodeName());
		     return;
		  }
	       
		  workspace.load(list.item(0));
	       }
	    }
	    catch(CharConversionException cce)
	    {
	       JOptionPane.showMessageDialog(null, "The program has crashed because there were non-standard characters in the input file.\nUsually this is related to Microsoft Smart Quote characters, which get into this program by cutting & pasting text from Word.\n\n",//Cooker will now try and convert the Smart Quotes to normal quotes to make the file usable.",
					     "Error", JOptionPane.ERROR_MESSAGE);
	       
	       // FIXME try stripping the file of smart quote chars then reloading

	       // load file into string
	       // strip smart chars
	       // load from string
	       // display success message, check manually
	       // catch exception, sorry still cnat load it, please edit by hand
	    }
	    catch(ProgramLoadingException excep)
	    {
	       JOptionPane.showMessageDialog(null, "Cannot load program - the file has been corrupted.\n\nTechnical information follows:\n" + Workspace.prettyStackTrace(excep), "Error", JOptionPane.ERROR_MESSAGE);
	       // generally speaking, we can have no idea where the load failed,
	       // so we have to start from a clean slate.
	       Workspace.clearWorkspace();
	    }
	    catch(Exception excep)
	    {
	       JOptionPane.showMessageDialog(null, "Cannot load program - something strange has gone wrong!  Maybe try restarting the program and then reloading?  If this problem is reproducible please file a bug.\n\nTechnical information follows:\n" + Workspace.prettyStackTrace(excep), "Error", JOptionPane.ERROR_MESSAGE);
	       Workspace.clearWorkspace();
	    }
	 }
      });
      menu.add(item);
      item = new JMenuItem("Save...");
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter("Cooker programs (*.ckz, *.ckr)", "ckr", "ckz");
	    chooser.setFileFilter(filter);
	    chooser.setCurrentDirectory(null); // home dir
	    int returnVal = chooser.showSaveDialog(null);
	    if(returnVal == JFileChooser.APPROVE_OPTION)
	    {
	       File file = chooser.getSelectedFile();
	       // if file already exists, prompt for overwrite?
	       if(file.exists())
	       {
		  int answer = JOptionPane.showConfirmDialog(null, "File " + file.getName() + " already exists.  Overwrite?", "Overwrite?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		  if(answer != JOptionPane.YES_OPTION)
		     return;
	       }

	       // give workspace a stream to save to...
	       try
	       {
		  if(file.getName().endsWith(".ckr"))
		  {
		     System.err.println("Saving CKR...");
		     FileWriter writer = new FileWriter(file);
		     PrintWriter pw = new PrintWriter(writer);
		     workspace.save(pw);
		     pw.close();
		  }
		  else
		  {
		     if(!file.getName().endsWith(".ckz"))
		     {
			file = new File(file.getParent(), file.getName() + ".ckz");
		     }
		     
		     System.err.println("Saving CKZ...");
		     FileOutputStream stream = new FileOutputStream(file);
		     ZipOutputStream zip = new ZipOutputStream(stream);
		     zip.putNextEntry(new ZipEntry("program.ckr"));
		     PrintWriter pw = new PrintWriter(zip);
		     workspace.save(pw);
		     pw.flush();
		     zip.closeEntry();
		     zip.finish();
		     zip.close();
		  }
	       }
	       catch(IOException ioe)
	       {
		  String traceMessage = Workspace.prettyStackTrace(ioe);
		  JOptionPane.showMessageDialog(null, "Error while trying to save file.  Please try again.\n\nTechnical information follows:\n" + traceMessage , "Error", JOptionPane.ERROR_MESSAGE); 
	       }
	    }
	 }
      });
      menu.add(item);

      menu.addSeparator();
      item = new JMenuItem("Import...");
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    try
	    {
	       JFileChooser chooser = new JFileChooser();
	       FileNameExtensionFilter filter = new FileNameExtensionFilter("Cooker programs (*.ckz, *.ckr)", "ckz", "ckr");
	       chooser.setFileFilter(filter);
	       chooser.setCurrentDirectory(null); // home dir
	       int returnVal = chooser.showOpenDialog(null);
	       if(returnVal == JFileChooser.APPROVE_OPTION)
	       {
		  File file = chooser.getSelectedFile();
		  
		  DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		  Document doc;
		  if(file.getName().endsWith(".ckz"))
		  {
		     FileInputStream stream = new FileInputStream(file);
		     ZipInputStream zip = new ZipInputStream(stream);
		     ZipEntry entry = zip.getNextEntry();
		     if(!entry.getName().equals("program.ckr"))
		     {
			System.err.println("compressed program.ckr not found");
			return;
		     }
		     doc = docBuild.parse(zip);
		     zip.close();
		  }
		  else
		  {
		     doc = docBuild.parse(file);
		  }

		  // check that the first is a <workspace> tag
		  NodeList list = doc.getChildNodes();
		  Node n = list.item(0);
		  if(list.getLength() != 1 || !list.item(0).getNodeName().equals("workspace"))
		  {
		     System.err.println("first tag is not <workspace>, it is " + doc.getNodeName());
		     return;
		  }
		  
		  workspace.importProgram(list.item(0));
	       }
	    }
	    catch(ProgramLoadingException excep)
	    {
	       JOptionPane.showMessageDialog(null, "Cannot import program - the file has been corrupted.\n\nTechnical information follows:\n" + Workspace.prettyStackTrace(excep), "Error", JOptionPane.ERROR_MESSAGE);
	       // generally speaking, we can have no idea where the load failed,
	       // so we have to start from a clean slate.
	       Workspace.clearWorkspace();
	    }
	    catch(Exception excep)
	    {
	       JOptionPane.showMessageDialog(null, "Cannot import program - something strange has gone wrong!  Maybe try restarting the program and then reloading?  If this problem is reproducible please file a bug.\n\nTechnical information follows:\n" + Workspace.prettyStackTrace(excep), "Import Error", JOptionPane.ERROR_MESSAGE);
	       Workspace.clearWorkspace();
	    }
	 }
      });
      menu.add(item);

      item = new JMenuItem("Export image...");
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gd = ge.getDefaultScreenDevice();
	    GraphicsConfiguration gc = gd.getDefaultConfiguration();

	    BufferedImage bImage = gc.createCompatibleImage(workspace.getWidth(), workspace.getHeight(), Transparency.BITMASK);
	    Graphics2D g = bImage.createGraphics();
	    workspace.paint(g);

	    try
	    {
	       JFileChooser chooser = new JFileChooser();
	       FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG images (*.png)", "png");
	       chooser.setFileFilter(filter);
	       chooser.setCurrentDirectory(null); // home dir
	       int returnVal = chooser.showSaveDialog(null);
	       if(returnVal == JFileChooser.APPROVE_OPTION)
	       {
		  File file = chooser.getSelectedFile();
		  // if file already exists, prompt for overwrite?
		  if(file.exists())
		  {
		     int answer = JOptionPane.showConfirmDialog(null, "File " + file.getName() + " already exists.  Overwrite?", "Overwrite?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		     if(answer != JOptionPane.YES_OPTION)
			return;
		  }
		  
		  ImageIO.write(bImage, "png", file);
	       }
	    }
	    catch(IOException ioe)
	    {
	       JOptionPane.showMessageDialog(null, "Error while trying to save image to disk.\n\nTechnical information follows:\n" + Workspace.prettyStackTrace(ioe), "Image Export Error", JOptionPane.ERROR_MESSAGE);
	    }
	 }
      });
      menu.add(item);

      menu.addSeparator();
      item = new JMenuItem("Print...");
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    PrintUtilities.printComponent(workspace);
	 }
      });
      menu.add(item);
      
      menu.addSeparator();
      item = new JMenuItem("Quit");
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    if(maybeQuit())
	       System.exit(0);
	 }
      });
      menu.add(item);

      menu = new JMenu("View");
      bar.add(menu);
      // Note that this will not expand collsapsed blocks *nested inside* top-level blocks.
      final JMenuItem expandAllItem = new JMenuItem("Expand top-level blocks");
      expandAllItem.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    workspace.setAllCustomPanelVisible(true);
	 }
      });
      menu.add(expandAllItem);
      final JMenuItem collapseAllItem = new JMenuItem("Collapse top-level blocks");
      collapseAllItem.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    workspace.setAllCustomPanelVisible(false);
	 }
      });
      menu.add(collapseAllItem);
      menu.addSeparator();
      final JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem("Use colours", true);
      checkItem.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    workspace.setUseColor(checkItem.isSelected());
	 }
      });
      menu.add(checkItem);


      bar.add(Box.createHorizontalGlue());
      
      menu = new JMenu("Help");
      bar.add(menu);
      item = new JMenuItem("About");
      menu.add(item);
      item.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    JOptionPane.showMessageDialog(null,
					    "<html><h1><center>Cooker</center></h1>A visual programming language designed for<br>teaching introductory programming.<br><br>2008-2011 Paul Dempster &lt;paul@scotek.org&gt;</html>",
					  "About Cooker", JOptionPane.INFORMATION_MESSAGE,
					  new ImageIcon(getClass().getResource("/icons/cooker-icon-256.png")));
	 }
      });
      menu.add(new JSeparator());
      createHelpMenus(menu);
      
      return bar;
   }

   /** Creates the dynamic help menu by scanning the language-appropriate help directory
       and creating items for each file name which ends in ".html".  Other files are ignored.
       This deliberately does not try to load from inside the JAR file so that the local site
       can customise which/if files appear without rebuilding the jar. */
   private void createHelpMenus(JMenu menu)
   {
      File helpDir = new File("help/en/");
      if(!helpDir.exists() || !helpDir.isDirectory())
      {
	 // if we can't find any files, just give up and continue with no extra help
	 return;
      }
      
      File[] helpFiles = helpDir.listFiles();
      for(File helpFile : helpFiles)
      {
	 if(!helpFile.isFile() || !helpFile.getName().endsWith(".html"))
	    continue;

	 String helpName = helpFile.getName();
	 int idx = helpName.indexOf(".html");
	 if(idx == -1)
	    continue;
	 helpName = helpName.substring(0, idx);
	 
	 JMenuItem item = new JMenuItem(helpName);
	 final File helpFileFinal = helpFile;
	 item.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e)
	    {
	       HelpViewer viewer = new HelpViewer(helpFileFinal);
	       viewer.setVisible(true);
	    }
	 });
	 menu.add(item);
      }
   }

   private void placeWorkspaceObject(WorkspaceObject obj)
   {
      Point pt = obj.getPoint();
      // find a place to put the new object, fixed for testing
      pt.translate(50, 50);
      workspace.add(obj, pt);
      workspace.invalidate();
   }

   // create the parts panel and the individual launchers within it
   JComponent createPartsPanel()
   {
      Box overallPanel = new Box(BoxLayout.Y_AXIS);//Box.createVerticalBox();
//      overallPanel.setBorder(BorderFactory.createLineBorder(Color.red));
      overallPanel.add(Box.createHorizontalStrut(200));
      overallPanel.add(algsLauncher());
      overallPanel.add(flowLauncher());
      overallPanel.add(varLauncher());
      overallPanel.add(literalLauncher());
      overallPanel.add(mathsLauncher());
      overallPanel.add(listLauncher());
      overallPanel.add(treeLauncher());
      overallPanel.add(miscLauncher());
//      overallPanel.add(graphicsLauncher());

      
      JPanel biggestPanel = new JPanel(new BorderLayout());
      biggestPanel.add(overallPanel, BorderLayout.NORTH);
      biggestPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);
      JScrollPane scroller = new JScrollPane(biggestPanel);
      return scroller;
   }


   /** Create a standard launcher button that instantiates the given class with a Workspace
    ** and places it into the workspace. */
   JButton createStandardLauncherButton(final Class<? extends WorkspaceObject> klass,
					final String buttonName, String shortName,
					String type, String tooltip)
   {
      JButton but = new JButton(buttonName);
      but.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
      but.setToolTipText(buildToolTipText(shortName, type, tooltip));
      but.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    try
	    {
	       WorkspaceObject obj = klass.getConstructor(Workspace.class).newInstance(workspace);
	       placeWorkspaceObject(obj);
	    }
	    catch(Exception exp)	// there are lots of different possible ones here!
	    {
	       throw new RuntimeException("Unable to create part " + buttonName, exp);
	    }
	 }
      });
      return but;
   }

   /** Iterate through a list of button data and creates a launcher containing them. */
   JCompactablePanel createStandardLauncher(String launcherName, String[][] buttonData)
   {
      Box panel = Box.createVerticalBox();

      try
      {
	 for(int i = 0; i < buttonData.length; i++)
	 {
	    String[] button = buttonData[i];
	    // kz should be type Class<? extends WorkspaceObject> but that causes compiler errors.
	    Class kz = Class.forName(button[0]);
	    panel.add(createStandardLauncherButton(kz, button[1], button[2], button[3], button[4]));
	 }
      }
      catch(Exception eeee)
      {
	 System.err.println("Error creating launcher button, skipping");
	 System.err.println(eeee);
      }

      return new JCompactablePanel(launcherName, panel);
   }


   String[][] miscButtons = { {"org.scotek.vpl.PrintWorkspaceObject", "Print Statement", "Print", "Statement", "Display a message on the screen for the user to see"},
			      {"org.scotek.vpl.RandomIntegerWorkspaceObject", "Random Integer", "Random Integer", "Expression", "Return a random integer between 0 and the given number" },
			      {"org.scotek.vpl.InputNumberWorkspaceObject", "Input Number", "Input Number", "Expression", "Prompt the user to enter a number using the supplied prompt" },
			      {"org.scotek.vpl.CombineStringOperatorWorkspaceObject", "Combine as String", "Combine as String", "Expression", "Converts the two values to Strings and combines them together into a single String."} };
   JCompactablePanel miscLauncher()
   {
      return createStandardLauncher("Misc.", miscButtons);
   }

   String[][] varButtons = { {"org.scotek.vpl.VarRefWorkspaceObject", "Variable Reference", "Variable Reference", "Expression", "Use the current value of a variable"},
			     {"org.scotek.vpl.VarAssignmentWorkspaceObject", "Variable Assignment", "Variable Assignment", "Statement", "Change the current value of a variable"},
			     {"org.scotek.vpl.VarDeclarationWorkspaceObject", "Variable Declaration", "Variable Declaration", "Statement", "Create a new variable with a starting value"} };
   JCompactablePanel varLauncher()
   {
      return createStandardLauncher("Variables", varButtons);
   }

   String[][] mathsButtons = { {"org.scotek.vpl.EqualsOperatorWorkspaceObject", "<html>Equals Operator (&equiv;)</htm>", "Equals", "Expression", "Returns true if the two parts are equal in value, false otherwise."},
			       {"org.scotek.vpl.GreaterOperatorWorkspaceObject", "Greater Operator (>)", "Greater", "Expression", "Returns true if the left part is greater in value than the right part, false otherwise."},
			       {"org.scotek.vpl.LesserOperatorWorkspaceObject", "Lesser Operator (<)", "Lesser", "Expression", "Returns true if the left part is lesser in value than the right part, false otherwise."},
			       {"org.scotek.vpl.MultiplicationOperatorWorkspaceObject", "Multiply Operator (*)", "Multiply", "Expression", "Multiplies the two values together."},
			       {"org.scotek.vpl.DivisionOperatorWorkspaceObject", "Division Operator (/)", "Division", "Expression", "Divides the first value by the second."},
			       {"org.scotek.vpl.ModulusOperatorWorkspaceObject", "Modulus Operator (%)", "Modulus", "Expression", "Returns the remainder of the first value divided by the second."},
			       {"org.scotek.vpl.AdditionOperatorWorkspaceObject", "Add Operator (+)", "Addition", "Expression", "Adds the first value and the second."},
			       {"org.scotek.vpl.SubtractionOperatorWorkspaceObject", "Subtraction Operator (-)", "Subtraction", "Expression", "Subtracts the second value from the first."} };
   JCompactablePanel mathsLauncher()
   {
      return createStandardLauncher("Maths", mathsButtons);
   }


   String[][] graphicsButtons = { {"org.scotek.vpl.CreateGraphicsWorkspaceObject", "Create Graphics (Graphics)", "", "", ""},
				  {"org.scotek.vpl.DrawPixelWorkspaceObject", "Draw Point (Graphics)", "", "", ""},
				  {"org.scotek.vpl.DrawStringWorkspaceObject", "Draw String (Graphics)", "", "", ""},
				  {"org.scotek.vpl.DrawCircleWorkspaceObject", "Draw Circle (Graphics)", "", "", ""},
				  {"org.scotek.vpl.ClearGraphicsWorkspaceObject", "Clear (Graphics)", "", "", ""} };
   JCompactablePanel graphicsLauncher()
   {
      return createStandardLauncher("Graphics", graphicsButtons);
   }

   
   String[][] treeButtons = { {"org.scotek.vpl.TreeLeafWorkspaceObject", "Leaf (Tree)", "Leaf (Tree)", "Expression", "An empty tree"},
			      {"org.scotek.vpl.TreeNodeWorkspaceObject", "Node (Tree)", "Node (Tree)", "Expression", "Creates a tree from two existing subtrees and a value."},
			      {"org.scotek.vpl.TreeIsLeafWorkspaceObject", "isLeaf (Tree", "isLeaf (Tree)", "Expression", "Returns true if the given tree is empty, false otherwise"},
			      {"org.scotek.vpl.TreeValueWorkspaceObject", "Value (Tree)", "Value (Tree)", "Expression", "Returns the value at this node of a tree"},
			      {"org.scotek.vpl.TreeLeftWorkspaceObject", "Left (Tree)", "Left (Tree)", "Expression", "Returns the left subtree of this node"},
			      {"org.scotek.vpl.TreeRightWorkspaceObject", "Right (Tree)", "Right (Tree)", "Expression", "Returns the right subtree of this node"} };
   JCompactablePanel treeLauncher()
   {
      return createStandardLauncher("Trees", treeButtons);
   }

   String[][] listButtons = { {"org.scotek.vpl.ListNilWorkspaceObject", "Empty List (List)", "Empty List", "Expression", "The empty list value."},
			      {"org.scotek.vpl.ListConsWorkspaceObject", "Cons (List)", "Cons", "Expression", "Combine a value and an existing list into a new list."},
			      {"org.scotek.vpl.ListIsEmptyWorkspaceObject", "isEmpty (List)", "isEmpty", "Expression", "Returns true if the given list is empty, false otherwise."},
			      {"org.scotek.vpl.ListValueWorkspaceObject", "Value (List)", "Value", "Expression", "Returns the first value in a non-empty list."},
			      {"org.scotek.vpl.ListTailWorkspaceObject", "Tail (List)", "Tail", "Expression", "Returns the tail of a list."} };
   JCompactablePanel listLauncher()
   {
      return createStandardLauncher("Lists", listButtons);
   }

   String[][] flowButtons = { {"org.scotek.vpl.IfWorkspaceObject", "If Statement", "If", "Statement", "Checks condition and runs left part if true, right part if false"},
			      {"org.scotek.vpl.WhileWorkspaceObject", "While Statement", "While", "Statement", "Repeatedly runs the body of the loop while the condition is true.<br>The body may be executed zero times if the condition is initially false."},
			      {"org.scotek.vpl.StatementSequenceWorkspaceObject", "Statement Sequence", "Statement Sequence", "Statement", "Sequentially run a sequence of statements."} };
   JCompactablePanel flowLauncher()
   {
      return createStandardLauncher("Flow Control", flowButtons);
   }


   String[][] algButtons = { {"org.scotek.vpl.AlgorithmWorkspaceObject", "New Algorithm", "New Algorithm", "Statement", "Create a new algorithm.  The name must be unique in the program, and the formal parameters must have different names."},
			     {"org.scotek.vpl.CallWorkspaceObject", "Call Algorithm", "Call", "Statement/Expression", "Call an algorithm with the supplied actual parameter values.<br>If this block is not combined into anything else, then a run button allows you to start the program with this call."},
			     {"org.scotek.vpl.ReturnWorkspaceObject", "Return", "Return", "Expression", "Ends the algorithm, returning the given value to the caller."} };
   JCompactablePanel algsLauncher()
   {
      return createStandardLauncher("Algorithms", algButtons);
   }

   JCompactablePanel literalLauncher()
   {
      Box panel = Box.createVerticalBox();

      JButton litIntBut = new JButton("Literal Integer");
      litIntBut.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
      litIntBut.setToolTipText(buildToolTipText("Literal Integer", "Expression", "A part that represents a fixed integer value."));
      litIntBut.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    String valStr = JNumericPadDialog.showDialog(null, "Literal Integer", "Please enter integer literal");
	    if(valStr == null)	// user clicked cancel
	       return;
	    try
	    {
	       int val = Integer.parseInt(valStr);

	       LiteralIntegerWorkspaceObject obj = new LiteralIntegerWorkspaceObject(workspace, val);
	       placeWorkspaceObject(obj);
	    }
	    catch(NumberFormatException nfe)
	    {
	       JOptionPane.showMessageDialog(null, "Sorry, that is not a valid integer value.",
					     "Invalid value", JOptionPane.ERROR_MESSAGE);
	    }
	 }
      });
      panel.add(litIntBut);

      JButton litBoolBut = new JButton("Literal Boolean");
      litBoolBut.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
      litBoolBut.setToolTipText(buildToolTipText("Literal Boolean", "Expression", "A part that represents a fixed boolean value."));
      litBoolBut.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    Object[] possibleValues = { "True", "False", "Cancel" };
	    int selectedValue = JOptionPane.showOptionDialog(null,
								"Choose one",
								"Boolean Literal",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								possibleValues,
								null);

	    boolean val;
	    switch(selectedValue)
	    {
	       case 0:
		  val = true;
		  break;
	       case 1:
		  val = false;
		  break;
	       default:
		  return;
	    }
	    
	    LiteralBooleanWorkspaceObject obj = new LiteralBooleanWorkspaceObject(workspace, val);
	    placeWorkspaceObject(obj);
	 }
      });
      panel.add(litBoolBut);

      JButton litStrBut = new JButton("Literal String");
      litStrBut.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
      litStrBut.setToolTipText(buildToolTipText("Literal String", "Expression", "A part that represents a fixed string value."));
      litStrBut.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e)
	 {
	    String valStr = JOptionPane.showInputDialog("Please enter string literal");
	    if(valStr == null)
	       return;

	    // try to make sure this is not malformed unicode - mainly for Word smart quote type issues
//	    valStr = valStr.replace('\u2018','\'').replace('\u2019', '\'').replace('\u201c', '\"').replace('\u201d', '\"');

	    LiteralStringWorkspaceObject obj = new LiteralStringWorkspaceObject(workspace, valStr);
	    placeWorkspaceObject(obj);
	 }
      });
      panel.add(litStrBut);

      return new JCompactablePanel("Literals", panel);
   }


   /** Use old, non-standard HTML to layout the tooltip contents. */
   private String buildToolTipText(String name, String type, String description)
   {
      return "<html><table width=100%><tr><td><b>" + name + "</b></td><td align=right><i>" + type + "</i></td></tr></table>" + description + "</html>";
   }
   

   public static void main(String[] argv)
   {
      // simple command line arg parsing
      try
      {
	 for(int i = 0; i < argv.length; i++)
	 {
	    if(argv[i].equals("-printdpi") && i + 1 < argv.length)
	    {
	       Cooker.printDPI = Integer.parseInt(argv[i+1]);
	       i++;
	    }
	    else if(argv[i].equals("-zoomer"))
	    {
	       Cooker.useZoomer = true;
	    }
	    else
	    {
	       throw new RuntimeException("Invalid Option");
	    }
	 }
      }
      catch(Exception e)
      {
	 System.out.println("Invalid option");
	 printUsage();
	 System.exit(-1);
      }

      Cooker c = new Cooker();

      c.setSize(800, 600);
      c.setVisible(true);
   }

   private static void printUsage()
   {
      System.out.println("Usage: Cooker [-zoomer] [-printdpi <int>]");
   }
}
