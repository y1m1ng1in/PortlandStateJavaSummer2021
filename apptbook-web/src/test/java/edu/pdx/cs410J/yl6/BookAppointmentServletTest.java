package edu.pdx.cs410J.yl6;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import edu.pdx.cs410J.yl6.database.AppointmentBookStorage;
import edu.pdx.cs410J.yl6.database.PlainTextFileDatabase;
import edu.pdx.cs410J.yl6.database.plaintextoperator.UserTableEntryDumper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookAppointmentServletTest {

    static final String OWNER_PARAMETER = "owner";
    static final String DESCRIPTION_PARAMETER = "description";
    static final String BEGIN_PARAMETER = "start";
    static final String END_PARAMETER = "end";
    static final String DURATION_PARAMETER = "duration";
    static final String ID_PARAMETER = "id";

    static Map<String, AppointmentBook<Appointment>> bookedAppointments;
    static Map<String, ArrayList<AppointmentSlot>> recordedSlots;
    static Map<String, ArrayList<AppointmentSlot>> returnedSlots;

    File dir = new File("./unittest/");
    AppointmentBookStorage storage = new PlainTextFileDatabase(dir);


    Cookie createAuthCookie(String username, String password) {
        String toEncode = username + ":" + password;
        Cookie cookie = new Cookie("Authentication", Base64.getEncoder().encodeToString(toEncode.getBytes()));
        return cookie;
    }

    HttpServletRequest createMockedRequest(String owner, String description, String begin, String end, String id,
                                           Integer duration) {
        HttpServletRequest request = mock(HttpServletRequest.class);

        if (owner != null)
            when(request.getParameter(OWNER_PARAMETER)).thenReturn(owner);
        if (description != null)
            when(request.getParameter(DESCRIPTION_PARAMETER)).thenReturn(description);
        if (begin != null)
            when(request.getParameter(BEGIN_PARAMETER)).thenReturn(begin);
        if (end != null)
            when(request.getParameter(END_PARAMETER)).thenReturn(end);
        if (id != null)
            when(request.getParameter(ID_PARAMETER)).thenReturn(id);
        if (duration != null)
            when(request.getParameter(DURATION_PARAMETER)).thenReturn(duration.toString());

        return request;
    }

    HttpServletRequest addMockedCookie(String username, String password, HttpServletRequest request) {
        Cookie cookie = createAuthCookie(username, password);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        return request;
    }

    HttpServletResponse createMockedResponse(StringWriter stringWriter) throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter pw = new PrintWriter(stringWriter, true);
        when(response.getWriter()).thenReturn(pw);
        return response;
    }

    String generateStatusMessageJsonString(String message, int status) {
        JsonObject object = new JsonObject();
        object.addProperty("message", message);
        object.addProperty("status", status);
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    void recordBookAppointmentRequest(String owner, AppointmentSlot requestedSlot) {
        if (recordedSlots.containsKey(owner)) {
            AppointmentBook<AppointmentSlot> sorter = new AppointmentBook<>(owner);
            for (AppointmentSlot slot1 : recordedSlots.get(owner)) {
                sorter.addAppointment(slot1);
            }
            sorter.addAppointment(requestedSlot);
            recordedSlots.put(owner, new ArrayList<>(sorter.getAppointments()));
        } else {
            recordedSlots.put(owner, new ArrayList<>() {{
                add(requestedSlot);
            }});
        }
    }

    void saveGetAllBookableSlotsResponse(String owner, AppointmentBook<AppointmentSlot> respondedSlots) {
        ArrayList<AppointmentSlot> toAssign = new ArrayList<>();
        for (AppointmentSlot returnedSlot : respondedSlots.getAppointments()) {
            toAssign.add(returnedSlot);
        }
        returnedSlots.put(owner, toAssign);
    }

    void updateRecordsAfterBooked(String owner, int indexAt, Appointment bookedOne) {
        returnedSlots.get(owner).remove(indexAt);
        recordedSlots.get(owner).remove(indexAt);
        if (bookedAppointments.containsKey(owner)) {
            bookedAppointments.get(owner).addAppointment(bookedOne);
        } else {
            bookedAppointments.put(owner, new AppointmentBook<>(owner) {{
                addAppointment(bookedOne);
            }});
        }
    }

    void testAddBookableSlotWithValidArgument(String owner, String begin, String end, String username,
                                              String password) throws ServletException, IOException {
        BookAppointmentServlet servlet = new BookAppointmentServlet(storage);

        HttpServletRequest request = createMockedRequest(owner, null, begin, end, null, null);
        request = addMockedCookie(username, password, request);

        StringWriter stringWriter = new StringWriter();
        HttpServletResponse response = createMockedResponse(stringWriter);

        servlet.doPost(request, response);

        AppointmentSlot slot = new AppointmentSlot(owner, Helper.validateAndParseDate(begin),
                Helper.validateAndParseDate(end));

        JsonObject jobj = new Gson().fromJson(stringWriter.toString().trim(), JsonObject.class);
        String jsonSlot = jobj.get("requestResult").toString();
        Type appointmentType = new TypeToken<AppointmentSlot>() {
        }.getType();
        AppointmentSlot returned = new Gson().fromJson(jsonSlot, appointmentType);

        assertThat(returned.getBeginTime(), equalTo(slot.getBeginTime()));
        assertThat(returned.getEndTime(), equalTo(slot.getEndTime()));
        assertThat(returned.getOwner(), equalTo(slot.getOwner()));
        assertThat(returned.getSlotType(), equalTo(AppointmentSlot.SlotType.OPEN_TO_EVERYONE));
        assertThat(returned.getParticipatorIdentifier(), equalTo(null));
        assertThat(returned.getParticipatorType(), equalTo(null));

        recordBookAppointmentRequest(owner, slot);

        ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
        verify(response).setStatus(statusCode.capture());
        assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));
    }

    void testGetAllBookableSlotWithValidArgument(String owner) throws ServletException, IOException {
        BookAppointmentServlet servlet = new BookAppointmentServlet(storage);
        HttpServletRequest request = createMockedRequest(owner, null, null, null, null, null);

        StringWriter stringWriter = new StringWriter();
        HttpServletResponse response = createMockedResponse(stringWriter);

        servlet.doGet(request, response);

        String returnedFromResponse = stringWriter.toString();
        Gson gson = new Gson();
        Type collectionType = new TypeToken<AppointmentBook<AppointmentSlot>>() {
        }.getType();
        AppointmentBook<AppointmentSlot> returnedSlotBook = gson.fromJson(returnedFromResponse, collectionType);

        saveGetAllBookableSlotsResponse(owner, returnedSlotBook);

        for (AppointmentSlot recordedslot : recordedSlots.get(owner)) {
            AppointmentSlot returnedSlot = returnedSlotBook.getAppointments().pollFirst();

            assertThat(recordedslot.getBeginTime(), equalTo(returnedSlot.getBeginTime()));
            assertThat(recordedslot.getEndTime(), equalTo(returnedSlot.getEndTime()));
            assertThat(returnedSlot.getOwner(), equalTo(owner));
            assertThat(returnedSlot.getSlotType(), equalTo(AppointmentSlot.SlotType.OPEN_TO_EVERYONE));
            assertThat(returnedSlot.getParticipatorIdentifier(), equalTo(null));
            assertThat(returnedSlot.getParticipatorType(), equalTo(null));
        }

        ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
        verify(response).setStatus(statusCode.capture());
        assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));
    }

    void testBookBookableSlotWithoutLogin(String owner, String description, int indexAt) throws ServletException,
            IOException {
        BookAppointmentServlet servlet = new BookAppointmentServlet(storage);

        AppointmentSlot fromReturnedSlots = returnedSlots.get(owner).get(indexAt);

        HttpServletRequest request = createMockedRequest(owner, description, fromReturnedSlots.getBeginTimeString(),
                fromReturnedSlots.getEndTimeString(), fromReturnedSlots.getId(), null);

        StringWriter stringWriter = new StringWriter();
        HttpServletResponse response = createMockedResponse(stringWriter);

        servlet.doPost(request, response);

        Appointment appointmentBooked = new Appointment(owner, fromReturnedSlots.getId(),
                fromReturnedSlots.getBeginTime(), fromReturnedSlots.getEndTime(), fromReturnedSlots.getSlotType(),
                fromReturnedSlots.getParticipatorType(), fromReturnedSlots.getParticipatorIdentifier(), description);

        JsonObject jobj = new Gson().fromJson(stringWriter.toString().trim(), JsonObject.class);
        String jsonSlot = jobj.get("requestResult").toString();
        Type appointmentType = new TypeToken<Appointment>() {
        }.getType();
        Appointment returned = new Gson().fromJson(jsonSlot, appointmentType);

        assertThat(returned.getBeginTime(), equalTo(appointmentBooked.getBeginTime()));
        assertThat(returned.getEndTime(), equalTo(appointmentBooked.getEndTime()));
        assertThat(returned.getOwner(), equalTo(appointmentBooked.getOwner()));
        assertThat(returned.getSlotType(), equalTo(AppointmentSlot.SlotType.PARTICIPATOR_BOOKED));
        assertThat(returned.getParticipatorIdentifier(), not(equalTo(null)));
        assertThat(returned.getParticipatorType(), equalTo(AppointmentSlot.ParticipatorType.UNREGISTERED));

        updateRecordsAfterBooked(owner, indexAt, returned);

        ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
        verify(response).setStatus(statusCode.capture());
        assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));
    }

    void verifyBookedAppointmentsHaveBeenAddedToOwner(String owner, String password) throws ServletException,
            IOException {
        AppointmentBookServlet servlet = new AppointmentBookServlet(storage);

        HttpServletRequest request = createMockedRequest(owner, null, null, null, null, null);
        request = addMockedCookie(owner, password, request);

        StringWriter stringWriter = new StringWriter();
        HttpServletResponse response = createMockedResponse(stringWriter);

        servlet.doGet(request, response);

        String s = stringWriter.toString();

        Gson gson = new Gson();
        Type collectionType = new TypeToken<AppointmentBook<Appointment>>() {
        }.getType();
        String j = gson.toJson(bookedAppointments.get(owner), collectionType);

        assertThat(s, equalTo(j));

        ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
        verify(response).setStatus(statusCode.capture());
        assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));
    }

    @Test
    @Order(1)
    void addSomeUserFirst() throws IOException {
        User u1 = new User("the fourth user", "the_fourth_user_password", "fourth@email.com", "street 4");
        User u2 = new User("the fifth user", "the_fifth_user_password", "fifth@email.com", "street 5");
        User u3 = new User("the sixth user", "the_sixth_user_password", "sixth@email.com", "street 6");
        File f = new File(dir, "db_user.txt");

        bookedAppointments = new HashMap<>();
        recordedSlots = new HashMap<>();
        returnedSlots = new HashMap<>();

        Writer writer = new FileWriter(f, true);
        UserTableEntryDumper.UserProfilerTableEntryDumper dumper =
                new UserTableEntryDumper.UserProfilerTableEntryDumper(writer);
        dumper.dump(u1);
        dumper.dump(u2);
        dumper.dump(u3);
        writer.flush();
        writer.close();
    }

    @Test
    @Order(2)
    void addBookableSlot() throws IOException, ServletException {
        String owner = "the fourth user";
        String begin = "7/30/2021 4:00 am";
        String end = "7/30/2021 5:00 am";

        testAddBookableSlotWithValidArgument(owner, begin, end, owner, "the_fourth_user_password");
    }

    @Test
    @Order(3)
    void addAnotherBookableSlot() throws IOException, ServletException {
        String owner = "the fourth user";
        String begin = "7/30/2021 5:01 am";
        String end = "7/30/2021 6:00 am";

        testAddBookableSlotWithValidArgument(owner, begin, end, owner, "the_fourth_user_password");
    }

    @Test
    @Order(4)
    void addBookableSlotForAnotherUser() throws IOException, ServletException {
        String owner = "the fifth user";
        String begin = "8/1/2021 8:00 am";
        String end = "8/1/2021 8:30 am";

        testAddBookableSlotWithValidArgument(owner, begin, end, owner, "the_fifth_user_password");
    }

    @Test
    @Order(5)
    void addBookableSlotForAnotherUserAgain() throws IOException, ServletException {
        String owner = "the fifth user";
        String begin = "8/1/2021 8:31 am";
        String end = "8/1/2021 9:00 am";

        testAddBookableSlotWithValidArgument(owner, begin, end, owner, "the_fifth_user_password");
    }

    @Test
    @Order(6)
    void testGetAll() throws IOException, ServletException {
        String owner = "the fourth user";
        testGetAllBookableSlotWithValidArgument(owner);
    }

    @Test
    @Order(7)
    void testGetAllForAnotherUser() throws IOException, ServletException {
        String owner = "the fifth user";
        testGetAllBookableSlotWithValidArgument(owner);
    }

    @Test
    @Order(8)
    void addMoreSlotsForU1() throws IOException, ServletException {
        String owner = "the fourth user";

        testAddBookableSlotWithValidArgument(owner, "7/28/2021 4:00 am", "7/28/2021 5:00 am", owner,
                "the_fourth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/28/2021 5:01 am", "7/28/2021 6:00 am", owner,
                "the_fourth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/28/2021 6:01 am", "7/28/2021 7:00 am", owner,
                "the_fourth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/28/2021 7:01 am", "7/28/2021 8:00 am", owner,
                "the_fourth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/28/2021 8:01 am", "7/28/2021 9:00 am", owner,
                "the_fourth_user_password");
    }

    @Test
    @Order(9)
    void addMoreSlotsForU2() throws IOException, ServletException {
        String owner = "the fifth user";

        testAddBookableSlotWithValidArgument(owner, "7/31/2021 5:00 pm", "7/31/2021 5:30 pm", owner,
                "the_fifth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/31/2021 5:31 pm", "7/31/2021 6:00 pm", owner,
                "the_fifth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/31/2021 6:01 pm", "7/31/2021 6:30 pm", owner,
                "the_fifth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/31/2021 6:31 pm", "7/31/2021 7:00 pm", owner,
                "the_fifth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/31/2021 7:01 pm", "7/31/2021 7:30 pm", owner,
                "the_fifth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/31/2021 7:31 pm", "7/31/2021 8:00 pm", owner,
                "the_fifth_user_password");
        testAddBookableSlotWithValidArgument(owner, "7/31/2021 8:01 pm", "7/31/2021 8:30 pm", owner,
                "the_fifth_user_password");
    }

    @Test
    @Order(10)
    void testGetAllAgain() throws IOException, ServletException {
        String owner = "the fourth user";
        testGetAllBookableSlotWithValidArgument(owner);
    }

    @Test
    @Order(11)
    void testGetAllForAnotherUserAgain() throws IOException, ServletException {
        String owner = "the fifth user";
        testGetAllBookableSlotWithValidArgument(owner);
    }

    @Test
    @Order(12)
    void testBookAnAppointmentWithU1() throws IOException, ServletException {
        String owner = "the fourth user";
        String description = "a book appointment test without login with the fourth user";
        testBookBookableSlotWithoutLogin(owner, description, 1);
    }

    @Test
    @Order(13)
    void testBookAnAppointmentWithU2() throws IOException, ServletException {
        String owner = "the fifth user";
        String description = "a book appointment test without login with the fifth user";
        testBookBookableSlotWithoutLogin(owner, description, 1);
    }

    @Test
    @Order(14)
    void testGetAllAgainAfterBookOne() throws IOException, ServletException {
        String owner = "the fourth user";
        testGetAllBookableSlotWithValidArgument(owner);
    }

    @Test
    @Order(15)
    void testGetAllForAnotherUserAgainAfterBookOne() throws IOException, ServletException {
        String owner = "the fifth user";
        testGetAllBookableSlotWithValidArgument(owner);
    }

    @Test
    @Order(16)
    void testBookMoreValidAppointmentWithU1() throws IOException, ServletException {
        String owner = "the fourth user";
        String description = "more booking test without login with the fourth user";
        testBookBookableSlotWithoutLogin(owner, description, 0);
        testBookBookableSlotWithoutLogin(owner, description, 1);
        testBookBookableSlotWithoutLogin(owner, description, 2);
        testBookBookableSlotWithoutLogin(owner, description, 2);
        testBookBookableSlotWithoutLogin(owner, description, 0);
    }

    @Test
    @Order(17)
    void testBookMoreValidAppointmentWithU2() throws IOException, ServletException {
        String owner = "the fifth user";
        String description = "more booking test without login with the fifth user";
        testBookBookableSlotWithoutLogin(owner, description, 0);
        testBookBookableSlotWithoutLogin(owner, description, 3);
        testBookBookableSlotWithoutLogin(owner, description, 2);
        testBookBookableSlotWithoutLogin(owner, description, 1);
        testBookBookableSlotWithoutLogin(owner, description, 0);
        testBookBookableSlotWithoutLogin(owner, description, 0);
    }

    @Test
    @Order(18)
    void verifyGetAllAgainAfterBookMore() throws IOException, ServletException {
        String owner = "the fourth user";
        testGetAllBookableSlotWithValidArgument(owner);
    }

    @Test
    @Order(19)
    void verifyGetAllForAnotherUserAgainAfterBookMore() throws IOException, ServletException {
        String owner = "the fifth user";
        testGetAllBookableSlotWithValidArgument(owner);
    }

    @Test
    @Order(20)
    void testOwnerGotBookedAppointments() throws IOException, ServletException {
        String owner = "the fourth user";
        String password = "the_fourth_user_password";

        verifyBookedAppointmentsHaveBeenAddedToOwner(owner, password);
    }

    @Test
    @Order(21)
    void testAnotherOwnerGotBookedAppointments() throws IOException, ServletException {
        String owner = "the fifth user";
        String password = "the_fifth_user_password";

        verifyBookedAppointmentsHaveBeenAddedToOwner(owner, password);
    }
}
