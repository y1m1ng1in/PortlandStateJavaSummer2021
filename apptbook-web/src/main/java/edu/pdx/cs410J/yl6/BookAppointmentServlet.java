package edu.pdx.cs410J.yl6;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import edu.pdx.cs410J.yl6.database.AppointmentBookStorage;
import edu.pdx.cs410J.yl6.database.StorageException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.UUID;

/**
 * BookAppointmentServlet is the class that processes clients' requests for publishing bookable appointment slots,
 * booking a existing bookable slot, and retrieving bookable appointment slots given the name of the slot owner.
 */
public class BookAppointmentServlet extends HttpServletHelper {

    static final String OWNER_PARAMETER = "owner";
    static final String DESCRIPTION_PARAMETER = "description";
    static final String BEGIN_PARAMETER = "start";
    static final String END_PARAMETER = "end";
    static final String DURATION_PARAMETER = "duration";
    static final String ID_PARAMETER = "id";
    static final String PARTICIPATOR_PARAMETER = "participator";

    static final int APPOINTMENT_SLOT_TYPE_TOKEN = 1;
    static final int APPOINTMENT_TYPE_TOKEN = 2;

    private final NonemptyStringValidator ownerValidator;
    private final AppointmentValidator appointmentValidator;

    /**
     * Create an BookAppointmentServlet instance with default storage implementation, which declared in
     * {@link HttpServletHelper#HttpServletHelper()}, which connects to a Postgresql server.
     */
    public BookAppointmentServlet() {
        super();
        this.ownerValidator = new NonemptyStringValidator("owner");
        this.appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
    }

    /**
     * Create an BookAppointmentServlet instance
     *
     * @param storage any instance implements {@link AppointmentBookStorage}
     */
    public BookAppointmentServlet(AppointmentBookStorage storage) {
        this.storage = storage;
        this.ownerValidator = new NonemptyStringValidator("owner");
        this.appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
    }

    /**
     * Handle incoming Http GET requests as following:
     * <ul>
     *     <li>/book?owner=username
     *          return a list of bookable slots to client</li>
     *     <li>/book?owner=username&amp;start=begintime&amp;end=endtime
     *          return a list of bookable slots begins within specified interval</li>
     * </ul>
     *
     * @param request  a {@link HttpServletRequest} instance
     * @param response a {@link HttpServletResponse} instance
     * @throws IOException If an input or output exception occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    /**
     * Handle incoming Http POST requests as following:
     * <ul>
     *     <li>post params: owner, begin, end: create a public bookable slot</li>
     *     <li>post params: owner appointmentId, begin, end, description: book a slot as a unregistered user</li>
     *     <li>post params: owner appointmentId, begin, end, description, participator: book a slot participate by
     *     specified participator who must be a registered user</li>
     * </ul>
     *
     * @param request  a {@link HttpServletRequest} instance
     * @param response a {@link HttpServletResponse} instance
     * @throws IOException If an input or output exception occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String owner = getParameter(OWNER_PARAMETER, request);
        String begin = getParameter(BEGIN_PARAMETER, request);
        String end = getParameter(END_PARAMETER, request);
        String id = getParameter(ID_PARAMETER, request);
        String description = getParameter(DESCRIPTION_PARAMETER, request);
        String participator = getParameter(PARTICIPATOR_PARAMETER, request);

        if (owner != null && begin != null && end != null && id != null && description != null) {
            if (participator != null) {
                if (authenticateUser(request, response, participator)) {
                    bookAppointment(response, owner, id, begin, end, description, true, participator);
                }
                return;
            }
            String confirmationCode = UUID.randomUUID().toString();
            bookAppointment(response, owner, id, begin, end, description, false, confirmationCode);
            return;
        }

        if (owner != null && begin != null && end != null) {
            if (authenticateUser(request, response, owner)) {
                insertBookableAppointmentSlot(response, owner, begin, end);
            }
            return;
        }

        writeMessageAndSetStatus(response, "Cannot process query parameter passed in", 422);
    }

    /**
     * Retrieve all bookable slots owned by <code>owner</code>.
     * If string <code>owner</code> does not meet format requirement of username, a <code>400</code> status code is
     * returned.
     * If there is no bookable appointment slot found, a <code>404</code> status code is returned.
     * If any server internal error occurred during retrieving, a <code>500</code> status code is returned.
     * Otherwise, there exists at least one bookable slot found, a list of bookable slots is written to
     * <code>response</code>, with a <code>200</code> status code.
     *
     * @param response a {@link HttpServletResponse} instance
     * @param owner    the owner name of appointments to retrieve
     * @throws IOException If an input or output exception occurs
     */
    private void getAllBookableAppointmentSlotsByOwner(HttpServletResponse response, String owner) throws IOException {
        if (!this.ownerValidator.isValid(owner)) {
            writeMessageAndSetStatus(response, this.ownerValidator.getErrorMessage(),
                    HttpServletResponse.SC_BAD_REQUEST);
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
            writeMessageAndSetStatus(response, "No appointment found with owner " + owner,
                    HttpServletResponse.SC_NOT_FOUND);
        } else {
            writeAppointmentBookAndOkStatus(response, book);
        }
    }

