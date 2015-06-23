/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College XML export code
 * is also Copyright (C) 2009-2010 Nicolas Froment (aka Lasconic).
 *
 * Impro-Visor is free software; you can redistribute it and/or modifyc it under
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
import imp.com.PlayScoreCommand;
import imp.data.Chord;
import imp.data.GuideLineGenerator;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.NoteSymbol;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import imp.data.PianoKey;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import polya.Polylist;

/**
 * Display that lets the user control the options for generating a guide 
 * tone line.
 * @author Mikayla Konst and Carli Lessard
 */
public class GuideToneLineDialog extends javax.swing.JDialog implements Constants {
    
    private PianoKey lowKey;
    private PianoKey highKey;
    
    private final int THREE_SEVEN = 0;
    private final int SEVEN_THREE = 1;
    private final int FIVE_NINE = 2;
    private final int NINE_FIVE = 3;
    
    
    private int keysPressed = 0;
    //Images from VoicingKeyboard.java
    /**
    * Getting the piano key images.
    */
   public javax.swing.ImageIcon whiteKey = 
       new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/whitekey.jpg"));

   public javax.swing.ImageIcon whiteKeyPressed = 
       new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/whitekeypressed.jpg"));

   public javax.swing.ImageIcon blackKey = 
       new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/blackkey.jpg"));

   public javax.swing.ImageIcon blackKeyPressed = 
       new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/blackkeypressed.jpg"));

   public javax.swing.ImageIcon bassKey = 
       new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/rootkey.jpg"));

   public javax.swing.ImageIcon bassKeyPressed = 
       new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/rootkeypressed.jpg"));

   public javax.swing.ImageIcon blackBassKey = 
       new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/blackrootkey.jpg"));

   public javax.swing.ImageIcon blackBassKeyPressed = 
       new javax.swing.ImageIcon(
           getClass().getResource("/imp/gui/graphics/blackrootkeypressed.jpg"));
    
    //Constants from VoicingKeyboard.java
    public boolean playback = false;    // true if keyboard is in playback mode
    public final int WKWIDTH = 20;      // width of a white key
    public final int WKHEIGHT = 120;    // height of a white key
    public final int BKHEIGHT = 80;     // height of a black key
    public final int BKWIDTH = 14;      // width of a black key
    public final int OCTKEYS = 7;       // 7 white keys per octave
    public final int A = 21;            // MIDI value of 1st key on keyboard
    public final int P_OCTAVE = 12;     // 12 notes per octave
    public final int OCTAVE = 140;      // width of an octave
    
    /**
 * the array of PianoKeys for this Keyboard
 */
    
public PianoKey[] pkeys;
    
/**
 * Initialize all keys.
 */

/**
 * pressKey changes the images of the keys based on whether they have been
 * pressed or not.
 * 
 * @param keyPlayed
 */
private void pressKey(PianoKey keyPlayed)
{
    JLabel label = keyPlayed.getLabel();
    Icon onIcon = keyPlayed.getOnIcon();
    Icon offIcon = keyPlayed.getOffIcon();
    Icon rootIcon = keyPlayed.getBassIcon();
    Icon rootIconOn = keyPlayed.getBassOnIcon();

    if (keyPlayed.isPressed()) 
    {
        if (!keyPlayed.isBass()) 
        {
            label.setIcon(onIcon);
        }
        //else 
        //{
        //    label.setIcon(onIcon);
        //}
    }
    else if (!keyPlayed.isPressed()) 
    {
        if (!keyPlayed.isBass()) 
        {
            label.setIcon(offIcon);
        }
        //else 
        //{
        //    label.setIcon(offIcon);
        //}
    }
    forcePaint();
}

/**
 * Force painting the window, without waiting for repaint to do it,
 * as repaints may be queued when the calling application sleeps.
 */

private void forcePaint()
{
  paint(getGraphics());
}

private void initKeys()
{
    pkeys = new PianoKey[88];
    // 0th octave keys
    pkeys[0] = new PianoKey(21, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA0);
    pkeys[1] = new PianoKey(22, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb0);
    pkeys[2] = new PianoKey(23, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB0);
    
    // 1st octave keys
    pkeys[3] = new PianoKey(24, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC1);
    pkeys[4] = new PianoKey(25, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp1);
    pkeys[5] = new PianoKey(26, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD1);
    pkeys[6] = new PianoKey(27, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb1);
    pkeys[7] = new PianoKey(28, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE1);
    pkeys[8] = new PianoKey(29, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF1);
    pkeys[9] = new PianoKey(30, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp1);
    pkeys[10] = new PianoKey(31, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG1);
    pkeys[11] = new PianoKey(32, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp1);
    pkeys[12] = new PianoKey(33, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA1);
    pkeys[13] = new PianoKey(34, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb1);
    pkeys[14] = new PianoKey(35, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB1);
    
    // 2nd octave keys
    pkeys[15] = new PianoKey(36, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC2);
    pkeys[16] = new PianoKey(37, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp2);
    pkeys[17] = new PianoKey(38, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD2);
    pkeys[18] = new PianoKey(39, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb2);
    pkeys[19] = new PianoKey(40, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE2);
    pkeys[20] = new PianoKey(41, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF2);
    pkeys[21] = new PianoKey(42, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp2);
    pkeys[22] = new PianoKey(43, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG2);
    pkeys[23] = new PianoKey(44, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp2);
    pkeys[24] = new PianoKey(45, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA2);
    pkeys[25] = new PianoKey(46, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb2);
    pkeys[26] = new PianoKey(47, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB2);
    
    // 3rd octave keys
    pkeys[27] = new PianoKey(48, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC3);
    pkeys[28] = new PianoKey(49, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp3);
    pkeys[29] = new PianoKey(50, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD3);
    pkeys[30] = new PianoKey(51, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb3);
    pkeys[31] = new PianoKey(52, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE3);
    pkeys[32] = new PianoKey(53, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF3);
    pkeys[33] = new PianoKey(54, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp3);
    pkeys[34] = new PianoKey(55, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG3);
    pkeys[35] = new PianoKey(56, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp3);
    pkeys[36] = new PianoKey(57, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA3);
    pkeys[37] = new PianoKey(58, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb3);
    pkeys[38] = new PianoKey(59, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB3);
    
    // 4th octave keys
    pkeys[39] = new PianoKey(60, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC4);
    pkeys[40] = new PianoKey(61, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp4);
    pkeys[41] = new PianoKey(62, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD4);
    pkeys[42] = new PianoKey(63, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb4);
    pkeys[43] = new PianoKey(64, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE4);
    pkeys[44] = new PianoKey(65, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF4);
    pkeys[45] = new PianoKey(66, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp4);
    pkeys[46] = new PianoKey(67, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG4);
    pkeys[47] = new PianoKey(68, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp4);
    pkeys[48] = new PianoKey(69, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA4);
    pkeys[49] = new PianoKey(70, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb4);
    pkeys[50] = new PianoKey(71, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB4);
    
    // 5th octave keys
    pkeys[51] = new PianoKey(72, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC5);
    pkeys[52] = new PianoKey(73, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp5);
    pkeys[53] = new PianoKey(74, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD5);
    pkeys[54] = new PianoKey(75, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb5);
    pkeys[55] = new PianoKey(76, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE5);
    pkeys[56] = new PianoKey(77, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF5);
    pkeys[57] = new PianoKey(78, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp5);
    pkeys[58] = new PianoKey(79, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG5);
    pkeys[59] = new PianoKey(80, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp5);
    pkeys[60] = new PianoKey(81, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA5);
    pkeys[61] = new PianoKey(82, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb5);
    pkeys[62] = new PianoKey(83, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB5);
    
    // 6th octave keys
    pkeys[63] = new PianoKey(84, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC6);
    pkeys[64] = new PianoKey(85, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp6);
    pkeys[65] = new PianoKey(86, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD6);
    pkeys[66] = new PianoKey(87, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb6);
    pkeys[67] = new PianoKey(88, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE6);
    pkeys[68] = new PianoKey(89, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF6);
    pkeys[69] = new PianoKey(90, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp6);
    pkeys[70] = new PianoKey(91, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG6);
    pkeys[71] = new PianoKey(92, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp6);
    pkeys[72] = new PianoKey(93, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA6);
    pkeys[73] = new PianoKey(94, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb6);
    pkeys[74] = new PianoKey(95, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB6);
    
    // 7th octave keys
    pkeys[75] = new PianoKey(96, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC7);
    pkeys[76] = new PianoKey(97, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyCsharp7);
    pkeys[77] = new PianoKey(98, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyD7);
    pkeys[78] = new PianoKey(99, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyEb7);
    pkeys[79] = new PianoKey(100, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyE7);
    pkeys[80] = new PianoKey(101, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyF7);
    pkeys[81] = new PianoKey(102, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyFsharp7);
    pkeys[82] = new PianoKey(103, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyG7);
    pkeys[83] = new PianoKey(104, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyGsharp7);
    pkeys[84] = new PianoKey(105, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyA7);
    pkeys[85] = new PianoKey(106, blackKeyPressed, blackKey, blackBassKey, blackBassKeyPressed, keyBb7);
    pkeys[86] = new PianoKey(107, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyB7);
    
    // 8th octave keys
    pkeys[87] = new PianoKey(108, whiteKeyPressed, whiteKey, bassKey, bassKeyPressed, keyC8);
    
    for(int i = 0; i < 24; ++i){
        pkeys[i].setIsBass(true);
        Icon rootOnIcon = pkeys[i].getBassOnIcon();
        pkeys[i].getLabel().setIcon(rootOnIcon);
}

    for(int i = 76; i < 88; ++i){
        pkeys[i].setIsBass(true);
        Icon rootOnIcon = pkeys[i].getBassOnIcon();
        pkeys[i].getLabel().setIcon(rootOnIcon);
    }
}

