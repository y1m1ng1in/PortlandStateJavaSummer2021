package edu.pdx.cs410J.yl6;

import com.google.common.annotations.VisibleForTesting;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * This servlet ultimately provides a REST API for working with an
 * {@link AppointmentBook}
 */
public class AppointmentBookServlet extends HttpServlet {

  static final String OWNER_PARAMETER = "owner";
  static final String DESCRIPTION_PARAMETER = "description";
  static final String BEGIN_PARAMETER = "start";
  static final String END_PARAMETER = "end";

  private PlainTextAsStorage storage = new PlainTextAsStorage(".");

  /**
   * Handles an HTTP GET request from a client by writing the owner, begin, and
   * end time of the appointment specified in the "owner", "start", "end" HTTP
   * parameter to the HTTP response.
   * <p>
   * If the "begin" and "end" parameter is not specified, all of the entries in
   * the appointment book are written to the HTTP response; otherwise, the
   * appointments whose begin time between "begin" and "end" are written to the
   * HTTP response.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String owner = getParameter(OWNER_PARAMETER, request);
    String begin = getParameter(BEGIN_PARAMETER, request);
    String end = getParameter(END_PARAMETER, request);

    response.setContentType("text/plain");
    if (owner == null) {
      missingRequiredParameter(response, OWNER_PARAMETER);
      return;
    }
    if (begin == null && end == null) {
      AppointmentBook<Appointment> book = this.storage.getAllAppointmentsByOwner(owner);
      if (book == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "No appointment found with owner " + owner);
      } else {
        writeAppointmentBook(response, book);
      }
      return;
    }
    if (begin != null && end != null) {
      try {
        DateFormat df = new SimpleDateFormat("M/d/yyyy h:m a");
        df.setLenient(false);
        Date from = df.parse(begin);
        Date to = df.parse(end);
        AppointmentBook<Appointment> book = this.storage.getAppointmentsByOwnerWithBeginInterval(owner, from, to);
        if (book == null) {
          response.sendError(HttpServletResponse.SC_NOT_FOUND,
              "No appointment found with owner " + owner + " that begins between " + begin + " and " + end);
        } else {
          writeAppointmentBook(response, book);
        }
      } catch (Exception e) {
        writeMessageAndSetStatus(response, e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
      }
      return;
    }
    if (begin == null) {
      missingRequiredParameter(response, BEGIN_PARAMETER);
    }
    missingRequiredParameter(response, END_PARAMETER);
  }

  /**
   * Handles an HTTP POST request by storing the dictionary entry for the "word"
   * and "definition" request parameters. It writes the dictionary entry to the
   * HTTP response.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/plain");
    String[] requiredFields = { OWNER_PARAMETER, BEGIN_PARAMETER, END_PARAMETER, DESCRIPTION_PARAMETER };
    String[] fields = new String[4];

    for (int i = 0; i < 4; ++i) {
      String value = getParameter(requiredFields[i], request);
      if (value == null) {
        missingRequiredParameter(response, requiredFields[i]);
        return;
      }
      fields[i] = value;
    }

    NonemptyStringValidator ownerValidator = new NonemptyStringValidator("owner");
    AppointmentValidator appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
    String[] appointmentFields = { fields[1], fields[2], fields[3] };

    if (!ownerValidator.isValid(fields[0])) {
      writeMessageAndSetStatus(response, ownerValidator.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if (!appointmentValidator.isValid(appointmentFields)) {
      writeMessageAndSetStatus(response, appointmentValidator.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Appointment appointment = null;
    try {
      appointment = new Appointment(fields[1], fields[2], fields[3]);
    } catch (ParseException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Program internal error: " + e.getMessage());
      return;
    }
    if (this.storage.insertAppointmentWithOwner(fields[0], appointment)) {
      writeMessageAndSetStatus(response, "Add appointment " + appointment.toString(), HttpServletResponse.SC_OK);
    } else {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, this.storage.getErrorMessage());
    }
  }

  /**
   * Writes all of the dictionary entries to the HTTP response.
   */
  private void writeAppointmentBook(HttpServletResponse response, AppointmentBook<Appointment> book)
      throws IOException {
    PrintWriter pw = response.getWriter();
    TextDumper<AppointmentBook<Appointment>, Appointment> dumper = new TextDumper<>(pw);
    dumper.dump(book);
    pw.flush();
    response.setStatus(HttpServletResponse.SC_OK);
  }

  /**
   * Writes an error message about a missing parameter to the HTTP response.
   */
  private void missingRequiredParameter(HttpServletResponse response, String parameterName) throws IOException {
    String message = missingRequiredParameter(parameterName);
    response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
  }

  private static String missingRequiredParameter(String parameterName) {
    return String.format("The required parameter \"%s\" is missing", parameterName);
  }

  /**
   * Returns the value of the HTTP request parameter with the given name.
   *
   * @return <code>null</code> if the value of the parameter is <code>null</code>
   *         or is the empty string
   */
  private String getParameter(String name, HttpServletRequest request) {
    String value = request.getParameter(name);
    if (value == null || "".equals(value)) {
      return null;
    } else {
      return value;
    }
  }

  private void writeMessageAndSetStatus(HttpServletResponse response, String message, int status) throws IOException {
    PrintWriter pw = response.getWriter();
    pw.println(message);
    pw.flush();
    response.setStatus(status);
  }

}
