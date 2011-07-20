/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *

 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.print.*;
import javax.print.*;

/** 
 *  A simple utility class that lets you very simply print
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
 *  May be freely used or adapted.
 */

public class PrintUtilities implements Printable {
    private Component componentToBePrinted;
    
    private int tracker = 0;
    
    /**
     * BufferedImage that is to be printed
     */
    BufferedImage img;
    
    /**
     * Height of the sub-images from img
     * to be put on each page
     */
    private int numStavesConverted =0;
    
    /**
     * Number of Staves on each page.
     * Arbitrary value for now, will change so that
     * it will get a number from a text field within preferences
     */
    private int numStavesPerPage=3;
    
    /**
     * Magic Number that is the height of a single Stave
     */
    private static final int staveHeight = 160;
    
    public static void printComponent(Component c, int numLines) {
        new PrintUtilities(c, numLines).print();
    }
  
    public PrintUtilities(Component componentToBePrinted, int numLines) {
        this.componentToBePrinted = componentToBePrinted;
        int height = numLines*staveHeight;
        img = new BufferedImage(this.componentToBePrinted.getWidth(), height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = img.getGraphics();
        this.componentToBePrinted.paint(graphics);
    }
    
        public void setComponent(Component c, int numLines)
      {
        this.componentToBePrinted = c;
        int height = numLines*staveHeight;
        img = new BufferedImage(this.componentToBePrinted.getWidth(), height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = img.getGraphics();
        this.componentToBePrinted.paint(graphics);
      }
  
    public static void printMultipleComponents(Component comp[], int numLines) {  
        PrintUtilities utility = new PrintUtilities();
        PrintService[] pservices = PrinterJob.lookupPrintServices();
        
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(utility);
        
       if (printJob.printDialog()) 
         {
         for(int i = 0; i<comp.length; i++)
            {
            utility.setComponent(comp[i], numLines);
            try 
              {
                printJob.print();
              }
            catch(PrinterException pe)
              {
                System.out.println("Error printing: " + pe);
              }
            }
         }
        
    }
  
    public PrintUtilities() {
    }

    

  
    public void print() {
        PrintService[] pservices = PrinterJob.lookupPrintServices();
        
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
/*
        HashPrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
        if (printJob.printDialog(attr)) {
            try {
                for(Attribute a : attr.toArray()) {
                    if(a.getName().equals("spool-data-destination") && a.toString().length() > 5 && a.toString().substring(0, 5).equals("file:")) {
                        ErrorLog.log(ErrorLog.COMMENT, "Sorry, printing to RAW printer files (.prn) has been disabled.  Printing will not continue.  ", true);
                        return;
                    }
                }
                printJob.print(attr);
            } catch(PrinterException pe) {
                System.out.println("Error printing: " + pe);
            }
        }
*/        
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch(PrinterException pe) {
                System.out.println("Error printing: " + pe);
            }
        }
    }

    /**
     * 7/20/11 This method was created by Amos Byon, who edited the
     * method created by Rob MacGrogan, who edited the
     * original method written by Marty Hall.
     * 2/05 Rob MacGrogan, http://www.developerdotstar.com/community/node/124/print
     */
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        int response = NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D) g;
        
        // for faster printing, turn off double buffering
        disableDoubleBuffering(componentToBePrinted);
        numStavesConverted =staveHeight*numStavesPerPage;
        int frameHeight = numStavesConverted;
        if(tracker+numStavesConverted >= img.getHeight())
        {
            frameHeight = img.getHeight()-tracker;
        }
        BufferedImage subImage = img.getSubimage(0,tracker,1000,frameHeight);
        tracker = numStavesConverted*pageIndex;
        Dimension d = componentToBePrinted.getSize();       //  get size of document
        double panelWidth = d.width;                        //  width in pixels
        double panelHeight = d.height;                      //  height in pixels
        double pageHeight = pf.getImageableHeight();        //  height of printer page
        double pageWidth = pf.getImageableWidth();          //  width of printer page
        double scale = pageWidth / panelWidth;
        int totalNumPages = ((int) Math.ceil(img.getHeight()/numStavesConverted))+1;
        
        // make sure not print empty pages
        if (pageIndex >= totalNumPages) {
            response = NO_SUCH_PAGE;
            tracker = 0;
        } else {
            g2.scale(scale, scale);
            if(pageIndex == 0)
            {
                g2.translate(pf.getImageableX(), pf.getImageableY()+10);
            }
            else
            {
                g2.translate(pf.getImageableX(), pf.getImageableY());
            }
            g2.translate(0, -pageIndex*pageHeight);
            int topOfNextPage = (int)(pageIndex*pageHeight);
            g2.drawImage(subImage, null, 0,topOfNextPage);
            response= Printable.PAGE_EXISTS;
        }
        return response;
    }

    /** 
     *  The speed and quality of printing suffers dramatically if
     *  any of the containers have double buffering turned on.
     *  So this turns if off globally.
     *  @see #enableDoubleBuffering
     */
    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    /** 
     * Re-enables double buffering globally. 
     */
    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
    
}
