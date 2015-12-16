package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.ArrayList;
import java.util.Vector;
import java.util.UUID;
import java.io.*;
import org.w3c.dom.*;

import org.scotek.util.swing.XYLayout;

class ProgramLoadingException extends Exception {}
class Workspace extends JPanel
{
   static Workspace workspaceInstance;
   static UUID currentUUID;
   boolean useColor = true;
   boolean isDragged = false;
   private String currentContent;
   private StringWriter stringWriter;
   private PrintWriter writer;

   public Workspace()
   {
      Workspace.workspaceInstance = this;
      currentUUID = UUID.randomUUID();
      this.setLayout(new XYLayout());
      this.setOpaque(true);
      stringWriter = new StringWriter();
      writer = new PrintWriter(stringWriter);

      currentContent = getCurrentWorkspace();
   }

   // wipe the workspace clean, as if the program was just started.
   public static void clearWorkspace()
   {
      currentUUID = UUID.randomUUID();
      // clear out the Library
      Library.clearLibrary();
      // remove all components from panel
      workspaceInstance.removeAll();
      workspaceInstance.invalidate();
      workspaceInstance.repaint();
   }

   // ugly, but getting the workspace to the partsinks would be uglier.
   public static Workspace getWorkspace()
   {
      return Workspace.workspaceInstance;
   }

   public void setUseColor(boolean s)
   {
      useColor = s;
   }

   /**  Collapse or expand all top-level components. */
   public void setAllCustomPanelVisible(boolean state)
   {
      int compCount = getComponentCount();
      for(int i = 0; i < compCount; i++)
      {
	 WorkspaceObject wo = (WorkspaceObject)getComponent(i);
	 wo.setCustomPanelVisible(state);
      }
      revalidate();
   }

   // welcome to the world of ad-hoc, horrible, "xml" :)
   public void save(PrintWriter out)
   {
      printWorkspace(out);
      currentContent = getCurrentWorkspace();
      isDragged = false;
   }

   private void printWorkspace(PrintWriter out){
      out.println("<workspace checksum=\"" + currentUUID + "\">");
      int compCount = getComponentCount();
//      System.err.println("Component count is " + compCount);
      WorkspaceObject[] objs = new WorkspaceObject[compCount];
      for(int i = 0; i < compCount; i++)
      {
          objs[i] = (WorkspaceObject)getComponent(i);
          objs[i].save(out);
      }
      
      out.println("</workspace>");
   }

