/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
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

package imp.roadmap;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import imp.brickdictionary.*;
import java.util.Random;


/**
 * A class for viewing and modifying the roadmap
 * @author August Toman-Yih
 */
public class RoadMapPanel extends JPanel {
    /** Number of lines in the roadmap (for grid drawing) */
    private int numLines = 1;
    
    /* HOW SELECTION IS IMPLEMENTED:
     * The indices of the first and last brick in the selection are kept of here
     * Within a brick, a data member keeps track of selection, and another keeps
     * track of which chord (if any) is selected.
     */
    /** Index of the start of selection (inclusive) */
    private int selectionStart = -1;
    /** Index of the end of selection (inclusive) */
    private int selectionEnd = -1;
    
    /** Index of the insertion line */ //TODO change to slot, maybe
    private int insertLineIndex = -1;
    
    /** Slot offset of the play line */
    private int playLineSlot = -1;
    /** Line of the play line */
    private int playLineLine = -1;
    /** Keeps track of the offset (in slots) of the playline (for when playback starts at a chord in the middle) */
    private int playLineOffset = 0;
    /** Index of the start of the play section */
    private int playSectionStart = 0;
    /** Index of the end of the play section */
    private int playSectionEnd = 0;
    
    /** Position of the rollover (prevents flickering during playback)*/
    private Point rolloverPos = null;
    /** Buffer for drawing */
    private Image buffer;
    /** Roadmap. Stores the chords and junk. */
    private RoadMap roadMap = new RoadMap();
    /** Graphic representation of the roadmap */
    private ArrayList<GraphicBrick> graphicMap = new ArrayList();
    /** Section breaks list. Only used for keymapping. Possibly unideal. */
    private ArrayList<Long> sectionBreaks = new ArrayList();
    /** Keeps track of graphical settings for the roadmap */
    RoadMapSettings settings;
    /** RoadMapFrame containing this panel */
    RoadMapFrame view;
    
    /** Creates new form RoadMapPanel */
    public RoadMapPanel(RoadMapFrame view)
    {
        this.view = view;
        settings = view.getSettings();
    }
    
    /** Returns the roadmap */
    public RoadMap getRoadMap()
    {
        return roadMap;
    }
    
    /** Sets the roadmap */
    public void setRoadMap(RoadMap roadMap)
    {
        this.roadMap = roadMap;
        graphicMap = makeBricks(roadMap.getBlocks());
        roadMap.process();
    }
    
    /** Returns the number of blocks in the roadmap */
    public int getNumBlocks()
    {
        return roadMap.size();
    }
    
    /** Puts the bricks in the correct position onscreen based on sequence and line breaks */
    public void placeBricks()
    {
        long currentSlots = 0;
        int lines = 0;
        long lineBeats = 0;
        
        sectionBreaks.clear();
        
        for( GraphicBrick brick : graphicMap ) {
            brick.setSlot(lineBeats);
            brick.setLine(lines);
            currentSlots += brick.getDuration();
            lineBeats += brick.getDuration();
            
            int[] wrap = settings.wrapFromSlots((int)lineBeats);
            lineBeats = wrap[0];
            lines += wrap[1];
            
            if(brick.getBlock().getSectionEnd() == Block.SECTION_END &&
                    lineBeats != 0) {
                lineBeats = 0;
                lines++;
                sectionBreaks.add(currentSlots);
            }
            
            if(lineBeats == 0 && brick == graphicMap.get(graphicMap.size()-1)) {
                lines--;
            }
        }
        numLines = (int)lines+1;
        
        setPreferredSize(
                new Dimension(settings.getCutoff() + settings.xOffset,
                settings.getLineOffset()*numLines + settings.yOffset));
        
        draw();
    }
    
    /** Process and draw bricks */
    public void updateBricks()
    {
        roadMap.process();
        draw();
    }
    
    /** Updates graphicMap to match the roadmap.
     * You shouldn't need to use this; using the other methods will keep them in sync*/
    public void rebuildRoadMap()
    {
        graphicMap = makeBricks(roadMap.getBlocks());
        placeBricks();
    }
    
