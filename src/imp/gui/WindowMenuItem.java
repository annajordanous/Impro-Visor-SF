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

/*
 */

package imp.gui;

import javax.swing.*;
import java.awt.event.*;

/**
 * Class for containing a single window instance
 *
 *    contains:
 *
 *      a reference to the notate object
 *
 *      a JMenuItem for the Window menu
 *
 *      an identifying number for distinguishing windows with the same title
 *
 * Originally created by Martin Hunt, in Notate.
 * Made a separate file by Robert Keller
 */

public class WindowMenuItem
  {
  private JFrame window;

  private JMenuItem menuItem;

  private int number;

  /**
   *
   * constructs the Window menu JMenuItem for a particular Notate object
   *
   */
  public WindowMenuItem(JFrame w)
    {

    window = w;

    menuItem = new JMenuItem();

    number = WindowRegistry.windowNumber++;



    // on click, call the focus method which brings the window to front

    menuItem.addActionListener(new ActionListener()
      {
      public void actionPerformed(java.awt.event.ActionEvent evt)
        {

        window.requestFocus();

        }

      });



    // add a mnemonic if it is a known number

    switch( number )
      {

      case 1:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_1);
        break;

      case 2:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_2);
        break;

      case 3:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_3);
        break;

      case 4:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_4);
        break;

      case 5:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_5);
        break;

      case 6:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_6);
        break;

      case 7:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_7);
        break;

      case 8:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_8);
        break;

      case 9:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_9);
        break;

      case 10:
        menuItem.setMnemonic(java.awt.event.KeyEvent.VK_0);
        break;

      default:
        break;

      }

    }

  // get the menu item for this object
  public JMenuItem getMI()
    {

    return getMI(null);

    }

  public JMenuItem getMI(JFrame current)
    {

    String title = window.getTitle();

    if( title.equals("") )
      {
      title = "Untitled";
      }


    if( current == window )
      {

      menuItem.setText(number + ": " + title + " (current window)");

      }
    else
      {

      menuItem.setText(number + ": " + title);

      }

    return menuItem;

    }

  public JFrame getWindow()
    {

    return window;

    }

  }
