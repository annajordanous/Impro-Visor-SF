/*
 *
 */
package imp.gui;

import java.io.*;
import java.util.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import polya.*;

/**
 * Manages communication between the user interface and individual complexity windows. Maintains references
 * to all the complexity graphs.
 *
 * @author Julia Botev
 */
public class ComplexityWindowController {

    ComplexityPanel overallComplexityPanel, densityPanel, varietyPanel,
            syncopationPanel, consonancePanel, leapSizePanel, directionChangePanel;
    private ArrayList<ComplexityPanel> complexityPanels;
    /** Denotes the number of attributes that need to be factored into the computation of complexity */
    private int numValidAttrs;
    private int beatsPerBar;
    private int totalNumBeats;
    private int attrGranularity;
    private int totalWidth; // width of the graph at any given time
    public JCheckBox manageSpecific;
    public JComboBox granBox;

    /** panels will always be of length 7 */
    public ComplexityWindowController(int beats, int gran, ComplexityPanel... panels) {
        if (panels.length != 7) {
            System.out.println("Incorrect number of panels passed to complexity window constructor!");
        }
        else {
            overallComplexityPanel = panels[0];
            densityPanel = panels[1];
            varietyPanel = panels[2];
            syncopationPanel = panels[3];
            consonancePanel = panels[4];
            leapSizePanel = panels[5];
            directionChangePanel = panels[6];

            complexityPanels = new ArrayList<ComplexityPanel>(7);
            for (int i = 0; i < panels.length; i++) {
                complexityPanels.add(i, panels[i]);
            }
            numValidAttrs = 6;
            totalNumBeats = beats;
            attrGranularity = gran;
            totalWidth = overallComplexityPanel.getWidth();
        }
    }

