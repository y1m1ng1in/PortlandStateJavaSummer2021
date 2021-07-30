package edu.pdx.cs410J.yl6;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.util.Base64;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * This servlet ultimately provides a REST API for working with an
 * {@link AppointmentBook}
 */
public class AppointmentBookServlet extends HttpServlet {

  static final String OWNER_PARAMETER = "owner";
  static final String DESCRIPTION_PARAMETER = "description";
  static final String BEGIN_PARAMETER = "start";
  static final String END_PARAMETER = "end";

  private AppointmentBookStorage storage;
  private NonemptyStringValidator ownerValidator;
  private AppointmentValidator appointmentValidator;

  public AppointmentBookServlet() {
    this.storage = new PlainTextAsStorage(new File("."));
    this.ownerValidator = new NonemptyStringValidator("owner");
    this.appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
  }

  public AppointmentBookServlet(AppointmentBookStorage storage) {
    this.storage = storage;
    this.ownerValidator = new NonemptyStringValidator("owner");
    this.appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
  }

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

    // response.setContentType("text/plain");
    // owner is always a required Get parameter in this servlet
    if (owner == null) {
      missingRequiredParameter(response, OWNER_PARAMETER);
      return;
    }

    if (!authenticateUser(request, response, owner)) {
      return;
    }

