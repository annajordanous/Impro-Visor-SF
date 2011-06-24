/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2010 Robert Keller and Harvey Mudd College
 * XML export code is also Copyright (C) 2009-2010 Nicolas Froment (aka Lasconic).
 *
 * Impro-Visor is free software; you can redistribute it and/or modifyc
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
package imp;

import imp.com.OpenLeadsheetCommand;
import imp.data.Score;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

/**
 * RecentFiles keeps track of the recent leadsheets that the user has opened.
 * This class writes a text file to the 'vocab' folder in order to keep track of
 * the previous leadsheets opened.
 * @author ImproVisor
 */
public class RecentFiles {
    /**
     * filname name and path created for the text file
     */
    private static String filename;
    
    /**
     * path is the name of the directory for the current leadsheet opened
     */
    private String path;
    
    /**
     * Stack that holds the paths in the correct order
     */
    private Stack stk;
    
    /**
     * Temporary stack used to maintain the order of the list
     */
    private Stack tempStk;
    
    /**
     * Integer to denote the max number of leadsheets that the program will keep track of
     */
    private int MAX_RECENT_FILES;
    
    public RecentFiles(String pathName)
    {
        filename = "vocab/RecentFiles.txt";
        stk = new Stack();
        tempStk = new Stack();
        path = pathName;
        stk.push(path);
        path = (String)stk.peek();
        MAX_RECENT_FILES = 11;
    }
    
    public RecentFiles()
    {
        filename = "vocab/RecentFiles.txt";
        stk = new Stack();
        tempStk= new Stack();
        MAX_RECENT_FILES = 11;
    }
    
    /**
     * Method used in two imp.com classes: OpenLeadSheetCommand & SaveLeadsheetCommand
     * This method generates the text file named "RecentFiles.txt" inside the 'vocab'
     * folder. The text file keeps track of the most recently opened/edited/created leadsheets
     * up to MAX_RECENT_FILES
     * @throws IOException 
     */
    public void writeNewFile() throws IOException
    {
        try{
            FileInputStream inStream = new FileInputStream(filename);
            DataInputStream datIn = new DataInputStream(inStream);
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(datIn));
            String line;
            String entered;
            while((line = buffRead.readLine()) != null)
            {
                tempStk.push(line);
            }
            String rec;
            while(!stk.empty())
            {
                stk.pop();
            }
            while(!tempStk.empty())
            {
                rec= (String)tempStk.pop();
                stk.push(rec);
            }
            Stack cleared = removeAnyPrev(stk, path);
            if(cleared.size() >= MAX_RECENT_FILES)
            {
                cleared = truncateLast(cleared);
                cleared.push(path);
            }
            else
            {
                cleared.push(path);
            }
            BufferedWriter recentFiles = new BufferedWriter(new FileWriter(filename));
            while(!cleared.empty())
            {
                recentFiles.write((String)cleared.pop());
                recentFiles.newLine();
            }
            recentFiles.close();
        }
        catch(Exception e){
        BufferedWriter recentFiles = new BufferedWriter(new FileWriter(filename));
        recentFiles.write(path);
        recentFiles.close();
        }
    }
    
    public void openMostRecent()
    {
        File mostRec;
        try{
            FileInputStream inStream = new FileInputStream(filename);
            DataInputStream datIn = new DataInputStream(inStream);
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(datIn));
            path= buffRead.readLine();
            datIn.close();
        }catch(Exception e){
        }
        mostRec = new File(path);
        Score score = new Score();
        OpenLeadsheetCommand com = new OpenLeadsheetCommand(mostRec, score);
    }
    
    
    /**
     * Returns the first path on top of the list of "RecentFiles.txt"
     * Used so that when Impro Visor is opened, it opens with the
     * most recent leadsheet.
     * @return 
     */
    public String getFirstPathName()
    {
        try{
            FileInputStream inStream = new FileInputStream(filename);
            DataInputStream datIn = new DataInputStream(inStream);
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(datIn));
            path = buffRead.readLine();
            datIn.close();
            return path;
        }catch(Exception e){
            return path;
        }
    }
    
    
    /**
     * Reads the text file "RecentFiles.txt" and converts it into a stack
     * in the correct order
     * @return 
     */
    public Stack getMostRecentFiles()
    {
        try{
            FileInputStream inStream = new FileInputStream(filename);
            DataInputStream datIn = new DataInputStream(inStream);
            BufferedReader buffRead = new BufferedReader(new InputStreamReader(datIn));
            String line;
            String entered;
            Stack temp = new Stack();
            while((line = buffRead.readLine()) != null)
            {
                temp.push(line);
            }
            String rec;
            Stack recentLeads = new Stack();
            while(!temp.empty())
            {
                rec= (String)temp.pop();
                recentLeads.push(rec);
            }
            return recentLeads;
        }
        catch(Exception e){
            return null;
        }
    }
    
    
    /**
     * Method removes any duplicate paths that might occur
     * when opening a previous file
     * @param a
     * @param dir
     * @return 
     */
    public Stack removeAnyPrev(Stack a, String dir)
    {
        Stack temporary = new Stack();
        Stack temporary2 = new Stack();
        String line = "";
        while(!a.empty())
        {
            line = (String)a.pop();
            if(!line.equals(dir))
            {
                temporary.push(line);
            }
        }
        while(!temporary.empty())
        {
            temporary2.push(temporary.pop());
        }
        return temporary2;
    }
    
    /**
     * Returns the number of leadsheets in "RecentFiles.txt"
     * @return 
     */
    public int getSize()
    {
        Stack temp = getMostRecentFiles();
        if(temp == null)
        {
            return 0;
        }
        else
        {
            return temp.size();
        }
    }
    
    
    /**
     * Converts the current stack into an Array so
     * it's easier to generate the popup menu within
     * imp.gui class 'notate'
     * @return 
     */
    public String[] convertToArray()
    {
        Stack recFiles = getMostRecentFiles();
        String list[] = new String[getSize()-1];
        recFiles.pop();
        for(int i=0; i<getSize()-1; i++)
        {
            list[i] = (String)recFiles.pop();
        }
        return list;
    }
    
    
    /**
     * Gets rid of the very last path in "RecentFiles.txt".
     * Used when the number of most recent leadsheets exceeds
     * MAX_RECENT_FILES.
     * @param a
     * @return 
     */
    public Stack truncateLast(Stack a)
    {
        Stack temp = new Stack();
        Stack finalStk = new Stack();
        while(!a.empty())
        {
            temp.push(a.pop());
        }
        temp.pop();
        while(!temp.empty())
        {
            finalStk.push(temp.pop());
        }
        return finalStk;
    }
    
}