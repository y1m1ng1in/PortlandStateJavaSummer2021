package edu.pdx.cs410J.yl6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
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
  static final String MISSING_FILENAME = "Missing file of the appointment book to load";
  static final String README = loadPlainTextFromResource("README.txt");
  static final String USAGE = loadPlainTextFromResource("usage.txt");

  static final int maximumCommandlineArgs = 10;
  static final int requiredArgumentNum = 6;

  static final int ownerArgIndex = 0;
  static final int descriptionArgIndex = 1;
  static final int beginDateArgIndex = 2;
  static final int beginTimeArgIndex = 3;
  static final int endDateArgIndex = 4;
  static final int endTimeArgIndex = 5;

  static final String optionReadme = "-README";
  static final String optionPrintAppointment = "-print";
  static final String optionLoadFile = "-textFile";

  static final String[] options = { 
      optionReadme, optionPrintAppointment, optionLoadFile
  };

  static HashMap<String, Boolean> optionStatus;
  static HashMap<String, ArrayList<String>> optionArgs;

  /**
   * Main program that parses the command line, creates a <code>Appointment</code>,
   * and prints a description of the appointment to standard out by invoking its
   * <code>toString</code> method if <code>-print</code> option is enabled, or only
   * prints readme information to standard error once <code>-README</code> 
   * option is enabled.
   */
  public static void main(String[] args) {
    optionStatus = new HashMap<>();
    optionArgs = new HashMap<>();
    for (String opt : options) {
      optionStatus.put(opt, false);
      optionArgs.put(opt, new ArrayList<String>());
    }

    HashMap<Integer, String> exitMsgs = new HashMap<Integer, String>();
    exitMsgs.put(-1, README);
    exitMsgs.put(0, MISSING_CMD_LINE_ARGS+ '\n' + USAGE);
    exitMsgs.put(1, MISSING_DESCRIPTION);
    exitMsgs.put(2, MISSING_BEGIN_DATE);
    exitMsgs.put(3, MISSING_BEGIN_TIME);
    exitMsgs.put(4, MISSING_END_DATE);
    exitMsgs.put(5, MISSING_END_TIME);
    exitMsgs.put(maximumCommandlineArgs, MORE_ARGS);

    if (args.length < 1)
      printErrorMessageAndExit(exitMsgs.get(0));

    if (args.length == 1) {
      if (markSwitch(args[0])) {
        if (optionStatus.get(optionReadme)) {
          printErrorMessageAndExit(exitMsgs.get(-1));
        } else {
          printErrorMessageAndExit(exitMsgs.get(0));
        }
      } else {
        printErrorMessageAndExit(exitMsgs.get(1));
      }
    }
  
    int argStartAt = detectAndMarkSwitches(args);
    int actualArgumentNum = args.length - argStartAt;

    if (optionStatus.get(optionReadme)) {
      printErrorMessageAndExit(exitMsgs.get(-1));
    }
    if (args.length > maximumCommandlineArgs) {
      printErrorMessageAndExit(exitMsgs.get(maximumCommandlineArgs));
    }
    if (actualArgumentNum < requiredArgumentNum) {
      printErrorMessageAndExit(exitMsgs.get(actualArgumentNum));
    }
      
    if (actualArgumentNum > requiredArgumentNum) {
      printErrorMessageAndExit(args[argStartAt] + " is not an available switch");
    }

    // number of args meet requirement
    validateNonemptyStringField(args[argStartAt + ownerArgIndex], "owner");
    validateNonemptyStringField(args[argStartAt + descriptionArgIndex], "description");
    validateDate(args[argStartAt + beginDateArgIndex]);
    validateDate(args[argStartAt + endDateArgIndex]);
    validateTime(args[argStartAt + beginTimeArgIndex]);
    validateTime(args[argStartAt + endTimeArgIndex]);

    try {
      AppointmentBook<Appointment> book;
      String apptbookFile = "";
      
      Appointment appointment 
          = new Appointment(args[argStartAt + beginDateArgIndex], 
                            args[argStartAt + beginTimeArgIndex], 
                            args[argStartAt + endDateArgIndex], 
                            args[argStartAt + endTimeArgIndex],
                            args[argStartAt + descriptionArgIndex]);

      if (optionStatus.get(optionLoadFile)) {
        apptbookFile = optionArgs.get(optionLoadFile).get(0);
        TextParser<AppointmentBook, Appointment> textParser 
            = new TextParser(apptbookFile, args[argStartAt + ownerArgIndex], 
                AppointmentBook.class, Appointment.class);
        book = textParser.parse();       
      } else {
        book = new AppointmentBook(args[argStartAt + ownerArgIndex]);
      }
      book.addAppointment(appointment);
      
      if (optionStatus.get(optionLoadFile)) {
        TextDumper<AppointmentBook, Appointment> textDumper = new TextDumper(apptbookFile);
        textDumper.dump(book);
      }
      if (optionStatus.get(optionPrintAppointment)) {
        System.out.println(appointment.toString());
      }
    } catch(ParserException | IOException ex) {
      printErrorMessageAndExit(ex.getMessage());
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
    while (indexStart < args.length) {
      if (markSwitch(args[indexStart])) {
        switch (args[indexStart]) {
          case optionReadme:
          case optionPrintAppointment:
            indexStart += 1;
            break;
          case optionLoadFile:
            if (indexStart + 1 < args.length) {
              ArrayList optionLoadFileArgs = optionArgs.get(optionLoadFile);
              optionLoadFileArgs.add(0, args[indexStart + 1]);
              indexStart += 2;
            } else {
              printErrorMessageAndExit(MISSING_FILENAME);
            }
            break;
          default:
        }
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
    boolean isSwitch = optionStatus.containsKey(s);
    if (isSwitch) {
      if (optionStatus.get(s)) {
        printErrorMessageAndExit("duplicated " + s + "in options");
      }
      optionStatus.put(s, true);
    }
    return isSwitch;
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