    /**
     * Retrieve all bookable slots owned by <code>owner</code>, filtered by which all the begin times of bookable
     * slots must be within the specified time interval.
     *
     * @param response a {@link HttpServletResponse} instance
     * @param owner    the owner name of appointments to retrieve
     * @throws IOException If an input or output exception occurs
     */
    private void getBookableAppointmentSlotsByOwnerBetweenInterval(HttpServletResponse response, String owner)
            throws IOException {
        // not implemented yet
        return;
    }

    /**
     * Create a new bookable slot owned by <code>owner</code>. If the time interval [<code>begin</code>,
     * <code>end</code>] overlaps with any existing appointment slots associated with <code>owner</code> (which means
     * any slot is either an existing slot which is bookable, or an existing appointment the <code>owner</code> owned,
     * or an appointment where <code>owner</code> is a participator), then a <code>409</code> status code is returned
     * and insertion will not happen. If the arguments for building a {@link AppointmentSlot} is not valid, then
     * <code>400</code> status code is returned along with a message indicates the error. Otherwise, the slot will be
     * added and <code>200</code> status code indicates success.
     *
     * @param response a {@link HttpServletResponse} instance
     * @param owner    the owner name of appointments to retrieve
     * @param begin    begin time of the slot
     * @param end      end time of the slot
     * @throws IOException If an input or output exception occurs
     */
    private void insertBookableAppointmentSlot(HttpServletResponse response, String owner, String begin, String end)
            throws IOException {
        // validate owner and fields for constructing new appointment
        if (!this.ownerValidator.isValid(owner)) {
            writeMessageAndSetStatus(response, this.ownerValidator.getErrorMessage(),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!Helper.validateTwoDateStringForDateInterval(begin, end, "begin time of bookable slot", "end time")) {
            writeMessageAndSetStatus(response, Helper.getErrorMessage(), HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Date from = Helper.getLowerDate();
        Date to = Helper.getUpperDate();
        AppointmentSlot slot = new AppointmentSlot(owner, from, to);

        // load appointment to persistent storage
        try {
            if (this.storage.insertBookableAppointmentSlot(owner, slot)) {
                writeBookedAppointmentAndOkStatus(response, slot, APPOINTMENT_SLOT_TYPE_TOKEN);
            } else {
                writeMessageAndSetStatus(response, "slot " + slot + " conflicts with existing appointment " +
                        "slot", HttpServletResponse.SC_CONFLICT);
            }
        } catch (StorageException e) {
            writeMessageAndSetStatus(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Book a <strong>bookable</strong> appointment. If given <code>id</code>, <code>begin</code> which is the beign
     * time of the slot, <code>end</code> which is the end time of the slot, the database cannot find a slot that has
     * exactly the same data with above three fields; or the database do find a slot that match <code>id</code>,
     * <code>begin</code>, and <code>end</code>, but already booked, then it is not bookable. If the client attempts
     * to book a not bookable slot, a <code>409</code> status code is written to response, with a message indicates
     * the reason. The arguments for constructing a {@link Appointment} instance will be validated, if any valiation
     * error occurs, a <code>400</code> status code will be written to reponse, with a message indicates any error.
     * <p>
     * If the request comes from a registered user that has logged in, this is determined by <code>authenticated</code>
     * set to <code>true</code>, then the participator must be the username of the participator (the "booker"). In this
     * case, the code also checks if the appointment slot to be booked conflicts with all the appointments the
     * <code>participator</code> owned, booked, and slots that are published by <code>participator</code>. If conflicts,
     * then a <code>409</code> will be written to response.
     * <p>
     * If above cases not happen, then the appointment is bookable, the appointment will be saved and a <code>200</code>
     * status code will be written to response.
     *
     * @param response      a {@link HttpServletResponse} instance
     * @param owner         the owner of the slot owner
     * @param id            the id of the appointment slot to be booked
     * @param begin         the begin time of the appointment slot to be booked
     * @param end           the end time of the appointment slot to be booked
     * @param description   a description to the appointment slot
     * @param authenticated indicate if the <code>participator</code> is authenticated via
     *                      {@link HttpServletHelper#authenticateUser}
     * @param participator  if <code>authenticated</code>, then it is a string of participator's username; otherwise,
     *                      it is a confirmation code to be sent back to booker
     * @throws IOException If an input or output exception occurs
     */
    public void bookAppointment(HttpServletResponse response, String owner, String id, String begin, String end,
                                String description, boolean authenticated, String participator) throws IOException {
        // validate owner and fields for constructing new appointment
        if (!this.ownerValidator.isValid(owner)) {
            writeMessageAndSetStatus(response, this.ownerValidator.getErrorMessage(),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Appointment appointment = null;
        if ((appointment = this.appointmentValidator.createAppointmentFromString(owner, id, begin, end, description)) == null) {
            writeMessageAndSetStatus(response, this.appointmentValidator.getErrorMessage(),
                    HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        appointment.setSlotType(AppointmentSlot.SlotType.PARTICIPATOR_BOOKED);
        if (authenticated) {
            appointment.setParticipatorType(AppointmentSlot.ParticipatorType.REGISTERED);
        } else {
            appointment.setParticipatorType(AppointmentSlot.ParticipatorType.UNREGISTERED);
        }
        appointment.setParticipatorIdentifier(participator);

        try {
            int bookResult = this.storage.bookAppointment(owner, appointment, authenticated);
            if (bookResult == AppointmentBookStorage.NOT_BOOKABLE) {
                writeMessageAndSetStatus(response, "Appointment " + appointment + " is not bookable",
                        HttpServletResponse.SC_CONFLICT);
            } else if (bookResult == AppointmentBookStorage.CONFLICT_WITH_EXISTING_APPOINTMENT) {
                writeMessageAndSetStatus(response, "appointment " + appointment + " conflicts with existing " +
                        "appointment", HttpServletResponse.SC_CONFLICT);
            } else if (bookResult == AppointmentBookStorage.BOOK_SUCCESS) {
                writeBookedAppointmentAndOkStatus(response, appointment, APPOINTMENT_TYPE_TOKEN);
            }
        } catch (StorageException e) {
            writeMessageAndSetStatus(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Write <code>book</code> as a json object to <code>response</code>,  and set status code to 200.
     *
     * @param response a {@link HttpServletResponse} instance
     * @param book     a <code>AppointmentBook</code> that collects {@link AppointmentSlot} to be written to
     *                 <code>response</code>
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

    /**
     * Write an {@link Appointment} or {@link AppointmentSlot} based on <code>type</code> as a json object
     * into <code>response</code>, and set status code to 200.
     *
     * @param response    a {@link HttpServletResponse} instance
     * @param appointment an {@link Appointment} or {@link AppointmentSlot} based on <code>type</code>
     * @param type        either <code>APPOINTMENT_SLOT_TYPE_TOKEN</code>, or <code>APPOINTMENT_TYPE_TOKEN</code>,
     *                    such that Gson library can determine the type parameter of <code>TypeToken</code>.
     * @throws IOException If an input or output exception occurs
     */
    protected void writeBookedAppointmentAndOkStatus(HttpServletResponse response, AppointmentSlot appointment,
                                                     int type) throws IOException {
        response.setContentType("text/json");
        response.setStatus(HttpServletResponse.SC_OK);

        Gson gson = new Gson();
        JsonObject object = new JsonObject();

        Type appointmentType;
        if (type == APPOINTMENT_SLOT_TYPE_TOKEN) {
            appointmentType = new TypeToken<AppointmentSlot>() {
            }.getType();
        } else if (type == APPOINTMENT_TYPE_TOKEN) {
            appointmentType = new TypeToken<Appointment>() {
            }.getType();
        } else {
            throw new IllegalArgumentException("type parameter is invalid when writing booked appointment json " +
                    "response");
        }
        JsonElement appointmentJson = gson.toJsonTree(appointment, appointmentType);
        object.addProperty("status", HttpServletResponse.SC_OK);
        object.add("requestResult", appointmentJson);

        PrintWriter pw = response.getWriter();
        pw.println(gson.toJson(object));
        pw.flush();
    }

}
