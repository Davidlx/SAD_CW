package org.scotek.vpl;

import java.util.*;
import java.lang.ref.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;

/** Will check every .ckr file in the directory given as argv[0].
 */
public class DuplicateChecker
{
   public static void main(String[] argv)
   {
      Hashtable<UUID, Vector<File>> results = new Hashtable<UUID, Vector<File>>();

      if(argv.length != 1)
      {
	 System.err.println("Must suplly directory to scan");
	 System.exit(-1);
      }

      File scanDir = new File(argv[0]);
      if(!scanDir.isDirectory())
      {
	 System.err.println("Given path is not a directory!");
	 System.exit(-1);
      }
      
      System.out.println("Scanning directory " + scanDir);
      File[] allFiles = scanDir.listFiles(new FilenameFilter() {
	 public boolean accept(File dir, String name)
	 {
	    if(name.endsWith(".ckr"))
	       return true;
	    return false;
	 }
      });

      System.out.println("Found " + allFiles.length + " files");

      for(int i = 0; i < allFiles.length; i++)
      {
	 System.out.print("(" + (i + 1) + " of " + allFiles.length + ") " + allFiles[i] + "...");
	 scanFile(allFiles[i], results);
      }
      System.out.println("Scan complete");
      
      printStats(results);

      System.exit(0);
   }


   static void scanFile(File f, Hashtable<UUID, Vector<File>> table)
   {
      try
      {
	 DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	 Document doc = docBuild.parse(f);
	 
	 NodeList list = doc.getChildNodes();
	 Node node = list.item(0);
	 if(!node.getNodeName().equals("workspace"))
	 {
	    System.err.println("File is not a valid cooker program");
	    System.exit(-2);
	 }
	 
	 Element el = (Element)node;
	 String str = el.getAttribute("checksum");
	 UUID id = UUID.fromString(str);
	 
	 Vector<File> v = table.get(id);
	 if(v == null)
	 {
	    v = new Vector<File>();
	 }
	 v.add(f);
	 table.put(id, v);
	 
	 System.out.println(id);
      }
      catch(Exception e)
      {
	 System.err.println("Exception while scanning " + f);
	 System.err.println(e);
	 e.printStackTrace();
	 System.exit(-3);
      }
   }

   static void printStats(Hashtable<UUID, Vector<File>> table)
   {
      Enumeration<UUID> en = table.keys();

      while(en.hasMoreElements())
      {
	 UUID id = en.nextElement();
	 Vector<File> v = table.get(id);
	 if(v.size() > 1)
	 {
	    System.out.println("\n" + v.size() + " files with ID  " + id);
	    for(int i = 0; i < v.size(); i++)
	    {
	       System.out.println(v.elementAt(i));
	    }
	 }
      }
   }
}