    /** Adds a block to the roadmap. */
    public void addBlock(Block block)
    {
        addBlock(block,false);
    }
    /** Adds a block to the roadmap
     * @param block block to be added
     * @param selectBlocks Whether the block is selected after insertion
     */
    public void addBlock(Block block, Boolean selectBlocks)
    {
        roadMap.add(block);
        graphicMap.add(new GraphicBrick(block, settings));
        if(selectBlocks)
            selectBrick(roadMap.size() - 1);
    }
    
    /** Adds a list of blocks to the roadmap */
    public void addBlocks(ArrayList<Block> blocks)
    {
        addBlocks(blocks, false);
    }
    /** Adds a list of blocks to the roadmap
     * @param blocks blocks to be added
     * @param selectBlocks Whether the blocks are selected after insertion
     */
    public void addBlocks(ArrayList<Block> blocks, Boolean selectBlocks)
    {  
        
        roadMap.addAll(blocks);
        graphicMap.addAll(makeBricks(blocks));
        
        if(selectBlocks)
            selectBricks(roadMap.size() - 1);
        
    }
    
    /** Adds a list of blocks at the specified position */
    public void addBlocks(int ind, ArrayList<Block> blocks)
    {
        addBlocks(ind, blocks, false);
    }
    /** Adds a list of blocks at the specified position
     * @param ind Index to insert the blocks
     * @param blocks Blocks to be inserted
     * @param selectBlocks Whether the blocks are selected after insertion
     */
    public void addBlocks(int ind, ArrayList<Block> blocks, Boolean selectBlocks)
    {
        roadMap.addAll(ind, blocks);
        graphicMap.addAll(ind, makeBricks(blocks));
        if(selectBlocks) {
            selectBricks(ind, ind + blocks.size() - 1 );
        }
    }
    
