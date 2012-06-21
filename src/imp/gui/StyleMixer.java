/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.gui;

import imp.Constants;
import imp.ImproVisor;
import imp.com.PlayScoreCommand;
import imp.data.*;
import imp.util.ErrorLog;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import polya.Polylist;

/**
 * @author Robert Keller, Caitlin Chen
 * 
 * Use of public access to elements in MIDIBeast should
 * be changed to use proper methods.
 */

@SuppressWarnings("serial")

public class StyleMixer extends javax.swing.JDialog implements Constants
{
public static final int DRUM_CHANNEL = 9;
public static final String DRUM_NAMES = "DRUMS";

Notate notate;
StyleEditor styleEditor;
RepresentativeBassRules repBassRules;
ArrayList<RepresentativeBassRules.BassPattern> selectedBassRules;

RepresentativeDrumRules repDrumRules;
ArrayList<RepresentativeDrumRules.DrumPattern> selectedDrumRules;

RepresentativeChordRules repChordRules;
ArrayList<RepresentativeChordRules.ChordPattern> selectedChordRules;

/**
 * Models for the raw and selected JLists
 */
DefaultListModel rawRulesModelBass;
DefaultListModel selectedRulesModelBass;
DefaultListModel rawRulesModelChord;
DefaultListModel selectedRulesModelChord;
DefaultListModel rawRulesModelDrum;
DefaultListModel selectedRulesModelDrum;

/**
 * minimum duration (in slots) for a note not to be counted as a rest.
 */
private int minDuration = 0;


public static final int BASS = 0;
public static final int DRUM = 1;
public static final int CHORD = 2;

/**
 * Creates new form ExtractionEditor
 */

public StyleMixer(java.awt.Frame parent, 
                        boolean modal, 
                        StyleEditor p)
  {
    super(parent, modal);
    this.styleEditor = p;
    this.notate = p.getNotate();
    
    rawRulesModelBass       = new DefaultListModel();
    selectedRulesModelBass  = new DefaultListModel();
    rawRulesModelChord      = new DefaultListModel();
    selectedRulesModelChord = new DefaultListModel();
    rawRulesModelDrum       = new DefaultListModel();
    selectedRulesModelDrum  = new DefaultListModel();

    initComponents();
    initComponents2();
    setSize(900, 425);

    SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1);
    //numberOfClustersSpinnerBass.setModel(model);

    //setPotentialParts();
    
    rawRulesModelBass.addElement("B1");
    rawRulesModelChord.addElement("X1");
    rawRulesModelDrum.addElement("(drum Ride_Cymbal_1 X4 X4 X8 X8 X4) (drum Closed_Hi-Hat R4 X4 R4 X4)(drum Acoustic_Snare R2+4 V50 X8 R8)");
  }

public void setBass()
  {
    styleMixerPanel.setBackground(Color.orange);
    //setBassDefaults();
    repBassRules = MIDIBeast.getRepBassRules();
    setBassSelectedRules();
    setBassRawRules();
  }

public void setChords()
  {
    //chordPanel.setBackground(Color.green);
    //setChordDefaults();
    repChordRules = MIDIBeast.getRepChordRules();
    setChordRawRules();
    setChordSelectedRules();
  }

public void setDrums()
  {
   // drumPanel.setBackground(Color.yellow);
   // setDrumDefaults();
    repDrumRules = MIDIBeast.getRepDrumRules();
    setDrumRawRules();
    setDrumSelectedRules();
  }

