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
import imp.util.Trace;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.*;

/**
 * Created in June 2013  
 * @author Hayden Blauzvern (Based on Robert Keller's C++ implementation of a 
 *                           multi-layer neural network for backpropagation)
 */
public class Critic {
    
    // output should always be 1
    private final int outputDimension = 1;
    private int lickLength = 0;
    
    public static final int ONLINE = 0;
    public static final int BATCH = 1;
    public static final int RPROP = 2;
    
    public enum MODE
    {
        ONLINE, BATCH, RPROP    
    }
    
    String modeName[] = {"on-line", "batch", "rprop"};
    
    public static final int NONE = 0;
    public static final int GOAL_REACHED = 1;
    public static final int LIMIT_EXCEEDED = 2;
    public static final int LACK_OF_PROGRESS = 3;
    public static final int WEIGHTS_ALREADY_SET = 4;
    
    public enum TERMINATION_REASON 
    {
        NONE, GOAL_REACHED, LIMIT_EXCEEDED, LACK_OF_PROGRESS, WEIGHTS_ALREADY_SET
    }
    
    String reasonName[] = {"", "goal reached", "limit exceeded", 
                           "lack of progress", "testing with set weights"};
    
    public final MODE defaultMode = MODE.ONLINE;
    public final double defaultGoal = 0.01;
    public final double defaultLearningRate = 0.01;
    public final int defaultEpochLimit = 20000;
    public final int defaultTrace = 1;
    public final int minimumParameters = 9;

    ActivationFunction hardlim  = new Hardlim();
    ActivationFunction hardlims = new Hardlims();
    ActivationFunction logsig   = new Logsig();
    ActivationFunction purelin  = new Purelin();
    ActivationFunction satlin   = new Satlin();
    ActivationFunction satlins  = new Satlins();
    ActivationFunction tansig   = new Tansig();
    
    private Network network;
    
    public Critic()
    {
        network = null;
    }

    private ActivationFunction getLayerType(String name)
    {
        name = name.toLowerCase();
        if( "hardlim".equals(name) )  return hardlim;
        if( "hardlims".equals(name) ) return hardlims;
        if( "logsig".equals(name) )   return logsig;
        if( "purelin".equals(name) )  return purelin;
        if( "satlin".equals(name) )   return satlin;
        if( "satlins".equals(name) )  return satlins;
        if( "tansig".equals(name) )   return tansig;

        System.out.println("error, unrecognized function: " + name);
        return null;
    }

    public void getSamples(String inputFile, AtomicInteger outputDimension, 
            AtomicInteger inputDimension, ArrayList<Sample> listOfSamples) throws Exception
    {
        File f = new File(inputFile);
        
        if (!f.exists())
        {
            //do something
        }
        
        Scanner in = new Scanner(f);
        
        // Save input and output dimensions
        outputDimension.set(in.nextInt());
        inputDimension.set(in.nextInt());
        in.nextLine();
        
        while (in.hasNextLine())
        {
            String line = in.nextLine();
            Scanner reader = new Scanner(line);
            Sample thisSample = new Sample(outputDimension.get(), inputDimension.get());
            
            for (int i = 0; i < outputDimension.get(); i++)
                thisSample.setOutput(i, reader.nextDouble());
            for (int j = 0; j < inputDimension.get(); j++)
                thisSample.setInput(j, reader.nextDouble());
            
            listOfSamples.add(thisSample);
        }
    }
    
    public StringBuilder showAndCountSamples(String title, 
            ArrayList<Sample> samples, AtomicInteger nSamples)
    {
        StringBuilder output = new StringBuilder();
        
        if (Trace.atLevel(4))
            System.out.println("\n" + title + " samples are:\n");

        for (Sample s : samples)
        {
            nSamples.getAndIncrement();
            
            if (Trace.atLevel(4))
                System.out.println(nSamples.get() + ": "+ s.toString());
        }
        
        output.append(nSamples.get()).append(" ").append(title).append(" samples");
        output.append("\n");
        
        return output;
    }
    
    //training
    private String trainingFile;
    
    //epoch
    private int epochLimit;
    
    //rate
    private double rate;
    
    //goal
    private double goal;
    
    //mode
    private int modeInt;
    private MODE mode;
    
    //weights
    private String weightFile;
    
    //layers
    private int numberLayers;
    private int[] layerSize;
    private ActivationFunction[] layerType;
    
