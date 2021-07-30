package edu.pdx.cs410J.yl6;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import com.google.gson.Gson;

public class BookAppointmentServlet extends HttpServletHelper {

  static final String OWNER_PARAMETER = "owner";
  static final String DESCRIPTION_PARAMETER = "description";
  static final String BEGIN_PARAMETER = "start";
  static final String END_PARAMETER = "end";
  static final String DURATION_PARAMETER = "duration";
  static final String ID_PARAMETER = "id";

  private NonemptyStringValidator ownerValidator;
  // private AppointmentValidator appointmentValidator;

  public BookAppointmentServlet() {
    super();
    this.ownerValidator = new NonemptyStringValidator("owner");
    // this.appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
  }

  // GET: /book?owner=username
  // return a list of bookable slots
  // GET: /book?owner=username&start=begintime&end=endtime
  // return a list of bookable slots begins within specified interval
  //
  // POST: /book?owner=username
  // - param: begin, end: create a public bookable slot
  // - param: begin, end, duration: create slots as much as possible with
  // duration, between begin and end
  // - param: appointmentId, begin, end, description: book a slot

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String owner = getParameter(OWNER_PARAMETER, request);
    String begin = getParameter(BEGIN_PARAMETER, request);
    String end = getParameter(END_PARAMETER, request);

    // owner is always a required Get parameter in this servlet
    if (owner == null) {
      missingRequiredParameter(response, OWNER_PARAMETER);
      return;
    }

    if (begin == null && end == null) {
      getAllBookableAppointmentSlotsByOwner(response, owner);
    } else if (begin != null && end != null) {
      getBookableAppointmentSlotsByOwnerBetweenInterval(response, owner);
    } else if (begin == null) {
      missingRequiredParameter(response, BEGIN_PARAMETER);
    } else {
      missingRequiredParameter(response, END_PARAMETER);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String[] requiredFields = { OWNER_PARAMETER, BEGIN_PARAMETER, END_PARAMETER };
    String[] fields = new String[3];

    for (int i = 0; i < 3; ++i) {
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

    insertBookableAppointmentSlot(response, fields[0], fields[1], fields[2]);
  }

  private void getAllBookableAppointmentSlotsByOwner(HttpServletResponse response, String owner) throws IOException {
    if (!this.ownerValidator.isValid(owner)) {
      writeMessageAndSetStatus(response, this.ownerValidator.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    AppointmentBook<AppointmentSlot> book = null;
    try {
      book = this.storage.getAllBookableAppointmentSlotsByOwner(owner);
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

  private void getBookableAppointmentSlotsByOwnerBetweenInterval(HttpServletResponse response, String owner)
      throws IOException {
    // not implemented yet
    return;
  }

  private void insertBookableAppointmentSlot(HttpServletResponse response, String owner, String begin, String end)
      throws IOException {
    // validate owner and fields for constructing new appointment
    if (!this.ownerValidator.isValid(owner)) {
      writeMessageAndSetStatus(response, this.ownerValidator.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    if (!Helper.validateTwoDateStringForDateInterval(begin, end, "begin time of bookable slot", "end time")) {
      writeMessageAndSetStatus(response, Helper.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
      return;
    }
    Date from = Helper.getLowerDate();
    Date to = Helper.getUpperDate();
    AppointmentSlot slot = new AppointmentSlot(from, to);

    // load appointment to persistent storage
    try {
      AppointmentBook<AppointmentSlot> existingSlots = this.storage.getAllExistingAppointmentSlotsByOwner(owner);
      if (existingSlots != null && existingSlots.contains(slot)) {
        writeMessageAndSetStatus(response,
            "slot " + slot.toString() + " conflicts with existing appointment slot",
            HttpServletResponse.SC_CONFLICT);
        return;
      }
      this.storage.insertBookableAppointmentSlot(owner, slot);
      writeMessageAndSetStatus(response, "Add bookable appointment slot " + slot.toString(), HttpServletResponse.SC_OK);
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
  private void writeAppointmentBookAndOkStatus(HttpServletResponse response, AppointmentBook<AppointmentSlot> book)
      throws IOException {
    response.setContentType("text/json");
    response.setStatus(HttpServletResponse.SC_OK);

    Gson gson = new Gson();
    String json = gson.toJson(book);
    PrintWriter pw = response.getWriter();
    pw.write(json);
    pw.flush();
  }
}
