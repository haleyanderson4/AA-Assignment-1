/**
 * @TODO
 * Parsing
 * Conversion
 */

import java.util.*;
import java.lang.String;

public class Main
{

    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in); 
        String fileName = in.args[0]; 
        
        File file = new File(fileName); 
        Scanner sc = new Scanner(file); 
        List<String> nfaStates = new ArrayList<String>;
        List<String> language = new ArrayList<String>;
        String startState = "";
        Set<String> nfaAcceptS = new HashSet<String>;
        List<String> nfaRules = new ArrayList<String>;
        List<String> nfaEplisonRules = new ArrayList<String>;
        int lineCount = 1;
        while (sc.hasNextLine()){  //loop through all the lines in text file
            String line = sc.nextLine();
            switch (lineCount){  
                case 1: lineCount = 1;  //taking in states
                    for (character c in line){
                        if (c != '\t'){
                            nfaStates.add(c);
                        }
                    }
                    lineCount++;
                    break;
                case 2: lineCount = 2; //language
                    for (character c in line){
                        if (c != '\t'){
                            nfaStates.add(c);
                        }
                    }
                    lineCount++;
                    break;
                case 3: lineCount = 3; //start state
                    startState = line;
                    lineCount++;
                    break;
                case 4: lineCount = 4; //accept state
                    for (character c in line){
                        if (c != '\t'){
                            nfaAcceptS.add(c);
                        }
                    }
                    lineCount++;
                    break;
                default: //transitions 
                    if(line.subset(2, 5) == "EPS")
                        nfaEplisonRules.add(line);
                    else
                        nfaRules.add(line);
                    break; 
        }
        
        List<String> dfaStates = new ArrayList<String>;
        List<String> dfaAcceptS = new ArrayList<String>;
        List<String> dfaRules = new ArrayList<String>;
        //variable declarations
        }
        
        List<String> eplisonClosure = new ArrayList<String>; //list of new eplison closure states
        epsClosureCreate(eplisonClosure); //method to populate eplison states
        
        runThrough(startState, nfaRules, dfaRules, dfaStates);
        //calling the recursive method to create the dfaRules, begins with start state
    }
    
    public static List<String> epsClosureCreate(List<String> eplisonClosure)
    {
        for(int checkState = 0; checkState < nfaStates; checkState++) //looping through all states in original NFA
        {
            for(int checkRule = 0; checkRule < nfaEplisonRules.size(); checkRule++) //looping through all of the rules that have eplisons
            {
                String ruleState = nfaEplisonRules.get(checkRule).subset(0, 1); //this is the state that is mentioned in the eplison rule
                if(ruleState == nfaStates.get(checkState)) // if the state mentioned is equal to the original state we are looking at, we move to combine them
                 {
                    String destinationState = nfaEplisonRules.get(checkRule).subset(6));
                    newState = "(" + ruleState + "," + destinationState; //this is creating the grouped state
                    for(int alreadyClosed = 0; alreadyClosed < eplisonClosure.size(); alreadyClosed++) //now were checking that the destination state doesn't have
                        //anything that is eplison closes to
                    {
                        String current = eplisonClosure.get(alreadyClosed);
                        for(Character c in current)
                        {
                            if(c == ruleState)
                            {
                                eplisonClosure.set(alreadyClosed) = current + ruleState;
                            }
                        }
                    }
                    
                 }
            } //creates the combined eplison state we will be moving into 
    }
    
    public static String runThrough(String currentState, List<String> nfaRules, List<String> dfaRules, List<String> dfaStates)
    {
        String newState = ""; //this is the state we will go to next
        
       
            
        
        
        for(int i = 0; i < nfaRules.size(); i++) //loop through rules to see where this state goes
        {
            String line = nfaRules.get(i);
            
            
                for(int stateCount = 0; stateCount < currentState.length(); stateCount++)
                {
                    
                    
                    for(int j = 0; j < nfaRules.size(); j++)
                    {
                        Character ruleState = nfaRules.get(j).charAt(0);
                        if(ruleState == currentState.charAt(i))
                        {
                            Character letter = nfaRules.get(j).charAt(2);
                            
                        }
                        
                    }
                }
            
            
        }
        
        return newState;
    }
}