//public void setPotentialParts()
//  {
//    ArrayList<String> potentialInstruments = new ArrayList<String>();
//
//    for( int i = 0; i < MIDIBeast.allParts.size(); i++ )
//      {
//        if( MIDIBeast.allParts.get(i).getChannel() == DRUM_CHANNEL )
//          {
//            potentialInstruments.add(DRUM_NAMES);
//          }
//        else
//          {
//            potentialInstruments.add(MIDIBeast.getInstrumentForPart(i));
//          }
//      }
//    potentialInstrumentsJListBass.setListData(potentialInstruments.toArray());
//    potentialInstrumentsJListBass.setSelectedIndex(0);
//    potentialInstrumentsJListChord.setListData(potentialInstruments.toArray());
//    potentialInstrumentsJListChord.setSelectedIndex(0);
//
//  }
//
//public void setBassDefaults()
//  {
//    startBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.bassPart.getPhrase(0).getStartTime())));
//    endBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.bassPart.getPhrase(0).getEndTime())));
//  }
//
//public void setChordDefaults()
//  {
//    startBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.chordPart.getPhrase(0).getStartTime())));
//    endBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.chordPart.getPhrase(0).getEndTime())));
//  }
//
//public void setDrumDefaults()
//  {
//    startBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.drumPart.getPhrase(0).getStartTime())));
//    endBeatTextFieldBass.setText(Double.toString(Math.round(MIDIBeast.drumPart.getPhrase(0).getEndTime())));
//  }

public void setBassRawRules()
  {
    rawRulesModelBass.clear();
    ArrayList<RepresentativeBassRules.Section> sections = repBassRules.getSections();
    ArrayList<Object> rawRules = new ArrayList<Object>();
    //Add Clustered Rules
    for( int i = 0; i < sections.size(); i++ )
      {
        RepresentativeBassRules.Section currentSection = sections.get(i);
        // FIX: For some reason, getSlotCount() seems to return a value 
        // doubly multiplied by BEAT.
        int length = (currentSection.getSlotCount() / BEAT) / BEAT;
        ArrayList<RepresentativeBassRules.Cluster> clusters = currentSection.getClusters();
        for( int j = 0; j < clusters.size(); j++ )
          {
            RepresentativeBassRules.Cluster currentCluster = clusters.get(j);
            rawRules.add("---- Cluster " + j + " of length " + length + " patterns ----");
            for( int k = 0; k < currentCluster.size(); k++ )
              {
                rawRules.add(repBassRules.makeBassPatternObj(currentCluster.getStringRule(k), 1));
              }
          }
      }
    // Copy the rules to the model
    for( Object rawRule : rawRules )
      {
        rawRulesModelBass.addElement(rawRule);
      }
    rawRulesJListDrum.setModel(rawRulesModelBass);
    rawRulesJListDrum.setSelectedIndex(0);
  }

public void setChordRawRules()
  {
    rawRulesModelChord.clear();
    ArrayList<RepresentativeChordRules.Section> sections = repChordRules.getSections();
    ArrayList<Object> rawRules = new ArrayList<Object>();

    for( int i = 0; i < sections.size(); i++ )
      {
        RepresentativeChordRules.Section currentSection = sections.get(i);
        // FIX: For some reason, getSlotCount() seems to return a value 
        // doubly multiplied by BEAT.
        int length = (currentSection.getSlotCount() / BEAT) / BEAT;
        ArrayList<RepresentativeChordRules.Cluster> clusters = currentSection.getClusters();
        for( int j = 0; j < clusters.size(); j++ )
          {
            RepresentativeChordRules.Cluster currentCluster = clusters.get(j);
            rawRules.add("---- Cluster " + j + " of length " + length + " patterns ----");
            for( int k = 0; k < currentCluster.size(); k++ )
              {
                rawRules.add(repChordRules.makeChordPattern(currentCluster.getStringRule(k), 1));
              }
          }
      }

    ArrayList<String> duplicates = repChordRules.getDuplicates();
    if( duplicates.size() > 0 )
      {
        rawRules.add("Duplicates:");
        for( int i = 0; i < duplicates.size(); i++ )
          {
            rawRules.add("    " + duplicates.get(i));
          }
      }

    for( Object rawRule : rawRules )
      {
        rawRulesModelChord.addElement(rawRule);
      }
    rawRulesJListBass.setModel(rawRulesModelChord);
    rawRulesJListBass.setSelectedIndex(0);
  }

