package edu.pdx.cs410J.yl6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * ArgumentParser is the class that parses commandline arguments that are assumed to 
 * be [ options ] followed by &lt; arguments &gt; as follow:
 * <p>
 *            [ options ] &lt; arguments &gt;
 * <p>
 * where options consitute zero or more "option"s defined as
 * <p>
 *            option arg1 arg2 ... argn
 * <p>
 * where there can be 0 or more arg that must follow the option.
 * <p>
 * options appear in [ options ] can be any order without duplication, but the number 
 * of arguments expected corresponding to the option must be correct, and those 
 * arguments must follow its belonging option (otherwise it may cause a parse error, 
 * or even coincidently assignment of arguments to a different option without any 
 * warning or error message). 
 * <p>
 * the &lt; arguments &gt; must follow all the options that are intended to be enabled 
 * (so also follow the last argument for the last option to be enabled). The number of 
 * arguments in &lt; arguments &gt; must match specified number. 
 * <p>
 * ArgumentParser greedily match first n elements of the String array (as commandline 
 * args) with all available options until (n+1)th element is neither a valid option or 
 * an argument for an option; from (n+1)th element to the last one, they are treated 
 * as &lt; arguments &gt;.
 * <p>
 * A note on error message: 
 * <p>
 * Suppose A = specified size of &lt; arguments &gt;, M = A + summation_{i} (1 + opt(i)), 
 * where i is the indexing of available option, and opt(i) is expected number of 
 * arguments for ith option. 
 * <p>
 * If we have an array args[0 .. N] with size N, then if N &lt; M, args[0 .. d] contains 
 * valid options and their arguments, and d + A &lt; N, then args[d .. N-A] are treated as 
 * invalid option which shall be reported as error message. 
 * <p>
 * If N &gt;= M, then args[d+A .. N] are treated as extraneous arguments which shall be 
 * reported as error message.
 */
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
  static final String MORE_ARGS = "The following argument(s) passed in are extraneous:\n";

  /**
   * Create an ArgumentParser instance
   */
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

  /**
   * Add an argument required to be parsed.
   * 
   * @param arg the description of the argument that is prompted in error message
   *            when the argument is missing
   * @return    current invoking ArgumentParser instance
   */
  public ArgumentParser addArgument(String arg) {
    this.argumentNames.add(arg);
    this.maxArgumentPlusOptionAllowed += 1;
    this.requiredArgumentNum += 1;
    return this;
  }

  /**
   * Add an option with expected number of arguments for the option to be parsed.
   * 
   * @param option the option (ex. "-print") should appear before its arguments
   * @param argNum the number of arguments must follow the option and associate with
   *               the option
   * @return       current invoking ArgumentParser instance
   */
  public ArgumentParser addOption(String option, int argNum) {
    this.optionEnableStatusMap.put(option, false);
    this.optionArgumentMap.put(option, new ArrayList<String>());
    this.optionArgumentNumberMap.put(option, argNum);
    this.maxArgumentPlusOptionAllowed += argNum + 1;
    return this;
  }

  /**
   * Set usage message to be displayed 
   * 
   * @param usage a String that describes usage
   * @return      current invoking ArgumentParser instance
   */
  public ArgumentParser setUsage(String usage) {
    this.usage = usage;
    return this;
  }

  /**
   * Set readme message to be displayed when "-README" is enabled
   * 
   * @param readme the readme message
   * @return       current invoking ArgumentParser instance
   */
  public ArgumentParser setReadme(String readme) {
    this.readme = readme;
    return this;
  }

  /**
   * Given option <code>s</code>, return an ArrayList of String that stores 
   * arguments for option <code>s</code>.
   * 
   * @param s an option
   * @return  an ArrayList of String that stores arguments for option 
   *          <code>s</code>.
   */
  public ArrayList<String> getOptionArguments(String s) {
    return this.optionArgumentMap.get(s);
  }

  /**
   * Check if given option <code>s</code> is enabled (i.e. appear in commandline 
   * args, and also it is valid, which means it is uniquely appear in commandline 
   * args, the number of args for <code>s</code> is correct, and <code>s</code> is 
   * an available option).  
   * 
   * @param s the option to check
   * @return  <code>true</code> if it is enabled, <code>false</code> otherwise.
   */
  public boolean isEnabled(String s) {
    return this.optionEnableStatusMap.get(s);
  }

  /**
   * Get an array of Strings that stores &lt; arguments &gt;. 
   * 
   * @return an array of Strings that stores &lt; arguments &gt;. 
   */
  public String[] getArguments() {
    return this.arguments;
  }

  /**
   * Given an array <code>args</code> of Strings that represents command line 
   * arguments, parse it by storing enabled options, arguments for each enabled 
   * option, and arguments for &lt; arguments &gt;. If any error occur (ex. not 
   * enough arguments for &lt; arguments &gt;, or not enough arguments for its 
   * option), store corresponding error message to <code>errorMessage</code> which 
   * can be accessed by method <code>getErrorMessage</code>, and this method will 
   * return <code>false</code> indicates parse error has occurred.
   * 
   * @param args an array of Strings to be parsed
   * @return     <code>true</code> if parsed successfully; 
   *             <code>false</code> otherwise.
   */
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
      this.errorMessage = MORE_ARGS + 
          itemizeString(Arrays.asList(args), this.maxArgumentPlusOptionAllowed - 1, 
                        args.length - 1);
      return false;
    }
    if (actualArgumentNum < requiredArgumentNum) {
      if (actualArgumentNum == 0) {
        this.errorMessage = "Missing command line arguments\n" + this.usage;
      } else {
        this.errorMessage = "The following argument(s) are missing:\n" + 
            itemizeString(this.argumentNames, actualArgumentNum, 
                          requiredArgumentNum - 1);
      }
      return false;
    } 
    if (actualArgumentNum > requiredArgumentNum) {
      this.errorMessage = "The following option(s) cannot be recognized:\n" +
          itemizeString(Arrays.asList(args), argStartAt, 
                        args.length - requiredArgumentNum - 1);
      return false;
    }

    this.arguments = new String[this.requiredArgumentNum];

    for (int i = argStartAt; i < args.length; ++i) {
      this.arguments[i - argStartAt] = args[i];
    }

    return true;
  }

  /**
   * Starting with <code>n = 0</code>, greedily match first <code>n</code>
   * elements of <code>args</code> with options and their arguments until the 
   * <code>(n+1)</code>th element is neither an available option, nor an 
   * argument for an option. 
   * 
   * @param args an array of Strings to be parsed
   * @return     the index of the first occurrance of a string in <code>args</code>
   *             that is neither an available option, nor an argument for an option. 
   */
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
        // break the while loop once a non-option detected, current 
        // indexStart points to the first argument
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
   * Given a String <code>s</code> represents option, mark it as enabled if it has not 
   * been marked except that <code>s</code> is "-README" (since as long as "-README" 
   * appear, the error message always is the readme info); otherwise set 
   * <code>errorMessage</code> to indicate duplicate option detected.
   * 
   * @param s the option to mark
   * @return  <code>-1</code> if duplicated option detected; 
   *          <code>1</code> if <code>s</code> is an option either unique or it is -README;
   *          <code>0</code> otherwise.
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

  /**
   * This method is invoke when parse error has occurred. However, it is not sure if
   * there exists "-README" behind the index at which the error occurred in 
   * <code>args</code>. This method find "-README" after from index <code>start</code>
   * to the end of <code>args</code>. 
   * 
   * @param args  an array of Strings to be scanned to find "-README" starting 
   *              at <code>start</code>
   * @param start the starting index to scan
   */
  private void lookForReadmeOptionBehind(String[] args, int start) {
    for (int i = start; i < args.length; ++i) {
      if (args[i].equals(optionReadme)) {
        this.optionEnableStatusMap.put(optionReadme, true);
      }
    }
  }

  /**
   * Get error message set from last call of <code>parse</code>
   * 
   * @return error message reports error occurred during last call of <code>parse</code>
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }

  /**
   * Format a slice of <code>List</code> of Strings to be itemized (one line per String
   * with leading two spaces) and returns such a formatted string. 
   * 
   * @param args  a <code>List</code> of Strings
   * @param start the starting index of the <code>List</code>
   * @param end   the ending index of the <code>List</code>
   * @return      a formatted string as described above
   */
  private String itemizeString(List<String> args, int start, int end) {
    StringBuilder sb = new StringBuilder();

    for (int i = start; i <= end; ++i) {
      sb.append("  ");
      sb.append("* ");
      sb.append(args.get(i));
      sb.append('\n');
    }
    return sb.toString();
  }
}