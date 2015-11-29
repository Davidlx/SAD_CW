package org.scotek.vpl;

import java.awt.*;
import javax.swing.*;
import java.awt.print.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;

/** A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintUtilities.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more 
 *  efficient to first do the following:
 *  <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintUtilities.printComponent(componentToBePrinted).
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-Printing.html
 *  May be freely used or adapted.
 */

public class PrintUtilities implements Printable
{
   private Component componentToBePrinted;
//   static int DOT_PER_72ND_INCH = 1;
   
   public static void printComponent(Component c)
   {
      new PrintUtilities(c).print();
   }
  
   public PrintUtilities(Component componentToBePrinted) 
   {
      this.componentToBePrinted = componentToBePrinted;
   }
  
   public void print() 
   {
      PrinterJob printJob = PrinterJob.getPrinterJob();
      printJob.setPrintable(this);
      if (printJob.printDialog())
      {
	 try
	 {
	    printJob.print();
	 } catch(PrinterException pe)
	 {
	    System.err.println("Error printing: " + pe);
	 }
      }
   }

   // used for deployment debugging to check waht resolution printers are
   private void checkDPI(Graphics g)
   {
      try
      {
	 // check printer DPI test
	 PrinterGraphics pg = (PrinterGraphics)g;
	 PrinterJob pj = pg.getPrinterJob();
	 PrintService ps = pj.getPrintService();
//	 PrinterResolution pr = ps.getAttribute(PrinterResolution.class);
	 PrinterResolution pr = (PrinterResolution) ps.getDefaultAttributeValue(PrinterResolution.class);
	 if(pr != null)
	 {
	    int feedRes = pr.getFeedResolution(PrinterResolution.DPI);
	    int crossFeedRes = pr.getCrossFeedResolution(PrinterResolution.DPI);
	    System.err.println("Printer DPI " + feedRes + ", " + crossFeedRes);
	 }
	 else
	    System.err.println("No PrinterResolution attribute");
      }
      catch(Exception e)
      {
      }
   }

   public int print(Graphics g, PageFormat pageFormat, int pageIndex)
   {
      if (pageIndex > 0) 
      {
	 return(NO_SUCH_PAGE);
      } 
      else 
      {
	 checkDPI(g);

	 Graphics2D g2d = (Graphics2D)g;
	 g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

	 double DOT_PER_72ND_INCH = Cooker.printDPI / 72.0;
	 double paperWidthIn72 = pageFormat.getImageableWidth();
	 double compWidthIn72 = componentToBePrinted.getWidth() / DOT_PER_72ND_INCH;
	 double scaleFactorWidth = paperWidthIn72 / compWidthIn72;
	 double paperHeightIn72 = pageFormat.getImageableHeight();
	 double compHeightIn72 = componentToBePrinted.getHeight() / DOT_PER_72ND_INCH;
	 double scaleFactorHeight = paperHeightIn72 / compHeightIn72;
	 g2d.scale(scaleFactorWidth, scaleFactorHeight);

	 disableDoubleBuffering(componentToBePrinted);
	 componentToBePrinted.paint(g2d);
	 enableDoubleBuffering(componentToBePrinted);
	 return(PAGE_EXISTS);
      }
   }
   
   /** The speed and quality of printing suffers dramatically if
    *  any of the containers have double buffering turned on.
    *  So this turns if off globally.
    *  @see enableDoubleBuffering
    */
   public static void disableDoubleBuffering(Component c) 
   {
      RepaintManager currentManager = RepaintManager.currentManager(c);
      currentManager.setDoubleBufferingEnabled(false);
   }

   /** Re-enables double buffering globally. */
  
   public static void enableDoubleBuffering(Component c) 
   {
      RepaintManager currentManager = RepaintManager.currentManager(c);
      currentManager.setDoubleBufferingEnabled(true);
   }
}
