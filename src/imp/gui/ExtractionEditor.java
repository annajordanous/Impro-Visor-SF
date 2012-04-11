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
import imp.com.CommandManager;
import imp.com.PlayScoreCommand;
import imp.data.*;
import imp.util.ErrorLog;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import polya.Polylist;

/**
 * @author Robert Keller, from original code by Jim Herold
 * 
 * When there is time, this should be refactored into three separate
 * derived classes. There is no point in having all three lists: bass, chord,
 * and drums in each of three instances of this editor.
 * 
 * Before that, however, use of public access to elements in MIDIBeast should
 * be changed to use proper methods.
 */

public class ExtractionEditor extends javax.swing.JDialog implements Constants
{
Notate notate;
StyleEditor styleEditor;
CommandManager cm;
int type;
RepresentativeBassRules repBassRules;
ArrayList<RepresentativeBassRules.BassPatternObj> rawBassRules;
ArrayList<RepresentativeBassRules.BassPatternObj> selectedBassRules;

RepresentativeDrumRules repDrumRules;
ArrayList<RepresentativeDrumRules.DrumPattern> rawDrumRules;
ArrayList<RepresentativeDrumRules.DrumPattern> selectedDrumRules;

RepresentativeChordRules repChordRules;
ArrayList<RepresentativeChordRules.ChordPattern> rawChordRules;
ArrayList<RepresentativeChordRules.ChordPattern> selectedChordRules;

/**
 * Models for the raw and selected JLists
 */
DefaultListModel rawRulesModel;
DefaultListModel selectedRulesModel;

/**
 * minimum duration (in slots) for a note not to be counted as a rest.
 */
private int minDuration = 0;
public static final int BASS = 0;
public static final int DRUM = 1;
public static final int CHORD = 2;
public static final String PREVIEWCHORD = "CM";

/**
 * Creates new form ExtractionEditor
 */
public ExtractionEditor(java.awt.Frame parent, 
                        boolean modal, 
                        StyleEditor p, 
                        CommandManager cm, 
                        int type)
  {
    this(parent, modal, p, cm, type, 0);
  }

public ExtractionEditor(java.awt.Frame parent, 
                        boolean modal, 
                        StyleEditor p, 
                        CommandManager cm, 
                        int type, 
                        int minDuration)
  {
    super(parent, modal);
    this.styleEditor = p;
    this.notate = p.getNotate();
    this.cm = cm;
    this.type = type;
    this.minDuration = minDuration;
    
    rawRulesModel      = new DefaultListModel();
    selectedRulesModel = new DefaultListModel();

    initComponents();
    initComponents2();
    setSize(900, 425);

    SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1);
    numberOfClustersSpinner.setModel(model);

    setPotentialParts();
    switch( type )
      {
        case BASS:
            setTitle("Bass Extraction from " + styleEditor.getTitle());
            setBassDefaults();
            doubleDrumLength.setVisible(false);
            repBassRules = MIDIBeast.repBassRules;
            setBassSelectedRules();
            setBassRawRules();
            break;

        case CHORD:
            setTitle("Chord Extraction from " + styleEditor.getTitle());
            setChordDefaults();
            doubleDrumLength.setVisible(false);
            repChordRules = MIDIBeast.repChordRules;
            setChordRawRules();
            setChordSelectedRules();
            break;

        case DRUM:
            setTitle("Drum Extraction from " + styleEditor.getTitle());
            setDrumDefaults();
            repDrumRules = MIDIBeast.repDrumRules;
            setDrumRawRules();
            setDrumSelectedRules();
            break;
      }
  }

public void setPotentialParts()
  {
    ArrayList<String> potentialInstruments = new ArrayList<String>();
    for( int i = 0; i < MIDIBeast.allParts.size(); i++ )
      {
        if( MIDIBeast.allParts.get(i).getChannel() == 9 )
          {
            potentialInstruments.add("DRUMS");
          }
        else
          {
            potentialInstruments.add(MIDIBeast.getInstrumentName(MIDIBeast.allParts.get(i).getInstrument()));
          }
      }

    potentialInstrumentsJList.setListData(potentialInstruments.toArray());
    potentialInstrumentsJList.setSelectedIndex(0);
  }

