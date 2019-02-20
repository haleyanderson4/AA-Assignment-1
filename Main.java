/**
 * Haley Anderson and Jennifer Prosinski
 * CPSC 406: Algorithm Analysis
 * Assignment 1: NFA to DFA Converter
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

            //variable declarations
            List<String> nfaStates = new ArrayList<String>();
            List<String> language = new ArrayList<String>();
            String startState = "";
            List<String> nfaAcceptS = new ArrayList<String>();
            List<String> nfaRules = new ArrayList<String>();
            List<String> nfaEplisonRules = new ArrayList<String>();

            int lineCount = 1;
            while (sc.hasNextLine())  //loop through all the lines in text file
            {
                String line = sc.nextLine();
                switch (lineCount){
                    case 1: lineCount = 1;  //taking in states
                        for (int i = 0; i < line.length(); i++) //loop through the line 
                        {
                            char c = line.charAt(i);
                            if (c != '\t')
                            {
                                nfaStates.add(""+c); //add the states to the list and ignore the tabs
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
                        if(line.contains("EPS")) //add rule with epsilon transitions to special list
                        {
                            nfaEplisonRules.add(line);
                        }
                        else
                        {
                            nfaRules.add(line); //add rest to rule list
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

            letterByLetter(startState, language, dfaRules, nfaRules, dfaStates, eplisonClosure);
            //calling the recursive method to create the dfaRules, begins with start state

            for(int i = 0; i < dfaStates.size(); i++) //loop through all dfa states
            {
              String currentState = dfaStates.get(i);
              boolean add = false;
              for(int j = 0; j < currentState.length(); j++) //look at each of the states that compose the current state
              {
                for(int k = 0; k < nfaAcceptS.size(); k++) //look at all of the accept states for the nfa
                {
                  if(("" + currentState.charAt(j)).equals(nfaAcceptS.get(k))) //if an nfa accept state is in our current dfa state
                  {
                    add = true; //marks it
                  }
                }
              }
              if(add)
              {
                dfaAcceptS.add(currentState); //if the current state has an accept state add it to the dfa accept states
              }
            }

            // write DFA to new text file
            FileWriter outFile = new FileWriter("DFAinformation.txt");
            for(String str:dfaStates) //loop through list and write states to file with tab
            {
                outFile.write(str + '\t');
            }
            outFile.write("\n");
            for(String str:language)
            {
                outFile.write(str + '\t');
            }
            outFile.write("\n");
            outFile.write(startState);
            outFile.write("\n");
            for(String str:dfaAcceptS)
            {
                outFile.write(str + '\t');
            }
            outFile.write("\n");
            for(String str:dfaRules)
            {
                outFile.write(str + "\n");
            }
            outFile.close();
            }
            catch(Exception e)
            {
                System.out.println(e);
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

          String closeCheck = epCloseChecker(endState, eplisonClosure); // if there is a cascading ep close step it returns that
          if(closeCheck != "") // if not null another step to be added
          {
              epCloseState = "{" + startState + "," + endState + "," + closeCheck + "}"; //creating new rule
          }
          eplisonClosure.add(epCloseState); //add to epsilon list!
      }
      return eplisonClosure;
    }


    public static String epCloseChecker(String destinationState, List<String> eplisonClosure)
    {
      String cascadingDestination = "";
      for(int i = 0; i < eplisonClosure.size(); i++) //now were checking that all epsilon closures are complete
      {
          String epCloseStartState = eplisonClosure.get(i).substring(1,2); //the state set
          if(epCloseStartState.equals(destinationState)) //if the destination state has its own destination state, grab it
          {
              cascadingDestination = eplisonClosure.get(i).substring(3,eplisonClosure.get(i).length()-1); //getting the not start state and not brackets
          }
      }
      return cascadingDestination;
    }


    public static List<String> letterByLetter(String currentState, List<String> language, List<String> dfaRules, List<String> nfaRules, List<String> dfaStates, List<String> eplisonClosure)
    {
      Stack<String> checkStatesStack = new Stack<String>(); // a stack to put the destination states in so we can check that they are solved after running through all letters

      for(int letterNum = 0; letterNum < language.size(); letterNum++)
      {
          String letter = language.get(letterNum); //this is the letter we are finding the rule for
          String destinationState = "{";
          for(int stateCount = 0; stateCount < currentState.length(); stateCount++) // if our current state has multiple states
          {
            if(currentState.charAt(stateCount) == '{' || currentState.charAt(stateCount) == ',' || currentState.charAt(stateCount) == '}')
            {
                continue; //so we're not looking at nothing
            }
            String destination = findDestination(("" + currentState.charAt(stateCount)), letter, nfaRules, eplisonClosure);
            if(destination.equals("")) //if there is no rule for this state and letter
            {
              destinationState = currentState;
            }
            destinationState = destinationState + destination + ","; //calls the method to find the destination !
          } System.out.println(destinationState);
          destinationState = destinationState.substring(0,destinationState.length()-1) + "}"; // removing the last , and adding a close bracket

          String newRule = currentState + "," + letter + "=" + destinationState; // creates the new rule
          dfaRules.add(newRule); //adds new rule to the list
          checkStatesStack.push(destinationState); //puts the state on the stack to be checked later
          System.out.println(nfaRules + "\n" + currentState + "     " + letter + "       " + destinationState + "\n" + checkStatesStack);

      }

      while(!checkStatesStack.empty())
      {
        String destinationState = checkStatesStack.pop();
        boolean solved = false;
        for(int i = 0; i < dfaStates.size(); i++)
        {
            if(dfaStates.get(i).equals(destinationState)) //if that state has already been solved
            {
                solved = true;
            }
        }
        if(solved == false) //so only run through with the new state if it hasnt already been solved
        {
            dfaStates.add(destinationState); // add to list of dfa states after the solved loop for no confusion
            letterByLetter(destinationState, language, dfaRules, nfaRules, dfaStates, eplisonClosure); // to see what else the state goes to
        }
      } //once every hit destination stack gets called we're good!!

      return dfaRules;
    }


    public static String findDestination(String currentState, String letter, List<String> nfaRules, List<String> eplisonClosure)
    {
        String destinationState = ""; //this is the state we will go to next
        for(int i = 0; i < nfaRules.size(); i++) //loop through rules to see where this state goes
        {
            String currentRule = nfaRules.get(i); //this is the full rule we are looking at
            String ruleState = currentRule.substring(0, 1); //to get the state of the rule we are looking at
            if(ruleState.equals(currentState)) //only look at rules that deal with the state we currently have
            {
              destinationState = currentRule.substring(4); //adding this rule's destination state to the overall destination state
              for(int j = 0; j < eplisonClosure.size(); j++)
              {
                String epCloseStartState = eplisonClosure.get(j).substring(1,2);
                if(epCloseStartState.equals(destinationState)) //if this end state has more states to go to after
                {
                  destinationState = destinationState + eplisonClosure.get(j).substring(2); //get all the cascading destination states
                }
              }
            }
        }

        return destinationState.substring(0,destinationState.length()-1); //returning the full destination state without the final }
    }
}
