package org.scotek.vpl;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Vector;
import java.util.Iterator;

import java.lang.ref.WeakReference;

enum PartType { STATEMENT_PART, EXPRESSION_PART, BOTH_PART };
enum SinkState { NORMAL_STATE, COMBINING_STATE, COMBINED_STATE };
class PartSink extends JPanel implements ActionListener
{
   static Vector<WeakReference<PartSink>> allSinks = new Vector<WeakReference<PartSink>>();
   static SinkState overallState = SinkState.NORMAL_STATE; // if any are combining, this is combining.
   static WorkspaceObject offeredPart;			   // during an offer, this is part offered to all sinks

   PartType type;		// type of part this sink accepts
   JButton targBut;		// the button that can be clicked to recieve a combine
   CardLayout cardLayout;	// used to hide the recieve button and show the received component
   WorkspaceObject containedPart; // part that has been combined into here
   SinkState state = SinkState.NORMAL_STATE;
   
   public PartSink(PartType t)
   {
      type = t;
      allSinks.add(new WeakReference<PartSink>(this));
      setBorder(BorderFactory.createLineBorder(Color.black));
      cardLayout = new CardLayout();
      setLayout(cardLayout);
      // first card is the combine button
      targBut = new JButton(getLabelForType());
      targBut.setEnabled(false);
      targBut.addActionListener(this);
      add(targBut, "button");
      // second card is the space for the actual combined part itself, which is made on demand.
   }

   private String getLabelForType()
   {
      switch(type)
      {
         case STATEMENT_PART:
	    return "Statement";
         case EXPRESSION_PART:
	    return "Expression";
         case BOTH_PART:
	    return "Statement/Expression";
         default:
	    return "Unknown Part Type!!";
      }
   }

   // Informs all PartSinks that a part of the given type is available.
   // Sinks that accept this should turn into green buttons,
   // sinks that don't accept this should turn into red labels.
   static public void partAvailable(WorkspaceObject wsObj, PartType availType)
   {

      for (Iterator<WeakReference<PartSink>> it = allSinks.iterator(); it.hasNext(); )
      {
	 WeakReference<PartSink> ref = it.next();
	 PartSink sink = ref.get();
	 if(sink == null)
	 {
	    // PartSink has been GC'ed, remove weakref from vector
	    it.remove();
	    continue;
	 }
	 
	 if(sink.state == SinkState.COMBINED_STATE)
	    continue;

	 // check to see it this sink accepts the offered type
	 if(sink.type == availType || availType == PartType.BOTH_PART)
	 {
	    // move to combining state
	    PartSink.overallState = SinkState.COMBINING_STATE; // only want this to happen if at least 1 is combining.
	    offeredPart = wsObj; // only want this to happen if at least 1 is combining.
	    sink.state = SinkState.COMBINING_STATE;
	    sink.targBut.setForeground(Color.green);
	    sink.targBut.setEnabled(true);
	 }
      }
   }

   // ends a part being available, either because it was recieved or cancelled
   static public void endPartAvailable()
   {
      offeredPart = null;

      for (Iterator<WeakReference<PartSink>> it = allSinks.iterator(); it.hasNext(); )
      {

	 WeakReference<PartSink> ref = it.next();
	 PartSink sink = ref.get();
	 if(sink == null)
	 {
	    // PartSink has been GC'ed, remove weakref from vector
	    it.remove();
	    continue;
	 }
	 
	 if(sink.state == SinkState.COMBINED_STATE)
	    continue;

	 // now move back to normal state
	 PartSink.overallState = SinkState.NORMAL_STATE;
	 sink.state = SinkState.NORMAL_STATE;
	 sink.targBut.setForeground(Color.black);
	 sink.targBut.setEnabled(false);
      }
   }

   public void setToolTipText(String text)
   {
      targBut.setToolTipText(text);
   }

   // method to programatically combine a part to a sink, eg for loading.
   // c&p from actionPerformed method below.
   public void progCombine(WorkspaceObject prt)
   {
      containedPart = prt;
      add(containedPart, "part");
      cardLayout.show(this, "part");
      containedPart.justCombined(this);
      Workspace.getWorkspace().invalidate(); // this invalidates all containing parts too
      Workspace.getWorkspace().repaint();
      
      state = SinkState.COMBINED_STATE;
      fireCombined();
   }

   // remove the current contained part and going back to being available for another part
   public void split()
   {
      cardLayout.show(this, "button");
      remove(containedPart);
      state = SinkState.NORMAL_STATE;
      containedPart = null;
      fireSplit();
// FIXME FIXME FIXME !!!!!NEED TO ADD FIRESPLIT()??????? FIXME FIXME FIXME
   }

   public void actionPerformed(ActionEvent e)
   {
      if(state == SinkState.NORMAL_STATE)
      {
	 // should not be able to click it in normal state
	 System.err.println("sink pressed in normal state???");
      }
      else if(state == SinkState.COMBINING_STATE)  // COMBINING_STATE
      {
	 // move the thing that made itself available to inside the thing containing this partsink.
	 containedPart = offeredPart; // since this var gets cleared in endPartAvailable();

	 PartSink.endPartAvailable();

	 // combine the parts
	 // outline - the sink can have a cardlayout, and if something is combined into it, just display that.
	 // the containing part can then just ask the sink getContainedPart() which gives null if empty.
	 // we can add a COMBINED_STATE to say this sink already has something in it, so it should not
	 // take part in normal COMBINING_STATE activates and split will be allowed.
	 
	 // get the component which is being offered
	 // FIXME remove containedPart from workspace? seems to be auto-removed just now.
	 try
	 {
	    add(containedPart, "part");
	 }
	 catch(IllegalArgumentException iae) // happens if adding to parent comp to child comp
	 {
	    JOptionPane.showMessageDialog(null, "Cannot combine a part inside itself!",
					  "Error", JOptionPane.ERROR_MESSAGE);
	    state = SinkState.NORMAL_STATE;
	    return;
	 }
	 cardLayout.show(this, "part");
	 containedPart.justCombined(this);
	 Workspace.getWorkspace().invalidate(); // this invalidates all containing parts too
	 Workspace.getWorkspace().repaint();
	 
	 state = SinkState.COMBINED_STATE;
	 fireCombined();
      }
      // do nothing in COMBINED_STATE
   }

   

   Vector<CombineListener> combineListeners = new Vector<CombineListener>();
   public void addCombineListener(CombineListener cl)
   {
      combineListeners.add(cl);
   }
   void fireCombined()
   {
      for(CombineListener cl : combineListeners)
	 cl.combined();
   }
   void fireSplit()
   {
      for(CombineListener cl : combineListeners)
	 cl.split();
   }
   public WorkspaceObject getContainedPart()
   {
      return containedPart;
   }
}

interface CombineListener
{
   public void combined();	// called just after a combine has happened
   public void split();		// called just after a split has happened
}