public void setBassDefaults()
  {
    startBeatTextField.setText(Double.toString(Math.round(MIDIBeast.bassPart.getPhrase(0).getStartTime())));
    endBeatTextField.setText(Double.toString(Math.round(MIDIBeast.bassPart.getPhrase(0).getEndTime())));
  }

public void setChordDefaults()
  {
    startBeatTextField.setText(Double.toString(Math.round(MIDIBeast.chordPart.getPhrase(0).getStartTime())));
    endBeatTextField.setText(Double.toString(Math.round(MIDIBeast.chordPart.getPhrase(0).getEndTime())));
  }

public void setDrumDefaults()
  {
    startBeatTextField.setText(Double.toString(Math.round(MIDIBeast.drumPart.getPhrase(0).getStartTime())));
    endBeatTextField.setText(Double.toString(Math.round(MIDIBeast.drumPart.getPhrase(0).getEndTime())));
  }

public void setBassRawRules()
  {
    rawRulesModel.clear();
    ArrayList<RepresentativeBassRules.Section> sections = repBassRules.getSections();
    ArrayList<String> rawRules = new ArrayList<String>();
    //Add Clustered Rules
    for( int i = 0; i < sections.size(); i++ )
      {
        RepresentativeBassRules.Section currentSection = sections.get(i);
        rawRules.add("Patterns of length: " + currentSection.getSlotCount()/BEAT + " beats:");
        ArrayList<RepresentativeBassRules.Cluster> clusters = currentSection.getClusters();
        for( int j = 0; j < clusters.size(); j++ )
          {
            RepresentativeBassRules.Cluster currentCluster = clusters.get(j);
            rawRules.add("     Cluster (" + j + ")");
            for( int k = 0; k < currentCluster.size(); k++ )
              {
                rawRules.add("          " + currentCluster.getStringRule(k));
              }
          }
      }
    for( String rawRule: rawRules )
      {
      rawRulesModel.addElement(rawRule);
      }
    rawRulesJList.setModel(rawRulesModel);
    rawRulesJList.setSelectedIndex(0);
  }

