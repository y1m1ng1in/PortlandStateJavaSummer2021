package edu.pdx.cs410J.yl6;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main class for the CS410J appointment book Project
 */
public class Project1 {

  public static void main(String[] args) {
    if (args.length == 0) 
      printErrorMessageAndExit("Missing command line arguments");
    else if (args.length == 1) 
      printErrorMessageAndExit("Missing description of the appointment");
    else if (args.length == 2) 
      printErrorMessageAndExit("Missing begin date of the appointment");
    else if (args.length == 3) 
      printErrorMessageAndExit("Missing begin time of the appointment");
    else if (args.length == 4) 
      printErrorMessageAndExit("Missing end date of the appointment");
    else if (args.length == 5) 
      printErrorMessageAndExit("Missing end time of the appointment");
    
    boolean printReadme = false;
    boolean printAppointment = false;
    int argStartAt = 0;

    if (args.length == 7) {
      validateSwitch(args[0]);
      if (args[0].equals("-print")) 
        printAppointment = true;
      else if (args[0].equals("-README"))
        printReadme = true;
      argStartAt = 1;
    }

    if (args.length == 8) {
      validateSwitch(args[0]);
      validateSwitch(args[1]);
      if (args[0].equals(args[1]))
        printErrorMessageAndExit("switch " + args[0] + " is duplicated");
      printAppointment = true;
      printReadme = true;
      argStartAt = 2;
    }
    
    if (args.length > 8) 
      printErrorMessageAndExit("more arguments passed in than needed");

    // number of args meet requirement
    validateDate(args[argStartAt + 2]);
    validateDate(args[argStartAt + 4]);
    validateTime(args[argStartAt + 3]);
    validateTime(args[argStartAt + 5]);
    
    Appointment appointment 
      = new Appointment(args[argStartAt + 2], args[argStartAt + 3], 
                        args[argStartAt + 4], args[argStartAt + 5],
                        args[argStartAt + 1]);

    if (printReadme)
      System.out.println("print usage ...");
    
    if (printAppointment)
      System.out.println(appointment.toString());

    System.exit(1);
    
  }

  private static void printErrorMessageAndExit(String message) {
    System.err.println(message);
    // System.err.println(USAGE_MESSAGE);
    System.exit(1);
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