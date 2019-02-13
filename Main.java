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
        while (sc.hasNextLine()){
            String line = sc.nextLine();
            switch (lineCount){
                case 1: lineCount = 1;
                    for (character c in line){
                        if (c != '\t'){
                            nfaStates.add(c);
                        }
                    }
                    lineCount++;
                    break;
                case 2: lineCount = 2;
                    for (character c in line){
                        if (c != '\t'){
                            nfaStates.add(c);
                        }
                    }
                    lineCount++;
                    break;
                case 3: lineCount = 3;
                    startState = line;
                    lineCount++;
                    break;
                case 4: lineCount = 4;
                    for (character c in line){
                        if (c != '\t'){
                            nfaAcceptS.add(c);
                        }
                    }
                    lineCount++;
                    break;
                default: 
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
            
        List<String> eplisonClosure = new ArrayList<String>;
        for(int i = 0; i < nfaStates; i++)
        {
            for(int j = 0; j < nfaEplisonRules.size(); j++)
            {
                Character ruleState = nfaEplisonRules.get(j).charAt(0);
                if(ruleState == dfaStates.get(i))
                 {
                    newState = newState + ruleState;
                 }
            } //creates the combined eplison state we will be moving into 
        }
        
        runThrough(startState, nfaRules, dfaRules, dfaStates);
        //calling the recursive method to create the dfaRules, begins with start state
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
