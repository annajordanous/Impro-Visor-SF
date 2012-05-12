/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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
package imp.com;

import imp.data.MelodyPart;
import imp.data.Part;
import imp.gui.Stave;
import imp.util.Trace;

/**
 * A Command that pastes a Part over a section of a destination Part.
 * This is intended for "dynamic" use, i.e. while improvising.
 * It is adapted from SafePasteCommand
 * If it will overwrite existing notes, then it asks the user what to do.
 * @see         Command
 * @see         CommandManager
 * @see         Part
 * @see         PasteCommand
 * @author      Stephen Jones
 */
public class DynamicPasteCommand implements Command {

    /**
     * the Part to paste from
     */
    private Part source;

    /**
     * the Part to paste onto
     */
    private Part dest;

    /**
     * the slot to paste at
     */
    private int startSlot;

    /**
     * the PasteCommand used for the paste itself
     */
    private PasteCommand pasteCommand;

    /**
     * the JFrame to pop up the safe paste dialog from
     */
    private imp.gui.Notate notate = null;
    
    /**
     * if dialog is set to false, then this will tell whether we should
     * overwrite by default or not
     */
    private boolean overwrite = true;

    /**
     * if the paste will overwrite existing notes, pop up a dialog if this is
     * set to true, otherwise go by the default overwrite boolean
     */
    private boolean dialog = false;

    /**
     * this command is undoable by default, but may be set to false
     * if the paste is cancelled
     */
    private boolean undoable = true;

    private boolean play = true;

    /**
     * Creates a new SafePasteCommand with default options for overwrite and
     * dialog.
     * @param source    the Part to paste from
     * @param dest      the Part to paste onto
     * @param startSlot the slot to paste at
     */
    public DynamicPasteCommand(Part source, Part dest, int startSlot) {
        this.source = source;
        this.dest = dest;
        this.startSlot = startSlot;
    }

    /**
     * Creates a new SafePasteCommand, specifying values for dialog and
     * overwrite.
     * @param source    the Part to paste from
     * @param dest      the Part to paste onto
     * @param startSlot the slot to paste at
     * @param dialog    true if a dialog should be popped up
     * @param overwrite true if the command should overwrite by default
     */
    public DynamicPasteCommand(Part source, Part dest, int startSlot, 
                        boolean dialog, boolean overwrite) {
        this(source, dest, startSlot);
        this.overwrite = overwrite;
        this.dialog = dialog;
    }
    
    /**
     * Creates a new SafePasteCommand.
     * @param source    the Part to paste from
     * @param dest      the Part to paste onto
     * @param startSlot the slot to paste at
     * @param dialog    a boolean true if a dialog should be popped up
     * @param overwrite a boolean true if the command should default overwrite
     * @param frame     the JFrame to pop the dialog out of
     */
    public DynamicPasteCommand(Part source, Part dest, int startSlot, 
                        boolean dialog, boolean overwrite, 
                        imp.gui.Notate notate) {
        this(source, dest, startSlot, dialog, overwrite);
        this.notate = notate;
    }

    /**
     * Executes the safe paste.
     */
    public void execute() {
        Trace.log(2, "Executing SaftePasteCommand");
        if(!dialog && overwrite) {
            overwrite();
            return;
        }

        // Keep a copy of the compressed source, just in case we need it.
        int freeSlots = ((MelodyPart)dest).getFreeSlots(startSlot);
        Part fitPart = source.fitPart(freeSlots);

//        // if they didn't pop up the dialog, and we can't compress, cancel
//        if(fitPart == null) {
//            cancel();
//            return;
//        }

        compress(fitPart);
    }

    /**
     * Pastes the source onto the dest.
     */
    private void overwrite() {
        undoable = true;
        pasteCommand = new PasteCommand(source, dest, startSlot, play);
        
        Stave stave = notate.getStaveAtTab(notate.getCurrTabIndex());
        // selects the notes & rests just inserted
        if ( stave != null) {
            stave.setSelection(startSlot, startSlot + source.size() - 1);

        pasteCommand.execute();
        }
    }

    /**
     * Takes a compressed part, sets it as the source, then pastes.
     */
    private void compress(Part fitPart) {
        source = fitPart;
        overwrite();
    }

    /**
     * Cancels the paste, and makes the Command undoable.
     */
    private void cancel() {
        undoable = false;
    }

    /**
     * Undoes the paste.
     */
    public void undo() {
        pasteCommand.undo();
    }

    /**
     * Redoes the paste.
     */
    public void redo() {
        pasteCommand.redo();
    }

    public boolean isUndoable() {
        return undoable;
    }
}