    if (begin == null && end == null) {
      getAllAppointmentsByOwner(response, owner);
    } else if (begin != null && end != null) {
      getAppointmentsByOwnerWithBeginInterval(response, owner, begin, end);
    } else if (begin == null) {
      missingRequiredParameter(response, BEGIN_PARAMETER);
    } else {
      missingRequiredParameter(response, END_PARAMETER);
    }
  }

  /**
   * Handles an HTTP POST request by storing the dictionary entry for the "word"
   * and "definition" request parameters. It writes the dictionary entry to the
   * HTTP response.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // response.setContentType("text/plain");
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

    if (!authenticateUser(request, response, fields[0])) {
      return;
    }

    insertAppointmentWithOwner(response, fields[0], fields[3], fields[1], fields[2]);
  }

  /**
   * Check <code>username</code> credential stored via cookie in
   * <code>request</code>. If the username stored in credential is not same as
   * <code>username</code>; or either <code>username</code> or username stored in
   * credential is not a registered user; or in credential the password is wrong,
   * the authentication failed, and the function returns <code>false</code>.
   * 
   * @param request  {@link HttpServletRequest} instance of incoming http request
   * @param response {@link HttpServletResponse} instance of http response to be
   *                 sent
   * @param username the username to check credential
   * @return <code>true</code> if authentication is not failed; <code>false</code>
   *         otherwise
   * @throws IOException If an input or output exception occurs
   * @implNote currently the server uses basic authentication, which is not
   *           enough. Still looking for more advanced method... then replace the
   *           implementation in this method
   */
  private boolean authenticateUser(HttpServletRequest request, HttpServletResponse response, String username)
      throws IOException {
    Cookie[] cookies = request.getCookies();

    if (cookies == null || cookies.length < 1) {
      writeMessageAndSetStatus(response, "Invalid credential for user \"" + username + "\"",
          HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }

    String decodedString = new String(Base64.getDecoder().decode(cookies[0].getValue()));
    String[] toCheck = decodedString.split(":");

    if (!toCheck[0].equals(username)) {
      writeMessageAndSetStatus(response, "Invalid credential for user \"" + username + "\"",
          HttpServletResponse.SC_UNAUTHORIZED);
      return false;
    }

    try {
      User user = this.storage.getUserByUsername(username);
      if (user == null) {
        writeMessageAndSetStatus(response, "User \"" + username + "\" is not a registered user",
            HttpServletResponse.SC_FORBIDDEN);
        return false;
      }
      if (!user.getPassword().equals(toCheck[1])) {
        writeMessageAndSetStatus(response, "Password is wrong", HttpServletResponse.SC_UNAUTHORIZED);
        return false;
      }
    } catch (StorageException e) {
      writeMessageAndSetStatus(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return false;
    }
    return true;
  }

  /**
   * Handle {@link #doGet} requests that given a <code>owner</code>, return all
   * appointments that belong to <code>owner</code>. The following lists HTTP
   * status code with its indication:
   * <ul>
   * <li><code>200</code> indicates that all appointments with <code>owner</code>
   * is found
   * <li><code>400</code> indicates that the <code>owner</code> string is not
   * valid
   * <li><code>404</code> indicates that no appointment with <code>owner</code> is
   * found
   * <li><code>500</code> indicates any error related to storage
   * </ul>
   *
   * @param response a {@link HttpServletResponse} instance from {@link #doGet}
   * @param owner    a string of owner name
   * @throws IOException If an input or output exception occurs
   */
  private void getAllAppointmentsByOwner(HttpServletResponse response, String owner) throws IOException {
    if (!this.ownerValidator.isValid(owner)) {
      writeMessageAndSetStatus(response, this.ownerValidator.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    AppointmentBook<Appointment> book = null;
    try {
      book = this.storage.getAllAppointmentsByOwner(owner);
    } catch (StorageException e) {
      writeMessageAndSetStatus(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
    if (book == null) {
      writeMessageAndSetStatus(response, "No appointment found with owner " + owner, HttpServletResponse.SC_NOT_FOUND);
    } else {
      writeAppointmentBookAndOkStatus(response, book);
    }
  }

  /**
   * Handle {@link #doGet} requests that given a <code>owner</code>,
   * <code>begin</code> stores the lowerbound of searching time interval for
   * appointments that begin, <code>end</code> stores the upperbound of searching
   * time interval for appointments that begin, return all appointments that
   * belong to <code>owner</code> with begin time falls into seraching time
   * interal. The following lists HTTP status code with its indication:
   * <ul>
   * <li><code>200</code> indicates that all appointments with <code>owner</code>
   * that satisified seraching requirement is found
   * <li><code>400</code> indicates that the <code>owner</code> string,
   * <code>begin</code> string, or <code>end</code> string is not valid.
   * <li><code>404</code> indicates that no appointment with <code>owner</code> is
   * found
   * <li><code>500</code> indicates any error related to storage
   * </ul>
   * 
   * @param response a {@link HttpServletResponse} instance from {@link #doGet}
   * @param owner    a string of owner name
   * @param begin    the lowerbound of searching time interval for appointment's
   *                 begin time
   * @param end      the upperbound of searching time interval for appointment's
   *                 begin time
   * @throws IOException If an input or output exception occurs
   */
  private void getAppointmentsByOwnerWithBeginInterval(HttpServletResponse response, String owner, String begin,
      String end) throws IOException {
    if (!this.ownerValidator.isValid(owner)) {
      writeMessageAndSetStatus(response, this.ownerValidator.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Date from = Helper.validateAndParseDate(begin);
    if (from == null) {
      writeMessageAndSetStatus(response, Helper.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    Date to = Helper.validateAndParseDate(end);
    if (to == null) {
      writeMessageAndSetStatus(response, Helper.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if (!Helper.validateAndGetDateInterval(from, to, "lowerbound date", "upperbound date")) {
      writeMessageAndSetStatus(response, Helper.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // load appointments satisified from persistent storage
    AppointmentBook<Appointment> book = null;
    try {
      book = this.storage.getAppointmentsByOwnerWithBeginInterval(owner, from, to);
    } catch (StorageException e) {
      writeMessageAndSetStatus(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
    if (book == null) {
      writeMessageAndSetStatus(response,
          "No appointment found with owner " + owner + " that begins between " + begin + " and " + end,
          HttpServletResponse.SC_NOT_FOUND);
    } else {
      writeAppointmentBookAndOkStatus(response, book);
    }
  }

  /**
   * Handle {@link #doPost} requests that given a <code>owner</code>,
   * <code>description</code>, <code>begin</code>, and <code>end</code> for
   * constructing a new appointment, construct an appointment and store it to
   * persistent storage. The following lists HTTP status code with its indication:
   * <ul>
   * <li><code>200</code> indicates that appointment is successfully created and
   * stored
   * <li><code>400</code> indicates that the arguments for new appointment is not
   * valid
   * <li><code>409</code> indicates that the appointment to be added conflicts
   * with existing appointment slots
   * <li><code>500</code> indicates that appointment cannot be stored to storage,
   * or a program internal error (a bug: an invalid appointment is attempting to
   * store into storage) occurrs.
   * </ul>
   * 
   * @param response    a {@link HttpServletResponse} instance from
   *                    {@link #doPost}
   * @param owner       a string of owner name
   * @param description a string of description of appointment
   * @param begin       a string of begin time of appointment
   * @param end         a string of end time of appointment
   * @throws IOException If an input or output exception occurs
   */
  private void insertAppointmentWithOwner(HttpServletResponse response, String owner, String description, String begin,
      String end) throws IOException {
    // validate owner and fields for constructing new appointment
    if (!this.ownerValidator.isValid(owner)) {
      writeMessageAndSetStatus(response, this.ownerValidator.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Appointment appointment = null;
    if ((appointment = this.appointmentValidator.createAppointmentFromString(begin, end, description)) == null) {
      writeMessageAndSetStatus(response, this.appointmentValidator.getErrorMessage(),
          HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // load appointment to persistent storage
    try {
      AppointmentBook<AppointmentSlot> existingSlots = this.storage.getAllExistingAppointmentSlotsByOwner(owner);
      if (existingSlots != null && existingSlots.contains(appointment.getAppointmentSlot())) {
        writeMessageAndSetStatus(response,
            "appointment " + appointment.toString() + " conflicts with existing appointment",
            HttpServletResponse.SC_CONFLICT);
        return;
      }
      this.storage.insertAppointmentWithOwner(owner, appointment);
      writeMessageAndSetStatus(response, "Add appointment " + appointment.toString(), HttpServletResponse.SC_OK);
    } catch (StorageException e) {
      writeMessageAndSetStatus(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Dump the whole <code>book</code> to the HTTP response, and set HTTP status to
   * 200. The method uses <code>PrintWriter</code> instance obtained by
   * {@link HttpServletResponse#getWriter} to dump content by
   * {@link TextDumper#dump}.
   * 
   * @param response a {@link HttpServletResponse} instance
   * @param book     the appointment book to be write to <code>response</code>
   * @throws IOException If an input or output exception occurs
   */
  private void writeAppointmentBookAndOkStatus(HttpServletResponse response, AppointmentBook<Appointment> book)
      throws IOException {
    response.setContentType("text/json");
    response.setStatus(HttpServletResponse.SC_OK);

    Gson gson = new Gson();
    String json = gson.toJson(book);
    PrintWriter pw = response.getWriter();
    pw.write(json);
    pw.flush();
  }

  /**
   * Writes an error message about a missing parameter to the HTTP response, and
   * set HTTP status code to 400 using {@link HttpServletResponse#sendError}
   */
  private void missingRequiredParameter(HttpServletResponse response, String parameterName) throws IOException {
    String message = String.format("The required parameter \"%s\" is missing", parameterName);
    writeMessageAndSetStatus(response, message, HttpServletResponse.SC_BAD_REQUEST);
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

  /**
   * Write <code>message</code> to <code>response</code> and set
   * <code>status</code> to HTTP status
   * 
   * @param response a {@link HttpServletResponse} instance
   * @param message  the message to write to response
   * @param status   HTTP status code
   * @throws IOException If an input or output exception occurs
   */
  private void writeMessageAndSetStatus(HttpServletResponse response, String message, int status) throws IOException {
    response.setContentType("text/json");
    response.setStatus(status);

    JsonObject object = new JsonObject();
    object.addProperty("message", message);
    object.addProperty("status", status);
    Gson gson = new Gson();
    String json = gson.toJson(object);

    PrintWriter pw = response.getWriter();
    pw.println(json);
    pw.flush();
  }

}
