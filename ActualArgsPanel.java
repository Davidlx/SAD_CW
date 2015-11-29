package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.*;

class ActualArgsPanel extends JPanel implements CombineListener
{
   private Call call;
   private GridLayout layout;

   public ActualArgsPanel(Call c)
   {
      call = c;
      layout = new GridLayout();
      setLayout(layout);
   }
   
   // FIXME currently any arguments split to the top-level
   // would be better if the the first c are kept and any extra popped to top-level.
   public void setActualParameterCount(int c)
   {
      // split any existing argument parts to the top-level
      {
	 int num = getComponentCount();
	 for(int i = 0; i < num; i++)
	 {
	    PartSink sink = (PartSink)getComponent(i);
	    WorkspaceObject o = sink.getContainedPart();
	    if(o != null)
	    {
	       o.splitActionPerformed(null); // FIXME only works because event param not used!
	    }
	 }
      }

      removeAll();		// gets rid of the PartSinks themselves

      layout.setColumns(c);
      for(int i = 0; i < c; i++)
      {
	 PartSink sink = new PartSink(PartType.EXPRESSION_PART);
	 sink.addCombineListener(this);
	 add(sink);
      }
      validate();
      updateParams();
   }

   // get expressions from each sink, or null for a particular element if nothing combined.
   public Expression[] getActualParameterExpressions()
   {
      Expression[] exps = new Expression[getComponentCount()];
      
      for(int i = 0; i < exps.length; i++)
      {
	 PartSink sink = (PartSink)getComponent(i);
	 WorkspaceObject o = sink.getContainedPart();
	 if(o == null)
	 {
	    exps[i] = null;
	 }
	 else
	 {
	    Expression exp = (Expression)o.getPart();
	    exps[i] = exp;
	 }
      }
      return exps;
   }

   public WorkspaceObject[] getActualParameterWorkspaceObjects()
   {
      WorkspaceObject[] objs = new WorkspaceObject[getComponentCount()];
      for(int i = 0; i < objs.length; i++)
      {
	 PartSink sink = (PartSink)getComponent(i);
	 objs[i] = sink.getContainedPart();
      }
      return objs;
   }

   // return the i-th sink in the part, or null.
   public PartSink getSink(int i)
   {
      return (PartSink)getComponent(i);
   }

   public void combined()
   {
      updateParams();
   }
   public void split()
   {
      updateParams();
   }

   private void updateParams()
   {
      call.setActualParameters(getActualParameterExpressions());
   }
}
