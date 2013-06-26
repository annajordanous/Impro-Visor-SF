/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2013 Robert Keller and Harvey Mudd College
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

package imp.neuralnet;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created in June 2013  
 * @author Hayden Blauzvern
 */
public class Network {
    
    private int numberLayers;
    private int lastLayer;
    private Layer[] layer;
    
    public Network(int numberLayers, int[] layerSize, ActivationFunction[] type, int inputDimension)
    {
        this.numberLayers = numberLayers;
        this.lastLayer = numberLayers - 1;

        layer = new Layer[numberLayers];
        if (numberLayers < 1)
        {
            System.out.println("Too few layers, aborting.");
            System.exit(1);
        }
        
        layer[0] = new Layer(0, layerSize[0], type[0], inputDimension);
        
        for (int i = lastLayer - 1; i > 0; i--)
            layer[i] = new Layer(i, layerSize[i], type[i], layerSize[i-1]);
        
        layer[lastLayer] = new Layer(lastLayer, layerSize[lastLayer], 
                                      type[lastLayer], layerSize[lastLayer-1]);   
    }
    
    public void use(Sample sample)
    {
        layer[0].use(sample);
        
        for (int i = 1; i < numberLayers; i++)
            layer[i].use(layer[i-1]); //Does this work?
    }
    
    public void showOutput()
    {
        layer[lastLayer].showOutput();
    }
    
    public double getSingleOutput()
    {
        return layer[lastLayer].getSingleOutput();
    }
    
    public void showWeights(String message)
    {
        for (int i = 0; i < numberLayers; i++)
            layer[i].showWeights(message);
    }
    
    public void fixWeights(BufferedReader in) throws IOException
    {
        for (int i = 0; i < numberLayers; i++)
        {
            String line;
            String mem = "";
            
            for(int j = 0; j < layer[i].getSize(); j++)
            {
                line = in.readLine().toLowerCase();

                if (line.contains("layer " + i))
                    mem += line + "\n";
            }

            layer[i].fixWeights(mem);       
        }
    }
}