public void setDrumRawRules()
  {
    rawRulesModelDrum.clear();
    ArrayList<RepresentativeDrumRules.Cluster> clusters = repDrumRules.getClusters();
    ArrayList<Object> rawRules = new ArrayList<Object>();

    if( clusters != null )
      {
        for( int i = 1; i < clusters.size(); i++ )
          {
            RepresentativeDrumRules.Cluster cluster = clusters.get(i);

            // Need a check here for cluster emptiness.
            String[] clusterRules = cluster.getRules();
            if( clusterRules.length > 1 )
              {
                //System.out.println("clusterRules " + i + " = " + clusterRules);
                rawRules.add("---- Cluster " + i + " " + clusterRules[0]);
                for( int j = 1; j < clusterRules.length; j++ )
                  {
                    rawRules.add(makeDrumPattern(clusterRules[j] + "(weight 1))"));
                  }
              }
          }
      }

    ArrayList<String> duplicates = MIDIBeast.getRepDrumRules().getDuplicates();
    if( duplicates.size() > 0 )
      {
        rawRules.add("Duplicates");
        for( int i = 0; i < duplicates.size(); i++ )
          {
            rawRules.add(makeDrumPattern(duplicates.get(i) + "(weight 1))"));
          }
      }

    for( Object rawRule : rawRules )
      {
        rawRulesModelDrum.addElement(rawRule);
      }
    
    rawRulesJListDrum.setModel(rawRulesModelDrum);
    rawRulesJListDrum.setSelectedIndex(0);
  }

private RepresentativeDrumRules.DrumPattern makeDrumPattern(String string)
  {
    String[] split = string.split("\n");
    RepresentativeDrumRules.DrumPattern drumPattern = repDrumRules.makeDrumPattern();
    for( int i = 1; i < split.length - 1; i++ )
      {
        RepresentativeDrumRules.DrumRule drumRule = repDrumRules.makeDrumRule();
        int instrumentNumber = Integer.parseInt(split[i].substring(split[i].indexOf('m') + 2, split[i].indexOf('m') + 4));
        drumRule.setInstrumentNumber(instrumentNumber);
        int startIndex = split[i].indexOf('m') + 2;
        int endIndex = split[i].indexOf(')');
        String elements = split[i].substring(startIndex, endIndex);
        String[] split2 = elements.split(" ");
        // Start at 1 rather than 0, to skip over the drum number
        for( int j = 1; j < split2.length; j++ )
          {
            drumRule.addElement(split2[j]);
          }
        String weightString = split[split.length - 1];

        drumPattern.setWeight(1);
        //System.out.println("adding drumPattern " + drumPattern);
        drumPattern.addRule(drumRule);
      }
    return drumPattern;
  }


public void setBassSelectedRules()
  {
   selectedRulesModelBass.clear();
   selectedBassRules = repBassRules.getBassRules();
   for( RepresentativeBassRules.BassPattern selectedRule: selectedBassRules )
      {
      selectedRulesModelBass.addElement(selectedRule);
      }
    selectedRulesJListBass.setModel(selectedRulesModelBass);
    selectedRulesJListBass.setSelectedIndex(selectedBassRules.size()-1);
  }

public void setChordSelectedRules()
  {
   selectedRulesModelChord.clear();
   selectedChordRules = repChordRules.getChordRules();
   for( RepresentativeChordRules.ChordPattern selectedRule: selectedChordRules )
      {
      selectedRulesModelChord.addElement(selectedRule);
      }
    rawRulesJListChord.setModel(selectedRulesModelChord);
    rawRulesJListChord.setSelectedIndex(selectedChordRules.size()-1);
  }

public void setDrumSelectedRules()
  {
   selectedRulesModelDrum.clear();
   selectedDrumRules = repDrumRules.getRepresentativePatterns();
   for( RepresentativeDrumRules.DrumPattern selectedRule: selectedDrumRules )
      {
      selectedRulesModelDrum.addElement(selectedRule);
      }
    //selectedRulesJListDrum.setModel(selectedRulesModelDrum);
    //selectedRulesJListDrum.setSelectedIndex(selectedDrumRules.size()-1);
  }

private void initComponents2()
  {
    java.awt.GridBagConstraints gridBagConstraints;

    errorDialog = new javax.swing.JDialog();
    errorMessage = new javax.swing.JLabel();
    errorButton = new javax.swing.JButton();

    errorDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

    errorDialog.setTitle("Error");
    errorDialog.setBackground(java.awt.Color.white);
    errorMessage.setForeground(new java.awt.Color(255, 0, 51));
    errorMessage.setText("jLabel1");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
    errorDialog.getContentPane().add(errorMessage, gridBagConstraints);

    errorButton.setText("OK");
    errorButton.addActionListener(new java.awt.event.ActionListener()
    {

    public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        errorButtonActionPerformed(evt);
      }
    });
  }

