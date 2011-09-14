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

import imp.com.*;
import imp.data.*;
import imp.roadmap.RoadMapFrame;
import imp.util.BasicEditor;

/**
 *
 * @author  david
 */
public class SourceEditorDialog extends javax.swing.JDialog implements BasicEditor
{
     /**
     * Used as a prefix on editor window titles
     */
    
    Notate parent;
    java.awt.Frame frameParent;
    CommandManager cm;
    int type;
    
    public static final String titlePrefix = "Editor For: ";
    
    public static final int LEADSHEET = 0;
    public static final int GRAMMAR = 1;
    public static final int STYLE = 2;
    public static final int DICTIONARY = 3;
    public static final String[] typeStr = {"Leadsheet", "Grammar", "Style", "Dictionary"};

    public static String editorTitlePrefix = "Editor for: ";
    
    private boolean firstTime = true;
   
    /** Creates new form sourceEditorDialog */
    public SourceEditorDialog(java.awt.Frame parent, boolean modal, Notate p, CommandManager cm, int type)
    {
        super(parent, modal);
        frameParent = parent;
        this.parent = p;
        this.cm = cm;
        this.type = type;
        initComponents();
        setSize(200,200);
        setTitle("");
        editorToSourceButton.setText("Editor to " + typeStr[type]);
        sourceToEditorButton.setText(typeStr[type] + " to Editor");
        
        setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
    }

    private String title = "";
    public void setTitle(String title) {
        this.title = title;
        super.setTitle(titlePrefix + title);
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getText() {
        return sourceEditor.getText();
    }
    
    public void setText(String text) {
      
        sourceEditor.setSize(600, 2000);
        sourceEditor.setText(text);
    }

    public void setRows(int numRows)
    {
        sourceEditor.setRows(numRows);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourceEditorScrollPane = new javax.swing.JScrollPane();
        sourceEditor = new javax.swing.JTextArea();
        editorToSourceButton = new javax.swing.JButton();
        sourceToEditorButton = new javax.swing.JButton();

        setTitle("Editor");
        setAlwaysOnTop(true);

        sourceEditorScrollPane.setMinimumSize(new java.awt.Dimension(600, 20000));
        sourceEditorScrollPane.setPreferredSize(new java.awt.Dimension(600, 20000));
        sourceEditorScrollPane.setVerifyInputWhenFocusTarget(false);

        sourceEditor.setColumns(20);
        sourceEditor.setLineWrap(true);
        sourceEditor.setRows(5);
        sourceEditor.setAutoscrolls(false);
        sourceEditor.setMinimumSize(new java.awt.Dimension(600, 40000));
        sourceEditor.setPreferredSize(new java.awt.Dimension(600, 40000));
        sourceEditorScrollPane.setViewportView(sourceEditor);

        getContentPane().add(sourceEditorScrollPane, java.awt.BorderLayout.CENTER);

        editorToSourceButton.setToolTipText("Load the current leadsheet to the textual editor.\n");
        editorToSourceButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        editorToSourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editorToSourceButtonActionPerformed(evt);
            }
        });
        getContentPane().add(editorToSourceButton, java.awt.BorderLayout.SOUTH);

        sourceToEditorButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sourceToEditorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceToEditorButtonActionPerformed(evt);
            }
        });
        getContentPane().add(sourceToEditorButton, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sourceToEditorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sourceToEditorButtonActionPerformed
    {//GEN-HEADEREND:event_sourceToEditorButtonActionPerformed
    fillEditor();
    }//GEN-LAST:event_sourceToEditorButtonActionPerformed

    public void fillEditor()
    {
    switch (type)
        {
            case LEADSHEET:
                cm.execute(new LeadsheetToEditorCommand(parent.getScore(), this));
                break;
            case GRAMMAR:
                cm.execute(new GrammarToEditorCommand(parent.getGrammarFileName(), this));
                break;
            case STYLE:
                cm.execute(new StylesToEditorCommand(this));
                break;
            case DICTIONARY:
                new DictionaryToEditorCommand(((RoadMapFrame)frameParent).getDictionaryFilename(), this).execute();
                break;
        }
    if( firstTime )
      {
      sourceEditor.moveCaretPosition(0);
      firstTime = false;
      }
    }
    
    private void editorToSourceButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_editorToSourceButtonActionPerformed
    {//GEN-HEADEREND:event_editorToSourceButtonActionPerformed
        switch (type)
        {
            case LEADSHEET:
                Score newScore = new Score();
                cm.execute(new EditorToLeadsheetCommand(newScore, this));
                setTitle(newScore.getTitle().equals("")?"Untitled Leadsheet":newScore.getTitle());
                parent.setupScore(newScore);
                break;
            case GRAMMAR:
                cm.execute(new EditorToGrammarCommand(parent.getGrammarFileName(), this));
                parent.reloadGrammar2();
                break;
            case STYLE:
                cm.execute(new EditorToStylesCommand(this));
                parent.reloadStyles();
                break;
            case DICTIONARY:
                new EditorToDictionaryCommand((RoadMapFrame)frameParent, this).execute();
                break;        }
    }//GEN-LAST:event_editorToSourceButtonActionPerformed
    
    //used when calling the grammar to editor button automatically from Notate
    public void performEditorToSourceButton(java.awt.event.ActionEvent evt) {
        editorToSourceButtonActionPerformed(evt);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editorToSourceButton;
    private javax.swing.JTextArea sourceEditor;
    private javax.swing.JScrollPane sourceEditorScrollPane;
    private javax.swing.JButton sourceToEditorButton;
    // End of variables declaration//GEN-END:variables
    
}
