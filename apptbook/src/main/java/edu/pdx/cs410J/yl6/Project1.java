package edu.pdx.cs410J.yl6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import edu.pdx.cs410J.ParserException;

/**
 * The main class for the CS410J appointment book Project which parses commandline
 * arguments, processes arguments to construct an appointment, and based on the options
 * to either print the appointment, or print readme, or print nothing.
 * <p>
 * The class assumes that options are always followed by arguments. There are two valid
 * options available: -print, -README. This means that the parsing process will greedily
 * match commandline arguments from 0 index to higher index until either a invalid option 
 * detected, or all the available options matched, the rest of the commandline arguments
 * are treated as arguments for appointment. 
 */
public class Project1 {

  static final String MISSING_CMD_LINE_ARGS = "Missing command line arguments";
  static final String MISSING_DESCRIPTION = "Missing description of the appointment";
  static final String MISSING_BEGIN_DATE = "Missing begin date of the appointment";
  static final String MISSING_BEGIN_TIME = "Missing begin time of the appointment";
  static final String MISSING_END_DATE = "Missing end date of the appointment";
  static final String MISSING_END_TIME = "Missing end time of the appointment";
  static final String MORE_ARGS = "More arguments passed in than needed";
  static final String README = loadPlainTextFromResource("README.txt");
  static final String USAGE = loadPlainTextFromResource("usage.txt");

  static final int maximumCommandlineArgs = 8;
  static final int maximumArgs = 6;

  static final int ownerArgIndex = 0;
  static final int descriptionArgIndex = 1;
  static final int beginDateArgIndex = 2;
  static final int beginTimeArgIndex = 3;
  static final int endDateArgIndex = 4;
  static final int endTimeArgIndex = 5;

  static boolean printReadme = false;
  static boolean printAppointment = false;

  /**
   * Main program that parses the command line, creates a <code>Appointment</code>,
   * and prints a description of the appointment to standard out by invoking its
   * <code>toString</code> method if <code>-print</code> option is enabled, or only
   * prints readme information to standard error once <code>-README</code> 
   * option is enabled.
   */
  public static void main(String[] args) {
    printReadme = false;
    printAppointment = false;

    HashMap<Integer, String> exitMsgs = new HashMap<Integer, String>();
    exitMsgs.put(-1, README);
    exitMsgs.put(0, MISSING_CMD_LINE_ARGS+ '\n' + USAGE);
    exitMsgs.put(1, MISSING_DESCRIPTION);
    exitMsgs.put(2, MISSING_BEGIN_DATE);
    exitMsgs.put(3, MISSING_BEGIN_TIME);
    exitMsgs.put(4, MISSING_END_DATE);
    exitMsgs.put(5, MISSING_END_TIME);
    exitMsgs.put(8, MORE_ARGS);

    if (args.length < 1)
      printErrorMessageAndExit(exitMsgs.get(0));

    if (args.length == 1) {
      if (markSwitch(args[0])) {
        if (printReadme) {
          printErrorMessageAndExit(exitMsgs.get(-1));
        } else {
          printErrorMessageAndExit(exitMsgs.get(0));
        }
      } else {
        printErrorMessageAndExit(exitMsgs.get(1));
      }
    }
  
    int argStartAt = detectAndMarkSwitches(args);
    int argNums = args.length - argStartAt;

    if (printReadme) {
      printErrorMessageAndExit(exitMsgs.get(-1));
    }
    if (args.length > maximumCommandlineArgs) {
      printErrorMessageAndExit(exitMsgs.get(maximumCommandlineArgs));
    }
    if (argNums < maximumArgs) {
      printErrorMessageAndExit(exitMsgs.get(argNums));
    }
      
    if (args.length >= maximumArgs + 1) {
      for (int i = 0; i < args.length - maximumArgs; ++i)
        validateSwitch(args[i]);
    }

    // number of args meet requirement
    validateNonemptyStringField(args[argStartAt + ownerArgIndex], "owner");
    validateNonemptyStringField(args[argStartAt + descriptionArgIndex], "description");
    validateDate(args[argStartAt + beginDateArgIndex]);
    validateDate(args[argStartAt + endDateArgIndex]);
    validateTime(args[argStartAt + beginTimeArgIndex]);
    validateTime(args[argStartAt + endTimeArgIndex]);
    
    TextParser<AppointmentBook, Appointment> textParser 
        = new TextParser("test.txt", AppointmentBook.class, Appointment.class);

    try {
      AppointmentBook<Appointment> book = textParser.parse();
      Appointment appointment 
          = new Appointment(args[argStartAt + beginDateArgIndex], 
                            args[argStartAt + beginTimeArgIndex], 
                            args[argStartAt + endDateArgIndex], 
                            args[argStartAt + endTimeArgIndex],
                            args[argStartAt + descriptionArgIndex]);
      TextDumper<AppointmentBook, Appointment> textDumper = new TextDumper("test.txt");

      book.addAppointment(appointment);

      textDumper.dump(book);

      if (printAppointment) {
        System.out.println(appointment.toString());
      }
    } catch(ParserException ex) {
      System.err.println(ex);
    } catch (IOException ex) {
      System.err.println(ex);
    }

    System.exit(0);
  }