private void errorButtonActionPerformed(java.awt.event.ActionEvent evt)
  {//GEN-FIRST:event_errorButtonActionPerformed
    errorDialog.setVisible(false);
  }//GEN-LAST:event_errorButtonActionPerformed


//private void checkForAndThrowErrors()
//  {
//    double endBeat;
//    double startBeat;
//    try
//      {
//        endBeat = Double.parseDouble(endBeatTextFieldBass.getText());
//        startBeat = Double.parseDouble(startBeatTextFieldBass.getText());
//      }
//    catch( Exception e )
//      {
//        errorMessage.setText("ERROR: Malformed Start/End Beat.");
//        errorDialog.setSize(250, 200);
//        errorDialog.setLocationRelativeTo(this);
//        errorDialog.setVisible(true);
//        return;
//      }
//
//    if( endBeat < 0 || startBeat < 0 )
//      {
//        errorMessage.setText("ERROR: Start/End Beats must be positive.");
//        errorDialog.setSize(250, 200);
//        errorDialog.setLocationRelativeTo(this);
//        errorDialog.setVisible(true);
//      }
//    else if( startBeat > endBeat )
//      {
//        errorMessage.setText("ERROR: Start beat must be less than end beat.");
//        errorDialog.setSize(250, 200);
//        errorDialog.setLocationRelativeTo(this);
//        errorDialog.setVisible(true);
//      }
//    else if( endBeat < startBeat )
//      {
//        errorMessage.setText("ERROR: End beat must be greater than start beat.");
//        errorDialog.setSize(250, 200);
//        errorDialog.setLocationRelativeTo(this);
//        errorDialog.setVisible(true);
//      }
//  }


/**
 * Plays a selected rule. In this case, the rules themselves are stored
 * in the JList (in contrast to playRawRule()).
 */

public void playSelectedRule(int type)
  {
    Polylist rule = null;
    int duration = 0;
    Object selected;
        switch( type )
          {
            case BASS:
                try
                  {
                    selected = selectedRulesJListBass.getSelectedValue();
                    RepresentativeBassRules.BassPattern selectedBassRule 
                            = (RepresentativeBassRules.BassPattern) selected;
                    duration = selectedBassRule.getDuration();
                    rule = Notate.parseListFromString(selectedBassRule.toString());
                    break;
                  }
                catch( Exception e )
                  {
                    e.printStackTrace();
                  }
                break;

            case CHORD:
                selected = rawRulesJListChord.getSelectedValue();
                RepresentativeChordRules.ChordPattern selectedChordRule 
                        = (RepresentativeChordRules.ChordPattern) selected;
                duration = selectedChordRule.getDuration();
                rule = Notate.parseListFromString(selectedChordRule.toString());
                break;
            
            case DRUM:
                selected = rawRulesJListDrum.getSelectedValue();
                RepresentativeDrumRules.DrumPattern selectedDrumPattern 
                        = (RepresentativeDrumRules.DrumPattern) selected;
                duration = selectedDrumPattern.getDuration();
                rule = Notate.parseListFromString(selectedDrumPattern.toString());
                break;
          }

        if( rule.isEmpty() )
          {
            ErrorLog.log(ErrorLog.WARNING, "Internal Error:"
                    + "Extraction Editor: Empty Rule");
            return;
          }

        //System.out.println("rule for style = " + rule);
        Style tempStyle = Style.makeStyle(rule);
        tempStyle.setSwing(styleEditor.getSwingValue());
        tempStyle.setAccompanimentSwing(styleEditor.getAccompanimentSwingValue());
        tempStyle.setName("extractionPattern");
        Style.setStyle("extractionPattern", tempStyle);
        // This is necessary so that the StyleListModel menu in notate is reset.
        // Without it, the contents will be emptied.
        notate.reloadStyles();
        ChordPart c = new ChordPart();
        String chord = styleEditor.getChord();
        boolean muteChord = styleEditor.isChordMuted();
        c.addChord(chord, new Double(duration).intValue());
        c.setStyle(tempStyle);

        Score s = new Score(c);
        s.setBassVolume(styleEditor.getVolume());
        if( type == CHORD )
          {
            notate.setChordVolume(styleEditor.getVolume());
          }
        else
          {
            notate.setChordVolume(0);
          }
        notate.setDrumVolume(styleEditor.getVolume());
        s.setTempo(styleEditor.getTempo());
        //s.setVolumes(notate.getMidiSynth());

        new PlayScoreCommand(s,
                             0,
                             true,
                             notate.getMidiSynth(),
                             ImproVisor.getCurrentWindow(),
                             0,
                             notate.getTransposition()).execute();
  }


