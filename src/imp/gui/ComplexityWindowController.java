/*
 *
 */
package imp.gui;

import java.util.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

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
        return overallComplexityPanel.getWidth();
    }
    /** Takes a new granularity and redraws the graphs */
    public int updateGran(int gran) {
        attrGranularity = gran;
        for(int i = 0; i<complexityPanels.size(); i++) {
            complexityPanels.get(i).redrawGran(attrGranularity);
        }
        return overallComplexityPanel.getWidth();
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
}
