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
import java.util.ArrayList;
import java.util.Random;
import polya.Polylist;

/**
* This class is used to take a Drum Part object as obtained from 
* the Import class, and create a set of Patterns that can be used 
* in the RepresentativeDrumRules class.
*
* Brandy McMenamy and James Thomas Herold
* 7/18/2007 
*/

public class RepresentativeBassRules{
	private boolean debug = false;
        /**
	* The number of rules to be taken from the remaining unique rules
	* is currently hardcoded as a percentage.
	*/
	private double percentageOfClusters = 0.25;
        private int maxNumberOfClusters = -1;
	private int numberOfUniqueRules;
        private int numberOfRules=0; //Used in normalizing weights 0-100
	private BassChronotonic c;
        /**
         * The original rules with only one pitch value per note according
         * to the "most useful" value
         * ex: BNA8 stored as B8
         **/
	private ArrayList<String> simplifiedPitchesRules = new ArrayList<String>();
        /**
         * Every rule in this array list is unique
         **/
	private ArrayList<String> sansDuplicatesRules = new ArrayList<String>();
        /**
         * Each Section contains rules of the same length, one Section
         * per every unique length
         */
	//private ArrayList<Section> sections = new ArrayList<Section>();
        private ArrayList<Section> sections = new ArrayList<Section>();
        /**
         * The final array accessed by the StyleGenerator GUI
         **/
	private ArrayList<BassPatternObj> bassPatterns = new ArrayList<BassPatternObj>();
        
        //private ArrayList<String> duplicates = new ArrayList<String>();
        
        private ArrayList<RawRule> uniqueRules = new ArrayList<RawRule>();
        
       /**
         * This is a shameless hack that gives the Style GUI access to 
         * makeBassPatternObj without generating new rules
         **/
	public RepresentativeBassRules(boolean thisIsAHack) {
            //do nothing!
        }
        