public void setChordRawRules()
  {
    rawRulesModel.clear();
    ArrayList<RepresentativeChordRules.Section> sections = repChordRules.getSections();
    ArrayList<String> rawRules = new ArrayList<String>();

    for( int i = 0; i < sections.size(); i++ )
      {
        RepresentativeChordRules.Section currentSection = sections.get(i);
        rawRules.add("Patterns of length: " + currentSection.getSlotCount()/BEAT + " beats:");
        ArrayList<RepresentativeChordRules.Cluster> clusters = currentSection.getClusters();
        for( int j = 0; j < clusters.size(); j++ )
          {
            RepresentativeChordRules.Cluster currentCluster = clusters.get(j);
            rawRules.add("    Cluster(" + j + ")");
            for( int k = 0; k < currentCluster.size(); k++ )
              {
                rawRules.add("        " + currentCluster.getStringRule(k));
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
    else
      {
        rawRules.add("No Duplicates Found");
      }
    
   for( String rawRule: rawRules )
      {
      rawRulesModel.addElement(rawRule);
      }
    rawRulesJList.setModel(rawRulesModel);
    rawRulesJList.setSelectedIndex(0);
  }

public void setDrumRawRules()
  {
    rawRulesModel.clear();
    ArrayList<RepresentativeDrumRules.Cluster> clusters = repDrumRules.getClusters();
    ArrayList<String> rawRules = new ArrayList<String>();

    for( int i = 1; i < clusters.size(); i++ )
      {
        RepresentativeDrumRules.Cluster cluster = clusters.get(i);
        String[] clusterRules = cluster.getRules();
        //System.out.println("clusterRules " + i + " = " + clusterRules);
        rawRules.add(clusterRules[0]);
        for( int j = 1; j < clusterRules.length; j++ )
          {
            rawRules.add(clusterRules[j] + "(weight 1))");
          }
      }

    rawRules.add("Duplicates");
    ArrayList<String> duplicates = MIDIBeast.repDrumRules.getDuplicates();
    for( int i = 0; i < duplicates.size(); i++ )
      {
        rawRules.add(duplicates.get(i) + "(weight 1))");
      }
    
   for( String rawRule: rawRules )
      {
      rawRulesModel.addElement(rawRule);
      }
    rawRulesJList.setModel(rawRulesModel);
    rawRulesJList.setSelectedIndex(0);
  }

public void setBassSelectedRules()
  {
   selectedRulesModel.clear();
   selectedBassRules = repBassRules.getBassRules();
   for( RepresentativeBassRules.BassPatternObj selectedRule: selectedBassRules )
      {
      selectedRulesModel.addElement(selectedRule);
      }
    selectedRulesJList.setModel(selectedRulesModel);
    selectedRulesJList.setSelectedIndex(selectedBassRules.size()-1);
  }

public void setChordSelectedRules()
  {
   selectedRulesModel.clear();
   selectedChordRules = repChordRules.getChordRules();
   for( RepresentativeChordRules.ChordPattern selectedRule: selectedChordRules )
      {
      selectedRulesModel.addElement(selectedRule);
      }
    selectedRulesJList.setModel(selectedRulesModel);
    selectedRulesJList.setSelectedIndex(selectedChordRules.size()-1);
  }

public void setDrumSelectedRules()
  {
   selectedRulesModel.clear();
   selectedDrumRules = repDrumRules.getRepresentativePatterns();
   for( RepresentativeDrumRules.DrumPattern selectedRule: selectedDrumRules )
      {
      selectedRulesModel.addElement(selectedRule);
      }
    selectedRulesJList.setModel(selectedRulesModel);
    selectedRulesJList.setSelectedIndex(selectedDrumRules.size()-1);
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


private void checkForAndThrowErrors()
  {
    double endBeat;
    double startBeat;
    try
      {
        endBeat = Double.parseDouble(endBeatTextField.getText());
        startBeat = Double.parseDouble(startBeatTextField.getText());
      }
    catch( Exception e )
      {
        errorMessage.setText("ERROR: Malformed Start/End Beat.");
        errorDialog.setSize(250, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
        return;
      }

    if( endBeat < 0 || startBeat < 0 )
      {
        errorMessage.setText("ERROR: Start/End Beats must be positive.");
        errorDialog.setSize(250, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
      }
    else if( startBeat > endBeat )
      {
        errorMessage.setText("ERROR: Start beat must be less than end beat.");
        errorDialog.setSize(250, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
      }
    else if( endBeat < startBeat )
      {
        errorMessage.setText("ERROR: End beat must be greater than start beat.");
        errorDialog.setSize(250, 200);
        errorDialog.setLocationRelativeTo(this);
        errorDialog.setVisible(true);
      }
  }


/**
 * Plays a selected rule. In this case, the rules themselves are stored
 * in the JList (in contrast to playRawRule()).
 */

public void playSelectedRule()
  {
    Polylist rule = null;
    int duration = 0;
    Object selected = selectedRulesJList.getSelectedValue();
    switch( type )
      {
        case BASS:
            try
              {
                RepresentativeBassRules.BassPatternObj selectedBassRule 
                        = (RepresentativeBassRules.BassPatternObj) selected;
                duration = selectedBassRule.getDuration();
                rule = Notate.parseListFromString(selectedBassRule.toString());
                break;
              }
            catch( Exception e )
              {
                e.printStackTrace();
              }
            break;

        case DRUM:
            RepresentativeDrumRules.DrumPattern selectedDrumRule 
                    = (RepresentativeDrumRules.DrumPattern) selected;
            duration = selectedDrumRule.getDuration();
            rule = Notate.parseListFromString(selectedDrumRule.toString());
            break;

        case CHORD:
            RepresentativeChordRules.ChordPattern selectedChordRule 
                    = (RepresentativeChordRules.ChordPattern) selected;
            duration = selectedChordRule.getDuration();
            rule = Notate.parseListFromString(selectedChordRule.toString());
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

    Score s = new Score(4);
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
    s.setChordProg(c);

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

public void playRawRule()
  {
    String incompleteRule = (String) rawRulesJList.getSelectedValue();
    Polylist rule = null;
    int duration = 0;
    int firstParensIndex, lastParensIndex;
    switch( type )
      {
        case BASS:
              // There should be some criterion here to mask out lines that
              // don't correspond to rules. The old way, checking for
              // parens at the start and end, is no longer relevant.
            
            RepresentativeBassRules.BassPatternObj selectedBassRule 
                    = repBassRules.makeBassPatternObj(incompleteRule, 1);
            duration = selectedBassRule.getDuration();
            rule = Notate.parseListFromString(selectedBassRule.toString());
            break;
            
         case CHORD:
              // There should be some criterion here to mask out lines that
              // don't correspond to rules. The old way, checking for
              // parens at the start and end, is no longer relevant.
            
            RepresentativeChordRules.ChordPattern selectedChordRule 
                    = repChordRules.makeChordPattern(incompleteRule, 1);
            duration = selectedChordRule.getDuration();
            rule = Notate.parseListFromString(selectedChordRule.toString());
            break;
             
        case DRUM:
              // There should be some better criterion here to mask out lines that
              // don't correspond to rules. 

            if( incompleteRule.charAt(0) == 'C' )
              {
                return;
              }
            rule = Notate.parseListFromString(incompleteRule);
            duration = MIDIBeast.slotsPerMeasure;
            break;
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

    Score s = new Score(4);
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
    s.setChordProg(c);

    new PlayScoreCommand(s,
                         0,
                         true,
                         notate.getMidiSynth(),
                         ImproVisor.getCurrentWindow(),
                         0,
                         notate.getTransposition()).execute();
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

        rawPatternsPanel = new javax.swing.JScrollPane();
        rawRulesJList = new javax.swing.JList();
        selectedPatternsPanel = new javax.swing.JScrollPane();
        selectedRulesJList = new javax.swing.JList();
        optionPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        startBeatTextField = new javax.swing.JTextField();
        endBeatTextField = new javax.swing.JTextField();
        numberOfClustersSpinner = new javax.swing.JSpinner();
        reExtractBtn = new javax.swing.JButton();
        doubleDrumLength = new javax.swing.JCheckBox();
        partPanel = new javax.swing.JScrollPane();
        potentialInstrumentsJList = new javax.swing.JList();
        selectPatternBtn = new javax.swing.JButton();
        leftPlayPatternBtn = new javax.swing.JButton();
        removePatternBtn = new javax.swing.JButton();
        rightPlayPatternBtn = new javax.swing.JButton();
        moveSelectionsBtn = new javax.swing.JButton();
        dismissSelectionsBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        rawPatternsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Raw Patterns"));
        rawPatternsPanel.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanel.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJList.setModel(selectedRulesModel);
        rawRulesJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawPatternsMouseClicked(evt);
            }
        });
        rawPatternsPanel.setViewportView(rawRulesJList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 77;
        gridBagConstraints.ipady = 77;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(rawPatternsPanel, gridBagConstraints);

        selectedPatternsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Patterns"));
        selectedPatternsPanel.setMinimumSize(new java.awt.Dimension(300, 200));
        selectedPatternsPanel.setPreferredSize(new java.awt.Dimension(300, 200));

        selectedRulesJList.setModel(selectedRulesModel);
        selectedRulesJList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectedPatternsMouseClicked(evt);
            }
        });
        selectedPatternsPanel.setViewportView(selectedRulesJList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 77;
        gridBagConstraints.ipady = 77;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(selectedPatternsPanel, gridBagConstraints);

        optionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Re-Extraction Options"));
        optionPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Maximum Number of Clusters: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        optionPanel.add(jLabel1, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Start Beat: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        optionPanel.add(jLabel2, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("End Beat: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        optionPanel.add(jLabel3, gridBagConstraints);

        startBeatTextField.setText("8.0");
        startBeatTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBeatTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        optionPanel.add(startBeatTextField, gridBagConstraints);

        endBeatTextField.setText(" ");
        endBeatTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endBeatTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        optionPanel.add(endBeatTextField, gridBagConstraints);

        numberOfClustersSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 99, 1));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        optionPanel.add(numberOfClustersSpinner, gridBagConstraints);

        reExtractBtn.setText("Re-Extract Patterns");
        reExtractBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reExtractBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        optionPanel.add(reExtractBtn, gridBagConstraints);

        doubleDrumLength.setText("Double Drum Length");
        doubleDrumLength.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doubleDrumLengthActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        optionPanel.add(doubleDrumLength, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.2;
        getContentPane().add(optionPanel, gridBagConstraints);

        partPanel.setMinimumSize(new java.awt.Dimension(200, 150));
        partPanel.setPreferredSize(new java.awt.Dimension(200, 150));

        potentialInstrumentsJList.setBorder(javax.swing.BorderFactory.createTitledBorder("Instrument"));
        potentialInstrumentsJList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        partPanel.setViewportView(potentialInstrumentsJList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 77;
        gridBagConstraints.ipady = 77;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(partPanel, gridBagConstraints);

        selectPatternBtn.setText("Select Pattern");
        selectPatternBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPatternBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        getContentPane().add(selectPatternBtn, gridBagConstraints);

        leftPlayPatternBtn.setText("Play Pattern");
        leftPlayPatternBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftPlayPatternBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(leftPlayPatternBtn, gridBagConstraints);

        removePatternBtn.setText("Remove Pattern");
        removePatternBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePatternBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        getContentPane().add(removePatternBtn, gridBagConstraints);

        rightPlayPatternBtn.setText("Play Pattern");
        rightPlayPatternBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightPlayPatternBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(rightPlayPatternBtn, gridBagConstraints);

        moveSelectionsBtn.setText("Move Selections to Style & Close");
        moveSelectionsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveSelectionsBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        getContentPane().add(moveSelectionsBtn, gridBagConstraints);

        dismissSelectionsBtn.setText("Dismiss Selections & Close");
        dismissSelectionsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissSelectionsBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(dismissSelectionsBtn, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void leftPlayPatternBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_leftPlayPatternBtnActionPerformed
  {//GEN-HEADEREND:event_leftPlayPatternBtnActionPerformed
      playRawRule();
  }//GEN-LAST:event_leftPlayPatternBtnActionPerformed

private void rightPlayPatternBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rightPlayPatternBtnActionPerformed
  {//GEN-HEADEREND:event_rightPlayPatternBtnActionPerformed
      playSelectedRule();
  }//GEN-LAST:event_rightPlayPatternBtnActionPerformed

private void dismissSelectionsBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_dismissSelectionsBtnActionPerformed
  {//GEN-HEADEREND:event_dismissSelectionsBtnActionPerformed
      this.setVisible(false);
  }//GEN-LAST:event_dismissSelectionsBtnActionPerformed

private void moveSelectionsBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_moveSelectionsBtnActionPerformed
  {//GEN-HEADEREND:event_moveSelectionsBtnActionPerformed
      switch( type )
        {
          case BASS:
              MIDIBeast.selectedBassRules = selectedBassRules;
              styleEditor.loadBassPatterns(MIDIBeast.repBassRules.getBassRules());
              break;
              
          case CHORD:
              MIDIBeast.selectedChordRules = selectedChordRules;
              styleEditor.loadChordPatterns(MIDIBeast.repChordRules.getChordRules());
              break;
              
          case DRUM:
              MIDIBeast.selectedDrumRules = selectedDrumRules;
              styleEditor.loadDrumPatterns(MIDIBeast.repDrumRules.getRepresentativePatterns());
              break;
        }
      this.setVisible(false);
  }//GEN-LAST:event_moveSelectionsBtnActionPerformed

private void startBeatTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startBeatTextFieldActionPerformed
  {//GEN-HEADEREND:event_startBeatTextFieldActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_startBeatTextFieldActionPerformed

private void selectPatternBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_selectPatternBtnActionPerformed
  {//GEN-HEADEREND:event_selectPatternBtnActionPerformed
      String incompleteRule = (String) rawRulesJList.getSelectedValue();
      int firstParensIndex, lastParensIndex;
   System.out.println("selected rule " + incompleteRule);

      switch( type )
        {
          case BASS:
              // There should be some criterion here to mask out lines that
              // don't correspond to rules. The old way, checking for
              // parens at the start and end, is no longer relevant.

              RepresentativeBassRules.BassPatternObj selectedBassRule 
                     = repBassRules.makeBassPatternObj(incompleteRule, 1);

              selectedBassRules.add(selectedBassRule);
              setBassSelectedRules();
              rawRulesModel.removeElement(incompleteRule);
              break;
               
          case CHORD:
              // There should be some criterion here to mask out lines that
              // don't correspond to rules. The old way, checking for
              // parens at the start and end, is no longer relevant.

              RepresentativeChordRules.ChordPattern selectedChordRule 
                      = repChordRules.makeChordPattern(incompleteRule, 1);

              selectedChordRules.add(selectedChordRule);
              setChordSelectedRules();
              rawRulesModel.removeElement(incompleteRule);
              break;
          
          case DRUM:
              // There should be some better criterion here to mask out lines that
              // don't correspond to rules. 

              if( incompleteRule.charAt(0) == 'C' )
                {
                  return;
                }
              String[] split = incompleteRule.split("\n");
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
                  for( int j = 0; j < split2.length; j++ )
                    {
                      drumRule.addElement(split2[j]);
                    }
                  String weightString = split[split.length - 1];

                  drumPattern.setWeight(1);
                  drumPattern.addRule(drumRule);
                }
              selectedDrumRules.add(drumPattern);
              setDrumSelectedRules();
              rawRulesModel.removeElement(incompleteRule);
              break;
        }

  }//GEN-LAST:event_selectPatternBtnActionPerformed

private void doubleDrumLengthActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_doubleDrumLengthActionPerformed
  {//GEN-HEADEREND:event_doubleDrumLengthActionPerformed
      if( doubleDrumLength.isSelected() )
        {
          MIDIBeast.drumMeasureSize *= 2;
        }
      else
        {
          MIDIBeast.drumMeasureSize = MIDIBeast.slotsPerMeasure;
        }
  }//GEN-LAST:event_doubleDrumLengthActionPerformed

private void selectedPatternsMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_selectedPatternsMouseClicked
  {//GEN-HEADEREND:event_selectedPatternsMouseClicked
      if( evt.getClickCount() != 2 )
        {
          return;
        }
      playSelectedRule();
  }//GEN-LAST:event_selectedPatternsMouseClicked

private void rawPatternsMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_rawPatternsMouseClicked
  {//GEN-HEADEREND:event_rawPatternsMouseClicked
      if( evt.getClickCount() != 2 )
        {
          return;
        }
      playRawRule();
  }//GEN-LAST:event_rawPatternsMouseClicked

private void removePatternBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removePatternBtnActionPerformed
  {//GEN-HEADEREND:event_removePatternBtnActionPerformed
    int indexOfRuleToBeRemoved = selectedRulesJList.getSelectedIndex();
    switch( type )
      {
        case BASS:
            selectedBassRules.remove(indexOfRuleToBeRemoved);
            selectedRulesJList.setListData(selectedBassRules.toArray());
            break;
 
        case CHORD:
            selectedChordRules.remove(indexOfRuleToBeRemoved);
            selectedRulesJList.setListData(selectedChordRules.toArray());
            break;
            
        case DRUM:
            selectedDrumRules.remove(indexOfRuleToBeRemoved);
            selectedRulesJList.setListData(selectedDrumRules.toArray());
            break;
      }
    selectedRulesJList.setSelectedIndex(0);
  }//GEN-LAST:event_removePatternBtnActionPerformed

private void reExtractBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_reExtractBtnActionPerformed
  {//GEN-HEADEREND:event_reExtractBtnActionPerformed
      checkForAndThrowErrors();

      double endBeat = Double.parseDouble(endBeatTextField.getText());
      double startBeat = Double.parseDouble(startBeatTextField.getText());

      Integer maxNumberOfClusters = (Integer) numberOfClustersSpinner.getValue();
      switch( type )
        {
          case BASS:
              int selectedBassIndex = potentialInstrumentsJList.getSelectedIndex();
              jm.music.data.Part selectedBassPart = MIDIBeast.allParts.get(selectedBassIndex); //Implement part selection
              MIDIBeast.repBassRules = 
                      new RepresentativeBassRules(startBeat, 
                                                  endBeat, 
                                                  maxNumberOfClusters, 
                                                  selectedBassPart);
              repBassRules = MIDIBeast.repBassRules;
              setBassRawRules();
              setBassSelectedRules();
              break;
 
          case CHORD:
              int selectedChordIndex = potentialInstrumentsJList.getSelectedIndex();
              jm.music.data.Part selectedChordPart = MIDIBeast.allParts.get(selectedChordIndex);
              MIDIBeast.repChordRules = 
                      new RepresentativeChordRules(startBeat, 
                                                   endBeat, 
                                                   maxNumberOfClusters, 
                                                   selectedChordPart, 
                                                   minDuration);
              repChordRules = MIDIBeast.repChordRules;
              setChordRawRules();
              setChordSelectedRules();
              break;
              
         case DRUM:
              int selectedDrumIndex = potentialInstrumentsJList.getSelectedIndex();
              jm.music.data.Part selectedDrumPart = MIDIBeast.allParts.get(selectedDrumIndex);
              MIDIBeast.repDrumRules = 
                      new RepresentativeDrumRules(startBeat, 
                                                  endBeat, 
                                                  maxNumberOfClusters, 
                                                  selectedDrumPart);
              repDrumRules = MIDIBeast.repDrumRules;
              setDrumRawRules();
              setDrumSelectedRules();
              break;
        }
  }//GEN-LAST:event_reExtractBtnActionPerformed

private void endBeatTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_endBeatTextFieldActionPerformed
  {//GEN-HEADEREND:event_endBeatTextFieldActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_endBeatTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton dismissSelectionsBtn;
    private javax.swing.JCheckBox doubleDrumLength;
    private javax.swing.JTextField endBeatTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JButton leftPlayPatternBtn;
    private javax.swing.JButton moveSelectionsBtn;
    private javax.swing.JSpinner numberOfClustersSpinner;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JScrollPane partPanel;
    private javax.swing.JList potentialInstrumentsJList;
    private javax.swing.JScrollPane rawPatternsPanel;
    private javax.swing.JList rawRulesJList;
    private javax.swing.JButton reExtractBtn;
    private javax.swing.JButton removePatternBtn;
    private javax.swing.JButton rightPlayPatternBtn;
    private javax.swing.JButton selectPatternBtn;
    private javax.swing.JScrollPane selectedPatternsPanel;
    private javax.swing.JList selectedRulesJList;
    private javax.swing.JTextField startBeatTextField;
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
