package edu.pdx.cs410J.yl6;

import java.util.ArrayList;
import java.util.HashMap;

public class ArgumentParser {

  private HashMap<String, Boolean> optionEnableStatusMap;
  private HashMap<String, ArrayList<String>> optionArgumentMap;
  private HashMap<String, Integer> optionArgumentNumberMap;
  private ArrayList<String> argumentNames;
  private String usage;
  private String readme;
  private String errorMessage;
  private int maxArgumentPlusOptionAllowed;
  private int requiredArgumentNum;
  private String[] arguments;

  static final String optionReadme = "-README";

  static final String MISSING_OPTION_ARG = "Missing argument of option ";
  static final String MORE_ARGS = "More arguments passed in than needed";

  public ArgumentParser() {
    this.optionEnableStatusMap = new HashMap<>();
    this.optionArgumentMap = new HashMap<>();
    this.optionArgumentNumberMap = new HashMap<>();
    this.argumentNames = new ArrayList<>();
    this.usage = "not specified yet";
    this.readme = "not specified yet";
    this.maxArgumentPlusOptionAllowed = 0;
    this.requiredArgumentNum = 0;
    addOption(optionReadme, 0);
  }

  public ArgumentParser addArgument(String arg) {
    this.argumentNames.add(arg);
    this.maxArgumentPlusOptionAllowed += 1;
    this.requiredArgumentNum += 1;
    return this;
  }

  public ArgumentParser addOption(String option, int argNum) {
    this.optionEnableStatusMap.put(option, false);
    this.optionArgumentMap.put(option, new ArrayList<String>());
    this.optionArgumentNumberMap.put(option, argNum);
    this.maxArgumentPlusOptionAllowed += argNum + 1;
    return this;
  }

  public ArgumentParser setUsage(String usage) {
    this.usage = usage;
    return this;
  }

  public ArgumentParser setReadme(String readme) {
    this.readme = readme;
    return this;
  }

  public ArrayList<String> getOptionArguments(String s) {
    return this.optionArgumentMap.get(s);
  }

  public boolean isEnabled(String s) {
    return this.optionEnableStatusMap.get(s);
  }

  public String[] getArguments() {
    return this.arguments;
  }

  public boolean parse(String[] args) {
    int argStartAt = parseOptions(args);

    if (this.optionEnableStatusMap.get(optionReadme)) {
      this.errorMessage = this.readme;
      return false;
    }
    if (argStartAt < 0) {
      return false;
    }

    int actualArgumentNum = args.length - argStartAt;
    
    if (args.length > this.maxArgumentPlusOptionAllowed) {
      this.errorMessage = MORE_ARGS;
      return false;
    }
    if (actualArgumentNum < requiredArgumentNum) {
      if (actualArgumentNum == 0) {
        this.errorMessage = "Missing command line arguments\n" + this.usage;
      } else {
        this.errorMessage = "Missing " + this.argumentNames.get(actualArgumentNum);
      }
      return false;
    } 
    if (actualArgumentNum > requiredArgumentNum) {
      this.errorMessage = args[argStartAt] + " is not an available switch";
      return false;
    }

    this.arguments = new String[this.requiredArgumentNum];

    for (int i = argStartAt; i < args.length; ++i) {
      this.arguments[i - argStartAt] = args[i];
    }

    return true;
  }

  private int parseOptions(String[] args) {
    int indexStart = 0;

    while (indexStart < args.length) {
      int markResult = markOptionAsEnable(args[indexStart]);

      if (markResult == -1) {
        // duplicated option detected, but not sure if we have -README behind
        lookForReadmeOptionBehind(args, indexStart + 1);
        return -1;
      }
      if (markResult == 0) {
        // break the while loop once a non-option detected, current indexStart points
        // to the first argument
        break;
      }

      // the rest handles the option which is valid and unique in args[0..indexStart]
      int argNum = this.optionArgumentNumberMap.get(args[indexStart]);

      if (indexStart + argNum >= args.length) {
        // required argument number exceeds the number of actual arguments passed in
        this.errorMessage = MISSING_OPTION_ARG + args[indexStart];
        return -1;
      }

      // have enough arguments
      ArrayList optionArg = this.optionArgumentMap.get(args[indexStart]);
      for (int i = 0; i < argNum; ++i) {
        optionArg.add(i, args[indexStart + i + 1]);
      }  
      indexStart += argNum + 1; // the next index is the one after all the arguments 
    }

    return indexStart;
  }

  /**
   * 
   * @param s
   * @return  -1 if duplicated option detected; 
   *          1 if <code>s</code> is an option either unique or it is -README;
   *          0 otherwise.
   */
  private int markOptionAsEnable(String s) {
    if (!this.optionEnableStatusMap.containsKey(s)) {
      return 0;
    }
    if (!s.equals(optionReadme) && this.optionEnableStatusMap.get(s)) {
      this.errorMessage = "duplicated " + s + " in options";
      return -1;
    }
    this.optionEnableStatusMap.put(s, true);
    return 1;
  }

  private void lookForReadmeOptionBehind(String[] args, int start) {
    for (int i = start; i < args.length; ++i) {
      if (args[i].equals(optionReadme)) {
        this.optionEnableStatusMap.put(optionReadme, true);
      }
    }
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }
}