/**
 * Plays a raw rule. In this case, Strings are stored in the JList and
 * rules must be created from them (in contrast to playSelectedRule()).
 */

public void playRawRule(int type)
  {
    // Needs to be reworked for the Strings in StyleMixer.
    // This was copied from StyleExtractor.
/*    
    Object rawOb;
    RepPattern repPattern;
    Polylist rule = null;
    int duration = 0;
        switch( type )
          {
            case BASS:
                {
                rawOb = rawRulesJListBass.getSelectedValue();
                if( rawOb instanceof RepresentativeBassRules.BassPattern )
                {
                repPattern = (RepPattern)rawOb;

                RepresentativeBassRules.BassPattern selectedBassRule 
                        = (RepresentativeBassRules.BassPattern) repPattern;
                duration = selectedBassRule.getDuration();
                rule = Notate.parseListFromString(selectedBassRule.toString());
                break;
                }
                }

            case CHORD:
                {
                // There should be some criterion here to mask out lines that
                // don't correspond to rules. The old way, checking for
                // parens at the start and end, is no longer relevant.

                rawOb = rawRulesJListChord.getSelectedValue();
                if( rawOb instanceof RepresentativeChordRules.ChordPattern )
                {
                repPattern = (RepPattern)rawOb;
  
                RepresentativeChordRules.ChordPattern selectedChordRule 
                        = (RepresentativeChordRules.ChordPattern) repPattern;
                duration = selectedChordRule.getDuration();
                rule = Notate.parseListFromString(selectedChordRule.toString());
                break;
                }
                }
                
            case DRUM:
                rawOb = rawRulesJListDrum.getSelectedValue();
                if( rawOb instanceof RepresentativeDrumRules.DrumPattern )
                {
                repPattern = (RepPattern)rawOb;
                  
                RepresentativeDrumRules.DrumPattern selectedDrumPattern 
                        = (RepresentativeDrumRules.DrumPattern) repPattern;
                duration = selectedDrumPattern.getDuration();
                rule = Notate.parseListFromString(selectedDrumPattern.toString());
                break;
                }
          }

        //System.out.println("rule for style = " + rule);
        Style tempStyle = Style.makeStyle(rule);
        tempStyle.setSwing(styleEditor.getSwingValue());
        tempStyle.setAccompanimentSwing(styleEditor.getAccompanimentSwingValue());
        tempStyle.setName("extractionPattern");
        Style.setStyle("extractionPattern", tempStyle);
        // This is necessary so that the StyleListModel menu in notate is reset.
        // Without it, the contents will be emptied.
        notate.reloadStyles();
        ChordPart c = new ChordPart();
        String chord = styleEditor.getChord();
        boolean muteChord = styleEditor.isChordMuted();
        c.addChord(chord, new Double(duration).intValue());
        c.setStyle(tempStyle);

        Score s = new Score(c);
        s.setBassVolume(styleEditor.getVolume());
        if( type == CHORD )
          {
            notate.setChordVolume(styleEditor.getVolume());
          }
        else
          {
            notate.setChordVolume(0);
          }
        notate.setDrumVolume(styleEditor.getVolume());
        s.setTempo(styleEditor.getTempo());
        //s.setVolumes(notate.getMidiSynth());

        new PlayScoreCommand(s,
                             0,
                             true,
                             notate.getMidiSynth(),
                             ImproVisor.getCurrentWindow(),
                             0,
                             notate.getTransposition()).execute();      
  */
  }