    public void initController(int time, JCheckBox specific, JComboBox gran) {
        beatsPerBar = time;
        overallComplexityPanel.setEnabled(true);
        for (int i = 1; i < complexityPanels.size(); i++) {
            complexityPanels.get(i).upperLimitField.setEnabled(false);
            complexityPanels.get(i).lowerLimitField.setEnabled(false);
            complexityPanels.get(i).noComputeBox.setEnabled(false);
            complexityPanels.get(i).setEnabled(false);
            complexityPanels.get(i).setTime(beatsPerBar);
        }

        // Set the names of each panel for saving
        overallComplexityPanel.setName("overall");
        densityPanel.setName("density");
        varietyPanel.setName("variety");
        syncopationPanel.setName("syncopation");
        consonancePanel.setName("consonance");
        leapSizePanel.setName("leapSize");
        directionChangePanel.setName("directionChange");

        manageSpecific = specific;
        manageSpecific.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (manageSpecific.isSelected()) {
                    overallComplexityPanel.setEnabled(false);
                    overallComplexityPanel.upperLimitField.setEnabled(false);
                    ((ComplexityPanel) overallComplexityPanel).lowerLimitField.setEnabled(false);
                    for (int i = 1; i < complexityPanels.size(); i++) {
                        complexityPanels.get(i).upperLimitField.setEnabled(true);
                        complexityPanels.get(i).lowerLimitField.setEnabled(true);
                        complexityPanels.get(i).noComputeBox.setEnabled(true);
                        complexityPanels.get(i).setEnabled(true);
                    }
                    //ungray specific attrs!
                }
                if (!manageSpecific.isSelected()) {
                    overallComplexityPanel.setEnabled(true);
                    overallComplexityPanel.upperLimitField.setEnabled(true);
                    overallComplexityPanel.lowerLimitField.setEnabled(true);
                    for (int i = 1; i < complexityPanels.size(); i++) {
                        complexityPanels.get(i).upperLimitField.setEnabled(false);
                        complexityPanels.get(i).lowerLimitField.setEnabled(false);
                        complexityPanels.get(i).noComputeBox.setEnabled(false);
                        complexityPanels.get(i).setEnabled(false);
                    }
                }
                //re-gray specific attrs!
            }
        });
        granBox = gran;
        granBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                attrGranularity = Integer.parseInt((String) granBox.getSelectedItem());
                for (int i = 0; i < complexityPanels.size(); i++) {
                    complexityPanels.get(i).redrawGran(attrGranularity);
                }
            }
        });
        initBuffers();
    }

    /** Initializes the off-screen buffers and colors of the graphs */
    public void initBuffers() {
        overallComplexityPanel.initBuffer(new Color(255, 140, 0));
        densityPanel.initBuffer(new Color(154, 205, 0));
        varietyPanel.initBuffer(new Color(102, 205, 170));
        syncopationPanel.initBuffer(new Color(3, 168, 158));
        consonancePanel.initBuffer(new Color(191, 239, 255));
        leapSizePanel.initBuffer(new Color(28, 134, 238));
        directionChangePanel.initBuffer(new Color(131, 111, 255));
    }

    /** Takes a new number of beats and redraws the graphs */
    public int updateBeats(int beats) {
        totalNumBeats = beats;
        for(int i = 0; i<complexityPanels.size(); i++) {
            complexityPanels.get(i).redrawBeats(totalNumBeats);
        }
        totalWidth = overallComplexityPanel.getWidth();
        return totalWidth;
    }
    /** Takes a new granularity and redraws the graphs */
    public int updateGran(int gran) {
        attrGranularity = gran;
        for(int i = 0; i<complexityPanels.size(); i++) {
            complexityPanels.get(i).redrawGran(attrGranularity);
        }
        totalWidth = overallComplexityPanel.getWidth();
        return totalWidth;
    }

    public ArrayList<ComplexityPanel> getPanels() {
        return complexityPanels;
    }

    public int getNumValidAttrs() {
        return numValidAttrs;
    }

    public void setNumValidAttrs(int attrs) {
        numValidAttrs = attrs;
    }
    /** Assembles a list of attribute ranges of each attribute to be calculated.
      * If an attribute is to be left out, its value is null */
    public ArrayList<ArrayList> getAttributeRanges() {
        ArrayList<ArrayList> attrs = new ArrayList<ArrayList>(7);
        for (int i = 0; i<complexityPanels.size(); i++) {
            if(complexityPanels.get(i).toCompute()) {
                attrs.add(complexityPanels.get(i).valueRange());
            }
            else {
                attrs.add(null);
            }
        }
        return attrs;
    }

    public void mouseHandler(MouseEvent evt) {
        //If the action originated in the overall complexity panel
        if (((ComplexityPanel) evt.getSource()).equals(overallComplexityPanel)) {
            if (!manageSpecific.isSelected()) {
                //TODO: make the motion of the other curves dependent on their current position
                for (int i = 0; i < complexityPanels.size(); i++) {
                    complexityPanels.get(i).mouseHandler(evt);
                }
            }
        } else { // source is not the overall complexity curve
            if (manageSpecific.isSelected()) {
                if (!((ComplexityPanel) evt.getSource()).noComputeBox.isSelected()) {
                    int oldY, newY;
                    Double toAdd;
                    if (((MouseEvent) evt).isShiftDown()) {
                        oldY = ((ComplexityPanel) overallComplexityPanel).getBarLower(evt.getX());
                    } else {
                        oldY = ((ComplexityPanel) overallComplexityPanel).getBarUpper(evt.getX());
                    }
                    newY = evt.getY();
                    Double attrs = ((Integer) numValidAttrs).doubleValue();
                    toAdd = ((oldY - newY) * ((attrs - 1) / attrs)); //number to add to new y

                    ((ComplexityPanel) evt.getSource()).mouseHandler(evt);
                    evt.translatePoint(0, toAdd.intValue());
                    ((ComplexityPanel) overallComplexityPanel).mouseHandler(evt);
                }
            }
        }
    }
    /** Creates a file with the specified name and returns it, .soloProfile is the extension */
    public File saveComplexityWindow(String pathname, String filename) throws FileNotFoundException, IOException {
        String newPath = pathname + "/" + filename + ".soloProfile";
        File toReturn = new File(newPath);
        FileOutputStream stream = new FileOutputStream(toReturn);
        String windowInfo = convertComplexityWindowToString(); //turn essential info into a string
        stream.write(windowInfo.getBytes()); //write that string to the file
        return toReturn;
    }

    public void loadComplexityWindow(String pathname) throws FileNotFoundException, IOException {
        FileInputStream stream = new FileInputStream(pathname);
        String info = "";
        int next = stream.read();
        // -1 denotes EOF
        while(next != -1) {
            info += next;
            next = stream.read();
        }
        convertStringToComplexityWindow(info);
    }


    /** Converts essential info about the window into a String for saving.
     *  What needs to be saved: total width, numBars, granularity, individual curve values,
     *  values of each min and max range text field, whether or not a curve is to be computed
     */
    private String convertComplexityWindowToString() {
        Polylist info = new Polylist();
        // structure:
        // List 1: (numValidAttrs beatsPerBar numBeats gran width)
        // List 2: ((minLower maxUpper compute ([list of lower bounds])([list of upper bounds]))), ... <seven times>

        // List 1: global info
        Polylist front = Polylist.list("globalInfo",
                Polylist.list("numValidAttrs", numValidAttrs),
                Polylist.list("beatsPerBar", beatsPerBar),
                Polylist.list("totalNumBeats", totalNumBeats),
                Polylist.list("granularity", attrGranularity),
                Polylist.list("width", totalWidth));

        // List 2: combined attribute graph info
        Polylist back = Polylist.list("specificAttrs");
        // individual attribute graph info
        Polylist inner = new Polylist();

        for (int i = complexityPanels.size()-1; i>=0; i--) {
           inner = Polylist.list(complexityPanels.get(i).getName(),
                   Polylist.list("minLower", complexityPanels.get(i).getMinLower()),
                   Polylist.list("maxUpper", complexityPanels.get(i).getMaxUpper()),
                   Polylist.list("compute", complexityPanels.get(i).toCompute()),
                   Polylist.list("lowerBounds", Polylist.PolylistFromArray(complexityPanels.get(i).lowerBounds())),
                   Polylist.list("upperBounds", Polylist.PolylistFromArray(complexityPanels.get(i).upperBounds())));
           back = Polylist.cons(inner, back);
        }
        info = Polylist.cons(front, back);
        String toReturn = info.toString();
        return toReturn;
    }

    private void convertStringToComplexityWindow(String s) {
        Polylist info = Polylist.PolylistFromString(s);
        PolylistEnum itr, itr2, itr3;

        String label = "";
        Object next, next2;

        //iterate over the first polylist--the global info
        itr = new PolylistEnum((Polylist)info.first());
        next = itr.nextElement();
        if (next instanceof String && ((String)next).equals("globals")) {
            while (itr.hasMoreElements()) {
                next = itr.nextElement();
                if (next instanceof Polylist) {
                    if (((Polylist)next).first() instanceof String) {
                        if (((String)((Polylist)next).first()).equals("numValidAttrs")) {
                            numValidAttrs = (Integer)((Polylist)next).last();
                        }
                        else if(((String) ((Polylist) next).first()).equals("beatsPerBar")) {
                            beatsPerBar = (Integer)((Polylist)next).last();
                        }
                        else if(((String) ((Polylist) next).first()).equals("totalNumBeats")) {
                            totalNumBeats = (Integer)((Polylist)next).last();
                        }
                        else if(((String) ((Polylist) next).first()).equals("granularity")) {
                            attrGranularity = (Integer)((Polylist)next).last();
                        }
                        else if(((String) ((Polylist) next).first()).equals("width")) {
                            totalWidth = (Integer)((Polylist)next).last();
                        }
                        else {
                            System.out.println("Error: poorly formed soloProfile file");
                        }
                    }
                }
            }
        }
        itr = ((Polylist)info.rest()).elements(); //second half of the list, contains specific attr info
        next = itr.nextElement();
        if (next instanceof String && ((String)next).equals("specificAttrs")) {
            for (int i = 0; i<complexityPanels.size(); i++) {
                int[] lowers, uppers;
                next = itr.nextElement();
                itr2 = ((Polylist)next).elements(); //iterator for internal lists
                next2 = itr.nextElement(); //name of the panel
                while (itr2.hasMoreElements()) {
                    next2 = itr2.nextElement();
                    if (next2 instanceof Polylist) {
                        if (((Polylist)next).first() instanceof String){
                            if (((String)((Polylist)next).first()).equals("minLower")) {
                                complexityPanels.get(i).setMinLower((Integer)((Polylist)next).last());
                            }
                            if (((String)((Polylist)next).first()).equals("maxUpper")) {
                                complexityPanels.get(i).setMaxUpper((Integer)((Polylist)next).last());
                            }
                            if (((String)((Polylist)next).first()).equals("compute")) {
                                // Do not compute box was checked, check it again
                                if(!Boolean.valueOf((String)((Polylist)next).last())) {
                                    complexityPanels.get(i).noComputeBox.setSelected(true);
                                }
                            }
                            if (((String)((Polylist)next).first()).equals("lowerBounds")) {
                                //TODO: turn list into array, then at end of this method, redraw the graphs
                                itr3 = ((Polylist)((Polylist)next).last()).elements();
                                lowers = new int[((Polylist)((Polylist)next).last()).length()];
                                int j = 0;
                                while(itr3.hasMoreElements()) {
                                    lowers[j] = (Integer)itr3.nextElement();
                                }
                            }
                            if (((String)((Polylist)next).first()).equals("upperBounds")) {
                                //TODO: turn list into array, then at end of this method, redraw the graphs
                                itr3 = ((Polylist)((Polylist)next).last()).elements();
                                uppers = new int[((Polylist)((Polylist)next).last()).length()];
                                int j = 0;
                                while(itr3.hasMoreElements()) {
                                    uppers[j] = (Integer)itr3.nextElement();
                                }
                            }
                            else {

                            }
                        }
                    }
                }
                //TODO: draw this particular panel
            }
        }
    }
}
