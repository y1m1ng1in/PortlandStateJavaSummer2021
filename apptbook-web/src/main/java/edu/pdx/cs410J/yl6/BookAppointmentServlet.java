package edu.pdx.cs410J.yl6;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import edu.pdx.cs410J.yl6.database.AppointmentBookStorage;
import edu.pdx.cs410J.yl6.database.PlainTextFileDatabase;
import edu.pdx.cs410J.yl6.database.PostgresqlDatabase;
import edu.pdx.cs410J.yl6.database.StorageException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

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

    public BookAppointmentServlet() {
        super();
        this.ownerValidator = new NonemptyStringValidator("owner");
        this.appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
    }

    public BookAppointmentServlet(AppointmentBookStorage storage) {
        this.storage = storage;
//        this.tryConnect = PostgresqlDatabase.getDatabase();
        this.ownerValidator = new NonemptyStringValidator("owner");
        this.appointmentValidator = new AppointmentValidator("M/d/yyyy h:m a");
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
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

    private void getBookableAppointmentSlotsByOwnerBetweenInterval(HttpServletResponse response, String owner)
            throws IOException {
        // not implemented yet
        return;
    }

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
