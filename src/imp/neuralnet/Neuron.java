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
public class Neuron {
    
    private int neuronIndex;
    private int layerIndex;
    private ActivationFunction type;
    private int numberOfInputs;
    private double[] weights;
    private double output;
    private double sensitivity;
    private double net;

    
    public Neuron(int layerIndex, int neuronIndex, ActivationFunction type, int numberOfInputs)
    {
        this.neuronIndex = neuronIndex;
        this.layerIndex = layerIndex;
        this.type = type;
        this.numberOfInputs = numberOfInputs;

        weights = new double[numberOfInputs+1]; 
    }
    
    public void use(Source source)
    {
        net = weights[numberOfInputs];
        
        for (int j = 0; j < numberOfInputs; j++)
            net += weights[j]*source.get(j);
        
        output = type.use(net);
    }
    
    public double getOutput()
    {
        return output;
    }

    public void showWeights(String title) 
    {
        System.out.printf("Layer %d Neuron %d %s Weights: ", layerIndex, neuronIndex, title);
        
        for(double d : weights)
            System.out.printf("%9.4f", d);
        
        System.out.printf(" (Bias) Sensitivity: % 7.4f \n", sensitivity);
    }

    public void fixWeights(String input) 
    {
        Scanner in = new Scanner(input);
        String line;
        
        // Throw away wording before weights.
        for (int i = 0; i < 6; i++)
            in.next(); //Might need whitespace delimeter
        
        for (int i = 0; i <= numberOfInputs; i++)
        {
            line = in.next(); // Should work for any amount of whitespace
            double weight = Double.parseDouble(line);
            weights[i] = weight;
        }
        
        // Throw away wording before the bias.
        for (int i = 0; i < 2; i++)
            in.next();
        
        line = in.next();
        sensitivity = Double.parseDouble(line);
    }
}
