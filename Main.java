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
                    nfaRules.add(line);
                    break; 
        }
        
        List<String> dfaStates = new ArrayList<String>;
        List<String> dfaAcceptS = new ArrayList<String>;
        List<String> dfaRules = new ArrayList<String>;
        //variable declarations
        
        runThrough(startState, nfaRules, dfaRules, dfaStates);
        //calling the recursive method to create the dfaRules, begins with start state
    }
    
    public static String runThrough(String currentState, List<String> nfaRules, List<String> dfaRules, List<String> dfaStates)
    {
        String newState = ""; //this is the state we will go to next
        for(int i = 0; i < nfaRules.size(); i++) //loop through rules to see where this state goes
        {
            String line = nfaRules.get(i);
            while(True)
            {
                for(int stateCount = 0; stateCount < currentState.length(); stateCount++)
                {
                    for(int j = 0; j < nfaRules.size(); j++)
                    {
                        int charCount = 0;
                        if(line.charAt(charCount) == currentState.charAt(i)
                        {
                            
                        }
                        if(line.charAt(charCount) == ',')
                        {
                            break; //if you hit the comma then the current state is not represented
                        }
                    }
                }
            }
            
        }
        
        return newState;
    }
}