  /**
   * Load the content of a plain text file <code>filename</code> in the resource. 
   * If any <code>IOException</code> catched during loading via 
   * <code>getResourceAsStream</code>, the program exits with status 1 with an error
   * message to standard error indicates that error.
   * 
   * @param filename the plain text filename in the resource to be loaded
   * @return         a string that is the content of the file <code>filename</code>.
   */
  private static String loadPlainTextFromResource(String filename) {
    try {
      InputStream is = Project1.class.getResourceAsStream(filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      String line = "";
      StringBuilder sb = new StringBuilder();
      while((line = reader.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
      }
      return sb.toString();
    } catch (IOException e) {
      printErrorMessageAndExit("Cannot load plain text file from resource " + filename);
      return null;
    }
  }

  /** 
   * Write <code>message</code> passed in to standard error and exit the program
   * with status 1.
   * 
   * @param message the error message to be written to standard error       
   */
  private static void printErrorMessageAndExit(String message) {
    System.err.println(message);
    System.exit(1);
  }

  /** 
   * Given an array of arguments <code>args</code>, starting from index 0 to higher 
   * index, greedily match each argument in <code>args</code> until either an argument
   * that does not belong to valid options occurs, or all the available options 
   * are matched. 
   * 
   * @param args  the array of whole commandline arguments       
   * @return      an integer indicates the number of options detected
   */
  private static int detectAndMarkSwitches(String[] args) {
    int indexStart = 0;
    for (int i = 0; i < 2; ++i) {
      if (markSwitch(args[indexStart])) {
        indexStart += 1; 
      } else {
        break;
      }
    }
    return indexStart;
  }

  /** 
   * Given a string <code>s</code>, match it with available options. If there is 
   * a match, assign its corresponding flag (one of this class field 
   * <code>printReadme</code>, <code>printAppointment</code>) to <code>true</code>.
   * <p>
   * This function checks any duplicated options passed in from commandline, any
   * detected causes the program exits in status 1 with an error message indicates
   * duplication.
   * 
   * @param s the string to be matched with all available options       
   * @return  <code>true</code> if there is a match; <code>false</code> otherwise.
   */
  private static boolean markSwitch(String s) {
    boolean isSwitch = false;
    if (s.equals("-print")) {
      if (printAppointment) {
        printErrorMessageAndExit("duplicated -print in options");
      }
      printAppointment = true;
      isSwitch = true;
    } 
    if (s.equals("-README")) {
      if (printReadme) {
        printErrorMessageAndExit("duplicated -README in options");
      }
      printReadme = true;
      isSwitch = true;
    }
    return isSwitch;
  }

  /** 
   * Given a string <code>s</code>, match it with all available options, if there
   * isn't a match, exit the program with status 1 with error message indicates that 
   * the option is invalid. 
   * 
   * @param s the string to be matched with all available options       
   */
  private static void validateSwitch(String s) {
    if (!s.equals("-print") && !s.equals("-README")) {
      printErrorMessageAndExit(s + " is not an available switch");
    }
  }

  /**
   * Given a string <code>s</code>, check if it is empty after removing leading 
   * and tailing spaces. If it is empty, exit the program with status 1 with error message
   * indicates that field <code>fieldName</code> is empty.  
   * 
   * @param s         the string to check whether it is empty after being trimed.
   * @param fieldName the name of the field to display in the message to the standard error.
   */
  private static void validateNonemptyStringField(String s, String fieldName) {
    String trimed = s.trim();
    if (trimed.equals("")) {
      printErrorMessageAndExit("Field " + fieldName + " should not be empty");
    }
  }

  /** 
   * Given a string <code>s</code>, match it with date format mm/dd/yyyy, where
   * mm and dd can be either 1 digit or 2 digit, yyyy must be 4 digit, each field
   * must be delimited by "/". 
   * <p>
   * If the string does not match the above pattern, the program exits with status
   * 1 with an error message indicates format error. If the mm or dd field does not live
   * in the range 1 - 12, 1 - 31, respectively, then the program exits with status 1
   * with an error message indicates value out-of-range error. 
   *
   * @param s the string to be matched with above described pattern.     
   */
  private static void validateDate(String s) {
    String ptn = "([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})";
    Pattern r = Pattern.compile(ptn);
    Matcher m = r.matcher(s);
    
    if (!m.matches()) {
      printErrorMessageAndExit("date " + s + " format does not meet requirement");
    }
    
    int month = Integer.parseInt(m.group(1));
    int day = Integer.parseInt(m.group(2));
    if (month > 12) {
      printErrorMessageAndExit(month + " is not a valid month");
    }
    if (day > 31) {
      printErrorMessageAndExit(day + " is not a valid day");
    }
  }

  /** 
   * Given a string <code>s</code>, match it with time format hh:mm, where hh and mm
   * can be either 1 digit or 2 digit, each field must be delimited by ":". 
   * 
   * <p>
   * If the string does not match the above pattern, the program exits with status
   * 1 with an error message indicates format error. If the hh or mm field does not live
   * in the range 0 - 23, 0 - 59, respectively, then the program exits with status 1
   * with an error message indicates value out-of-range error. 
   *
   * @param s the string to be matched with above described pattern.     
   */
  private static void validateTime(String s) {
    String ptn = "([0-9]{1,2}):([0-9]{1,2})";
    Pattern r = Pattern.compile(ptn);
    Matcher m = r.matcher(s);
    
    if (!m.matches()) {
      printErrorMessageAndExit("time " + s + " format does not meet requirement");
    }

    int hour = Integer.parseInt(m.group(1));
    int minute = Integer.parseInt(m.group(2));
    if (hour >= 24) {
      printErrorMessageAndExit(hour + " is not a valid hour");
    }
    if (minute > 59) {
      printErrorMessageAndExit(minute + " is not a valid minute");
    }
  }

}