    /** Adds a list of blocks before the selection. Updates the selection */
    public void addBlocksBeforeSelection(ArrayList<Block> blocks, Boolean selectBlocks)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            addBlocks(selectionStart, blocks, selectBlocks);
            if(!selectBlocks) {
                selectionStart+=blocks.size();
                selectionEnd+=blocks.size();
            }
        } else {
            addBlocks(blocks, true);
        }
    }
            
    /** Method that takes a list of blocks and creates GraphicBrick counterparts*/
    public ArrayList<GraphicBrick> makeBricks(ArrayList<Block> blocks)
    {
        ArrayList<GraphicBrick> bricks = new ArrayList();
        
        for( Block block : blocks )
            bricks.add(new GraphicBrick(block, settings));
        
        return bricks;
    }
    
    /** Method takes a list of GraphicBricks and gets the blocks contained within them*/
    public ArrayList<Block> makeBlocks(ArrayList<GraphicBrick> bricks)
    {
        ArrayList<Block> blocks = new ArrayList();
        
        for( GraphicBrick brick : bricks )
            blocks.add(brick.getBlock());
        
        return blocks;
    }
    
    /** Changes the chord at the selection.
     * @param name Chord name
     * @param dur Chord duration
     */
    public void changeChord(String name, int dur)
    {
        int chordInd = graphicMap.get(selectionStart).getSelected();
        Block block = removeSelection().get(0);
        ArrayList<Block> newBlocks = new ArrayList(block.flattenBlock());
        ChordBlock chord = (ChordBlock)newBlocks.get(chordInd);
        newBlocks.set(chordInd, new ChordBlock(name, dur));
        
        addBlocks(selectionStart, newBlocks);
        selectBrick(selectionStart + chordInd);
        placeBricks();
    }
    
    /** Returns a list of the selected blocks*/
    public ArrayList<Block> getSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1)
            return getBlocks(selectionStart, selectionEnd+1);
        return new ArrayList();
    }
    
    /** Removes and returns the selected blocks*/
    public ArrayList<Block> removeSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1)
            return removeBlocks(selectionStart, selectionEnd+1);
        return new ArrayList();
    }
    
    /** Removes and returns the selected blocks <b>without post processing</b>. */
    public ArrayList<Block> removeSelectionNoUpdate()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> blocks = new ArrayList(roadMap.getBlocks().subList(selectionStart, selectionEnd+1));
            roadMap.getBlocks().subList(selectionStart, selectionEnd+1).clear();
            graphicMap.subList(selectionStart, selectionEnd+1).clear();
            return blocks;
        }
        return new ArrayList();
    }
    
    /** Returns the GraphicBrick at the specified index
     * @param index
     * @return 
     */
    public GraphicBrick getBrick(int index)
    {
        return graphicMap.get(index);
    }
    
    /** Returns the Blocks between the two indices
     * @param start
     * @param end
     * @return 
     */
    public ArrayList<Block> getBlocks(int start, int end)
    {
        return roadMap.getBlocks(start, end);
    }
    
    /** Removes and returns all blocks.*/
    public ArrayList<Block> removeBlocks()
    {
        graphicMap.clear();
        return roadMap.removeBlocks();
    }
    
    /**
     * Removes and returns the blocks between the two indices
     * @param start
     * @param end
     * @return 
     */
    public ArrayList<Block> removeBlocks(int start, int end)
    {
        ArrayList<Block> blocks = roadMap.removeBlocks(start, end);
        graphicMap.subList(start, end).clear();
        return blocks;
    }
    
    /**
     * Select a chord within a brick
     * @param brickInd
     * @param chordInd 
     */
    public void selectChord(int brickInd, int chordInd)
    {
        selectBrick(brickInd);
        GraphicBrick brick = getBrick(brickInd);
        
        brick.selectChord(chordInd);
        drawBrick(brickInd);
        drawKeyMap();
    }
    
    /** Selects the brick at the specified index with proper selection behavior.
     * <p> If the index is outside of the current selection, extend the selection.
     * If not, just select that brick.
     */
    public void selectBricks(int index)
    {
        if(selectionStart == -1 && selectionEnd == -1)
            selectionStart = selectionEnd = index;
        else {
            if(index < selectionStart) {
                getBrick(selectionEnd).setSelected(true);
                selectionStart = index;
            }
            else if (index > selectionEnd) {
                getBrick(selectionStart).setSelected(true);
                selectionEnd = index;
            }
            else {
                selectBrick(index);
            }
        }
        

        for(int i = selectionStart; i <= selectionEnd; ) {
            graphicMap.get(i).setSelected(true);
            drawBrick(i);
            i++;
        }
        
        drawKeyMap();
    }
    
    /** Selects the bricks between the start and end indices, inclusive */
    public void selectBricks(int start, int end)
    {
        deselectBricks();
        selectionStart = start;
        selectionEnd = end;
        for(int ind = start; ind <= end; ind++)
            graphicMap.get(ind).setSelected(true);
        drawBricks();
        drawKeyMap();
    }
    
    /** Selects the brick at the specified index. Deselects all other bricks. */
    public void selectBrick(int index)
    {
        deselectBricks();
        selectionStart = selectionEnd = index;
        graphicMap.get(index).setSelected(true);
        drawBrick(index);
        
        drawKeyMap();
    }
    
    /** Selects all bricks */
    public void selectAll()
    {
        if(!roadMap.isEmpty())
            selectBricks(0, roadMap.size()-1);

    }
    
    /** Deselects all bricks */
    public void deselectBricks()
    {
        for(GraphicBrick brick : graphicMap)
            brick.setSelected(false);
        
        draw();
        
        selectionStart = selectionEnd = -1;
    }
  
    /** Returns true if there are bricks currently selected */
    public boolean hasSelection()
    {
        return selectionStart != -1 && selectionEnd != -1;
    }
    
    /** Transpose all the bricks in the selection by the desired number of semitones */
    public void transposeSelection(long diff)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            for(Block block : roadMap.getBlocks(selectionStart, selectionEnd + 1))
                block.transpose(diff);
            roadMap.process();
            draw();
        }
        
    }
    
    /** Scale all bricks in the selection by the given amount */
    public void scaleSelection(int scale)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            for(Block block : roadMap.getBlocks(selectionStart, selectionEnd + 1))
                block.scaleDuration(scale);
            roadMap.process();
        }
        placeBricks();
    }
    
    /** Delete all bricks in the selection (or the chord if it is selected)*/
    public void deleteSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            int chordSelected = graphicMap.get(selectionStart).getSelected();
            if(chordSelected != -1)
                deleteChord(roadMap.getBlock(selectionStart), chordSelected);
            else
                deleteRange(selectionStart, selectionEnd+1);
        }
        selectionStart = selectionEnd = -1;
    }
    
    /** Delete a chord within a brick */
    public void deleteChord(Block block, int chordInd)
    {
        removeSelectionNoUpdate();
        ArrayList<Block> newBlocks = new ArrayList(block.flattenBlock());
        newBlocks.remove(chordInd);
        
        addBlocks(selectionStart, newBlocks);
        selectBrick(selectionStart + chordInd);
        placeBricks();
    }
    
    /** Delete all bricks within the two indices
     * @param start start index (inclusive)
     * @param end end index (exclusive)
     */
    public void deleteRange(int start, int end)
    {
        roadMap.removeBlocks(start, end);
        graphicMap.subList(start, end).clear();
        placeBricks();
    }
    
    /** Replaces the selection with a collection of blocks */
    public void replaceSelection(ArrayList<Block> blocks)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            removeSelection();
            addBlocks(selectionStart,blocks);
            placeBricks();
            selectBricks(selectionStart, selectionStart + blocks.size() - 1);
        }
    }
    
    /** Breaks the selected bricks into component bricks */
    public void breakSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> blocks = getSelection();
            ArrayList<Block> newBlocks = new ArrayList();
            
            for( Block block : blocks )
                newBlocks.addAll(block.getSubBlocks());
            
            replaceSelection(newBlocks);
        }       
    }
    
    /** Flattens the selected bricks to individual chords */
    public void flattenSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> newBlocks = new ArrayList(RoadMap.getChords(getSelection()));
            replaceSelection(newBlocks);
        }
    }
    
    /** Makes the selection into a new brick with the given parameters
     * @param name
     * @param key
     * @param mode
     * @param type
     * @return the new brick
     */
    public Brick makeBrickFromSelection(String name, long key, String mode, String type)
    {
        if(selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd) {
            ArrayList<Block> blocks = getSelection();
            ArrayList<Block> newBlock = new ArrayList();
            newBlock.add(new Brick(name, key, type, blocks, mode));
            replaceSelection(newBlock);
            return (Brick)newBlock.get(0);
        }
        return null;
    }
    
    /** Returns the index of the brick containing the point(x,y)*/
    public int getBrickIndexAt(int x, int y)
    {
        int index = 0;
        for ( GraphicBrick brick : graphicMap) {
            if( brick.contains(x, y) )
                return index;
            index++;
        }
        return -1;
    }
    
    /** Returns the index in the roadmap containing the point (x,y) */
    public int getIndexAt(int x, int y)
    {
        int index = 0;
        for ( GraphicBrick brick : graphicMap ) {
            if( brick.contains(x, y) )
                return index;
            
            if( y < brick.y() || x < brick.x() && y > brick.y() && y < brick.y() + settings.lineHeight )
                return index;
            
            index++;
        }
        
        return index;
    }
    
    /** Adds a section end to the end of the selection */
    public void toggleSection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            roadMap.getBlock(selectionEnd).setSectionEnd(!roadMap.getBlock(selectionEnd).isSectionEnd());
            roadMap.process();
            draw();
        }
    }
    
    /** Adds a phrase end to the end of the selection */
    public void togglePhrase()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            Block block = roadMap.getBlock(selectionEnd);
            if(block.getSectionEnd() != Block.PHRASE_END)
                block.setSectionEnd(Block.PHRASE_END);
            else
                block.setSectionEnd(Block.NO_END);
            roadMap.process();
            draw();
        }
    }
    
    /** Sets the last brick to be a section end */
    public void endSection()
    {
        roadMap.getBlock(roadMap.size()-1).setSectionEnd(true);
    }
    
    /** Sets the insertion line to the point (x,y) */
    public void setInsertLine(int x, int y)
    {
        insertLineIndex = getIndexAt(x,y);
    }
    
    /** Sets the insertion line to the desired index */
    public void setInsertLine(int index)
    {
        insertLineIndex = index;
    }
    
    /** Sets the play section to the current selection */
    public void setPlaySection()
    {
        playSectionStart = selectionStart;
        playSectionEnd = selectionEnd;
    }
    
    /** Sets the offset of the playline to the first brick */
    public void setPlayLineOffset()
    {
        int offset = 0;
        for(int i = 0; i < selectionStart; i++)
            offset += roadMap.getBlock(i).getDuration();
        playLineOffset = offset;
    }
    
    /** Sets the playline to the given slot */
    public void setPlayLine(int slot)
    {
        int[] wrap = findLineAndSlot(slot + playLineOffset);
        playLineSlot = wrap[0];
        playLineLine = wrap[1];
    }
    
    /** Returns the position of the given slot with line breaks
     * @param slots
     * @return a two element int array where the first element is the slot offset
     * and the second the line
     */
    public int[] findLineAndSlot(int slots)
    {
       int totalSlots = 0;
       int slotOffset = 0;
       int line = 0;
       
       for(GraphicBrick brick : graphicMap) {
           totalSlots += brick.getDuration();
           slotOffset = (int)brick.getSlot() + brick.getDuration() + slots - totalSlots;
           line = brick.getLine();
           
           if(slots < totalSlots)
               break;
       }
       int[] wrap = settings.wrapFromSlots(slotOffset);
       return new int[]{wrap[0],line + wrap[1]};
    }
    
    /** Sets the rollover to the given point */
    public void setRolloverPos(Point point)
    {
        rolloverPos = point;
    }
    
    /** Returns a point containing the current position of the rollover */
    public Point getRolloverPos()
    {
        return rolloverPos;
    }
    
    /* Drawing and junk */
    /** Assign this panel a buffer */
    public void setBuffer(Image buffer)
    {
        this.buffer = buffer;
    }
    
    /** Draw all elements of the roadmap*/
    public void draw()
    { 
       view.setBackground(buffer);
       drawGrid();
       drawText();
       drawBricks();
       drawKeyMap();
       if(view.isPlaying()) {
           drawPlaySection();
           setPlayLine(view.getMidiSlot()/settings.slotsPerBeat * settings.slotsPerBeat);
           drawPlayLine();
       }
       drawRollover();
       repaint();
    }
    
    /** Draws a cursor line of the desired color at the desired slot */
    public void drawCursorLine(int slot, Color color)
    {
        int[] wrap = findLineAndSlot(slot);
        drawCursorLine(wrap[0], wrap[1], color);
    }
    
    /** Draws a cursor line of the desired color at the desired line/slot point */
    public void drawCursorLine(int slotOffset, int line, Color color)
    {
        Graphics2D g2d = (Graphics2D)buffer.getGraphics();
        g2d.setColor(color);
        g2d.setStroke(settings.cursorLine);
        
        int x = settings.getLength(slotOffset) + settings.xOffset;
        int y = line * settings.getLineOffset() + settings.yOffset;
        
        g2d.drawLine(x,y-5,x,y+settings.lineHeight+5);
    }
    
    /** Draw the lines for the play section */
    public void drawPlaySection()
    {
        GraphicBrick startBrick = graphicMap.get(playSectionStart);
        GraphicBrick endBrick = graphicMap.get(playSectionEnd);
        
        Color color = settings.playSectionColor;
        
        drawCursorLine((int)startBrick.getSlot(), startBrick.getLine(), color);
        
        int[] wrap = settings.wrapFromSlots((int)endBrick.getSlot() + endBrick.getDuration());
        if(wrap[0] == 0 && wrap[1] > 0) { // TODO, maybe making a wrap method that doesn't wrap until it's over the edge?
            wrap[0] = (int)settings.getSlotsPerLine();
            wrap[1]--;
        }
        
        drawCursorLine((int)wrap[0], (int)wrap[1]+endBrick.getLine(), color);
    }
    
    /** Draw a playline */ 
    public void drawPlayLine()
    { 
        drawCursorLine(playLineSlot, playLineLine, settings.playLineColor);
        //Maybe we should use the draw cursor line method from slots instead of two data members
    }
    
    /** Draws the grid */
    public void drawGrid()
    {
        Graphics g = buffer.getGraphics();

        for(int i = 0; i < numLines; i++) {
            g.setColor(settings.gridBGColor);
            g.fillRect(settings.xOffset,
                    settings.yOffset + i*(settings.lineHeight + settings.lineSpacing),
                    settings.getLineLength(), settings.lineHeight);
            
            for(int j = 0; j <= settings.barsPerLine; ) {
                g.setColor(settings.gridLineColor);
                g.drawLine(settings.xOffset + j*settings.measureLength,
                        settings.yOffset + i*(settings.lineHeight + settings.lineSpacing) - 5,
                        settings.xOffset + j*settings.measureLength,
                        settings.yOffset + (i+1)*settings.lineHeight + i*settings.lineSpacing + 5);
                j++;
            }
        }
        
        setSize(WIDTH, numLines * (settings.lineHeight+settings.lineSpacing));
    }
    
    /** Draws the roadmap text (title, style, tempo, etc) */
    public void drawText()
    {
        Graphics g = buffer.getGraphics();
        g.setFont(settings.titleFont);
        g.setColor(settings.textColor);
        g.drawString(view.roadMapTitle, settings.xOffset, settings.yOffset - settings.lineSpacing);
        
        /**/
        g.setFont(settings.basicFont);
        FontMetrics metrics = g.getFontMetrics();
        String text = view.style + " " + view.tempo + " bpm";
        int width = metrics.stringWidth(text);
        g.drawString(text,settings.getCutoff() - width, settings.yOffset - 5); 
        //g.drawString(text,settings.xOffset,settings.yOffset-5);
        /**/
        
    }
    
    /** Draws the brick at the given index */
    public void drawBrick(int ind)
    {
        graphicMap.get(ind).draw(buffer.getGraphics());
        repaint();
    }
    
    /** Draws all bricks */
    public void drawBricks()
    {        
        Graphics2D g = (Graphics2D)buffer.getGraphics();
        
        ArrayList<String> joinList = roadMap.getJoins();
        
        //Random r = new Random(System.nanoTime());
        for( int ind = 0; ind < graphicMap.size(); ind++ ) {
            GraphicBrick brick = graphicMap.get(ind);      
            //g.shear((r.nextDouble()-.5)/100, (r.nextDouble()-.5)/100);
            //g.setStroke(new BasicStroke(r.nextFloat()*4));
            
            int x = brick.x();
            int y = brick.y();
            
            brick.draw(g);
            
            if(ind < joinList.size() && !joinList.get(ind).isEmpty()) { //JOINS
                String joinName = joinList.get(ind);
                int length = settings.getBlockLength(brick.getBlock());
                
                FontMetrics metrics = g.getFontMetrics();
                
                int width = metrics.stringWidth(joinName) + 4;
                int offset = metrics.getAscent();
                
                int joinX = x + length - width - 4 - settings.xOffset;
                int joinY = y + joinX/settings.getLineLength() * settings.getLineOffset() +
                        settings.lineHeight;
                joinX = joinX%settings.getLineLength() + settings.xOffset;
        
                g.setColor(settings.joinBGColor);
                g.setStroke(settings.basicLine);
                
                g.fillRect(joinX+2,joinY+2, width, offset + 2);
        
                g.setColor(settings.lineColor);
                g.drawRect(joinX+2,joinY+2, width, offset + 2);
                
                g.setColor(settings.textColor);
                g.drawString(joinName,joinX+4, joinY+2+offset);
            }
                
            
            if( ind == insertLineIndex ) {
                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(2));
                g.drawLine(x, y-5, x, y+settings.lineHeight+5);
                g.setStroke(new BasicStroke(1));
            }
        }
        
        drawKeyMap();
    }
    
    /** Draws the given bricks at the given point */
    public void drawBricksAt(ArrayList<GraphicBrick> bricks, int x, int y)
    {
        Graphics g = buffer.getGraphics();
        
        int xOffset = x;
        
        for(GraphicBrick brick : bricks) {
            brick.drawAt(g, xOffset, y);
            xOffset+=brick.getLength();
        }
    }

    /** Draws the keymap */
    public void drawKeyMap()
    {
        
        Graphics g = buffer.getGraphics();
        
        long currentBeats = 0;
        long lines = 0;
        long lineBeats = 0;
        
        //Iterator<Long> number = sectionBreaks.iterator();
        //System.out.println(roadMap.getKeyMap());
        
        for( KeySpan keySpan : roadMap.getKeyMap() ) {
            drawKeySpan(keySpan, settings.xOffset + settings.getLength(lineBeats),
                    settings.yOffset + (int)(settings.getLineOffset() * lines), g);
            
            currentBeats += keySpan.getDuration();
            lineBeats += keySpan.getDuration();
            
            int[] wrap = settings.wrapFromSlots((int)lineBeats);
            lineBeats = wrap[0];
            lines += wrap[1];
            
            if(sectionBreaks.contains(currentBeats)) {
                lineBeats = 0;
                lines++;
                sectionBreaks.add(currentBeats);
            }
        }
            
    }
    
    /** Draws an individual keySpan */
    private void drawKeySpan(KeySpan keySpan, int x, int y, Graphics g)
    {       //TODO this should really be using beats and junk to find the end
            Graphics2D g2d = (Graphics2D)g;
        
            g2d.setStroke(settings.brickOutline);
            
            int blockHeight = settings.getBlockHeight();
            FontMetrics metrics = g2d.getFontMetrics();
            int fontOffset = (blockHeight + metrics.getAscent())/2;
            long key = keySpan.getKey();
            String keyName = BrickLibrary.keyNumToName(key) + " " + keySpan.getMode();
            long dur = keySpan.getDuration();
            int cutoff = settings.getCutoff();
            
            Color keyColor = settings.brickBGColor;
            
            if(key != -1)
                keyColor = settings.getKeyColor(keySpan);
            
            int[] wrap = settings.wrap(x+settings.getLength(keySpan.getDuration()));

            int endX = wrap[0];
            int lines = wrap[1];
            
            if(endX == settings.xOffset) {  // This is to prevent the last line
                endX = settings.getCutoff();// from being on the next line
                lines--;
            }
            int endY = y+lines*settings.getLineOffset();
            
            if(lines > 0) {
                g2d.setColor(keyColor);
                g2d.fillRect(x, y, cutoff - x, blockHeight);
                g2d.fillRect(settings.xOffset, endY,
                        endX-settings.xOffset, blockHeight);
                
                g2d.setColor(settings.lineColor);
                g2d.drawLine(x,y,cutoff,y);
                g2d.drawLine(x,y+blockHeight,cutoff,y+blockHeight);
                
                g2d.drawLine(settings.xOffset, endY, endX, endY);
                g2d.drawLine(settings.xOffset, endY+blockHeight,
                                       endX, endY+blockHeight);
                for(int line = 1; line < lines; line++) {
                    g2d.setColor(keyColor);
                    g2d.fillRect(settings.xOffset, y+line*settings.getLineOffset(),
                            settings.getLineLength(), blockHeight);
                    
                    g2d.setColor(settings.lineColor);
                    g2d.drawLine(settings.xOffset,
                            y+line*settings.getLineOffset(),
                            cutoff,
                            y+line*settings.getLineOffset());
                    g2d.drawLine(settings.xOffset,
                            y+line*settings.getLineOffset() + blockHeight,
                            cutoff,
                            y+line*settings.getLineOffset() + blockHeight);
                }
            } else {
                g2d.setColor(keyColor);
                g2d.fillRect(x,y, endX - x, blockHeight);
                
                g2d.setColor(settings.textColor);
                g2d.drawLine(x,y,endX,y);
                g2d.drawLine(x,y+blockHeight,endX,y+blockHeight);
            }
            
            g2d.drawLine(endX, endY, endX, endY+blockHeight);
            g2d.drawLine(x, y, x, y+blockHeight);
            
            g2d.setColor(settings.textColor);
            keyName = RoadMapSettings.trimString(keyName, cutoff - x, metrics);
            keyName = RoadMapSettings.trimString(keyName, settings.getLength(dur), metrics);
            g2d.drawString(keyName, x+2, y+fontOffset);
    }
        
    /** Draws the rollover */
    public void drawRollover()
    {
        if(rolloverPos != null) {
            int x = rolloverPos.x;
            int y = rolloverPos.y;
            int index = getBrickIndexAt(x,y);
            
            if(index != -1) {
                String text = roadMap.getBlock(index).getName();

                Graphics g = buffer.getGraphics();
                FontMetrics metrics = g.getFontMetrics();
                int width = metrics.stringWidth(text) + 4;
                int height = metrics.getAscent() + 2;

                g.setColor(settings.brickBGColor);

                g.fillRect(x, y, width, height);

                g.setColor(settings.lineColor);
                g.drawString(text, x+2, y+height - 2);
                g.drawRect(x, y, width, height);
                repaint();
            }
        }
    }
    
    /**
    * Override the paint method to draw the buffer image on this panel's graphics.
    * This method is called implicitly whenever repaint() is called.
    */
    @Override
    public void paint(Graphics g) 
    {
        g.drawImage(buffer, 0, 0, null);
    }
    
}