    public PianoKey[] pianoKeys() {
        return pkeys;
    }
    
    private final Notate notate;
    private final TransformPanel transformationPanel;
    
    private Boolean transformed = false;

    /**
     * Creates new form GuideToneLineDialog
     * @param parent Frame that spawned this dialog box
     * @param modal true if user cannot access main window until dialog box is closed, false otherwise
     */
    public GuideToneLineDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle("Generate Guide Tone Line");
        this.setResizable(true);
        notate = (Notate)this.getParent();
        transformationPanel = notate.lickgenFrame.getTransformPanel();
        initComponents();
        initKeys();
        enableButtons(lineTypeButtons, false);
        updateButtons();
        clearKeyboard();
        
        lowKey = pkeys[C4-A];
        lowKey.setPressed(true);
        pressKey(lowKey);
        keysPressed++;


        highKey = pkeys[G5-A];
        highKey.setPressed(true);
        pressKey(highKey);
        keysPressed++;
        
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

        directionButtons = new javax.swing.ButtonGroup();
        numberOfLinesButtons = new javax.swing.ButtonGroup();
        scaleDegreeButtons = new javax.swing.ButtonGroup();
        maxDurationButtons = new javax.swing.ButtonGroup();
        lineTypeButtons = new javax.swing.ButtonGroup();
        linesPanel = new javax.swing.JPanel();
        numberOfLinesLabel = new javax.swing.JLabel();
        oneLine = new javax.swing.JRadioButton();
        twoLines = new javax.swing.JRadioButton();
        directionPanel = new javax.swing.JPanel();
        directionLabel = new javax.swing.JLabel();
        descending = new javax.swing.JRadioButton();
        noPreference = new javax.swing.JRadioButton();
        ascending = new javax.swing.JRadioButton();
        scaleDegPanel = new javax.swing.JPanel();
        scaleDegLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        generateLine = new javax.swing.JButton();
        leftFiller = new javax.swing.Box.Filler(new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20));
        rightFiller = new javax.swing.Box.Filler(new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20));
        transformPanel = new javax.swing.JPanel();
        transformLine = new javax.swing.JButton();
        revertLine = new javax.swing.JButton();
        maxDurationPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        noPref = new javax.swing.JRadioButton();
        whole = new javax.swing.JRadioButton();
        half = new javax.swing.JRadioButton();
        quarter = new javax.swing.JRadioButton();
        lineTypePanel = new javax.swing.JPanel();
        lineTypeLabel = new javax.swing.JLabel();
        threeSeven = new javax.swing.JRadioButton();
        sevenThree = new javax.swing.JRadioButton();
        fiveNine = new javax.swing.JRadioButton();
        nineFive = new javax.swing.JRadioButton();
        bottomFiller = new javax.swing.Box.Filler(new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20));
        topFiller = new javax.swing.Box.Filler(new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20));
        rangePanel = new javax.swing.JPanel();
        keyboardLP = new javax.swing.JLayeredPane();
        keyA0 = new javax.swing.JLabel();
        keyB0 = new javax.swing.JLabel();
        keyC1 = new javax.swing.JLabel();
        keyD1 = new javax.swing.JLabel();
        keyE1 = new javax.swing.JLabel();
        keyF1 = new javax.swing.JLabel();
        keyG1 = new javax.swing.JLabel();
        keyA1 = new javax.swing.JLabel();
        keyB1 = new javax.swing.JLabel();
        keyC2 = new javax.swing.JLabel();
        keyD2 = new javax.swing.JLabel();
        keyE2 = new javax.swing.JLabel();
        keyF2 = new javax.swing.JLabel();
        keyG2 = new javax.swing.JLabel();
        keyA2 = new javax.swing.JLabel();
        keyB2 = new javax.swing.JLabel();
        keyC3 = new javax.swing.JLabel();
        keyD3 = new javax.swing.JLabel();
        keyE3 = new javax.swing.JLabel();
        keyF3 = new javax.swing.JLabel();
        keyG3 = new javax.swing.JLabel();
        keyA3 = new javax.swing.JLabel();
        keyB3 = new javax.swing.JLabel();
        keyC4 = new javax.swing.JLabel();
        keyD4 = new javax.swing.JLabel();
        keyE4 = new javax.swing.JLabel();
        keyF4 = new javax.swing.JLabel();
        keyG4 = new javax.swing.JLabel();
        keyA4 = new javax.swing.JLabel();
        keyB4 = new javax.swing.JLabel();
        keyC5 = new javax.swing.JLabel();
        keyD5 = new javax.swing.JLabel();
        keyE5 = new javax.swing.JLabel();
        keyF5 = new javax.swing.JLabel();
        keyG5 = new javax.swing.JLabel();
        keyA5 = new javax.swing.JLabel();
        keyB5 = new javax.swing.JLabel();
        keyC6 = new javax.swing.JLabel();
        keyD6 = new javax.swing.JLabel();
        keyE6 = new javax.swing.JLabel();
        keyF6 = new javax.swing.JLabel();
        keyG6 = new javax.swing.JLabel();
        keyA6 = new javax.swing.JLabel();
        keyB6 = new javax.swing.JLabel();
        keyC7 = new javax.swing.JLabel();
        keyD7 = new javax.swing.JLabel();
        keyE7 = new javax.swing.JLabel();
        keyF7 = new javax.swing.JLabel();
        keyG7 = new javax.swing.JLabel();
        keyA7 = new javax.swing.JLabel();
        keyB7 = new javax.swing.JLabel();
        keyC8 = new javax.swing.JLabel();
        keyBb0 = new javax.swing.JLabel();
        keyCsharp1 = new javax.swing.JLabel();
        keyEb1 = new javax.swing.JLabel();
        keyFsharp1 = new javax.swing.JLabel();
        keyGsharp1 = new javax.swing.JLabel();
        keyBb1 = new javax.swing.JLabel();
        keyCsharp2 = new javax.swing.JLabel();
        keyEb2 = new javax.swing.JLabel();
        keyFsharp2 = new javax.swing.JLabel();
        keyGsharp2 = new javax.swing.JLabel();
        keyBb2 = new javax.swing.JLabel();
        keyCsharp3 = new javax.swing.JLabel();
        keyEb3 = new javax.swing.JLabel();
        keyFsharp3 = new javax.swing.JLabel();
        keyGsharp3 = new javax.swing.JLabel();
        keyBb3 = new javax.swing.JLabel();
        keyCsharp4 = new javax.swing.JLabel();
        keyEb4 = new javax.swing.JLabel();
        keyFsharp4 = new javax.swing.JLabel();
        keyGsharp4 = new javax.swing.JLabel();
        keyBb4 = new javax.swing.JLabel();
        keyCsharp5 = new javax.swing.JLabel();
        keyEb5 = new javax.swing.JLabel();
        keyFsharp5 = new javax.swing.JLabel();
        keyGsharp5 = new javax.swing.JLabel();
        keyBb5 = new javax.swing.JLabel();
        keyCsharp6 = new javax.swing.JLabel();
        keyEb6 = new javax.swing.JLabel();
        keyFsharp6 = new javax.swing.JLabel();
        keyGsharp6 = new javax.swing.JLabel();
        keyBb6 = new javax.swing.JLabel();
        keyCsharp7 = new javax.swing.JLabel();
        keyEb7 = new javax.swing.JLabel();
        keyFsharp7 = new javax.swing.JLabel();
        keyGsharp7 = new javax.swing.JLabel();
        keyBb7 = new javax.swing.JLabel();
        pointerC4 = new javax.swing.JLabel();
        rangeLabelPanel = new javax.swing.JPanel();
        rangeLabel = new javax.swing.JLabel();
        playPanel = new javax.swing.JPanel();
        playButton = new javax.swing.JButton();
        allowColorPanel = new javax.swing.JPanel();
        allowColorBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1150, 500));
        setPreferredSize(new java.awt.Dimension(1150, 500));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        linesPanel.setLayout(new java.awt.GridBagLayout());

        numberOfLinesLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        numberOfLinesLabel.setText("Number of Lines:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        linesPanel.add(numberOfLinesLabel, gridBagConstraints);

        numberOfLinesButtons.add(oneLine);
        oneLine.setSelected(true);
        oneLine.setText("One Line");
        oneLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneLineActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        linesPanel.add(oneLine, gridBagConstraints);

        numberOfLinesButtons.add(twoLines);
        twoLines.setText("Two Lines");
        twoLines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoLinesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        linesPanel.add(twoLines, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        getContentPane().add(linesPanel, gridBagConstraints);

        directionPanel.setLayout(new java.awt.GridBagLayout());

        directionLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        directionLabel.setText("Direction:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        directionPanel.add(directionLabel, gridBagConstraints);

        directionButtons.add(descending);
        descending.setText("Descending");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        directionPanel.add(descending, gridBagConstraints);

        directionButtons.add(noPreference);
        noPreference.setSelected(true);
        noPreference.setText("No Preference");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        directionPanel.add(noPreference, gridBagConstraints);

        directionButtons.add(ascending);
        ascending.setText("Ascending");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        directionPanel.add(ascending, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        getContentPane().add(directionPanel, gridBagConstraints);

        scaleDegPanel.setLayout(new java.awt.GridBagLayout());

        scaleDegLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        scaleDegLabel.setText("Start on Scale Degree:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        scaleDegPanel.add(scaleDegLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        getContentPane().add(scaleDegPanel, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        generateLine.setText("Generate Guide Tone Line");
        generateLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateLineActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        buttonPanel.add(generateLine, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        getContentPane().add(buttonPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        getContentPane().add(leftFiller, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        getContentPane().add(rightFiller, gridBagConstraints);

        transformLine.setText("Generate Solo Over Line");
        transformLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transformLineActionPerformed(evt);
            }
        });
        transformPanel.add(transformLine);

        revertLine.setText("Restore Guide Tone Line");
        revertLine.setToolTipText("");
        revertLine.setEnabled(false);
        revertLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertLineActionPerformed(evt);
            }
        });
        transformPanel.add(revertLine);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        getContentPane().add(transformPanel, gridBagConstraints);

        maxDurationPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Max Note Duration:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        maxDurationPanel.add(jLabel1, gridBagConstraints);

        maxDurationButtons.add(noPref);
        noPref.setSelected(true);
        noPref.setText("No Preference");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        maxDurationPanel.add(noPref, gridBagConstraints);

        maxDurationButtons.add(whole);
        whole.setText("Whole");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        maxDurationPanel.add(whole, gridBagConstraints);

        maxDurationButtons.add(half);
        half.setText("Half");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        maxDurationPanel.add(half, gridBagConstraints);

        maxDurationButtons.add(quarter);
        quarter.setText("Quarter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        maxDurationPanel.add(quarter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        getContentPane().add(maxDurationPanel, gridBagConstraints);

        lineTypeLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lineTypeLabel.setText("Line Type:");
        lineTypePanel.add(lineTypeLabel);

        lineTypeButtons.add(threeSeven);
        threeSeven.setSelected(true);
        threeSeven.setText("3-7 line");
        lineTypePanel.add(threeSeven);

        lineTypeButtons.add(sevenThree);
        sevenThree.setText("7-3 line");
        lineTypePanel.add(sevenThree);

        lineTypeButtons.add(fiveNine);
        fiveNine.setText("5-9 line");
        lineTypePanel.add(fiveNine);

        lineTypeButtons.add(nineFive);
        nineFive.setText("9-5 line");
        lineTypePanel.add(nineFive);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        getContentPane().add(lineTypePanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        getContentPane().add(bottomFiller, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        getContentPane().add(topFiller, gridBagConstraints);

        rangePanel.setLayout(new java.awt.GridBagLayout());

        keyboardLP.setMinimumSize(new java.awt.Dimension(1045, 150));
        keyboardLP.setRequestFocusEnabled(false);
        keyboardLP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                keyboardLPMouseClicked(evt);
            }
        });

        keyA0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA0);
        keyA0.setBounds(0, 0, 20, 120);

        keyB0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB0);
        keyB0.setBounds(20, 0, 20, 120);

        keyC1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC1);
        keyC1.setBounds(40, 0, 20, 120);

        keyD1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD1);
        keyD1.setBounds(60, 0, 20, 120);

        keyE1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE1);
        keyE1.setBounds(80, 0, 20, 120);

        keyF1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF1);
        keyF1.setBounds(100, 0, 20, 120);

        keyG1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG1);
        keyG1.setBounds(120, 0, 20, 120);

        keyA1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA1);
        keyA1.setBounds(140, 0, 20, 120);

        keyB1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB1);
        keyB1.setBounds(160, 0, 20, 120);

        keyC2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC2);
        keyC2.setBounds(180, 0, 20, 120);

        keyD2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD2);
        keyD2.setBounds(200, 0, 20, 120);

        keyE2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE2);
        keyE2.setBounds(220, 0, 20, 120);

        keyF2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF2);
        keyF2.setBounds(240, 0, 20, 120);

        keyG2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG2);
        keyG2.setBounds(260, 0, 20, 120);

        keyA2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA2);
        keyA2.setBounds(280, 0, 20, 120);

        keyB2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB2);
        keyB2.setBounds(300, 0, 20, 120);

        keyC3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC3);
        keyC3.setBounds(320, 0, 20, 120);

        keyD3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD3);
        keyD3.setBounds(340, 0, 20, 120);

        keyE3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE3);
        keyE3.setBounds(360, 0, 20, 120);

        keyF3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF3);
        keyF3.setBounds(380, 0, 20, 120);

        keyG3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG3);
        keyG3.setBounds(400, 0, 20, 120);

        keyA3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA3);
        keyA3.setBounds(420, 0, 20, 120);

        keyB3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB3);
        keyB3.setBounds(440, 0, 20, 120);

        keyC4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC4);
        keyC4.setBounds(460, 0, 20, 120);

        keyD4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD4);
        keyD4.setBounds(480, 0, 20, 120);

        keyE4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE4);
        keyE4.setBounds(500, 0, 20, 120);

        keyF4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF4);
        keyF4.setBounds(520, 0, 20, 120);

        keyG4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG4);
        keyG4.setBounds(540, 0, 20, 120);

        keyA4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA4);
        keyA4.setBounds(560, 0, 20, 120);

        keyB4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB4);
        keyB4.setBounds(580, 0, 20, 120);

        keyC5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC5);
        keyC5.setBounds(600, 0, 20, 120);

        keyD5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD5);
        keyD5.setBounds(620, 0, 20, 120);

        keyE5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE5);
        keyE5.setBounds(640, 0, 20, 120);

        keyF5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF5);
        keyF5.setBounds(660, 0, 20, 120);

        keyG5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG5);
        keyG5.setBounds(680, 0, 20, 120);

        keyA5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA5);
        keyA5.setBounds(700, 0, 20, 120);

        keyB5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB5);
        keyB5.setBounds(720, 0, 20, 120);

        keyC6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC6);
        keyC6.setBounds(740, 0, 20, 120);

        keyD6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD6);
        keyD6.setBounds(760, 0, 20, 120);

        keyE6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE6);
        keyE6.setBounds(780, 0, 20, 120);

        keyF6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF6);
        keyF6.setBounds(800, 0, 20, 120);

        keyG6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG6);
        keyG6.setBounds(820, 0, 20, 120);

        keyA6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA6);
        keyA6.setBounds(840, 0, 20, 120);

        keyB6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB6);
        keyB6.setBounds(860, 0, 20, 120);

        keyC7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC7);
        keyC7.setBounds(880, 0, 20, 120);

        keyD7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyD7);
        keyD7.setBounds(900, 0, 20, 120);

        keyE7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyE7);
        keyE7.setBounds(920, 0, 20, 120);

        keyF7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyF7);
        keyF7.setBounds(940, 0, 20, 120);

        keyG7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyG7);
        keyG7.setBounds(960, 0, 20, 120);

        keyA7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyA7);
        keyA7.setBounds(980, 0, 20, 120);

        keyB7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyB7);
        keyB7.setBounds(1000, 0, 20, 120);

        keyC8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/whitekey.jpg"))); // NOI18N
        keyboardLP.add(keyC8);
        keyC8.setBounds(1020, 0, 20, 120);

        keyBb0.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb0);
        keyBb0.setBounds(13, 0, 14, 80);
        keyboardLP.setLayer(keyBb0, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp1);
        keyCsharp1.setBounds(53, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb1);
        keyEb1.setBounds(73, 0, 14, 80);
        keyboardLP.setLayer(keyEb1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp1);
        keyFsharp1.setBounds(113, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp1);
        keyGsharp1.setBounds(133, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb1);
        keyBb1.setBounds(153, 0, 14, 80);
        keyboardLP.setLayer(keyBb1, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp2);
        keyCsharp2.setBounds(193, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb2);
        keyEb2.setBounds(213, 0, 14, 80);
        keyboardLP.setLayer(keyEb2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp2);
        keyFsharp2.setBounds(253, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp2);
        keyGsharp2.setBounds(273, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb2);
        keyBb2.setBounds(293, 0, 14, 80);
        keyboardLP.setLayer(keyBb2, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp3);
        keyCsharp3.setBounds(333, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb3);
        keyEb3.setBounds(353, 0, 14, 80);
        keyboardLP.setLayer(keyEb3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp3);
        keyFsharp3.setBounds(393, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp3);
        keyGsharp3.setBounds(413, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb3);
        keyBb3.setBounds(433, 0, 14, 80);
        keyboardLP.setLayer(keyBb3, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp4);
        keyCsharp4.setBounds(473, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb4);
        keyEb4.setBounds(493, 0, 14, 80);
        keyboardLP.setLayer(keyEb4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp4);
        keyFsharp4.setBounds(533, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp4);
        keyGsharp4.setBounds(553, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb4);
        keyBb4.setBounds(573, 0, 14, 80);
        keyboardLP.setLayer(keyBb4, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp5);
        keyCsharp5.setBounds(613, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb5);
        keyEb5.setBounds(633, 0, 14, 80);
        keyboardLP.setLayer(keyEb5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp5);
        keyFsharp5.setBounds(673, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp5);
        keyGsharp5.setBounds(693, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb5);
        keyBb5.setBounds(713, 0, 14, 80);
        keyboardLP.setLayer(keyBb5, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp6);
        keyCsharp6.setBounds(753, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb6);
        keyEb6.setBounds(773, 0, 14, 80);
        keyboardLP.setLayer(keyEb6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp6);
        keyFsharp6.setBounds(813, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp6);
        keyGsharp6.setBounds(833, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb6);
        keyBb6.setBounds(853, 0, 14, 80);
        keyboardLP.setLayer(keyBb6, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyCsharp7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyCsharp7);
        keyCsharp7.setBounds(893, 0, 14, 80);
        keyboardLP.setLayer(keyCsharp7, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyEb7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyEb7);
        keyEb7.setBounds(913, 0, 14, 80);
        keyboardLP.setLayer(keyEb7, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyFsharp7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyFsharp7);
        keyFsharp7.setBounds(953, 0, 14, 80);
        keyboardLP.setLayer(keyFsharp7, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyGsharp7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyGsharp7);
        keyGsharp7.setBounds(973, 0, 14, 80);
        keyboardLP.setLayer(keyGsharp7, javax.swing.JLayeredPane.PALETTE_LAYER);

        keyBb7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blackkey.jpg"))); // NOI18N
        keyboardLP.add(keyBb7);
        keyBb7.setBounds(993, 0, 14, 80);
        keyboardLP.setLayer(keyBb7, javax.swing.JLayeredPane.PALETTE_LAYER);

        pointerC4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/pointer.png"))); // NOI18N
        keyboardLP.add(pointerC4);
        pointerC4.setBounds(460, 120, 19, 30);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1045;
        gridBagConstraints.ipady = 150;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        rangePanel.add(keyboardLP, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        getContentPane().add(rangePanel, gridBagConstraints);

        rangeLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        rangeLabel.setText("Use the keyboard below to select an upper and lower range limit for your guide tone line.");
        rangeLabelPanel.add(rangeLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        getContentPane().add(rangeLabelPanel, gridBagConstraints);

        playButton.setText("Play");
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });
        playPanel.add(playButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        getContentPane().add(playPanel, gridBagConstraints);

        allowColorBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        allowColorBox.setText("Allow Color Tones");
        allowColorBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allowColorBoxActionPerformed(evt);
            }
        });
        allowColorPanel.add(allowColorBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        getContentPane().add(allowColorPanel, gridBagConstraints);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void generateLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateLineActionPerformed
        //Get which options are selected
        JRadioButton numberOfLines = getSelected(numberOfLinesButtons);
        JRadioButton direction = getSelected(directionButtons);
        JRadioButton scaleDeg = getSelected(scaleDegreeButtons);
        JRadioButton maxDur = getSelected(maxDurationButtons);
        JRadioButton lineTypeButton = getSelected(lineTypeButtons);
        
        //Get paramaters to pass into constructor
        //notate = (Notate)this.getParent();
        String scaleDegString = scaleDeg.getText();
        boolean alternating = false;
        int duration = buttonToDuration(maxDur);
        int lineType = buttonToLineType(lineTypeButton);
        boolean allowColor = allowColorBox.isSelected();
        
        //Passing in "mix" as the scaleDegString indicates that two lines should be generated
        //Right now, the order in which the two lines appear always alternates,
        //i.e. the line has a trapezoidal shape
        if(numberOfLines.equals(twoLines)){
            scaleDegString = "mix";
            alternating = true;
        }
        
        int [] limits = limits();
        int low = C4; //middle C (C4)
        int high = C5; //C above middle C (C5)
        if(limits[0]!=-1&&limits[1]!=-1&&(limits[1]-limits[0]+1)>=Constants.OCTAVE){
            low = limits[0];
            high = limits[1];
        }else{
            clearKeyboard();
            PianoKey lowKey = pkeys[low-A];
            lowKey.setPressed(true);
            pressKey(lowKey);
            keysPressed++;
            
            
            PianoKey highKey = pkeys[high-A];
            highKey.setPressed(true);
            pressKey(highKey);
            keysPressed++;
            
        }
        /*int low = lowLimitSlider.getValue();
        int high = highLimitSlider.getValue();
        */
        
        //construct a guide tone line generator, make a guide tone line (melody part), then add it as a new chorus
        GuideLineGenerator guideLine = new GuideLineGenerator(notate.getChordProg(), 
                                                              buttonToDirection(direction), 
                                                              scaleDegString, 
                                                              alternating, 
                                                              low, high, 
                                                              duration,
                                                              lineType,
                                                              allowColor);
        MelodyPart guideToneLine = guideLine.makeGuideLine();
        notate.addChorus(guideToneLine);
        
        transformed = false;
    }//GEN-LAST:event_generateLineActionPerformed

    private void clearKeyboard(){
        for(PianoKey pk : pkeys){
            if(pk.isPressed()){
                pk.setPressed(false);
                pressKey(pk);
                keysPressed--;
            }
            
        }
    }
    
    private int [] limits(){
        int [] limits = new int[2];
        limits[0] = lowKey.getMIDI();
        limits[1] = highKey.getMIDI();
        return limits;
    }
    
    private int buttonToDuration(JRadioButton b){
        if(b.equals(noPref)){
            return 0;
        }else if(b.equals(whole)){
            return WHOLE;
        }else if(b.equals(half)){
            return HALF;
        }else if(b.equals(quarter)){
            return QUARTER;
        }else{
            //shouldn't happen
            return 0;
        }
    }
    
    private int buttonToLineType(JRadioButton b){
        if(b.equals(threeSeven)){
            return THREE_SEVEN;
        }else if(b.equals(sevenThree)){
            return SEVEN_THREE;
        }else if(b.equals(fiveNine)){
            return FIVE_NINE;
        }else if(b.equals(nineFive)){
            return NINE_FIVE;
        }else{
            return THREE_SEVEN; // default, shouldn't happen
        }
    }
    public void updateButtons(){
        setButtonText(scaleDegreeButtons, scaleDegPanel);
    }
    private void setButtonText(ButtonGroup group, JPanel panel){
        panel.removeAll();
        panel.add(scaleDegLabel);
        Chord firstChord = notate.getChordProg().getChord(0);
        Polylist chordSpell = firstChord.getSpell();
        if(chordSpell==null){
            return;
        }
        while(!chordSpell.isEmpty()){
            Note nextNote = ((NoteSymbol)chordSpell.first()).toNote();
            Polylist relPitch = nextNote.toRelativePitch(firstChord);
            String degree = (String)relPitch.second();
            chordSpell = chordSpell.rest();
            JRadioButton b = new JRadioButton(degree);
            panel.add(b);
            group.add(b);
            b.setSelected(true);
        }
        if(allowColorBox.isSelected()){
            Polylist chordColor = firstChord.getColor();
            if(chordColor==null){
                return;
            }
            while(!chordColor.isEmpty()){
                Note nextNote = ((NoteSymbol)chordColor.first()).toNote();
                Polylist relPitch = nextNote.toRelativePitch(firstChord);
                String degree = (String)relPitch.second();
                chordColor = chordColor.rest();
                JRadioButton b = new JRadioButton(degree);
                panel.add(b);
                group.add(b);
                b.setSelected(true);
            }
        }
        
    }
    
    private void twoLinesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoLinesActionPerformed
        enableButtons(scaleDegreeButtons, false);
        enableButtons(lineTypeButtons, true);
    }//GEN-LAST:event_twoLinesActionPerformed

    /**
     * 
     * @param group ButtonGroup to enable/disable
     * @param enabled true to enable, false to disable
     */
    private void enableButtons(ButtonGroup group, boolean enabled){
        for(Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();){
            AbstractButton b = buttons.nextElement();
            b.setEnabled(enabled);
        }
    }
    
    private void oneLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneLineActionPerformed
        enableButtons(scaleDegreeButtons, true);
        enableButtons(lineTypeButtons, false);
    }//GEN-LAST:event_oneLineActionPerformed

    private void transformLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transformLineActionPerformed
        //System.out.println(transformed);
        if(transformed){
            transformationPanel.revertSubs();
        }
        
        MelodyPart currentMelody = notate.getCurrentMelodyPart();
        
        transformationPanel.applySubstitutionsToPart(currentMelody,
                                                     notate.getChordProg());
        notate.playCurrentSelection(false, 
                                    0, 
                                    PlayScoreCommand.USEDRUMS, 
                                    "Transformed over guide tone line");
        transformed = true;
        revertLine.setEnabled(true);
    }//GEN-LAST:event_transformLineActionPerformed

    private void revertLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertLineActionPerformed
        //System.out.println(transformed);
        transformationPanel.revertSubs();
        transformed = false;
    }//GEN-LAST:event_revertLineActionPerformed
    
    private void keyboardLPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_keyboardLPMouseClicked

        playback = false;

        // Getting the position of the mouse click
        int y = evt.getY();
        int x = evt.getX();

        if (y < WKHEIGHT && !playback)
        {
            // True if the user clicked a black key.
            boolean blackPianoKey = false;

            // Determines the key number
            int keyNum = x / WKWIDTH;

            int note = keyNum;

            // gives the octave number (ex. 4 in C4 for middle C) by
            // determining where x is in relation to the pixel width of an octave
            int octave = ( (x + 5*WKWIDTH) / OCTAVE);

            // Only occurs if the click is at a y position that could be a black key
            if (y < BKHEIGHT) {
                // find the position of the click within the key
                int inKey = x - keyNum*WKWIDTH;

                // if click is in right half of black key
                if (inKey < (BKWIDTH/2 + 1)){
                    blackPianoKey = true;
                    note -= 1;

                    // not on a black key if note number is 1 or 4
                    if (note % OCTKEYS == 1 || note % OCTKEYS == 4) {
                        blackPianoKey = false;
                    }
                }

                // if click is in left half of black key
                else if (inKey > WKWIDTH - (BKWIDTH/2 + 1)) {
                    blackPianoKey = true;
                    note = keyNum;

                    // not on a black key if note number is 1 or 4
                    if (note % OCTKEYS == 1 || note % OCTKEYS == 4) {
                        blackPianoKey = false;
                    }
                }
            }

            // determine the MIDI value of the note clicked
            int baseMidi = 0;

            int oct = note - OCTKEYS*(octave - 1);

            if (octave == 0) {
                oct = note - OCTKEYS*octave;
            }

            // if the note is a black key
            if (blackPianoKey)
            {
                switch(oct) {
                    case 0:
                    baseMidi = A + 1;     //Bb
                    break;
                    case 2:
                    baseMidi = A + 4;     //C#
                    break;
                    case 3:
                    baseMidi = A + 6;     //Eb
                    break;
                    case 5:
                    baseMidi = A + 9;     //F#
                    break;
                    case 6:
                    baseMidi = A + 11;    //G#
                    break;
                    case 7:
                    baseMidi = A + 13;    //Bb
                    break;
                }
            }
            // if the note is not a black key
            else
            {
                switch(oct) {
                    case 0:
                    baseMidi = A;      //A
                    break;
                    case 1:
                    baseMidi = A + 2;  //B
                    break;
                    case 2:
                    baseMidi = A + 3;  //C
                    break;
                    case 3:
                    baseMidi = A + 5;  //D
                    break;
                    case 4:
                    baseMidi = A + 7;  //E
                    break;
                    case 5:
                    baseMidi = A + 8;  //F
                    break;
                    case 6:
                    baseMidi = A + 10; //G
                    break;
                    case 7:
                    baseMidi = A + 12; //A
                    break;
                    case 8:
                    baseMidi = A + 14; //B
                    break;
                }
            }
            
            // Adjust the MIDI value for different octaves
            int midiValue = baseMidi + P_OCTAVE*(octave - 1);
            

            if (octave == 0) {
                midiValue = baseMidi;
            }
            int m = evt.getModifiers();
            String mod = evt.getMouseModifiersText(m);
            setKeyboard(mod, midiValue);
        }
    }//GEN-LAST:event_keyboardLPMouseClicked

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        if(playButton.getText().equals("Play")){
            notate.playCurrentSelection(true,
                                        0,
                                        PlayScoreCommand.USEDRUMS,
                                        "Playing guide tone line");
            playButton.setText("Stop");
        }
        else{
            notate.stopPlaying();
            playButton.setText("Play");
        }
    }//GEN-LAST:event_playButtonActionPerformed

    private void allowColorBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowColorBoxActionPerformed
        this.setVisible(false);
        updateButtons();
        this.setVisible(true);
    }//GEN-LAST:event_allowColorBoxActionPerformed

    public void setKeyboard(String mod, int midiValue){
        
        // Pressing the keys and playing the notes
        PianoKey keyPlayed = pianoKeys()[midiValue - A];
        //if not blue note
        if((midiValue - A) > 23 && (midiValue - A) < 76){
            //new logic
            
            if(midiValue<lowKey.getMIDI()){
                keyPlayed.setPressed(true);
                pressKey(keyPlayed);
                lowKey.setPressed(false);
                pressKey(lowKey);
                lowKey = keyPlayed;
            }else if(midiValue>highKey.getMIDI()){
                keyPlayed.setPressed(true);
                pressKey(keyPlayed);
                highKey.setPressed(false);
                pressKey(highKey);
                highKey = keyPlayed;
            }else if(midiValue>lowKey.getMIDI()&&midiValue<highKey.getMIDI()){
                int distanceToLow = Math.abs(midiValue-lowKey.getMIDI());
                int distanceToHigh = Math.abs(midiValue-highKey.getMIDI());
                if(distanceToLow<distanceToHigh){
                    if(distanceToHigh>=Constants.OCTAVE){
                        keyPlayed.setPressed(true);
                        pressKey(keyPlayed);
                        lowKey.setPressed(false);
                        pressKey(lowKey);
                        lowKey = keyPlayed;
                    }
                }else{//if equal, change the high key (tiebreak)
                    if(distanceToLow>=Constants.OCTAVE){
                        keyPlayed.setPressed(true);
                        pressKey(keyPlayed);
                        highKey.setPressed(false);
                        pressKey(highKey);
                        highKey = keyPlayed;
                    }
                    
                }
            }
            
        }
    }
    
    /**
     * returns which JRadioButton in a ButtonGroup is selected
     * @param group the ButtonGroup from which you want to return the selected button
     * @return the JRadioButton that is selected
     */
    private JRadioButton getSelected(ButtonGroup group){
        for(Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();){
            AbstractButton b = buttons.nextElement();
            if(b.isSelected()){
                return (JRadioButton)b;
            }
        }
        return null;
    }
    
    /**
     * returns the direction associated with the given button
     * @param b a JRadioButton
     * @return the direction associated with that button (1 for up, 0 for same, -1 for down)
     */
    private int buttonToDirection(JRadioButton b){
        if(b.equals(ascending)){
            return 1;
        }else if(b.equals(descending)){
            return -1;
        }else if(b.equals(noPreference)){
            return 0;
        }else{
            //shouldn't happen
            return 0;
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GuideToneLineDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GuideToneLineDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GuideToneLineDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GuideToneLineDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GuideToneLineDialog dialog = new GuideToneLineDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allowColorBox;
    private javax.swing.JPanel allowColorPanel;
    private javax.swing.JRadioButton ascending;
    private javax.swing.Box.Filler bottomFiller;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JRadioButton descending;
    private javax.swing.ButtonGroup directionButtons;
    private javax.swing.JLabel directionLabel;
    private javax.swing.JPanel directionPanel;
    private javax.swing.JRadioButton fiveNine;
    private javax.swing.JButton generateLine;
    private javax.swing.JRadioButton half;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel keyA0;
    private javax.swing.JLabel keyA1;
    private javax.swing.JLabel keyA2;
    private javax.swing.JLabel keyA3;
    private javax.swing.JLabel keyA4;
    private javax.swing.JLabel keyA5;
    private javax.swing.JLabel keyA6;
    private javax.swing.JLabel keyA7;
    private javax.swing.JLabel keyB0;
    private javax.swing.JLabel keyB1;
    private javax.swing.JLabel keyB2;
    private javax.swing.JLabel keyB3;
    private javax.swing.JLabel keyB4;
    private javax.swing.JLabel keyB5;
    private javax.swing.JLabel keyB6;
    private javax.swing.JLabel keyB7;
    private javax.swing.JLabel keyBb0;
    private javax.swing.JLabel keyBb1;
    private javax.swing.JLabel keyBb2;
    private javax.swing.JLabel keyBb3;
    private javax.swing.JLabel keyBb4;
    private javax.swing.JLabel keyBb5;
    private javax.swing.JLabel keyBb6;
    private javax.swing.JLabel keyBb7;
    private javax.swing.JLabel keyC1;
    private javax.swing.JLabel keyC2;
    private javax.swing.JLabel keyC3;
    private javax.swing.JLabel keyC4;
    private javax.swing.JLabel keyC5;
    private javax.swing.JLabel keyC6;
    private javax.swing.JLabel keyC7;
    private javax.swing.JLabel keyC8;
    private javax.swing.JLabel keyCsharp1;
    private javax.swing.JLabel keyCsharp2;
    private javax.swing.JLabel keyCsharp3;
    private javax.swing.JLabel keyCsharp4;
    private javax.swing.JLabel keyCsharp5;
    private javax.swing.JLabel keyCsharp6;
    private javax.swing.JLabel keyCsharp7;
    private javax.swing.JLabel keyD1;
    private javax.swing.JLabel keyD2;
    private javax.swing.JLabel keyD3;
    private javax.swing.JLabel keyD4;
    private javax.swing.JLabel keyD5;
    private javax.swing.JLabel keyD6;
    private javax.swing.JLabel keyD7;
    private javax.swing.JLabel keyE1;
    private javax.swing.JLabel keyE2;
    private javax.swing.JLabel keyE3;
    private javax.swing.JLabel keyE4;
    private javax.swing.JLabel keyE5;
    private javax.swing.JLabel keyE6;
    private javax.swing.JLabel keyE7;
    private javax.swing.JLabel keyEb1;
    private javax.swing.JLabel keyEb2;
    private javax.swing.JLabel keyEb3;
    private javax.swing.JLabel keyEb4;
    private javax.swing.JLabel keyEb5;
    private javax.swing.JLabel keyEb6;
    private javax.swing.JLabel keyEb7;
    private javax.swing.JLabel keyF1;
    private javax.swing.JLabel keyF2;
    private javax.swing.JLabel keyF3;
    private javax.swing.JLabel keyF4;
    private javax.swing.JLabel keyF5;
    private javax.swing.JLabel keyF6;
    private javax.swing.JLabel keyF7;
    private javax.swing.JLabel keyFsharp1;
    private javax.swing.JLabel keyFsharp2;
    private javax.swing.JLabel keyFsharp3;
    private javax.swing.JLabel keyFsharp4;
    private javax.swing.JLabel keyFsharp5;
    private javax.swing.JLabel keyFsharp6;
    private javax.swing.JLabel keyFsharp7;
    private javax.swing.JLabel keyG1;
    private javax.swing.JLabel keyG2;
    private javax.swing.JLabel keyG3;
    private javax.swing.JLabel keyG4;
    private javax.swing.JLabel keyG5;
    private javax.swing.JLabel keyG6;
    private javax.swing.JLabel keyG7;
    private javax.swing.JLabel keyGsharp1;
    private javax.swing.JLabel keyGsharp2;
    private javax.swing.JLabel keyGsharp3;
    private javax.swing.JLabel keyGsharp4;
    private javax.swing.JLabel keyGsharp5;
    private javax.swing.JLabel keyGsharp6;
    private javax.swing.JLabel keyGsharp7;
    private javax.swing.JLayeredPane keyboardLP;
    private javax.swing.Box.Filler leftFiller;
    private javax.swing.ButtonGroup lineTypeButtons;
    private javax.swing.JLabel lineTypeLabel;
    private javax.swing.JPanel lineTypePanel;
    private javax.swing.JPanel linesPanel;
    private javax.swing.ButtonGroup maxDurationButtons;
    private javax.swing.JPanel maxDurationPanel;
    private javax.swing.JRadioButton nineFive;
    private javax.swing.JRadioButton noPref;
    private javax.swing.JRadioButton noPreference;
    private javax.swing.ButtonGroup numberOfLinesButtons;
    private javax.swing.JLabel numberOfLinesLabel;
    private javax.swing.JRadioButton oneLine;
    private javax.swing.JButton playButton;
    private javax.swing.JPanel playPanel;
    private javax.swing.JLabel pointerC4;
    private javax.swing.JRadioButton quarter;
    private javax.swing.JLabel rangeLabel;
    private javax.swing.JPanel rangeLabelPanel;
    private javax.swing.JPanel rangePanel;
    private javax.swing.JButton revertLine;
    private javax.swing.Box.Filler rightFiller;
    private javax.swing.JLabel scaleDegLabel;
    private javax.swing.JPanel scaleDegPanel;
    private javax.swing.ButtonGroup scaleDegreeButtons;
    private javax.swing.JRadioButton sevenThree;
    private javax.swing.JRadioButton threeSeven;
    private javax.swing.Box.Filler topFiller;
    private javax.swing.JButton transformLine;
    private javax.swing.JPanel transformPanel;
    private javax.swing.JRadioButton twoLines;
    private javax.swing.JRadioButton whole;
    // End of variables declaration//GEN-END:variables
}