        public ArrayList<String> getSimplifiedPitchesRules(){
            return simplifiedPitchesRules;
        }
        
        
        public RepresentativeBassRules(){
            try{
        	ImportBass im = new ImportBass();
                if(im.canContinue == true) {
                    BassPatternExtractor b = new BassPatternExtractor();
                    if(b.canContinue == true) {
                        c = new BassChronotonic();

                        initialize();

                        if(debug){
                                System.out.println("\n## Initial ##");
                                for(int i = 0; i < MIDIBeast.originalBassRules.size(); i++)
                                        System.out.println(MIDIBeast.originalBassRules.get(i));
                        }

                        simplifyRulePitches();
                        if(debug){
                                System.out.println("\n## After simplifyRulePitches() ##");
                                for(int i = 0; i < simplifiedPitchesRules.size(); i++)
                                        System.out.println(simplifiedPitchesRules.get(i));
                        }
                        //FIX: Broken: if(MIDIBeast.maxBassPatternLength!=0.0) truncateBassPatterns();
                        processDuplicateRules();
                        if(debug){
                                System.out.println("\n## After processDuplicateRules() ##");
                                for(int i = 0; i < sansDuplicatesRules.size(); i++)
                                        System.out.println(sansDuplicatesRules.get(i));
                                System.out.println("Resulting patterns");
                                for(int i = 0; i < uniqueRules.size(); i++)
                                        System.out.println(uniqueRules.get(i));
                        }

                        splitUpIntoSections();
                        if(debug){
                                System.out.println("\n## After splitUpIntoSections() ##");
                                for(int i = 0; i < sections.size(); i++){
                                        System.out.println("Section " + i);
                                        for(int j = 0; j < sections.get(i).size(); j++)
                                                System.out.println(sections.get(i).getRule(j));
                                }
                        }

                        pruneSections();

                        if(debug){
                                System.out.println("\n## After pruneSections() ##");
                                for(int i = 0; i < sections.size(); i++)
                                        System.out.println("Number of clusters for section " + i + ": " + sections.get(i).getNumberOfClusters());
                        }

                        findTenativeRepresentatives();
                        if(debug){
                                System.out.println("\n## After findTenativeRepresenatives() ##");
                                System.out.println("sections.size(): " + sections.size());
                                for(int i = 0; i < sections.size(); i++){
                                        System.out.println("Representative rules for section: " + i);
                                        for(int j = 0; j < sections.get(i).getClusters().size(); j++)
                                                System.out.println(sections.get(i).getCluster(j));
                                }
                        }
                        cluster();
                        if(debug){
                                System.out.println("\n## After cluster() ##");
                                for(int i = 0; i < sections.size(); i++){
                                        System.out.println("Section: " + i);
                                        for(int j = 0; j < sections.get(i).getNumberOfClusters(); j++){
                                                System.out.println("\tCluster: " + j);
                                                for(int k = 0; k < sections.get(i).getClusters().get(j).getRules().size(); k++)
                                                        System.out.println("\t\t" + sections.get(i).getCluster(j).getRule(k));
                                        }	
                                }
                        }

                        getRepresentativeRules();
                        if(debug) {
                            System.out.println("\n\n### Resulting Patterns ###");
                            for(int i = 0; i < bassPatterns.size(); i++)
                                    System.out.println(bassPatterns.get(i));
                       }
                    }
                }
                
                if(debug) {
                    System.out.println("\n### Errors that occurred during bass generation: ###");
                    ArrayList<String> err = MIDIBeast.errors;
                    for(int i = 0; i < err.size(); i++) {
                        System.out.println(i+1 + ": " + err.get(i));
                    }
                    
                    
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                MIDIBeast.addError("Sorry, there was an unknown internal error while generating " +
                    "the bass patterns.");    
            }
	}
        
        public RepresentativeBassRules(double startBeat, double endBeat, int maxNumberOfClusters, jm.music.data.Part selectedPart){
            try{
        	if(maxNumberOfClusters != 0)
                    this.maxNumberOfClusters = maxNumberOfClusters;
                ImportBass im = new ImportBass(startBeat, endBeat, selectedPart);
                if(im.canContinue == true) {
                    BassPatternExtractor b = new BassPatternExtractor(startBeat, endBeat);
                    if(b.canContinue == true) {
                        c = new BassChronotonic();

                        initialize();

                        if(debug){
                                System.out.println("\n## Initial ##");
                                for(int i = 0; i < MIDIBeast.originalBassRules.size(); i++)
                                        System.out.println(MIDIBeast.originalBassRules.get(i));
                        }

                        simplifyRulePitches();
                        if(debug){
                                System.out.println("\n## After simplifyRulePitches() ##");
                                for(int i = 0; i < simplifiedPitchesRules.size(); i++)
                                        System.out.println(simplifiedPitchesRules.get(i));
                        }

                        processDuplicateRules();
                        if(debug){
                                System.out.println("\n## After processDuplicateRules() ##");
                                for(int i = 0; i < sansDuplicatesRules.size(); i++)
                                        System.out.println(sansDuplicatesRules.get(i));
                                System.out.println("Resulting patterns");
                                for(int i = 0; i < bassPatterns.size(); i++)
                                        System.out.println("\t" + bassPatterns.get(i));
                        }

                        splitUpIntoSections();
                        if(debug){
                                System.out.println("\n## After splitUpIntoSections() ##");
                                for(int i = 0; i < sections.size(); i++){
                                        System.out.println("Section " + i);
                                        for(int j = 0; j < sections.get(i).size(); j++)
                                                System.out.println(sections.get(i).getRule(j));
                                }
                        }

                        pruneSections();
                        if(debug){
                                System.out.println("\n## After pruneSections() ##");
                                for(int i = 0; i < sections.size(); i++)
                                        System.out.println("Number of clusters for section " + i + ": " + sections.get(i).getNumberOfClusters());
                        }

                        findTenativeRepresentatives();
                        if(debug){
                                
                                System.out.println("\n## After findTenativeRepresenatives() ##");
                                for(int i = 0; i < sections.size(); i++){
                                        System.out.println("Representative rules for section: " + i);
                                        for(int j = 0; j < sections.get(i).getClusters().size(); j++)
                                                System.out.println(sections.get(i).getCluster(j));
                                }
                                
                        }
                        cluster();
                        if(debug){
                                System.out.println("\n## After cluster() ##");
                                for(int i = 0; i < sections.size(); i++){
                                        System.out.println("Section: " + i);
                                        for(int j = 0; j < sections.get(i).getNumberOfClusters(); j++){
                                                System.out.println("\tCluster: " + j);
                                                for(int k = 0; k < sections.get(i).getClusters().get(j).getRules().size(); k++)
                                                        System.out.println("\t\t" + sections.get(i).getCluster(j).getRule(k));
                                        }	
                                }
                        }

                        getRepresentativeRules();
                        if(debug) {
                            System.out.println("\n\n### Resulting Patterns ###");
                            for(int i = 0; i < bassPatterns.size(); i++)
                                    System.out.println(bassPatterns.get(i));
                       }
                    }
                }
                
                if(debug) {
                    System.out.println("\n### Errors that occurred during bass generation: ###");
                    ArrayList<String> err = MIDIBeast.errors;
                    for(int i = 0; i < err.size(); i++) {
                        System.out.println(i+1 + ": " + err.get(i));
                    }
                    
                    
                }
            }
            catch(Exception e) {
                e.printStackTrace();
                MIDIBeast.addError("Sorry, there was an unknown internal error while generating " +
                    "the bass patterns.");    
            }
        }
        
        public ArrayList<BassPatternObj> getBassRules() {
            adaptToNewSyntax();
            if(debug) {
                System.out.println("\n\n### After adaptToNewSyntax() ###");
                for(int i = 0; i < bassPatterns.size(); i++) 
                    System.out.println(bassPatterns.get(i).getRule());
            }                
            return bassPatterns;
        }
        
        public void setSimplifiedPitchesRules(ArrayList<String> s){
            this.simplifiedPitchesRules = s;
        }
        /**
         * At the end of the project, the syntax for an X(5)4 rule changed to (X 5 4).
         * This method changes all "X" items in bassPatterns for the viewing, editing, saving stage of the process
         * WARNING: THIS METHOD ASSUMES THAT PITCH INFORMATION HAS BEEN SIMPLIFIED BY simplifyRulePitches()
         **/
        private void adaptToNewSyntax() {
            ArrayList<BassPatternObj> adapted = new ArrayList<BassPatternObj>();
            for(int i = 0; i < bassPatterns.size(); i++) {
                BassPatternObj cur = bassPatterns.get(i);
                String adaptThis = cur.getRule();
                String[] split = adaptThis.split(" ");         
                String newRule = "";
                
                for(int j = 0; j < split.length; j++) {
                    String element = split[j];
                    String fixedEle = element;
                    if(element.charAt(0) == 'X') {
                       int indexClosed = element.indexOf(")");
                       fixedEle = "(X " + element.substring(2, indexClosed) + " " + element.substring(indexClosed+1, element.length()) + ")";
                    }
                    newRule += fixedEle + " ";                  
                }
                
                BassPatternObj b = new BassPatternObj(newRule, cur.getWeight(), cur.getDuration());
                adapted.add(b);
            }
            
            bassPatterns = adapted;
            
            //Adapt raw clustered rules to the new syntax
            
           for(int i = 0; i < simplifiedPitchesRules.size(); i++){
                String newRule = "";
                String [] split = simplifiedPitchesRules.get(i).split(" ");
                for(int j = 0; j < split.length; j++){
                    String element = split [j];
                    String fixedEle = element;
                    if(element.charAt(0) == 'X'){
                           int indexClosed = element.indexOf(")");
                           fixedEle = "(X " + element.substring(2, indexClosed) + " " + element.substring(indexClosed+1, element.length()) + ")";
                    }
                    newRule += fixedEle + " ";
                }
                simplifiedPitchesRules.set(i, newRule);
            }
            MIDIBeast.repBassRules.setSimplifiedPitchesRules(this.simplifiedPitchesRules);
        }
           
        
      
	
	public void changePart(jm.music.data.Part p)throws Exception{
		new ImportBass(p);
		new BassPatternExtractor();
		new BassChronotonic();
		initialize();
		simplifyRulePitches();
		processDuplicateRules();
		splitUpIntoSections();
		pruneSections();
		findTenativeRepresentatives();
		cluster();
		getRepresentativeRules();
	}
	
	
	
	/**
	* This method simply initializes the field members of the class and sets number of rules to
	* the desired fraction of the original set of rules
	*/
	public void initialize(){
		numberOfUniqueRules = (int)(percentageOfClusters * uniqueRules.size());
		simplifiedPitchesRules = new ArrayList<String>();
		sansDuplicatesRules = new ArrayList<String>();
		sections = new ArrayList<Section>();
		bassPatterns = new ArrayList<BassPatternObj>();
	}
	
	
	/**
	* This method iterates through the original set of rules and simplifies the pitch info contained eg: BNA8 --> B8
	*/
	public void simplifyRulePitches(){
		for(int i = 0; i < MIDIBeast.originalBassRules.size(); i++)
			simplifiedPitchesRules.add(simplifyPitchInfo(MIDIBeast.originalBassRules.get(i)));
	}
        
        // If a maximum bass pattern length is specified, this method will 
        // truncate each bass pattern in excess of the desired length
        // to the specified length
        // But this is broken
        private void truncateBassPatterns(){
            ArrayList<String> tempRules = new ArrayList<String>();
            double maxLength = MIDIBeast.maxBassPatternLength;
            int maxSlotLength = (int)(maxLength*MIDIBeast.slotsPerMeasure/MIDIBeast.denominator);
            for(int i = 0; i < simplifiedPitchesRules.size();i++){
                String currentRule = simplifiedPitchesRules.get(i);
                if(MIDIBeast.numBeatsInRule(currentRule)>maxSlotLength){
                    String[] split = currentRule.split(" ");
                    String temp = "";
                    int duration = 0;
                    for(int j = 0; j < split.length; j++){
                        duration += MIDIBeast.numBeatsInRule(split[j]);
                        if(duration < maxSlotLength) temp += split[j] + " ";
                        if(duration == maxSlotLength){
                            tempRules.add(temp+split[j]);
                            temp = "";
                            duration = 0;
                        }
                        if(duration > maxSlotLength){
                            String type = "";
                            if(split[j].charAt(0) == 'X') type = split[j].substring(0,4);  // FIX: Broken Here
                            else type = Character.toString(split[j].charAt(0));
                            int slotLength = MIDIBeast.numBeatsInBassRule(split[j]) - (duration - maxSlotLength);
                            int slotLength2 = MIDIBeast.numBeatsInBassRule(split[j]) - slotLength;
                            
                            // FIX: slotLength can be negative here!
                            String length = MIDIBeast.stringDuration(slotLength);
                            String length2 = MIDIBeast.stringDuration(slotLength2);
                            temp += type+length;
                            tempRules.add(temp);
                            temp = type+length2+" ";
                            duration = slotLength2;
                            while(duration >= maxSlotLength){
                                tempRules.add(type+MIDIBeast.stringDuration(maxSlotLength));
                                int slotLength3 = duration - maxSlotLength;
                                String length3 = MIDIBeast.stringDuration(slotLength3);
                                duration = slotLength3;
                                if(duration == 0) temp = "";
                                else temp = type + length3 + " ";
                            }
                        }
                    }
                }
                else tempRules.add(currentRule);
            }
            simplifiedPitchesRules = tempRules;
            if(debug){
                System.out.println("####After Truncate Bass Rules####");
                for(int i = 0; i < simplifiedPitchesRules.size(); i++)
                    System.out.println(simplifiedPitchesRules.get(i));
            }
        }
	
	/**
        * rk 10 April 2012: This has been broken for a long time.
        * The problem is that e.g. X(5)4 is no longer used. Now it would be (X 5 4)
        * In any case, it would be better to treat the string as a Polylist,
        * rather than re-parsing it character by character.
        * 
        * I am not sure what AX is supposed to mean; delving into it.
        * 
	* This method is called on each rule by simplifyRulePitches() it handles the logic involved in simplifying
	* the pitch info
	* @param String - The pattern element(eg NAX(5)4) that is to be simplified
	* @return String - The simplified string (eg X(5)4)
        * 
	*/
//	public String oldSimplifyPitchInfo(String s){
//                s = s.substring(1, s.length()-1); //Remove Parens
//		String[] ruleElements = s.split(" ");
//		String returnString = "";
//		for(int i = 0; i < ruleElements.length; i++){
//			int rhythmIndex = 0;                 
//			for(int j = 0; j < ruleElements[i].length();j++){
//				if(ruleElements[i].charAt(j) == '('){ rhythmIndex = j  + 3; break; }
//				if(ruleElements[i].charAt(j) > 47 && ruleElements[i].charAt(j) < 58) { rhythmIndex = j; break; }
//			}
//			int xIndex = ruleElements[i].indexOf('X');
//			if(ruleElements[i].indexOf('B') != -1) returnString += "B" + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
//			else if(ruleElements[i].indexOf('R') != -1) returnString += "R" + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
//                        else if(ruleElements[i].indexOf('C') != -1) returnString += "C" + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
//			else if(xIndex != -1) {
//                            returnString += ruleElements[i].substring(xIndex, xIndex+4) + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
//                        }
//			else if(ruleElements[i].indexOf('A') != -1) returnString += "A" + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
//			else if(ruleElements[i].indexOf('N') != -1) returnString += "N" + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
//			else returnString += "O" + ruleElements[i].substring(rhythmIndex, ruleElements[i].length()) + " ";
//		}
//		return returnString;
//	}
	
        // New version 10 April, changes old style X rule into new
        public String simplifyPitchInfo(String s){  // work in progress, 10 April 2012
                Polylist L = (Polylist)(Polylist.PolylistFromString(s).first()); // due to extra level of nesting
               //System.out.print("string = " + s + ", polylist = " + L);
               StringBuilder buffer = new StringBuilder();
                while( L.nonEmpty() )
                  {
                    Object item = L.first();
                    if( item instanceof String )
                      {
                        String stringItem = (String)item;
                        char firstChar = stringItem.charAt(0);
                        switch( firstChar )
                          {
                            case 'X':
                                // Convert old-style X rules to new
                                //i.e. X(5)8/3 becomes (X 5 8/3)
                                
                                L = L.rest();
                                Polylist P = (Polylist)L.first();
                                L = L.rest();
                                
                                buffer.append("(X ");
                                buffer.append(P.first());
                                buffer.append(" ");
                                buffer.append(L.first());
                                buffer.append(") ");
                                
                                break;
                                
                                
                            default:
                              buffer.append(stringItem);
                              buffer.append(" ");  
                              break;
                          }
                      }
                    else
                      {
                        assert false;
                      }
                    
                    L = L.rest();
                  }
                //System.out.println(", result = " + buffer.toString());
                return buffer.toString();
	}
                
	/**
	* This method searches through the set of rules with simplified pitch information, removes
	* all repeats, and creates a bass pattern for each set of repeats by assigning that pattern 
	* a weight equal to the number of repeats corresponding to that pattern
	*/

        
        public void processDuplicateRules(){
            ArrayList<Integer> repeats = new ArrayList<Integer>();
            
            for(int i = 0; i < simplifiedPitchesRules.size(); i++){
                
                if(repeats.contains(i)) continue;
                
                String rule = simplifiedPitchesRules.get(i);
                int multiplicity = 0;
                
                for(int j = 0; j < simplifiedPitchesRules.size(); j++)
                    if(rule.trim().equals(simplifiedPitchesRules.get(j).trim())){
                        repeats.add(j);
                        multiplicity++;
                    }
                
                uniqueRules.add(new RawRule(rule, multiplicity, i));
            }

        }
	/**
	* When it comes time to cluster, it would be disadvantageous to have patterns of different 
	* beat durations in the same cluster (eg (B8 B8)  should not be in the same group as (B4 B4)).
	* This method will assign each of the non duplicate patterns to a section which contains only
	*  patterns of equal beat duration.
	*/
	/*
	public void splitUpIntoSections(){
		if(debug) System.out.println("\n ## Split up into sections ##");
		ArrayList<Double> usedBeatCounts = new ArrayList<Double>();
		for(int i = 0; i < sansDuplicatesRules.size(); i++){
			if(!sansDuplicatesRules.get(i).equals("Erased Duplicate")){
			    double beats = calculateBeats(sansDuplicatesRules.get(i));
				if(debug) System.out.println(sansDuplicatesRules.get(i) + " = " + beats);
				if(!usedBeatCounts.contains(beats)){ //if section does not exist, create new one
					if(debug) System.out.println("\tRule created new section");
					usedBeatCounts.add(beats);
					sections.add(new Section(beats));
				}
				for(int j = 0; j < sections.size(); j++)
					if(sections.get(j).getBeatCount() == beats){
						if(debug) System.out.println("\tRule placed into section: " + j);
						sections.get(j).addRule(i);
				}
			}
		}
	}
        */
        public void splitUpIntoSections(){
            ArrayList<Integer> usedSlotCounts = new ArrayList<Integer>();
            for(int i = 0; i < uniqueRules.size(); i++){
                RawRule currentRule = uniqueRules.get(i);
                
                // If there already exists a section corresponding to the number
                // of slots that the current rule contains, then add the current
                // rule to that section
                if(usedSlotCounts.contains(currentRule.getSlots())){
                    for(int j = 0; j < sections.size(); j++)
                        if(sections.get(j).getSlotCount() == currentRule.getSlots())
                            sections.get(j).addRule(uniqueRules.get(i));
                }
                //If there does not exist a section corresponding to the number 
                // of slots that the current rule contains, then create such a
                // section and add the current rule to that section
                else{
                    Section newSection = new Section(currentRule.getSlots(), uniqueRules.get(i));
                    sections.add(newSection);
                    usedSlotCounts.add(newSection.getSlotCount());
                }
            }

        }
	
	/**
	* This function is used in splitUpIntoSections() and caclulates the beat duration of a given pattern.
	* @param String - the pattern whose beat duration is to be caclulated (eg (B4 B8 B8))
	* @return Double - the duration of the given pattern (eg 2.0)
	*/
	public Double calculateBeats(String s){
         if(debug) System.out.println("Calculating Beat of: " + s);
		String[] split = s.split(" ");
		int rhythmIndex = 0;
		double beats = 0.0;
		double slots = 0.0;
		for(int i = 0; i < split.length; i++){
			if(split[i].indexOf('(') != -1) rhythmIndex = split[i].indexOf('(') + 3;
			else rhythmIndex = 1;
			String rhythm = split[i].substring(rhythmIndex, split[i].length());
			String[] rhythmArray = rhythm.split("\\+");
			for(int j = 0; j < rhythmArray.length; j++){
				if(debug) System.out.println("\t" + rhythmArray[j] + " -> " + MIDIBeast.getSlotValueFor(rhythmArray[j]));
				slots += MIDIBeast.getSlotValueFor(rhythmArray[j]);
			}
		}
		return slots/MIDIBeast.beat;
	}
	
	
	/**
	* This method iterates through each section and determines how many clusters it is to have. 
	* The number of clusters is determined by finding the number of rules the section contains
	* and setting that as a fraction to how many total patterns there are, and then multiplying that
	* fraction by how many rules are requested as determined in the initialize() method
	*/
	public void pruneSections(){
		for(int i = 0; i < sections.size(); i++){
			int numClusters = (int)Math.round(numberOfUniqueRules * ((sections.get(i).size()*1.0)/MIDIBeast.originalBassRules.size()));
			if(numClusters == 0) numClusters = 1;
			sections.get(i).setNumClustersAllowed(numClusters);
		}
	}
        
        public ArrayList<Section> getSections(){
            return sections;
        }
	
	/**
	* This method finds the tenative representatives for the clusters of each section. One
	* representative is found for each of the clusters that a sections is supposed to have.
	* The first representative is picked non determanistically, and all proceeding 
	* representatives are choosen by finding the pattern with the greatest distance
	* to all other representatives
	*/
	/*
	public void findTenativeRepresentatives(){
		for(int i = 0; i < sections.size(); i++){
			Section s = sections.get(i);
			Random r = new Random();
			int randomRuleIndex = r.nextInt(s.size());
			int indexIntoOriginalRules = s.getRuleIndex().get(randomRuleIndex);
			s.addCluster(indexIntoOriginalRules);
			ArrayList<Integer> noRepeats = new ArrayList<Integer>();
			noRepeats.add(indexIntoOriginalRules);
			for(int j = 1; j < s.getNumberOfClusters(); j++){
				double maxDistance = Double.MIN_VALUE;
				int maxIndex = -1;
				for(int k = 0; k < s.size(); k++){
					if(!noRepeats.contains(k)){
						double distance = 0.0;
						for(int l = 0; l < s.getClusters().size(); l++){
							distance += c.getChronoValueAt(s.getRule(k), s.getClusters().get(l).getRepIndex());
						}
						if(distance > maxDistance){ maxDistance = distance; maxIndex = k; }
					}
				}
				s.addCluster(s.getRule(maxIndex));
				noRepeats.add(s.getRule(maxIndex));
			}
		}
	}
        */
        
        public void findTenativeRepresentatives(){
            for(int i = 0; i < sections.size(); i++){
                Section s = sections.get(i);
                ArrayList<Integer> usedRules = new ArrayList<Integer>();
                Random r = new Random();
                int randomRuleIndex = r.nextInt(s.size());
                RawRule randomRule = s.getRule(randomRuleIndex);
                s.addCluster(randomRule);
                usedRules.add(randomRuleIndex);
                for(int j = 1; j < s.getNumberOfClusters(); j++){
                    double maxDistance = Double.MIN_VALUE;
                    int maxIndex = -1;
                    for(int k = 0; k < s.size(); k++){
                        if(!usedRules.contains(k)){
                            RawRule currentRule = s.getRule(k);
                            double distance = 0.0;
                            for(int l = 0; l < usedRules.size(); l++){
                                RawRule repRule = s.getRule(usedRules.get(l));
                                distance += currentRule.compareTo(repRule);
                            } 
                            if(distance > maxDistance){ maxDistance = distance; maxIndex = k; }
                        }
                    }
                    s.addCluster(s.getRule(maxIndex));
                    usedRules.add(maxIndex);
                }
            }
        }
	
	/**
	* This method iterates through each rule of a seciton and assigns it to a cluster 
	* whose representative rule is the closest.
	*/
        /*
	public void cluster(){
		for(int i = 0; i < sections.size(); i++) {
			Section s = sections.get(i);
			for(int j = 0; j < s.size(); j++){
				double minDistance = Double.MAX_VALUE;
				int minIndex = -1;
				for(int k = 0; k < s.getNumberOfClusters(); k++){
					Cluster c = s.getClusters().get(k);	
					double distance = this.c.getChronoValueAt(s.getRule(j), c.getRepIndex());
					if(distance < minDistance){
						minDistance = distance;
						minIndex = c.getRepIndex();
					}
				}
				s.addRuleToCluster(s.getRule(j)	, minIndex);
			}
		}
	}
        */
        public void cluster(){
            for(int i = 0; i < sections.size(); i++){
                Section s = sections.get(i);
                for(int j = 0; j < s.size(); j++){
                    RawRule currentRule = s.getRule(j);
                    int minIndex = -1;
                    double minDistance = Double.MAX_VALUE;
                    for(int k = 0; k < s.getNumberOfClusters(); k++){
                        Cluster currentCluster = s.getCluster(k);
                        double distance = currentRule.compareTo(currentCluster.getRepRule());
                        if(distance < minDistance){
                            minDistance = distance;
                            minIndex = k;
                        }
                    }
                    s.getCluster(minIndex).addRule(currentRule);
                    numberOfRules += currentRule.weight;
                }
            }
        }
	
	/**
	* This method goes through each cluster and finds the rule
	* whose distance is least from all the other rules. This
	* rule is said to be the best representative of the cluster
	* and is added as a bass pattern with a weight equal to the 
	* number of rules in its cluster, or the number of
	* rules that it 'represents'
	*/
	/*
	public void getRepresentativeRules(){
		for(int i = 0; i < sections.size(); i++){
			Section s = sections.get(i);
			for(int j = 0; j < s.getNumberOfClusters(); j++){
				Cluster c = s.getClusters().get(j);
				double minDistance = Double.MAX_VALUE;
				int minIndex = -1;
				for(int k = 0; k < c.size(); k++){
					double totalDistance = 0.0;
					
					for(int l = 0; l < c.size(); l++)
						totalDistance += this.c.getChronoValueAt(c.getRule(k), c.getRule(l));
					if(totalDistance < minDistance){
						minDistance = totalDistance;
						minIndex = c.getRule(k);
					}
				}
				c.setBetterRepIndex(minIndex);
				bassPatterns.add(new BassPatternObj(sansDuplicatesRules.get(minIndex), c.size()));                        
			}
		}
        }
        */
        
        public void getRepresentativeRules(){
            for(int i = 0; i < sections.size(); i++){
                Section s = sections.get(i);
                for(int j = 0; j < s.getNumberOfClusters(); j++){
                    Cluster c = s.getCluster(j);
                    int maxWeight = Integer.MIN_VALUE;
                    ArrayList<RawRule> potentialRepList = new ArrayList<RawRule>();
                    for(int k = 0; k < c.size(); k++){
                        RawRule r = c.getRule(k);
                        if(r.getWeight() > maxWeight)
                            potentialRepList = new ArrayList<RawRule>();

                        if(r.getWeight() >= maxWeight)
                            potentialRepList.add(r);
                    }
                    Random r = new Random();
                    int randomIndex = r.nextInt(potentialRepList.size());
                    RawRule selectedRule = potentialRepList.get(randomIndex);
                    bassPatterns.add(new BassPatternObj(selectedRule.getRule(), c.calculateWeight()));
                    c.setBetterRep(selectedRule);
                }
            }
            
        }
        /** 
         * If the user so wishes, they may receive every rule generated
         * from the song, and this is the method to retrieve that list of rules
         */ 
        public ArrayList<BassPatternObj> getUnfilteredRules(){
            ArrayList<BassPatternObj> temp = new ArrayList<BassPatternObj>();
            for(int i = 0; i < simplifiedPitchesRules.size(); i++)
                temp.add(new BassPatternObj(simplifiedPitchesRules.get(i), 1));
            if(debug){
                System.out.println("## After getUnfilteredRules() ##");
                for(int i = 0; i < temp.size(); i++)
                    System.out.println(temp.get(i));
            }
            return temp;            
        }
        
        public BassPatternObj makeBassPatternObj(String r, float w){
                    return new BassPatternObj(r,w);
        }

	public class RawRule{
            private String rule;
            private float weight;
            private int slots;
            private int index;
            
            public RawRule(String rule, float weight, int index){
                this.rule = rule;
                this.weight = weight;
                this.index = index;
                
                double beats = MIDIBeast.numBeatsInRule(rule);
                slots = MIDIBeast.doubleValToSlots(beats);
            }
            
            public String getRule() { return rule; }
            public float getWeight() { return weight; }
            public int getSlots() { return slots; }
            public int getIndex() { return index; }
            
            public double compareTo(RawRule that){
                ArrayList<Double> rhythm1 = new ArrayList<Double>();
                ArrayList<Double> rhythm2 = new ArrayList<Double>();
                ArrayList<String> rule1 = new ArrayList<String>();
                ArrayList<String> rule2 = new ArrayList<String>();
                String[] split = this.rule.split(" ");
                for(int i = 0; i < split.length; i++)
                    rule1.add(split[i]);
                split = that.getRule().split(" ");
                for(int i = 0; i < split.length; i++)
                    rule2.add(split[i]);
                rhythm1 = c.toHistoArray(rule1);
                rhythm2 = c.toHistoArray(rule2);
                return c.chronoCompare(rhythm1, rhythm2);
                //return c.getChronoValueAt(this.index,that.getIndex());//This was all the method did before truncate lengths
            }
        
            //For testing purposes as of now
            public String toString(){
                return "\nRaw Rule\n\t-rule " + rule + 
                            "\n\t-weight: " + weight +
                            "\n\t-slots: " + slots +
                            "\n\t-index: " + index;
            }
            
        }
        
        public class BassPatternObj{
		private String rule;
		private float weight;
                private int duration;

		public BassPatternObj(String r, float w){
                    rule = r.trim(); 
                    weight = w;
                    duration = new Double(MIDIBeast.numBeatsInRule(rule)).intValue();
		}

            public void setDuration(int duration) {
                this.duration = duration;
            }
                
                public BassPatternObj(String r, float w, int d){
                    rule = r.trim();
                    weight = w;
                    duration = d;
                }
                
                public String toString(){
			return "(bass-pattern (rules " + rule + ")(weight " + weight + "))";
		}

		public String getBareRule() {
			return rule + "(weight " + weight + ")";
		}

		public String getRule() {
			return rule;
		}
		public float getWeight() {
			return weight;
                }
                
                public int getDuration(){
                    return duration;
                }
                
                public int getNewDuration(){
                   return MIDIBeast.numBeatsInBassRule(rule);
                }
	}
	/*
        public class Section{
		ArrayList<Cluster> clusters;
		ArrayList<Integer> ruleIndex;
		double beatCount;
		int numClustersAllowed;
		
		public Section(){} // YAY JAVA!
		
		public Section(double beatCount){
			ruleIndex = new ArrayList<Integer>();
			clusters = new ArrayList<Cluster>();
			this.beatCount = beatCount;
		}
		
		public double getBeatCount(){
			return beatCount;
		}
		
		public ArrayList<Cluster> getClusters(){
			return clusters;
		}
                
	
		public int getRule(int i){
			return ruleIndex.get(i);
		}
		
		public ArrayList<Integer> getRuleIndex(){
			return ruleIndex;
		}
		
		public int getNumberOfClusters(){
			return numClustersAllowed;
		}
		
		public void addRule(int i){
			ruleIndex.add(i);
		}
		
		public void setNumClustersAllowed(int i){
			numClustersAllowed = i;
                        if(maxNumberOfClusters > 0 && numClustersAllowed > maxNumberOfClusters)
                            numClustersAllowed = maxNumberOfClusters;
		}
		
		public int size(){
			return ruleIndex.size();
		}
		
		public void addCluster(int repIndex){
			clusters.add(new Cluster(repIndex));
		}
		
		public void addRuleToCluster(int ruleIndex, int repIndex){
			for(int i = 0; i < clusters.size(); i++)
				if(clusters.get(i).getRepIndex() == repIndex)
					clusters.get(i).addRule(ruleIndex);
		}
	}
	*/
        public class Section{
		ArrayList<Cluster> clusters;
		ArrayList<RawRule> rules;
		int slotCount;
		int numClustersAllowed;
		
		public Section(){} // YAY JAVA!
		
		public Section(int slotCount, RawRule rule){
			rules = new ArrayList<RawRule>();
			clusters = new ArrayList<Cluster>();
			this.slotCount = slotCount;
                        addRule(rule);
		}
		
		public int getSlotCount(){
			return slotCount;
		}
		
		public ArrayList<Cluster> getClusters(){
			return clusters;
		}
		
		// Takes an index into the uniqueRules ArrayList and returns
                // the corresponding rules index into the original array of rules
                public RawRule getRule(int i){
			return rules.get(i);
		}
                
                public Cluster getCluster(int i){
                    return clusters.get(i);
                }
		
		public ArrayList<RawRule> getRules(){
			return rules;
		}
		
		public int getNumberOfClusters(){
			return numClustersAllowed;
		}
		
		public void addRule(RawRule i){
			rules.add(i);
		}
		
		public void setNumClustersAllowed(int i){
			numClustersAllowed = i;
                        if(maxNumberOfClusters > 0 && numClustersAllowed > maxNumberOfClusters)
                            numClustersAllowed = maxNumberOfClusters;
		}
		
		public int size(){
			return rules.size();
		}
		
		public void addCluster(RawRule repRule){
			clusters.add(new Cluster(repRule));
		}
                
                
		
		public void addRuleToCluster(RawRule newRule, int repIndex){
			for(int i = 0; i < clusters.size(); i++)
				if(clusters.get(i).getRepIndex() == repIndex)
					clusters.get(i).addRule(newRule);
		}
	}
        /*        
	public class Cluster{
		private int repIndex;
		private int betterRepIndex;
		private ArrayList<Integer> rules;
		
		public Cluster(){}
		
		public Cluster(int repIndex){
			this.repIndex = repIndex;
			rules = new ArrayList<Integer>();
		}
		
		public int getRepIndex(){
			return repIndex;
		}
		
		public int getBetterRepIndex(){
			return betterRepIndex;
		}
		
		public int getRule(int i){
			return rules.get(i);
		}
		
		public void setBetterRepIndex(int b){
			betterRepIndex = b;
		}
		
		public ArrayList<Integer> getRules(){
			return rules;
		}
		
		public void addRule(int ruleIndex){
			rules.add(ruleIndex);
		}
                
                public String getStringRule(int i){
                    return "(" + MIDIBeast.repBassRules.simplifiedPitchesRules.get(rules.get(i))+ ")";
                }
		
		public int size(){
			return rules.size();
		}
	}
        */
        //Need: String method
        //      Compare Method
        public class Cluster{
		private RawRule repRule;
		private ArrayList<RawRule> rules = new ArrayList<RawRule>();
		
		public Cluster(){}
		
		public Cluster(RawRule initialRule){
                        rules.add(initialRule);
                        repRule = initialRule;
                }
		
		public RawRule getRule(int index){
			return rules.get(index);
		}
		
                public String getStringRule(int i){
                    return rules.get(i).getRule();
                }
                
		public void setBetterRep(RawRule repRule){
			this.repRule = repRule;
		}
		
		public ArrayList<RawRule> getRules(){
			return rules;
		}
                
                public RawRule getRepRule(){ 
                    return repRule;
                }
		
		public void addRule(RawRule rule){
			rules.add(rule);
		}
                
		public int size(){
			return rules.size();
		}
                
                public double compare(int i, int j){
                    return rules.get(i).compareTo(rules.get(j));
                }
                
                public int getRepIndex(){
                    return repRule.getIndex();
                }
                
                public float calculateWeight(){
                    float weight = 0;
                    for(int i = 0; i < rules.size(); i++)
                        weight += rules.get(i).getWeight();
                    weight = (float)(Math.floor(((weight/numberOfRules)*100)/2));
                    if(weight < 1.0) return 1;
                    return  weight;
                }
                
                public String toString(){
                    String s = "Rep Rule: ";
                    if(repRule != null) s += repRule.getRule();
                    s += "\n";
                    RawRule r = rules.get(0);
                    for(int i = 0; i < rules.size(); r = rules.get(i++))
                            s += "\t" + r.getRule() + "\n";
                    return s;
                }
	}
}
