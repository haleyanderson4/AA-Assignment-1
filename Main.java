/**
 * Haley Anderson and Jennifer Prosinski
 * CPSC 406: Algorithm Analysis
 * Assignment 1: NFA to DFA Converter
 */

 /**
  * fix start state to be epsilon close state
  * fix find destination for rule & letter combos with multiple destinations
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

            List<String> repeatingRules = new ArrayList<String>(); //list of rules for repeated leter & state combos
            doubleLetterStateCheck(language, nfaStates, nfaRules,repeatingRules); //methof to populate the repeating rules list

            //check if the start state needs an epsilon closure
            String closeCheck = epCloseChecker(startState, eplisonClosure); // if there is a cascading ep close step it returns that
            if(closeCheck != "") // if not null another step to be added
            {
                startState = "{" + startState + "," + closeCheck + "}"; //creating new rule
            }
            dfaStates.add(startState);

            letterByLetter(startState, language, dfaRules, nfaRules, dfaStates, eplisonClosure, repeatingRules);
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
            for(String str:dfaStates)
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


    public static void epsClosureCreate(List<String> eplisonClosure, List<String> nfaEplisonRules, List<String> nfaStates)
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


    public static void doubleLetterStateCheck(List<String> language, List<String> nfaStates, List<String> nfaRules, List<String> repeatingRules)
    {
      for(int i = 0; i < language.size(); i++)
      {
        String currentLetter = language.get(i); //getting the current letter to search for
        for(int j = 0; j < nfaStates.size(); j++)
        {
          String currentState = nfaStates.get(j); //getting the current state to search for
          String newRule = (currentState + "," + currentLetter + "={"); //starting the new rule
          Queue<Integer> destinationQueue = new LinkedList<>(); //if there are multiple destinations in the queue grab them all

          int ruleRepeatCount = 0; //if there are more than 1 rule with this state and letter combo, eplison close it
          for(int k = 0; k < nfaRules.size(); k++) // loop through all rules
          {
            String currentRule = nfaRules.get(k);
            String ruleState = currentRule.substring(0, 1); //to get the state of the rule we are looking at
            String ruleLetter = currentRule.substring(2, 3); //to get the letter of the rule we are looking at

            if(ruleState.equals(currentState) && ruleLetter.equals(currentLetter))
            {
              destinationQueue.add(k); //adds the index of the rule in question to the queue
              ruleRepeatCount++; //if the state & letter in the rule are what we are looking for, count it
            }
          }

          if(ruleRepeatCount > 1) //if the combination was repeated more than once
          {
            int ruleDeleteCount = 0; //this is a counter to change the index location when we delete rules
            while(destinationQueue.size() > 0) //add the destinations that were hit in the multiple rules to the new master rule!
            {
              int index = destinationQueue.remove() - ruleDeleteCount;
              String destination = nfaRules.get(index).substring(4);
              nfaRules.remove(index);
              newRule = newRule + destination + ",";
              ruleDeleteCount++;
            }

            newRule = newRule.substring(0,newRule.length()-1) + "}"; //removing the trailing , and adding the closing }
            repeatingRules.add(newRule); //add this new rule to the rule list
          }

        }
      }
    }


    public static List<String> letterByLetter(String currentState, List<String> language, List<String> dfaRules, List<String> nfaRules, List<String> dfaStates, List<String> eplisonClosure, List<String> repeatingRules)
    {
      Stack<String> checkStatesStack = new Stack<String>(); // a stack to put the destination states in so we can check that they are solved after running through all letters
      Stack<Integer> checkForRepeats = new Stack<Integer>(); // to make sure there are no repeating states in a destination

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
            String destination = findDestination(("" + currentState.charAt(stateCount)), letter, nfaRules, eplisonClosure, repeatingRules); //calls the method to find the destination !
System.out.println("yo " + currentState + "      " + letter + "     " + destination);
            for(int i = 0; i < destinationState.length(); i++)
            {
              char state1 = destinationState.charAt(i);
              if(state1 == '{' || state1 == ',') { continue; } //look at only the states
              for(int j = 0; j < destinationState.length(); j++)
              {
                char state2 = destinationState.charAt(j);
                if(i == j) { continue; } //dont want to look at the same index
                if(state2 == '{' || state2 == ',') { continue; } //look at only the states
                if(state1 == state2)
                {
                  checkForRepeats.push(i);
                }
              }
            }
            while(!checkForRepeats.empty())
            {
              int index = checkForRepeats.pop();
              destinationState = destinationState.substring(0,index-1) + destinationState.substring(index+1);
            }

            if(!destination.equals("")) //if there is a rule for this state and letter combo
            {
              destinationState = destinationState + destination + ","; // adds the new destination to the destination state
            } //if there is no rule for this combo then destinationState does not change
          }
          if(destinationState.equals("{"))
          {
System.out.println("WHYYYYYY");
            continue;
          }
System.out.println("wut" + destinationState);
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
            letterByLetter(destinationState, language, dfaRules, nfaRules, dfaStates, eplisonClosure, repeatingRules); // to see what else the state goes to
        }
      } //once every hit destination stack gets called we're good!!

      return dfaRules;
    }


    public static String findDestination(String currentState, String letter, List<String> nfaRules, List<String> eplisonClosure, List<String> repeatingRules)
    {
        String destinationState = ""; //this is the state we will go to next

        for(int k = 0; k < repeatingRules.size(); k++) //finds if the current state and letter combination is in the repeating rules
        {
          String currentRule = repeatingRules.get(k); //this is the full rule we are looking at
          String ruleState = currentRule.substring(0, 1); //to get the state of the rule we are looking at
          String ruleLetter = currentRule.substring(2, 3); //to get the letter of the rule we are looking at

          if(ruleState.equals(currentState) && ruleLetter.equals(letter)) //only look at rules that deal with the state & letter we currently have
          {
            destinationState = destinationState + currentRule.substring(5, currentRule.length()-1); //adding this rule's destination state to the overall destination state
            for(int j = 0; j < eplisonClosure.size(); j++)
            {
              String epCloseStartState = eplisonClosure.get(j).substring(1,2);
              if(epCloseStartState.equals(destinationState)) //if this end state has more states to go to after
              {
                destinationState = destinationState + eplisonClosure.get(j).substring(2); //get all the cascading destination states
              }
            }
System.out.println(repeatingRules +  "\n" + "HEY WHAT UP " + destinationState);
            return destinationState;
          }
        }

        for(int i = 0; i < nfaRules.size(); i++) //loop through rules to see where this state goes
        {
            String currentRule = nfaRules.get(i); //this is the full rule we are looking at
            String ruleState = currentRule.substring(0, 1); //to get the state of the rule we are looking at
            String ruleLetter = currentRule.substring(2, 3); //to get the letter of the rule we are looking at
            if(ruleState.equals(currentState) && ruleLetter.equals(letter)) //only look at rules that deal with the state & letter we currently have
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

        if(destinationState.length() > 1) //if there are more than 1 state it drops the }
        {
          destinationState = destinationState.substring(0, destinationState.length()-1);
        }

        return destinationState; //returning the full destination state without the final }
    }
}