    public StringBuilder trainNetwork(Object ... args)
    {
        StringBuilder output = new StringBuilder();
        
        // If we have weights already, set this to true
        boolean fixWeights = false;
        
        //Asumed
        trainingFile = (String) args[0];
        
        if (args[1] != null)
            epochLimit = Integer.parseInt( (String) args[1]);
        else
            epochLimit = defaultEpochLimit;
        
        if (args[2] != null)
            rate = Double.parseDouble( (String) args[2]);
        else
            rate = defaultLearningRate;
        
        if (args[3] != null)
            goal = Double.parseDouble( (String) args[3]);
        else
            goal = defaultGoal;
        
        if (args[4] != null)
        {
            int modeTemp = Integer.parseInt( (String) args[4]);
            mode = MODE.values()[modeTemp];
        }
        else
            mode = defaultMode;
        
        if (args[5] != null)
            weightFile = (String) args[5];
        else
            weightFile = trainingFile + ".weights.save";
        
        numberLayers = (Integer) args[6];
        LinkedHashMap<Integer, String> layerData = (LinkedHashMap<Integer, String>) args[7];
        layerSize = new int[numberLayers];
        layerType = new ActivationFunction[numberLayers];
        
        int j = 0;
        for (Integer i : layerData.keySet())
        {
            layerSize[j] = i;
            layerType[j] = getLayerType(layerData.get(i));
            j++;
        }
        
        // Check if we're using a file with already-set weights
        File f = new File(ImproVisor.getVocabDirectory(), weightFile);
        if (f.length() != 0)
            fixWeights = true;
        
        output.append(numberLayers).append(" layers structured (from input to output) as: \n");

        for( int i = 0; i < numberLayers; i++ )
        {
            output.append("    ").append(layerType[i].getName()).append(" (").append(layerSize[i]).append(" " + "neurons" + ")");
            output.append("\n");
        }

        output.append("\n");
        output.append("epoch limit = ").append(epochLimit).append("\n");
        output.append("specified rate = ").append(rate).append("\n");
        output.append("goal = ").append(goal).append("\n");
        output.append("mode = ").append(modeName[mode.ordinal()]).append("\n");
        output.append("\n");
            
        AtomicInteger inputD = new AtomicInteger();
        AtomicInteger outputD = new AtomicInteger();
        ArrayList<Sample> trainingSamples = new ArrayList<Sample>();
        
        try 
        {
            getSamples(trainingFile, outputD, inputD, trainingSamples);
        }
        catch (Exception e)
        {
            
        }
        
        AtomicInteger nTrainingSamples = new AtomicInteger();
        output.append(showAndCountSamples("training", trainingSamples, nTrainingSamples));
        
        Network thisNetwork = new Network(numberLayers, layerSize, layerType, inputD.get());
     
        if (fixWeights)
        {
            try 
            {
                BufferedReader in = new BufferedReader(new FileReader(
                        new File(ImproVisor.getVocabDirectory(), weightFile)));
                thisNetwork.fixWeights(in);
                in.close();
            }
            catch (Exception e) 
            {
                
            }    
        }
        
        if (Trace.atLevel(4))
        {
            System.out.println("\nInitial Weights:");
            System.out.println(thisNetwork.showWeights("initial"));
        }
        
        output.append("\nTraining begins with epoch 1.\n");
        
        int epoch = 1;
        double sse;
        double mse = 1 + goal;
        double oldmse = 2 + goal;
        double etaPlus = 1.2; //for rprop
        double etaMinus = 0.5;
        
        TERMINATION_REASON reason = TERMINATION_REASON.NONE;
        
        if (fixWeights)
        {
            reason = TERMINATION_REASON.WEIGHTS_ALREADY_SET;
        }
        
        // The total number of output values across all samples and network outputs
        
        int interval = 1;
        
        // Training loop
        while ( reason == TERMINATION_REASON.NONE)
        {
            sse = 0;
            switch (mode)
            {
                case RPROP:
                case BATCH:
                    thisNetwork.clearAccumulation();;
                case ONLINE:
            }
            
            for (Sample sample : trainingSamples)
            {
                // Forward propagation
                thisNetwork.fire(sample);
                double sampleSSE = thisNetwork.computeError(sample);
                sse += sampleSSE;
                
                if (Trace.atLevel(4))
                {
                    System.out.print("\nforward output: ");
                    thisNetwork.showOutput();
                    System.out.print(sample);
                    System.out.printf(", sample sse: % 6.3f\n", sampleSSE);
                }
                
                // backpropagation
                thisNetwork.setSensitivity(sample);
                
                switch (mode)
                {
                    case RPROP:
                        thisNetwork.accumulateGradient(sample);
                    case BATCH:
                        thisNetwork.accumulateWeights(sample, rate);
                    case ONLINE:
                        thisNetwork.adjustWeights(sample, rate);
                }
                
                if (Trace.atLevel(4))
                {
                    thisNetwork.showWeights("current");
                }
            }
            
            switch (mode)
            {
                case RPROP:
                    thisNetwork.adjustByRprop(etaPlus, etaMinus);
                case BATCH:
                    thisNetwork.installAccumulation();
                case ONLINE:
            }

            mse = sse / nTrainingSamples.get();

            double usageError = 0;

            // Evaluate with use
            for (Sample sample : trainingSamples)
            {
                thisNetwork.use(sample);
                usageError += (thisNetwork.computeUsageError(sample) != 0) ? 1 : 0;
            }
            
            output.append(String.format("end epoch %d, mse: %10.8f %s, usage error: %d/%d (%5.2f%%)\n",
                            epoch, 
                            mse, 
                            mse < oldmse ? "decreasing" : "increasing",
                            (int)usageError,
                            nTrainingSamples.get(),
                            100 * usageError / nTrainingSamples.get()));
            output.append("\n");

            epoch++;

            if (mse <= goal)
            {
                reason = TERMINATION_REASON.GOAL_REACHED;
            }
            else if (epoch > epochLimit)
            {
                reason = TERMINATION_REASON.LIMIT_EXCEEDED;
            }

            oldmse = mse;
        }
        
        output.append("Training ends at epoch ").append(epoch).append(", ").append(reasonName[reason.ordinal()]).append(".");
        output.append("\n").append("\n");
        output.append("Final weights:");
        output.append("\n");
        
        output.append(thisNetwork.showWeights("Final"));
        
        try 
        {
            PrintWriter out = new PrintWriter(new File(ImproVisor.getVocabDirectory(), weightFile));
            out.print(inputD.get() + " " + numberLayers);
            for (int i = 0; i < numberLayers; i++)
                out.print(" " + layerSize[i] + " " + layerType[i].getName());            
            out.println();
            thisNetwork.printWeights("final", out);
            out.close();
        } 
        catch (Exception e) 
        {
            
        }
        
        return output;
    }
    
    
    // Different steps/usages
    // -From input file, generate weight file
    // -From input file, generate weight file and network?
    // -From weight file, generate appropriate network
    // -From single Sample, use network and grade
    
    
    
    
    
    
    
    
    
    
 
 
 
