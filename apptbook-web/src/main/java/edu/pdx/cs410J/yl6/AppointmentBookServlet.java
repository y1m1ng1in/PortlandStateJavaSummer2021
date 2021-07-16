package edu.pdx.cs410J.yl6;

import com.google.common.annotations.VisibleForTesting;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.text.ParseException;

/**
 * This servlet ultimately provides a REST API for working with an 
 * {@link AppointmentBook}
 */
public class AppointmentBookServlet extends HttpServlet
{
  static final String WORD_PARAMETER = "word";
  static final String DEFINITION_PARAMETER = "definition";

  static final String OWNER_PARAMETER = "owner";
  static final String DESCRIPTION_PARAMETER = "description";
  static final String BEGIN_PARAMETER = "start";
  static final String END_PARAMETER = "end";

  private final Map<String, String> dictionary = new HashMap<>();

  private final Map<String, AppointmentBook> books = new HashMap<>();

  /**
   * Handles an HTTP GET request from a client by writing the owner, begin, and 
   * end time of the appointment specified in the "owner", "start", "end" HTTP 
   * parameter to the HTTP response.  
   * <p>
   * If the "begin" and "end" parameter is not specified, all of the entries 
   * in the appointment book are written to the HTTP response; otherwise, the 
   * appointments whose begin time between "begin" and "end" are written to the
   * HTTP response.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {
    String owner = getParameter(OWNER_PARAMETER, request);
    String begin = getParameter(BEGIN_PARAMETER, request);
    String end = getParameter(END_PARAMETER, request);

    response.setContentType( "text/plain" );
    if (owner == null) {
      missingRequiredParameter(response, OWNER_PARAMETER);
      return;
    }
    if (begin == null && end == null) {
      writeAppointmentBook(response, getAppointmentBookByOwner(owner));
      return;
    } 
    if (begin != null && end != null) {
      writeAppointmentBook(response, getAppointmentBookByOwner(owner));
      return;
    } 
    if (begin == null) {
      missingRequiredParameter(response, BEGIN_PARAMETER);
    } 
    missingRequiredParameter(response, END_PARAMETER);
  }

  /**
   * Handles an HTTP POST request by storing the dictionary entry for the
   * "word" and "definition" request parameters.  It writes the dictionary
   * entry to the HTTP response.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) 
      throws ServletException, IOException {
    response.setContentType( "text/plain" );

    String owner = getParameter(OWNER_PARAMETER, request);
    if (owner == null) {
      missingRequiredParameter(response, OWNER_PARAMETER);
      return;
    }

    String description = getParameter(DESCRIPTION_PARAMETER, request);
    if (description == null) {
        missingRequiredParameter(response, DESCRIPTION_PARAMETER);
        return;
    }

    String begin = getParameter(BEGIN_PARAMETER, request);
    if (begin == null) {
      missingRequiredParameter(response, BEGIN_PARAMETER);
      return;
    }

    String end = getParameter(END_PARAMETER, request);
    if (end == null) {
      missingRequiredParameter(response, END_PARAMETER);
      return;
    }

    NonemptyStringValidator ownerValidator = new NonemptyStringValidator("owner");
    AppointmentValidator appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
    String[] appointmentFields = { begin, end, description };
    
    PrintWriter pw = response.getWriter();

    if (!ownerValidator.isValid(owner)) {
      pw.println(ownerValidator.getErrorMessage());
      pw.flush();
      response.setStatus(HttpServletResponse.SC_OK);
      return;
    }
    if (!appointmentValidator.isValid(appointmentFields)) {
      pw.println(appointmentValidator.getErrorMessage());
      pw.flush();
      response.setStatus(HttpServletResponse.SC_OK);
      return;
    }
    
    Appointment appointment = null;
    try {
      appointment = new Appointment(begin, end, description);
    } catch (ParseException e) {
      pw.println("server internal error " + e.getMessage());
      pw.flush();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    if (this.books.containsKey(owner)) {
      this.books.get(owner).addAppointment(appointment);
    } else {
      AppointmentBook newBook = new AppointmentBook(owner);
      newBook.addAppointment(appointment); 
      this.books.put(owner, newBook);
    }

    pw.println("Add appointment " + appointment.toString());
    pw.flush();

    response.setStatus( HttpServletResponse.SC_OK);
  }

  /**
   * Writes all of the dictionary entries to the HTTP response.
   */
  private void writeAppointmentBook(HttpServletResponse response, AppointmentBook book) 
      throws IOException {
    PrintWriter pw = response.getWriter();
    
    if (book != null) {
      TextDumper dumper = new TextDumper(pw);
      dumper.dump(book);
    } else {
      pw.println("No appointment found");
    }
    pw.flush();
    response.setStatus(HttpServletResponse.SC_OK);
  }

  private AppointmentBook getAppointmentBookByOwner(String owner) {
    return this.books.get(owner);
  }

  /**
   * Writes an error message about a missing parameter to the HTTP response.
   *
   * The text of the error message is created by 
   * {@link Messages#missingRequiredParameter(String)}
   */
  private void missingRequiredParameter(HttpServletResponse response, String parameterName)
      throws IOException {
    String message = Messages.missingRequiredParameter(parameterName);
    response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
  }

  /**
   * Returns the value of the HTTP request parameter with the given name.
   *
   * @return <code>null</code> if the value of the parameter is
   *         <code>null</code> or is the empty string
   */
  private String getParameter(String name, HttpServletRequest request) {
    String value = request.getParameter(name);
    if (value == null || "".equals(value)) {
      return null;
    } else {
      return value;
    }
  }
  
}
