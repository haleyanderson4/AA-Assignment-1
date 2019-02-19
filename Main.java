/**
 * @TODO
 * Fix epsilon closure
 * fix find destination
 */

import java.util.*;
import java.lang.String;
import java.io.*;

public class Assignment1
{

    public static void main(String[] args)
    {
        try
        {
            File file = null;
            if (0 < args.length)
            {
               file = new File(args[0]);
            }
            else
            {
               System.err.println("Invalid arguments count:" + args.length);
            }
            Scanner sc = new Scanner(file);

            List<String> nfaStates = new ArrayList<String>();
            List<String> language = new ArrayList<String>();
            String startState = "";
            Set<String> nfaAcceptS = new HashSet<String>();
            List<String> nfaRules = new ArrayList<String>();
            List<String> nfaEplisonRules = new ArrayList<String>();

            int lineCount = 1;
            while (sc.hasNextLine())  //loop through all the lines in text file
            {
                String line = sc.nextLine();
                switch (lineCount){
                    case 1: lineCount = 1;  //taking in states
                        for (int i = 0; i < line.length(); i++)
                        {
                            char c = line.charAt(i);
                            if (c != '\t')
                            {
                                nfaStates.add(""+c);
                            }
                        }
                        lineCount++;
                        break;
                    case 2: lineCount = 2; //language
                        for (int i = 0; i < line.length(); i++)
                        {
                            char c = line.charAt(i);
                            if (c != '\t')
                            {
                                language.add(""+c);
                            }
                        }
                        lineCount++;
                        break;
                    case 3: lineCount = 3; //start state
                        startState = line;
                        lineCount++;
                        break;
                    case 4: lineCount = 4; //accept state
                        for (int i = 0; i < line.length(); i++)
                        {
                            char c = line.charAt(i);
                            if (c != '\t')
                            {
                                nfaAcceptS.add(""+c);
                            }
                        }
                        lineCount++;
                        break;
                    default: //transitions
                        if(line.contains("EPS"))
                        {
                            nfaEplisonRules.add(line);
                        }
                        else
                        {
                            nfaRules.add(line);
                        }
                        break;
                }
            }
            List<String> dfaStates = new ArrayList<String>();
            List<String> dfaAcceptS = new ArrayList<String>();
            List<String> dfaRules = new ArrayList<String>();
            //variable declarations

            List<String> eplisonClosure = new ArrayList<String>(); //list of new eplison closure states
            epsClosureCreate(eplisonClosure, nfaEplisonRules, nfaStates); //method to populate eplison states
            for(int i = 0; i < eplisonClosure.size(); i++)
            {
              System.out.println(eplisonClosure.get(i));
            }

            //runThrough(startState, nfaRules, dfaRules, dfaStates);
            //calling the recursive method to create the dfaRules, begins with start state

            // write DFA to new text file
            FileWriter outFile = new FileWriter("DFAinformation.txt");
            for(String str:dfaStates)
            {
                outFile.write(str);
            }
            outFile.write("\n");
            for(String str:language)
            {
                outFile.write(str);
            }
            outFile.write("\n");
            outFile.write(startState);
            outFile.write("\n");
            for(String str:dfaAcceptS)
            {
                outFile.write(str);
            }
            outFile.write("\n");
            for(String str:dfaRules)
            {
                outFile.write(str);
            }
            outFile.close();
            }
            catch(Exception e)
            {
                System.err.println("Invalid input");
            }
        }

    public static List<String> epsClosureCreate(List<String> eplisonClosure, List<String> nfaEplisonRules, List<String> nfaStates)
    { //do this for all rules
      for(int checkRule = 0; checkRule < nfaEplisonRules.size(); checkRule++) //looping through all of the rules that have eplisons
      {
          String startState = nfaEplisonRules.get(checkRule).substring(0, 1); //pulls the start state from the rule
          String endState = nfaEplisonRules.get(checkRule).substring(6); //pulls the destination state from the rule
          String epCloseState = "{" + startState + "," + endState + "}"; // concatenates them to form the ep close state

          String closeCheck = epCloseChecker(String endState, eplisonClosure); // if there is a cascading ep close step it returns that
          if(closeCheck != "") // if not null another step to be added
          {
              epCloseState = "{" + startState + "," + endState + "," + closeCheck + "}"; //creating new rule
          }
          eplisonClosure.add(epCloseState); //add to epsilon list!
      }
    }

    public static String epCloseChecker(String destinationState, List<String> eplisonClosure)
    {
      for(int i = 0; i < eplisonClosure.size(); i++) //now were checking that all epsilon closures are complete
      {
          String epCloseStartState = eplisonClosure.get(i).substring(1,2); //the state set
          String cascadingDestination = "";
          if(epCloseStartState.equals(destinationState)) //if the destination state has its own destination state, grab it
          {
              cascadingDestination = eplisonClosure.get(i).substring(3,-1); //getting the not start state and not brackets
          }
      }
      return cascadingDestination;
    }

    public static String findDestination(String currentState, String letter, List<String> nfaRules)
    {
        String destinationState = ""; //this is the state we will go to next

        for(int stateCount = 0; stateCount < currentState.length(); stateCount++) // if our current state has multiple states
        {
            if(currentState.charAt(stateCount) == '{' || currentState.charAt(stateCount) == ',' || currentState.charAt(stateCount) == '}') //so we're not looking at nothing
            {
                continue;
            }

            String state = "" + currentState.charAt(stateCount); //the actual state we are finding the next of
            for(int rules = 0; rules < nfaRules.size(); rules++) //loop through rules to see where this state goes
            {
                String ruleState = nfaRules.get(rules).substring(0, 1); //to get the state of the rule we are looking at
                if(ruleState != state || //only look at rules that deal with the state we currently have
                   ( currentState.charAt(stateCount) == '{' || currentState.charAt(stateCount) == ',' || currentState.charAt(stateCount) == '}') ) //so we're not looking at nothing
                {
                    continue;
                }
                else //if the state in the rule is what we are currently at
                {
                    String currentRule = nfaRules.get(rules); //this is the full rule we are looking at
                    destinationState = destinationState + "," + currentRule.substring(4); //adding this rule's destination state to the overall destination state

                }
            }
        }

        return destinationState;
    }

    public static List<String> letterByLetter(String currentState, List<String> language, List<String> dfaRules, List<String> nfaRules, List<String> dfaStates)
    {
      for(int letterNum = 0; letterNum < language.size(); letterNum++)
      {
          String letter = language.get(letterNum); //this is the letter we are finding the rule for
          String destinationState = findDestination(currentState, letter, nfaRules); //calls the method to find the destination !

          String newRule = currentState + "," + letter + "=" + destinationState; // creates the new rule
          dfaRules.add(newRule); //adds new rule to the list

          boolean solved = false;
          for(int i = 0; i < dfaStates.size(); i++)
          {
              if(dfaStates.get(i).substring(0,-1) == destinationState) //if that state has already been solved
              {
                  solved = true;
              }
          }
          if(solved == false) //so only run through with the new state if it hasnt already been solved
          {
              letterByLetter(destinationState, language, dfaRules, nfaRules, dfaStates); // to see what else the state goes to
              dfaStates.add(destinationState);
          }
      }
      return dfaRules;
    }

}