    private Sample parseData (String data, Sample s)
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
                
                try 
                {
                     s.setInput(i - 1, dataInput); //Had one time issue with out of
                                                   //range Sample input (int = 342)
                                                   //Happens if the data input is too large
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
        return s;
    }
    
    public double filter(String data)
    {
        //Get sample inputDimension
        int inputDimension = (data.length() - 4) / 2;
        
        Sample s = new Sample(outputDimension, inputDimension);
        
        parseData(data, s);
        network.use(s);
        return network.getSingleOutput();
    }

    public int getLickLength()
    {
        return lickLength;
    }
    
    public Network getNetwork()
    {
        return network;
    }
    
    public StringBuilder prepareNetwork(String weights) throws Exception
    {
        try 
        {
            File file = new File(ImproVisor.getVocabDirectory(), weights);
            BufferedReader in = new BufferedReader(new FileReader(file));
            StringBuilder output = new StringBuilder();
            
            // Parse first line, containing network stats
            String[] networkInfo = in.readLine().split(" ");
            int currPos = 0;
            int thisInputDimension = Integer.parseInt(networkInfo[currPos++]);
            lickLength = (thisInputDimension * 2) + 4;
            int thisNumLayers = Integer.parseInt(networkInfo[currPos++]);
            int[] thisLayerSize = new int[thisNumLayers];
            ActivationFunction[] thisLayerType = new ActivationFunction[thisNumLayers];
            
            int j = 0;
            int layers = currPos + 2 * thisNumLayers; 
            for (int i = 2; i < layers; i+=2, j++)
            {
                thisLayerSize[j] = Integer.parseInt(networkInfo[currPos++]);
                thisLayerType[j] = getLayerType(networkInfo[currPos++]);
            }

            output.append("length of input: ").append(thisInputDimension).append("\n");
            output.append(thisNumLayers).append(" layers structured (from input to output) as: \n");
            for( int i = 0; i < thisNumLayers; i++ )
            {
                output.append("    ").append(thisLayerType[i].getName()).append(" (").append(thisLayerSize[i]).append(" " + "neurons" + ")");
                output.append("\n");
            }
            
            network = new Network(thisNumLayers, thisLayerSize, thisLayerType, thisInputDimension);
            network.fixWeights(in);
            output.append(network.showWeights("final"));
            in.close();
            
            return output;
        }
        catch (Exception e) 
        {
            throw new Exception(e);
        }
    }
}
