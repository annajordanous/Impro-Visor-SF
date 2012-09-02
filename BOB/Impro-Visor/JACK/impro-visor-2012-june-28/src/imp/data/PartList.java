/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imp.data;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author keller
 */
public class PartList
{
private ArrayList<MelodyPartAccompanied> theList;

public PartList(int size)
  {
  theList = new ArrayList<MelodyPartAccompanied>(size);
  }

public ListIterator<MelodyPartAccompanied> listIterator()
  {
    return theList.listIterator();
  }

public int size()
  {
    return theList.size();
  }

public MelodyPartAccompanied get(int i)
  {
    return theList.get(i);
  }

/**
 * Caution: set seems not to work, for reasons I don't understand.
 * The capacity doesn't increase as prescribed.
 *
 @param i
 @param part
 */
public void set(int i, MelodyPartAccompanied part)
  {
    theList.ensureCapacity(i);
    for( int j = theList.size(); j <= i; j++ )
      {
        theList.add(null);
      }
    theList.set(i, part);
  }

public void setSize(int size)
  {
    theList.ensureCapacity(size);
    for( int j = theList.size(); j < size; j++ )
      {
        theList.add(null);
      }
  }

public void add(MelodyPartAccompanied part)
  {
    theList.add(part);
  }

public void remove(int i)
  {
    theList.remove(i);
  }
}
