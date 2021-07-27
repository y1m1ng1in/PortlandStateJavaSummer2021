package edu.pdx.cs410J.yl6;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.web.HttpRequestHelper.RestException;

/**
 * The main class that parses the command line and communicates with the
 * Appointment Book server using REST.
 */
public class Project4 {

  public static final String MISSING_ARGS = "Missing command line arguments";
  public static final String PARSE_INT_ERROR = "%s, it cannot be parsed as an integer for port number";
  public static final String SEARCH_TIME_INTERVAL_ERROR = "lower bound date %s is later than upperbound date %s";
  public static final String NEW_APPOINTMENT_BEGIN_LATER_THAN_END = "begin date %s is later than end date %s";
  public static final String SEARCH_AND_PRINT_BOTH_ENABLED = "When -search is enabled, -print must not be enabled, since nothing can be added to appointment book";

  public static void main(String... args) {
    ArgumentParser argumentParser = new ArgumentParser();
    argumentParser.addOption("-host", 1).addOption("-port", 1).addOption("-search", 0).addOption("-print", 0)
        .setUsage(usage());

    if (!argumentParser.parse(args)) {
      error(argumentParser.getErrorMessage());
    }

    if (!argumentParser.isEnabled("-host") || !argumentParser.isEnabled("-port")) {
      error("host and port must be specified");
    }

    String hostName = argumentParser.getOptionArguments("-host").get(0);
    int port = -1;
    try {
      port = Integer.parseInt(argumentParser.getOptionArguments("-port").get(0));
    } catch (NumberFormatException e) {
      error(String.format(PARSE_INT_ERROR, e.getMessage()));
    }

    AppointmentBookRestClient client = new AppointmentBookRestClient(hostName, port);

    String[] fields = { "Begin at", "End at", "Description", "Duration" };
    PrettyPrinter<AppointmentBook<Appointment>, Appointment> prettyPrinter = new PrettyPrinter<>(System.out, fields);

    String[] arguments;
    if (argumentParser.isEnabled("-search")) {
      // -search and -print cannot present at the same time
      if (argumentParser.isEnabled("-print")) {
        error(SEARCH_AND_PRINT_BOTH_ENABLED);
      }

      arguments = argumentParser.getArguments("owner", "the lowerbound of date that appt begins",
          "the lowerbound of time that appt begins", "the lowerbound of am/pm marker that appt begins",
          "the upperbound of date that appt begins", "the upperbound of time that appt begins",
          "the upperbound of am/pm marker that appt begins");

      if (arguments == null) {
        error(argumentParser.getErrorMessage());
      }

      String lowerbound = String.join(" ", arguments[1], arguments[2], arguments[3]);
      String upperbound = String.join(" ", arguments[4], arguments[5], arguments[6]);
      validateTwoDates(lowerbound, upperbound, SEARCH_TIME_INTERVAL_ERROR);

      try {
        AppointmentBook<Appointment> book = client.getAppointmentsByOwnerWithBeginInterval(arguments[0], lowerbound,
            upperbound);
        prettyPrinter.dump(book);
      } catch (IOException e) {
        error("While connecting to host, " + e.getMessage());
      } catch (ParserException e) {
        error("Cannot parse content gets returned from server as an appointment book: " + e.getMessage());
      } catch (RestException e) {
        if (e.getHttpStatusCode() == 404) {
          error("Cannot find any appointment that begins between " + lowerbound + " and " + upperbound);
        } else if (e.getHttpStatusCode() == 400) {
          // since two dates have been validated, the only reason for bad request is
          // "owner"
          error("Owner is either missing or invalid");
        } else {
          error(e.getMessage());
        }
      }

      System.exit(0);
    }

    arguments = argumentParser.getAllArguments();
    // argument length can be either 1 or 8
    if (arguments.length == 1) {
      // when only one argument presents, which is the owner name, then get all the
      // appointments belong to the owner
      try {
        AppointmentBook<Appointment> book = client.getAppointmentBookByOwner(arguments[0]);
        prettyPrinter.dump(book);
      } catch (IOException e) {
        error("While connecting to host, " + e.getMessage());
      } catch (ParserException e) {
        error("Cannot parse content gets returned from server as an appointment book: " + e.getMessage());
      } catch (RestException e) {
        if (e.getHttpStatusCode() == 404) {
          error("Cannot find any appointment with owner " + arguments[0]);
        } else if (e.getHttpStatusCode() == 400) {
          // owner is the only parameter
          error("Owner" + arguments[0] + " is invalid");
        } else {
          error(e.getMessage());
        }
      }

      System.exit(0);
    }

    if (arguments.length == 8) {
      // add new appointment, in this case "-print" option is valid, and the
      // only place that "-print" option is valid
      String begin = String.join(" ", arguments[2], arguments[3], arguments[4]);
      String end = String.join(" ", arguments[5], arguments[6], arguments[7]);
      validateTwoDates(begin, end, NEW_APPOINTMENT_BEGIN_LATER_THAN_END);
      AppointmentValidator appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
      if (!appointmentValidator.isValid(new String[] { begin, end, arguments[1] })) {
        error(appointmentValidator.getErrorMessage());
      }
      try {
        client.addAppointment(arguments[0], arguments[1], begin, end);
      } catch (IOException e) {
        error("While connecting to host, " + e.getMessage());
      } catch (RestException e) {
        error(e.getMessage());
      }
      try {
        Appointment newAdded = new Appointment(begin, end, arguments[1]);
        if (argumentParser.isEnabled("-print")) {
          System.out.println(newAdded.toString());
        }
      } catch (Exception e) {
        error(e.getMessage());
      }
      System.exit(0);
    }

    error("Cannot process arguments passed in");

    System.exit(0);
  }

  private static void error(String message) {
    PrintStream err = System.err;
    err.println(message);

    System.exit(1);
  }

  private static void validateTwoDates(String date1, String date2, String message) {
    DateFormat df = new SimpleDateFormat("M/d/yyyy h:m a");
    df.setLenient(false);
    Date lowerboundDate = null, upperboundDate = null;
    try {
      lowerboundDate = df.parse(date1);
      upperboundDate = df.parse(date2);
    } catch (ParseException e) {
      error(e.getMessage());
    }
    if (!lowerboundDate.before(upperboundDate)) {
      error(String.format(message, date1, date2));
    }
  }

  /**
   * Prints usage information for this program
   */
  public static String usage() {
    return "usage: java edu.pdx.cs410J.yl6.Project4 [options] <args>\n" + "  args are (in this order):\n"
        + "    owner              The person who owns the appt book\n"
        + "    description        A description of the appointment\n" + "    begin              When the appt begins\n"
        + "    end                When the appt ends\n" + "  options are (options may appear in any order):\n"
        + "    -host hostname     Host computer on which the server runs\n"
        + "    -port port         Port on which the server is listening\n"
        + "    -search            Appointments should be searched for\n"
        + "    -print             Prints a description of the new appointment\n"
        + "    -README            Prints a README for this project and exits\n";
  }
}