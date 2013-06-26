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

import java.util.Scanner;

/**
 * Created in June 2013  
 * @author Hayden Blauzvern
 */
public class Layer implements Source{
    
    private int layerIndex;
    private int numberInLayer;
    private Neuron[] neurons;
    
    public Layer(int layerIndex, int numberInLayer, ActivationFunction type, int numberOfInputs)
    {
        this.layerIndex = layerIndex;
        this.numberInLayer = numberInLayer;
        
        neurons = new Neuron[numberInLayer];
       
        for (int i = 0; i < numberInLayer; i++)
        {
            neurons[i]= new Neuron(layerIndex, i, type, numberOfInputs);
        }
    }
    
    public int getIndex()
    {
        return layerIndex;
    }
    
    public int getSize()
    {
        return numberInLayer;
    }
    
    /**
     *
     * @param i
     * @return
     */
    @Override
    public double get(int i)
    {
        return neurons[i].getOutput();
    }
    
    public void use(Source source)
    {
        for (Neuron n : neurons)
            n.use(source);
    }
    
    public void showOutput()
    {
        for (Neuron n : neurons)
            System.out.println(n.getOutput() +" ");
    }
    
    // To be used only with a single neuron in a layer
    public double getSingleOutput()
    {
        return neurons[0].getOutput();
    }
    
    public void showWeights(String message)
    {
        for (Neuron n : neurons)
            n.showWeights(message);
    }
    
    public void fixWeights(String input)
    {
        Scanner in = new Scanner(input);
        
        for (Neuron n : neurons)
        {
            String line = in.nextLine();
            n.fixWeights(line);
        }
    }
}
