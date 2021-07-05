package edu.pdx.cs410J.yl6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

/**
 * The main class for the CS410J appointment book Project
 */
public class Project1 {

  static final String MISSING_CMD_LINE_ARGS = "Missing command line arguments";
  static final String MISSING_DESCRIPTION = "Missing description of the appointment";
  static final String MISSING_BEGIN_DATE = "Missing begin date of the appointment";
  static final String MISSING_BEGIN_TIME = "Missing begin time of the appointment";
  static final String MISSING_END_DATE = "Missing end date of the appointment";
  static final String MISSING_END_TIME = "Missing end time of the appointment";
  static final String MORE_ARGS = "More arguments passed in than needed";
  static final String USAGE_MESSAGE = "usage";

  static final int maximumArgs = 8;

  static boolean printReadme = false;
  static boolean printAppointment = false;

  public static void main(String[] args) {
    printReadme = false;
    printAppointment = false;

    HashMap<Integer, String> exitMsgs = new HashMap<Integer, String>();
    exitMsgs.put(-1, USAGE_MESSAGE);
    exitMsgs.put(0, MISSING_CMD_LINE_ARGS);
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
  
    int argStartAt = getArgumentStart(args);
    int argNums = args.length - argStartAt;

    if (printReadme) {
      printErrorMessageAndExit(exitMsgs.get(-1));
    }

    if (args.length > maximumArgs)
      printErrorMessageAndExit(exitMsgs.get(maximumArgs));

    if (argNums < 6) 
      printErrorMessageAndExit(exitMsgs.get(argNums));
    
    if (args.length >= 7) {
      for (int i = 0; i < args.length - 6; ++i)
        validateSwitch(args[i]);
    }

    // number of args meet requirement
    validateDate(args[argStartAt + 2]);
    validateDate(args[argStartAt + 4]);
    validateTime(args[argStartAt + 3]);
    validateTime(args[argStartAt + 5]);
    
    Appointment appointment 
      = new Appointment(args[argStartAt + 2], args[argStartAt + 3], 
                        args[argStartAt + 4], args[argStartAt + 5],
                        args[argStartAt + 1]);
     
    if (printAppointment)
      System.out.println(appointment.toString());

    System.exit(0);
  }

  private static void printErrorMessageAndExit(String message) {
    System.err.println(message);
    System.exit(1);
  }

  private static int getArgumentStart(String[] args) {
    int indexStart = 0;
    for (int i = 0; i < 2; ++i) {
      if (markSwitch(args[indexStart]))
        indexStart += 1; 
      else
        break;
    }
    return indexStart;
  }

  private static boolean markSwitch(String s) {
    boolean isSwitch = false;
    if (s.equals("-print")) {
      printAppointment = true;
      isSwitch = true;
    } 
    if (s.equals("-README")) {
      printReadme = true;
      isSwitch = true;
    }
    return isSwitch;
  }

  private static void validateSwitch(String s) {
    if (!s.equals("-print") && !s.equals("-README")) 
      printErrorMessageAndExit(s + " is not an available switch");
  }

  private static void validateDate(String s) {
    String ptn = "([0-9]{1,2})/([0-9]{1,2})/([0-9]{4})";
    Pattern r = Pattern.compile(ptn);
    Matcher m = r.matcher(s);
    
    if (!m.matches())
      printErrorMessageAndExit("date " + s + " format does not meet requirement");
    
    int month = Integer.parseInt(m.group(1));
    int day = Integer.parseInt(m.group(2));
    if (month > 12)
      printErrorMessageAndExit(month + " is not a valid month");
    if (day > 31)
      printErrorMessageAndExit(day + " is not a valid day");
  }

  private static void validateTime(String s) {
    String ptn = "([0-9]{1,2}):([0-9]{1,2})";
    Pattern r = Pattern.compile(ptn);
    Matcher m = r.matcher(s);
    
    if (!m.matches())
      printErrorMessageAndExit("time " + s + " format does not meet requirement");

    int hour = Integer.parseInt(m.group(1));
    int minute = Integer.parseInt(m.group(2));
    if (hour >= 24)
      printErrorMessageAndExit(hour + " is not a valid hour");
    if (minute > 59)
      printErrorMessageAndExit(minute + " is not a valid minute");
  }

}