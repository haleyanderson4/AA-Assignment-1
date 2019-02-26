
    
/**
 * Haley Anderson and Jennifer Prosinski
 * CPSC 406: Algorithm Analysis
 * Assignment 1: NFA to DFA Converter
 */

 /**
  * fix the substring methods to include the { }
 */

import java.util.*;
import java.lang.String;
import java.io.*;

public class Main
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
                            if (c != '\t' && c != '{' && c != '}')
                            {
                                nfaAcceptS.add(""+c);
                            }
                        }
                        lineCount++;
                        break;
                    default: //transitions
                        String noSpacesLine = "";
                        int lastSpaceIndex = 0;
                        for(int i = 0; i < line.length(); i++)
                        {
                          if(line.charAt(i) == ' ')
                          {
                            noSpacesLine = noSpacesLine + line.substring(lastSpaceIndex, i);
                            lastSpaceIndex = i + 1;
                          }
                        }
                        noSpacesLine = noSpacesLine + line.substring(lastSpaceIndex);
                        if(line.contains("EPS")) //add rule with epsilon transitions to special list
                        {
                            nfaEplisonRules.add(noSpacesLine);
                        }
                        else
                        {
                            nfaRules.add(noSpacesLine); //add rest to rule list
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
            doubleLetterStateCheck(language, nfaStates, nfaRules,repeatingRules, eplisonClosure); //methof to populate the repeating rules list
            System.out.println(eplisonClosure);
            //check if the start state needs an epsilon closure
            String closeCheck = epCloseChecker(startState.substring(1,startState.length()-1), eplisonClosure); // if there is a cascading ep close step it returns that
            if(closeCheck != null && !closeCheck.isEmpty())// if not null another step to be added
            {
                startState = "{" + startState.substring(1,startState.length()-1) + "," + closeCheck + "}"; //creating new rule
                System.out.println("blahh");
            }
            dfaStates.add(startState);
            System.out.println(closeCheck);
            System.out.println(startState);
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
                  System.out.println(nfaAcceptS);
                  System.out.println(nfaAcceptS.get(k));
                  if(("" + currentState.charAt(j)).equals(nfaAcceptS.get(k))) //if an nfa accept state is in our current dfa state, ignoring brackets
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
          String currentRule = nfaEplisonRules.get(checkRule);
          String startState = getStartState(currentRule);
          String endState = getEndState(currentRule);
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
          String epCloseStartState = getEpsStartState(eplisonClosure.get(i)); //the state set
          System.out.println(destinationState);
          if(epCloseStartState.equals(destinationState)) //if the destination state has its own destination state, grab it
          {
              cascadingDestination = eplisonClosure.get(i).substring(3,eplisonClosure.get(i).length()-1); //getting the not start state and not brackets
          }
      }
      return cascadingDestination;
    }


    public static String getStartState(String currentRule)
    {
      boolean firstSight = true;
      String startState = "";
      for(int a = 0; a < currentRule.length(); a++) //loops through the rule to account for variable state sizes
      {
        if(firstSight && currentRule.charAt(a) == '}') //this would be the first } in the rule, ending the origin state
        {
          firstSight = false;
          startState = currentRule.substring(1, a); //grab what is inside the { }, start state!!!
        }
      }
      return startState;
    }


    public static String getEpsStartState(String currentRule)
    {
      boolean firstSight = true;
      String startState = "";
      for(int a = 0; a < currentRule.length(); a++) //loops through the rule to account for variable state sizes
      {
        if(firstSight && currentRule.charAt(a) == ',') //this would be the end of first state in the rule
        {
          firstSight = false;
          startState = currentRule.substring(1, a); //grab what is inside the { }, start state!!!
        }
      }
      return startState;
    }


    public static String getLetter(String currentRule)
    {
      int startIndex = 0;
      boolean firstSight = true;
      String letter = "";
      for(int a = 0; a < currentRule.length(); a++) //loops through the rule to account for variable state sizes
      {
        if(firstSight && currentRule.charAt(a) == '}') //this would be the first } in the rule, ending the origin state
        {
          startIndex = a;
          firstSight = false;
        }
        if(!firstSight && currentRule.charAt(a) == '=') //everything before this is the letter, skipping the spaces
        {
          letter = currentRule.substring(startIndex + 2, a); //this is the letter, +2 to skip the },
          firstSight = true;
        }
      }
      return letter;
    }


    public static String getEndState(String currentRule)
    {
      boolean firstSight = true;
      String endState = "";
      for(int a = 0; a < currentRule.length(); a++) //loops through the rule to account for variable state sizes
      {
        if(firstSight && currentRule.charAt(a) == '=') //everything after this is the end state
        {
          endState = currentRule.substring(a + 2, currentRule.length() - 1); // after the ={ to the end is the end state not with }
          firstSight = false;
        }
      }
      return endState;
    }


    public static void doubleLetterStateCheck(List<String> language, List<String> nfaStates, List<String> nfaRules, List<String> repeatingRules, List<String> eplisonClosure)
    {
      for(int i = 0; i < language.size(); i++)
      {
        String currentLetter = language.get(i); //getting the current letter to search for
        for(int j = 0; j < nfaStates.size(); j++)
        {
          String currentState = nfaStates.get(j); //getting the current state to search for
          String newRule = ("{" + currentState + "}," + currentLetter + "={"); //starting the new rule
          Queue<Integer> destinationQueue = new LinkedList<>(); //if there are multiple destinations in the queue grab them all

          int ruleRepeatCount = 0; //if there are more than 1 rule with this state and letter combo, eplison close it
          for(int k = 0; k < nfaRules.size(); k++) // loop through all rules
          {
            String currentRule = nfaRules.get(k);
            String ruleState = getStartState(currentRule); //to get the state of the rule we are looking at
            String ruleLetter = getLetter(currentRule); //to get the letter of the rule we are looking at

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
              String destination = getEndState(nfaRules.get(index));

              String closeCheck = epCloseChecker(destination, eplisonClosure);
              if(!closeCheck.equals(""))
              {
                destination = destination + "," + closeCheck;
              }        

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

            if(!destination.equals("")) //if there is a rule for this state and letter combo
            {
              destinationState = destinationState + destination + ","; // adds the new destination to the destination state
            } //if there is no rule for this combo then destinationState does not change
          }
          if(destinationState.equals("{"))
          {
            continue;
          }
          else
          {
            //CHECKING FOR REPEATING STATES
            String destinationCopy = "";

            for(int i = 0; i < destinationState.length(); i++)
            {
              char currentChar = destinationState.charAt(i);
              if(currentChar != '{' && currentChar != '}' && currentChar != ',')
              {
                destinationCopy = destinationCopy + currentChar;
              }
            }
            char tempCharArray[] = destinationCopy.toCharArray(); // convert input string to char array
            Arrays.sort(tempCharArray); // sort tempArray
            char compareToNext = tempCharArray[0];
            for(int j = 1; j < tempCharArray.length; j++) //now to delete the multiples, since it is now sorted everything will be in line
            {
              if(tempCharArray[j] != compareToNext) //if an element is equal to the one next to it, its a repeat
              {
                compareToNext = tempCharArray[j]; //if its not a repeat, move to the new element
              }
              else
              {
                tempCharArray[j] = '/'; //if it is a repeat, null out that element
              }
            }

            destinationCopy = "{"; //start this over
            for(int i = 0; i < tempCharArray.length; i++) //recombine to a string
            {
              if(tempCharArray[i] != '/') //if its null dont put it in
              {
                destinationCopy = destinationCopy + tempCharArray[i] + ","; //put a comma after all the numbers
              }
            }

            destinationState = destinationCopy; //recombine into a string with proper format
            //BACK TO OTHER STUFF
          }
          destinationState = destinationState.substring(0,destinationState.length()-1) + "}"; // removing the last , and adding a close bracket

          String newRule = currentState + "," + letter + "=" + destinationState; // creates the new rule
          dfaRules.add(newRule); //adds new rule to the list
          checkStatesStack.push(destinationState); //puts the state on the stack to be checked later
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
          String ruleState = getStartState(currentRule); //to get the state of the rule we are looking at
          String ruleLetter = getLetter(currentRule); //to get the letter of the rule we are looking at

          if(ruleState.equals(currentState) && ruleLetter.equals(letter)) //only look at rules that deal with the state & letter we currently have
          {
            destinationState = destinationState + getEndState(currentRule); //adding this rule's destination state to the overall destination state
            for(int j = 0; j < eplisonClosure.size(); j++)
            {
              String epCloseStartState = getEpsStartState(eplisonClosure.get(j));
              if(epCloseStartState.equals(destinationState)) //if this end state has more states to go to after
              {
                destinationState = destinationState + eplisonClosure.get(j).substring(3); //get all the cascading destination states
              }
            }
            System.out.println(ruleState + "  no   " + letter + "    " + destinationState);
            return destinationState;
          }
        }

        for(int j = 0; j < eplisonClosure.size(); j++)
        {
          String currentEpClose = eplisonClosure.get(j);
          String epCloseStartState = getEpsStartState(currentEpClose);
          if(epCloseStartState.equals(currentState)) //if this end state has more states to go to after
          {
            destinationState = destinationState + currentEpClose.substring(3, currentEpClose.length()-1) + ","; //get all the cascading destination states
          } System.out.println("dest    " + currentEpClose.substring(3, currentEpClose.length()-1) + "       " + destinationState + " " + epCloseStartState);
        }


        for(int i = 0; i < nfaRules.size(); i++) //loop through rules to see where this state goes
        {
            String currentRule = nfaRules.get(i); //this is the full rule we are looking at
            String ruleState = getStartState(currentRule); //to get the state of the rule we are looking at
            String ruleLetter = getLetter(currentRule); //to get the letter of the rule we are looking at
            if(ruleState.equals(currentState) && ruleLetter.equals(letter)) //only look at rules that deal with the state & letter we currently have
            {
              destinationState = destinationState + getEndState(currentRule); //adding this rule's destination state to the overall destination state
              for(int j = 0; j < eplisonClosure.size(); j++)
              {
                String currentEpClose = eplisonClosure.get(j);
                String epCloseStartState = getEpsStartState(currentEpClose); if(ruleLetter.equals("b")) { System.out.println("b    " + epCloseStartState); }
                if(epCloseStartState.equals(destinationState)) //if this end state has more states to go to after
                {
                  destinationState = destinationState + "," + currentEpClose.substring(3, currentEpClose.length()-1); //get all the cascading destination states
                } System.out.println("dest    " + currentEpClose.substring(3, currentEpClose.length()-1) + "       " + destinationState + " " + epCloseStartState);
              }
            }
        }


        /*if(destinationState.length() > 1) //if there are more than 1 state it drops the }
        {
          destinationState = destinationState.substring(1, destinationState.length()-1);
        }*/
                  System.out.println(currentState + "     " + letter + "  hi   " + destinationState);

        return destinationState; //returning the full destination state without the final }
    }
  }