   // this method takes the work out of checking each possible tag name in a
   // massive cascading if structure.  it then calls the load method in the
   // appropriate class and returns the result.
   public static WorkspaceObject dispatchLoad(Node node) throws ProgramLoadingException
   {
      String name = node.getNodeName();

      if(name.equals("algorithm"))
	 return AlgorithmWorkspaceObject.load(node);
      else if(name.equals("if"))
	 return IfWorkspaceObject.load(node);
      else if(name.equals("while"))
	 return WhileWorkspaceObject.load(node);
      else if(name.equals("repeat"))
    return RepeatWorkspaceObject.load(node);
      else if(name.equals("equals"))
	 return EqualsOperatorWorkspaceObject.load(node);
      else if(name.equals("greater"))
	 return GreaterOperatorWorkspaceObject.load(node);
      else if(name.equals("lesser"))
	 return LesserOperatorWorkspaceObject.load(node);
      else if(name.equals("print"))
	 return PrintWorkspaceObject.load(node);
      else if(name.equals("input"))
	 return InputNumberWorkspaceObject.load(node);
      else if(name.equals("randint"))
	 return RandomIntegerWorkspaceObject.load(node);
      else if(name.equals("call"))
	 return CallWorkspaceObject.load(node);
      else if(name.equals("statementsequence"))
	 return StatementSequenceWorkspaceObject.load(node);
      else if(name.equals("multiplication"))
	 return MultiplicationOperatorWorkspaceObject.load(node);
      else if(name.equals("division"))
	 return DivisionOperatorWorkspaceObject.load(node);
      else if(name.equals("modulus"))
	 return ModulusOperatorWorkspaceObject.load(node);
      else if(name.equals("addition"))
	 return AdditionOperatorWorkspaceObject.load(node);
      else if(name.equals("subtraction"))
	 return SubtractionOperatorWorkspaceObject.load(node);
      else if(name.equals("combinestring"))
	 return CombineStringOperatorWorkspaceObject.load(node);
      else if(name.equals("literalinteger"))
	 return LiteralIntegerWorkspaceObject.load(node);
      else if(name.equals("literalboolean"))
	 return LiteralBooleanWorkspaceObject.load(node);
      else if(name.equals("literalstring"))
	 return LiteralStringWorkspaceObject.load(node);
      else if(name.equals("varref"))
	 return VarRefWorkspaceObject.load(node);
      else if(name.equals("varassign"))
	 return VarAssignmentWorkspaceObject.load(node);
      else if(name.equals("vardecl"))
	 return VarDeclarationWorkspaceObject.load(node);
      else if(name.equals("nillist"))
	 return ListNilWorkspaceObject.load(node);
      else if(name.equals("listcons"))
	 return ListConsWorkspaceObject.load(node);
      else if(name.equals("listisempty"))
	 return ListIsEmptyWorkspaceObject.load(node);
      else if(name.equals("listvalue"))
	 return ListValueWorkspaceObject.load(node);
      else if(name.equals("listtail"))
	 return ListTailWorkspaceObject.load(node);
      else if(name.equals("treeleaf"))
	 return TreeLeafWorkspaceObject.load(node);
      else if(name.equals("treenode"))
	 return TreeNodeWorkspaceObject.load(node);
      else if(name.equals("treeisleaf"))
	 return TreeIsLeafWorkspaceObject.load(node);
      else if(name.equals("treevalue"))
	 return TreeValueWorkspaceObject.load(node);
      else if(name.equals("treeleft"))
	 return TreeLeftWorkspaceObject.load(node);
      else if(name.equals("treeright"))
	 return TreeRightWorkspaceObject.load(node);
      else if(name.equals("graphicscontext"))
	 return CreateGraphicsWorkspaceObject.load(node);
      else if(name.equals("drawpixel"))
	 return DrawPixelWorkspaceObject.load(node);
      else if(name.equals("drawstring"))
	 return DrawStringWorkspaceObject.load(node);
      else if(name.equals("drawcircle"))
	 return DrawCircleWorkspaceObject.load(node);
      else if(name.equals("cleargraphics"))
	 return ClearGraphicsWorkspaceObject.load(node);
      else if(name.equals("return"))
	 return ReturnWorkspaceObject.load(node);
      else
      {
	 System.err.println("UNKNOWN TAG |" + name + "|");
	 throw new ProgramLoadingException();
      }
   }

   public void load(Node node) throws ProgramLoadingException
   {
      Workspace.clearWorkspace();
      CallWorkspaceObject.clearLoadingQueue();

      importProgram(node);
      currentContent = getCurrentWorkspace();
      isDragged = false;
   }
   
   public void importProgram(Node node) throws ProgramLoadingException
   {
      // call load on each object in the workspace, with the load method returning the WorkspaceObject it created.  Each load method will be called with that objects Node.
      if(!node.getNodeName().equals("workspace"))
	 throw new ProgramLoadingException();
      
      // update the UUID from the import file.
      // unfortunately we can only store one of these, so take the imported files one.
      {
	 Element el = (Element)node;
	 String str = el.getAttribute("checksum");
	 currentUUID = UUID.fromString(str);
      }
      
      NodeList children = node.getChildNodes();
      for(int i = 0; i < children.getLength(); i++)
      {
	 Node child = children.item(i);
	 if(child.getNodeType() != Node.ELEMENT_NODE)
	    continue;

	 String childName = child.getNodeName();
	 WorkspaceObject obj = null;

	 obj = dispatchLoad(child);

	 // Hack to make sure only top-level Call parts have a run button.  Hide it for all
	 // Call parts during load, then unhide it for top-level the parts.
	 if(obj instanceof CallWorkspaceObject)
	 {
	    CallWorkspaceObject c = (CallWorkspaceObject)obj;
	    c.setRunVisible(true);
	 }

	 if(obj != null)
	 {
	    Point pt = obj.getPoint();
	    if(pt.x == 0 && pt.y == 0) // give a default position if saved without position
	    {
	       pt.translate(250, 50);
	    }
	    add(obj, pt);
	 }
      }

      CallWorkspaceObject.processLoadingQueue();
      CallWorkspaceObject.clearLoadingQueue();
      
      invalidate();
      repaint();
   }

   // return the (0-based) idx-th child node which is type ELEMENT.  Convenience method, dumped here.
   // returns null if such a child not available.
   public static Node getNthChildElement(Node root, int idx)
   {
      int count = -1;
      NodeList list = root.getChildNodes();
      if(list.getLength() <= idx)
	 return null;
      for(int i = 0; i < list.getLength(); i++)
      {
	 Node n = list.item(i);
	 if(n.getNodeType() == Node.ELEMENT_NODE)
	 {
	    count++;

	    if(count == idx)
	       return n;
	 }
      }
      return null;
   }

