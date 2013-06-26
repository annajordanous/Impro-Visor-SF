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

import imp.ImproVisor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created in June 2013  
 * @author Hayden Blauzvern (Based on Robert Keller's C++ implementation of a 
 *                           multi-layer neural network for backpropagation)
 */
public class Critic {
    
    // Set values based on how the network was trained
    private static final int numberLayers = 2;
    private static final int[] layerSize = {64, 1};
    private static final ActivationFunction[] layerType = {new Logsig(), new Logsig()};
    private static final int inputDimension = 342;
    private static final int outputDimension = 1;
    private static final String WEIGHTS_FILE = "licks.weights.save";
   
    // FIX: Might need to make more global
    public static Network network = prepareNetwork(WEIGHTS_FILE);   
 
    private static Sample parseData (String data, Sample s)
    {
        String[] inputs = data.split(" ");
        for (int i = 0; i < inputs.length; i++)
        {
            if (i == 0)
            {
                double gradeOutput = Double.parseDouble(inputs[i]);
                s.setOutput(0, gradeOutput);
            }
            else if (inputs[i].length() != 1)
            {
                break;
            }
            else
            {
                double dataInput = Double.parseDouble(inputs[i]);
                s.setInput(i - 1, dataInput); //Had one time issue with out of
                                              //range Sample input (int = 342)
            }
        }
        return s;
    }
    
    public static double filter(String data, Network network)
    {
        Sample s = new Sample(outputDimension, inputDimension);
        
        parseData(data, s);
        network.use(s);
        return network.getSingleOutput();
    }

    /**
     * @param args the command line arguments
     */
    public static Network prepareNetwork(String weights)
    {
        try 
        {
            File weightFile = new File(ImproVisor.getVocabDirectory(), weights);
            Network networks = new Network(numberLayers, layerSize, layerType, inputDimension);
            BufferedReader in = new BufferedReader(new FileReader(weightFile));
            networks.fixWeights(in);
            in.close();
            return networks;
        }
        catch (Exception e) 
        {
            System.out.println("Missing the weight file, "
                    + "need to train the network offline first.");
            return null;
        }
    }
}
