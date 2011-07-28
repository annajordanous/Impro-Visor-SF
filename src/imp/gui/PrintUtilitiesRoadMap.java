/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.print.*;
import javax.print.*;

/**
 *
 * @author ImproVisor
 */
public class PrintUtilitiesRoadMap implements Printable{
    private Component compToBePrinted;
    
    public PrintUtilitiesRoadMap(Component comp) {
        compToBePrinted = comp;
    }
    
    public static void printRoadMap(Component comp)
    {
        PrintUtilitiesRoadMap util = new PrintUtilitiesRoadMap(comp);
        PrintService[] pservices = PrinterJob.lookupPrintServices();
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(util);
        if(printJob.printDialog())
        {
            try
            {
                printJob.print();
            }
            catch(PrinterException pe)
            {
                System.out.println("Error Printing: " + pe);
            }
        }
    }
    
    public int print(Graphics g, PageFormat pf, int pageIndex)
    {
        int response = NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D)g;
        Dimension d = compToBePrinted.getSize();
        double pageHeight = pf.getImageableHeight();
        double pageWidth = pf.getImageableWidth();
        double scale = pageWidth/d.width;
        int numPages = 1;
        if(pageIndex >= numPages) {
            response = NO_SUCH_PAGE;
        }
        else
        {
            g2.scale(scale, scale);
            g2.translate(pf.getImageableX(), pf.getImageableY());
            compToBePrinted.paint(g2);
            response = Printable.PAGE_EXISTS;
        }
        return response;
    }
    
}