/**
 * This method is called from within the constructor to initialize the form.
 * WARNING: Do NOT modify this code. The content of this method is always
 * regenerated by the Form Editor.
 */
@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        selectedPatternsPanelBass = new javax.swing.JScrollPane();
        selectedRulesJListBass = new javax.swing.JList();
        popupMenu1 = new java.awt.PopupMenu();
        styleMixerPanel = new javax.swing.JPanel();
        rawPatternsPanelBass = new javax.swing.JScrollPane();
        rawRulesJListBass = new javax.swing.JList();
        playPatternBtnBass = new javax.swing.JButton();
        selectPatternBtnBass = new javax.swing.JButton();
        rawPatternsPanelChord = new javax.swing.JScrollPane();
        rawRulesJListChord = new javax.swing.JList();
        selectPatternBtnChord = new javax.swing.JButton();
        playPatternBtnChord = new javax.swing.JButton();
        rawPatternsPanelDrum = new javax.swing.JScrollPane();
        rawRulesJListDrum = new javax.swing.JList();
        selectPatternBtnDrum = new javax.swing.JButton();
        playPatternBtnDrum = new javax.swing.JButton();
        extractionEditorMenuBar = new javax.swing.JMenuBar();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        windowMenuSeparator = new javax.swing.JSeparator();

        selectedPatternsPanelBass.setBorder(javax.swing.BorderFactory.createTitledBorder("Raw Chord Patterns"));
        selectedPatternsPanelBass.setMinimumSize(new java.awt.Dimension(300, 200));
        selectedPatternsPanelBass.setPreferredSize(new java.awt.Dimension(300, 200));

        selectedRulesJListBass.setModel(selectedRulesModelBass);
        selectedRulesJListBass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedRulesJListBassselectedPatternsMouseClickedBass(evt);
            }
        });
        selectedPatternsPanelBass.setViewportView(selectedRulesJListBass);

        popupMenu1.setLabel("popupMenu1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Style Mixer");
        setMinimumSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        styleMixerPanel.setLayout(new java.awt.GridBagLayout());

        rawPatternsPanelBass.setBorder(javax.swing.BorderFactory.createTitledBorder("Bass Patterns"));
        rawPatternsPanelBass.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanelBass.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJListBass.setBackground(java.awt.Color.orange);
        rawRulesJListBass.setModel(rawRulesModelBass);
        rawRulesJListBass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawRulesJListBassrawRulesJListMouseClickedChord(evt);
            }
        });
        rawPatternsPanelBass.setViewportView(rawRulesJListBass);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.9;
        styleMixerPanel.add(rawPatternsPanelBass, gridBagConstraints);

        playPatternBtnBass.setText("Play Pattern");
        playPatternBtnBass.setPreferredSize(new java.awt.Dimension(300, 23));
        playPatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPatternBtnBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.1;
        styleMixerPanel.add(playPatternBtnBass, gridBagConstraints);

        selectPatternBtnBass.setText("Copy Bass Pattern to Style Editor");
        selectPatternBtnBass.setToolTipText("Move the selected Bass Pattern to the next column of the Style Editor.");
        selectPatternBtnBass.setPreferredSize(new java.awt.Dimension(300, 23));
        selectPatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBassPatternToStyleEditor(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        styleMixerPanel.add(selectPatternBtnBass, gridBagConstraints);

        rawPatternsPanelChord.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord Patterns"));
        rawPatternsPanelChord.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanelChord.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJListChord.setBackground(java.awt.Color.green);
        rawRulesJListChord.setModel(rawRulesModelChord);
        rawRulesJListChord.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawRulesJListChordselectedPatternsMouseClickedBass(evt);
            }
        });
        rawPatternsPanelChord.setViewportView(rawRulesJListChord);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.9;
        styleMixerPanel.add(rawPatternsPanelChord, gridBagConstraints);

        selectPatternBtnChord.setText("Copy Chord Pattern to Style Editor");
        selectPatternBtnChord.setToolTipText("Move the selected Chord Pattern to the next column of the Style Editor.");
        selectPatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyChordPatternToStyleEditor(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        styleMixerPanel.add(selectPatternBtnChord, gridBagConstraints);

        playPatternBtnChord.setText("Play Pattern");
        playPatternBtnChord.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        playPatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPatternBtnChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.1;
        styleMixerPanel.add(playPatternBtnChord, gridBagConstraints);

        rawPatternsPanelDrum.setBorder(javax.swing.BorderFactory.createTitledBorder("Drum Patterns"));
        rawPatternsPanelDrum.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanelDrum.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJListDrum.setBackground(java.awt.Color.yellow);
        rawRulesJListDrum.setModel(rawRulesModelDrum);
        rawRulesJListDrum.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawRulesJListDrumrawPatternsMouseClickedBass(evt);
            }
        });
        rawPatternsPanelDrum.setViewportView(rawRulesJListDrum);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.9;
        styleMixerPanel.add(rawPatternsPanelDrum, gridBagConstraints);

        selectPatternBtnDrum.setText("Copy Drum Pattern to Style Editor");
        selectPatternBtnDrum.setToolTipText("Move the selected Drum Pattern to the next column of the Style Editor.");
        selectPatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyDrumPatternToStyleEditor(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.1;
        styleMixerPanel.add(selectPatternBtnDrum, gridBagConstraints);

        playPatternBtnDrum.setText("Play Pattern");
        playPatternBtnDrum.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        playPatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPatternBtnDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.1;
        styleMixerPanel.add(playPatternBtnDrum, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(styleMixerPanel, gridBagConstraints);

        windowMenu.setMnemonic('W');
        windowMenu.setText("Window");
        windowMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                windowMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        closeWindowMI.setMnemonic('C');
        closeWindowMI.setText("Close Window");
        closeWindowMI.setToolTipText("Closes the current window (exits program if there are no other windows)");
        closeWindowMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMIActionPerformed(evt);
            }
        });
        windowMenu.add(closeWindowMI);

        cascadeMI.setMnemonic('A');
        cascadeMI.setText("Cascade Windows");
        cascadeMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMIActionPerformed(evt);
            }
        });
        windowMenu.add(cascadeMI);
        windowMenu.add(windowMenuSeparator);

        extractionEditorMenuBar.add(windowMenu);

        setJMenuBar(extractionEditorMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeWindowMIActionPerformed
  {//GEN-HEADEREND:event_closeWindowMIActionPerformed
    dispose();
  }//GEN-LAST:event_closeWindowMIActionPerformed

private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cascadeMIActionPerformed
  {//GEN-HEADEREND:event_cascadeMIActionPerformed
    WindowRegistry.cascadeWindows(this);
  }//GEN-LAST:event_cascadeMIActionPerformed

private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt)//GEN-FIRST:event_windowMenuMenuSelected
  {//GEN-HEADEREND:event_windowMenuMenuSelected
    windowMenu.removeAll();

    windowMenu.add(closeWindowMI);

    windowMenu.add(cascadeMI);

    windowMenu.add(windowMenuSeparator);

    for( WindowMenuItem w : WindowRegistry.getWindows() )
      {
        windowMenu.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed
      }

    windowMenu.repaint();
  }//GEN-LAST:event_windowMenuMenuSelected

    private void playPatternBtnDrumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPatternBtnDrumActionPerformed
        playSelectedRule(DRUM);
    }//GEN-LAST:event_playPatternBtnDrumActionPerformed

    private void copyDrumPatternToStyleEditor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyDrumPatternToStyleEditor
        Object selectedOb = rawRulesJListDrum.getSelectedValue();
  //System.out.println("selected " + selectedOb);
        if (selectedOb instanceof String) 
          {
            //widePatternTextField.setText(selectedOb.toString());
            styleEditor.setNextDrumPattern((String)selectedOb);
          }
    }//GEN-LAST:event_copyDrumPatternToStyleEditor

    private void playPatternBtnChordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPatternBtnChordActionPerformed
        playRawRule(CHORD);
    }//GEN-LAST:event_playPatternBtnChordActionPerformed

    private void copyChordPatternToStyleEditor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyChordPatternToStyleEditor
        Object selectedOb = rawRulesJListChord.getSelectedValue();
  //System.out.println("selected " + selectedOb);
        if (selectedOb instanceof String) 
          {
            //widePatternTextField.setText(selectedOb.toString());
            styleEditor.setNextChordPattern((String)selectedOb);
          }
    }//GEN-LAST:event_copyChordPatternToStyleEditor

    private void selectedRulesJListBassselectedPatternsMouseClickedBass(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectedRulesJListBassselectedPatternsMouseClickedBass
        Object selectedOb = selectedRulesJListBass.getSelectedValue();
        if (selectedOb instanceof RepPattern) {
            //widePatternTextField.setText(selectedOb.toString());
            playSelectedRule(BASS);
        }
    }//GEN-LAST:event_selectedRulesJListBassselectedPatternsMouseClickedBass

    private void rawRulesJListDrumrawPatternsMouseClickedBass(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rawRulesJListDrumrawPatternsMouseClickedBass
        Object selectedOb = rawRulesJListDrum.getSelectedValue();
        if (selectedOb instanceof RepPattern) {
            //widePatternTextField.setText(selectedOb.toString());
            playRawRule(BASS);
        }
    }//GEN-LAST:event_rawRulesJListDrumrawPatternsMouseClickedBass

    private void rawRulesJListChordselectedPatternsMouseClickedBass(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rawRulesJListChordselectedPatternsMouseClickedBass
        // TODO add your handling code here:
    }//GEN-LAST:event_rawRulesJListChordselectedPatternsMouseClickedBass

    private void rawRulesJListBassrawRulesJListMouseClickedChord(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rawRulesJListBassrawRulesJListMouseClickedChord
        Object selectedOb = rawRulesJListBass.getSelectedValue();
        if (selectedOb instanceof RepPattern) {
            //widePatternTextFieldChord.setText(selectedOb.toString());
            playRawRule(CHORD);
        }
    }//GEN-LAST:event_rawRulesJListBassrawRulesJListMouseClickedChord

    private void playPatternBtnBassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPatternBtnBassActionPerformed
        playRawRule(BASS);
    }//GEN-LAST:event_playPatternBtnBassActionPerformed

