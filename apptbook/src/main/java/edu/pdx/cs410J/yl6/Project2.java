package edu.pdx.cs410J.yl6;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import edu.pdx.cs410J.ParserException;

/**
 * The main class for the CS410J appointment book Project which parses commandline
 * arguments, processes arguments to construct an appointment, and based on the options
 * to print the appointment, print readme, import and export existing appointment book
 * from external file. 
 * <p>
 * The class assumes that options are always followed by arguments. There are 3 valid
 * options available: -print, -README, and -textFile filename. This means that the 
 * parsing process will greedily match commandline arguments from 0 index to higher index 
 * until either a invalid option detected, or all the available options matched, the rest 
 * of the commandline arguments are treated as arguments for appointment. 
 */
public class Project2 {

  static final String MISSING_CMD_LINE_ARGS = "Missing command line arguments";
  static final String MISSING_DESCRIPTION = "Missing description of the appointment";
  static final String MISSING_BEGIN_DATE = "Missing begin date of the appointment";
  static final String MISSING_BEGIN_TIME = "Missing begin time of the appointment";
  static final String MISSING_END_DATE = "Missing end date of the appointment";
  static final String MISSING_END_TIME = "Missing end time of the appointment";
  static final String MORE_ARGS = "More arguments passed in than needed";
  static final String MISSING_OPTION_ARG = "Missing argument of option ";
  static final String README = loadPlainTextFromResource("README.txt");
  static final String USAGE = loadPlainTextFromResource("usage.txt");
  static HashMap<Integer, String> exitMsgs;

  static final int requiredArgumentNum = 6;

  static final int ownerArgIndex = 0;
  static final int descriptionArgIndex = 1;
  static final int beginDateArgIndex = 2;
  static final int beginTimeArgIndex = 3;
  static final int beginTimeMarkerArgIndex = 4;
  static final int endDateArgIndex = 5;
  static final int endTimeArgIndex = 6;
  static final int endTimeMarkerArgIndex = 7;

  /**
   * Main program that parses the command line, creates a <code>Appointment</code>,
   * and prints a description of the appointment to standard out by invoking its
   * <code>toString</code> method if <code>-print</code> option is enabled, or only
   * prints readme information to standard error once <code>-README</code> 
   * option is enabled.
   */
  public static void main(String[] args) {
    ArgumentParser argparser = new ArgumentParser()
        .addOption("-print", 0)
        .addOption("-textFile", 1)
        .addArgument("owner")
        .addArgument("description of the appointment")
        .addArgument("begin date of the appointment")
        .addArgument("begin time of the appointment")
        .addArgument("am/pm marker of begin time of the appointment")
        .addArgument("end date of the appointment")
        .addArgument("end time of the appointment")
        .addArgument("am/pm marker of end time of the appointment")
        .setReadme(README)
        .setUsage(USAGE);

    if (!argparser.parse(args)) {
      printErrorMessageAndExit(argparser.getErrorMessage());
    }
    // number of commandline args is valid
    String[] arguments = argparser.getArguments();

    // create related validators to validate content of each commandline arg
    DateTimeStringValidator dtValidator = new DateTimeStringValidator("M/d/yyyy h:m a");
    NonemptyStringValidator ownerValidator = new NonemptyStringValidator("owner");
    NonemptyStringValidator descriptionValidator = 
        new NonemptyStringValidator("description");

    // combine date and time as a string
    String begin = String.join(" ", arguments[beginDateArgIndex], 
                               arguments[beginTimeArgIndex], 
                               arguments[beginTimeMarkerArgIndex]);
    String end = String.join(" ", arguments[endDateArgIndex], 
                             arguments[endTimeArgIndex], 
                             arguments[endTimeMarkerArgIndex]);

    AbstractValidator[] commandLineArgumentValidators = { 
        ownerValidator, descriptionValidator, dtValidator, dtValidator
    };
    String[] toValidate = {
      arguments[ownerArgIndex], arguments[descriptionArgIndex], begin, end
    };
    
    for (int i = 0; i < toValidate.length; ++i) {
      validateArgumentByValidator(commandLineArgumentValidators[i], toValidate[i]);
    }

    try {
      // create an appointment
      Appointment appointment = new Appointment(begin, end, arguments[descriptionArgIndex]);

      // load appointment book from file or create new appointment book
      String apptbookFile = "";
      AppointmentBook<Appointment> book;
      if (argparser.isEnabled("-textFile")) {
        apptbookFile = argparser.getOptionArguments("-textFile").get(0);
        AbstractValidator[] validators = { dtValidator, dtValidator, descriptionValidator };
        TextParser<AppointmentBook, Appointment> textParser =
            new TextParser(apptbookFile, 
                           arguments[ownerArgIndex], 
                           AppointmentBook.class, 
                           Appointment.class,
                           ownerValidator, 
                           validators, 
                           appointment.getExpectedNumberOfField());
        book = textParser.parse();       
      } else {
        book = new AppointmentBook(arguments[ownerArgIndex]);
      }

      // add created appointment to book
      book.addAppointment(appointment);
      
      // based on option enabled, dump updated appointment, or print created appointment,
      // or both
      if (argparser.isEnabled("-textFile")) {
        TextDumper<AppointmentBook, Appointment> textDumper = new TextDumper(apptbookFile);
        textDumper.dump(book);
      }
      if (argparser.isEnabled("-print")) {
        System.out.println(appointment.toString());
      }

      PrintStream writer = System.out;
      PrettyPrinter<AppointmentBook, Appointment> printer = new PrettyPrinter(writer);
      printer.dump(book);
    } catch(ParserException | IOException ex) {
      printErrorMessageAndExit(ex.getMessage());
    } catch(Exception ex) {
      printErrorMessageAndExit("program internal error " + ex.getMessage());
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
      InputStream is = Project2.class.getResourceAsStream(filename);
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
   * Given a string <code>s</code>, check if it is valid via <code>validator</code>. 
   * If it is invalid, exit the program with status 1 with error message generated 
   * by <code>validator</code>.
   * 
   * @param validator object that is any concrete subclass of <code>AbstractValidator</code>.
   * @param s         the string that is to be validated. 
   */
  private static void validateArgumentByValidator(
      AbstractValidator validator, 
      String s) {
    if (!validator.isValid(s)) {
      printErrorMessageAndExit(validator.getErrorMessage());
    } 
  }

}