   // return the immediate child of root which has the tag == str, otherwise null.
   // if multiple such noted exist, the first one is returned.
   public static Node getChildElementByName(Node root, String str)
   {
      NodeList list = root.getChildNodes();
      for(int i = 0; i < list.getLength(); i++)
      {
	 Node n = list.item(i);
	 if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(str))
	    return n;
      }
      return null;
   }

   // seems like i'm just dumping all util methods in Workspace now :)
   public static String prettyStackTrace(Exception e)
   {
      StringBuffer traceMessage = new StringBuffer();
      traceMessage.append(e.toString() + "\n");

      StackTraceElement[] trace = e.getStackTrace();
      for(StackTraceElement t : trace)
	 traceMessage.append(t.toString() + "\n");
      return traceMessage.toString();
   }

   public Point getFreePosition(Dimension comSize){
      int compCount = getComponentCount();
      int height = comSize.height;
      int width = comSize.width;

      WorkspaceObject[] objs = new WorkspaceObject[compCount];
      java.util.List<Dimension> comSizes = new ArrayList<Dimension>();
      java.util.List<Integer> positionX = new ArrayList<Integer>();
      java.util.List<Integer> positionY = new ArrayList<Integer>();

      //add the actual
      for(int i = 0; i < compCount; i++){
         objs[i] = (WorkspaceObject)getComponent(i);

         Dimension temp_size = objs[i].getSize();
         int posi_x = objs[i].getX();
         int posi_y = objs[i].getY();

         comSizes.add(temp_size);
         positionX.add(posi_x);
         positionY.add(posi_y);
      }

      Point returnPoint = findRightPosi(comSize,comSizes,positionX,positionY);

      return returnPoint;
   }

   private Point findRightPosi(Dimension comSize, java.util.List<Dimension> comSizes, java.util.List<Integer> positionX ,java.util.List<Integer> positionY){
      Point returnPoint = new Point(10,10);

      return findRightPosiHelper(comSize,comSizes,positionX,positionY,returnPoint);
   }

   private Point findRightPosiHelper(Dimension comSize, java.util.List<Dimension> comSizes, java.util.List<Integer> positionX ,java.util.List<Integer> positionY, Point returnPoint) {
      while (isOverlapped(comSize,comSizes,positionX,positionY,returnPoint)) {
         //chose a new place
         if (returnPoint.x==10) {
            //border
            returnPoint.x = 10+returnPoint.y;
            returnPoint.y = 10;
         }else{
            returnPoint.x -= 1;
            returnPoint.y += 1;
         }
      }
      return returnPoint;
   }

   private boolean isOverlapped(Dimension comSize, java.util.List<Dimension> comSizes, java.util.List<Integer> positionX ,java.util.List<Integer> positionY, Point returnPoint){
      int size = comSizes.size();
      int padding = 10;
      for (int counter = 0;counter<size;counter++){
         if (isOverLapping(comSize, returnPoint.x,returnPoint.y,comSizes.get(counter),positionX.get(counter),positionY.get(counter),padding)){
            return true;
         }
      }
      return false;
   }

   public boolean isOverLapping(Dimension size, int x, int y, Dimension size2, int x2, int y2, int padding){
      int x_max = x+size.width;
      int y_max = y+size.height;

      int x2_min = x2-padding;
      int x2_max = x2+size2.width+padding;
      int y2_min = y2-padding;
      int y2_max = y2+size2.height+padding;


      if (x<x2_max&&x>x2_min&&y<y2_max&&y>y2_min) {
         return true;
      }
      if (x_max<x2_max&&x_max>x2_min&&y<y2_max&&y>y2_min) {
         return true;
      }
      if (x<x2_max&&x>x2_min&&y_max<y2_max&&y_max>y2_min) {
         return true;
      }
      if (x_max<x2_max&&x_max>x2_min&&y_max<y2_max&&y_max>y2_min) {
         return true;
      }
      return false;
   }

   private String getCurrentWorkspace(){
      printWorkspace(writer);

      String temp = stringWriter.toString();

      stringWriter = new StringWriter();
      writer = new PrintWriter(stringWriter);

      //System.out.println(temp);
      return temp;
   }

   public boolean isChanged(){
      String tempContent = getCurrentWorkspace();
      if (tempContent.contentEquals(currentContent)) {
         return false||isDragged;
      }else{
         return true;
      }
   }

   public void drag(){
      isDragged = true;
   }

}
