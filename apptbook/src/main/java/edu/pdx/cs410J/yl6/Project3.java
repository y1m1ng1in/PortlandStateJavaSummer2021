package edu.pdx.cs410J.yl6;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import edu.pdx.cs410J.ParserException;

/**
 * The main class for the CS410J appointment book Project which parses commandline
 * arguments, processes arguments to construct an appointment, and based on the options
 * to print the new added appointment; print readme; import and export existing 
 * appointment book from external file; or print formatted appointment book to standard
 * output or file based on its argument. 
 * <p>
 * The class assumes that options are always followed by arguments. There are 4 valid
 * options available: -print, -README, -pretty, and -textFile filename. This means that 
 * the parsing process will greedily match commandline arguments from 0 index to higher 
 * index until either a invalid option detected, or all the available options matched, 
 * the rest of the commandline arguments are treated as arguments for appointment. 
 */
public class Project3 {

  static final String README = loadPlainTextFromResource("README.txt");
  static final String USAGE = loadPlainTextFromResource("usage.txt");

  static final String FILE_CONFLICT = 
      "Cannot dump appointment book and pretty print to same file";

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
   * <code>toString</code> method if <code>-print</code> option is enabled; or parses 
   * an external file to create an appointment book, adds new created appointment to 
   * the book, then dumps updated book back to the file, if <code>-textFile file</code>
   * is enabled; or only prints readme information to standard error once 
   * <code>-README</code> option is enabled; or print formatted appointment book to 
   * standard output or file based on its argument if <code>-pretty</code> is enabled. 
   */
  public static void main(String[] args) {
    ArgumentParser argparser = new ArgumentParser()
        .addOption("-print", 0)
        .addOption("-textFile", 1)
        .addOption("-pretty", 1)
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
    if (argparser.isEnabled("-textFile") && argparser.isEnabled("-pretty")) {
      boolean fileConflict = argparser.getOptionArguments("-textFile").get(0)
          .equals(argparser.getOptionArguments("-pretty").get(0));
      if (fileConflict) {
        printErrorMessageAndExit(FILE_CONFLICT);
      }
    }

    // number of commandline args is valid
    String[] arguments = argparser.getArguments();

    // combine date and time as a string
    String begin = 
        String.join(" ", arguments[beginDateArgIndex], arguments[beginTimeArgIndex], 
                    arguments[beginTimeMarkerArgIndex]);
    String end = 
        String.join(" ", arguments[endDateArgIndex], arguments[endTimeArgIndex], 
                    arguments[endTimeMarkerArgIndex]);

    // create validator for appointment owner and appointment
    NonemptyStringValidator ownerValidator = new NonemptyStringValidator("owner");
    AppointmentValidator appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
    String[] appointmentFields = { begin, end, arguments[descriptionArgIndex] };
        
    if (!ownerValidator.isValid(arguments[ownerArgIndex])) {
      printErrorMessageAndExit(ownerValidator.getErrorMessage());
    }
    if (!appointmentValidator.isValid(appointmentFields)) {
      printErrorMessageAndExit(appointmentValidator.getErrorMessage());
    }

    try {
      // create an appointment
      Appointment appointment = new Appointment(begin, end, arguments[descriptionArgIndex]);

      // load appointment book from file or create new appointment book
      String apptbookFile = "";
      AppointmentBook<Appointment> book;
      if (argparser.isEnabled("-textFile")) {
        apptbookFile = argparser.getOptionArguments("-textFile").get(0);
        TextParser<AppointmentBook, Appointment> textParser =
            new TextParser(apptbookFile, 
                           arguments[ownerArgIndex], 
                           AppointmentBook.class, 
                           Appointment.class,
                           ownerValidator, 
                           appointmentValidator, 
                           appointment.getExpectedNumberOfField());
        book = textParser.parse();       
      } else {
        book = new AppointmentBook(arguments[ownerArgIndex]);
      }

      // add created appointment to book
      book.addAppointment(appointment);
      
      // if -textFile is enabled, dump appointment book to file
      if (argparser.isEnabled("-textFile")) {
        TextDumper<AppointmentBook, Appointment> textDumper = new TextDumper(apptbookFile);
        textDumper.dump(book);
      }

      // if -print is enabled, print new added appointment to standard output
      if (argparser.isEnabled("-print")) {
        System.out.println(appointment.toString());
      }

      // if -pretty is enabled, dump formatted appointment book to either file 
      // or standard output based on its argument
      if (argparser.isEnabled("-pretty")) {
        String[] fields = { "Begin at", "End at", "Description", "Duration" };
        String prettyFile = argparser.getOptionArguments("-pretty").get(0);
        PrettyPrinter<AppointmentBook, Appointment> printer;
        if (prettyFile.equals("-")) {
          printer = new PrettyPrinter(System.out, fields);
        } else {
          printer = new PrettyPrinter(new FileWriter(prettyFile), fields);
        }
        printer.dump(book);
      }
    } catch(ParserException | IOException ex) {
      printErrorMessageAndExit(ex.getMessage());
    } catch(Exception ex) {
      printErrorMessageAndExit("program internal error " + ex.getMessage() + "\n" + ex.toString());
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
      InputStream is = Project3.class.getResourceAsStream(filename);
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

}