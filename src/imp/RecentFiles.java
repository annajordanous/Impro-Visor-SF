/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author ImproVisor
 */
public class RecentFiles {
    private static String filename;
    private String path;
    private Stack stk;
    private Stack tempStk;
    
    public RecentFiles(String pathName)
    {
        filename = "RecentFiles.txt";
        stk = new Stack();
        tempStk = new Stack();
        path = pathName;
        stk.push(path);
        path = (String)stk.peek();
    }
    
    public RecentFiles()
    {
        filename = "RecentFiles.txt";
        stk = new Stack();
        tempStk= new Stack();
    }
    
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
            cleared.push(path);
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
    
    public String getPathName()
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
    
}