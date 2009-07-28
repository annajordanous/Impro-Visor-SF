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

package imp.data;

/**
 * A Pattern is base class for accompaniment patterns that have
 * a certain duration and weight.
 * This simplifies the code for choosing a random pattern
 * based on duration and weight.
 * @see Style
 * @author Stephen Jones, converted by Robert Keller from Interface to Class 12/1/2007
 */

public class Pattern {
    /**
     * the Style this DrumPattern belongs to
     */
  
    protected Style style;

    /**
     * the weight
     */
    protected float weight = 10;

    /**
     * Gets the duration. This is intended to be overridden by specific types of Pattern.
     * @return the duration
     */
    
    public int getDuration()
    {
      return 0;
    }

    /**
     * Gets the weight.
     * @return the weight
     */
    
    public float getWeight()
    {
      return weight;
    }

    /**
     * Gets the Style.
     * @return the style
     */
    
    public Style getStyle()
    {
      return style;
    }

    /**
     * Sets the weight.
     * @param w         a float containing the weight
     */
    
    public void setWeight(float w)
    {
      weight = w;
    }

    /**
     * Sets the style.
     * @param s         the Style
     */
    
    public void setStyle(Style s)
    {
      style = s;
    }
}