private void copyBassPatternToStyleEditor(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copyBassPatternToStyleEditor
  {//GEN-HEADEREND:event_copyBassPatternToStyleEditor
        Object selectedOb = rawRulesJListBass.getSelectedValue();
  //System.out.println("selected " + selectedOb);
        if (selectedOb instanceof String) 
          {
            //widePatternTextField.setText(selectedOb.toString());
            styleEditor.setNextBassPattern((String)selectedOb);
          }
  }//GEN-LAST:event_copyBassPatternToStyleEditor


    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.JMenuBar extractionEditorMenuBar;
    private javax.swing.JButton playPatternBtnBass;
    private javax.swing.JButton playPatternBtnChord;
    private javax.swing.JButton playPatternBtnDrum;
    private java.awt.PopupMenu popupMenu1;
    private javax.swing.JScrollPane rawPatternsPanelBass;
    private javax.swing.JScrollPane rawPatternsPanelChord;
    private javax.swing.JScrollPane rawPatternsPanelDrum;
    private javax.swing.JList rawRulesJListBass;
    private javax.swing.JList rawRulesJListChord;
    private javax.swing.JList rawRulesJListDrum;
    private javax.swing.JButton selectPatternBtnBass;
    private javax.swing.JButton selectPatternBtnChord;
    private javax.swing.JButton selectPatternBtnDrum;
    private javax.swing.JScrollPane selectedPatternsPanelBass;
    private javax.swing.JList selectedRulesJListBass;
    private javax.swing.JPanel styleMixerPanel;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    // End of variables declaration//GEN-END:variables

private javax.swing.JButton errorButton;
private javax.swing.JDialog errorDialog;
private javax.swing.JLabel errorMessage;

  /**
   * Override dispose so as to unregister this window first.
   */
  
  @Override
  public void dispose()
    {
    WindowRegistry.unregisterWindow(this);
    super.dispose();
    }
}
