package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.io.TempDir;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;

import edu.pdx.cs410J.ParserException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * A unit test for the {@link AppointmentBookServlet}. It uses mockito to
 * provide mock http requests and responses.
 */
@TestMethodOrder(OrderAnnotation.class)
public class AppointmentBookServletTest {

  File dir = new File("./unittest/");

  AppointmentBookStorage storage = new PlainTextAsStorage(dir);

  static Map<String, ArrayList<Appointment>> validAppointments = new HashMap<>();

  void createFileWithText(String content, String owner) throws IOException {
    File f = new File(dir, owner + "_slots.txt");
    Writer writer = new FileWriter(f);
    writer.write(content);
    writer.flush();
    writer.close();
  }

  /**
   * Create a cookie for authentication using basic strategy to be used for
   * mocking request. This method can be changed for future testing when
   * authentication method is changed.
   * 
   * @param username username
   * @param password password
   * @return a cookie instance
   */
  Cookie createAuthCookie(String username, String password) {
    String toEncode = username + ":" + password;
    Cookie cookie = new Cookie("Authentication", Base64.getEncoder().encodeToString(toEncode.getBytes()));
    return cookie;
  }

  /**
   * Create a mocked HttpServletRequest object, query for "owner", "description",
   * "start", "end", then mocked object return given argument specified in
   * parameters.
   */
  HttpServletRequest createMockedRequest(String owner, String description, String begin, String end, String username,
      String password) {
    HttpServletRequest request = mock(HttpServletRequest.class);
    if (owner != null)
      when(request.getParameter("owner")).thenReturn(owner);
    if (description != null)
      when(request.getParameter("description")).thenReturn(description);
    if (begin != null)
      when(request.getParameter("start")).thenReturn(begin);
    if (end != null)
      when(request.getParameter("end")).thenReturn(end);

    Cookie cookie = createAuthCookie(username, password);
    when(request.getCookies()).thenReturn(new Cookie[] { cookie });

    return request;
  }

  /**
   * Create a mocked HttpServletResponse, using <code>stringWriter</code> to
   * capture what is written to response
   */
  HttpServletResponse createMockedResponse(StringWriter stringWriter) throws IOException {
    HttpServletResponse response = mock(HttpServletResponse.class);
    PrintWriter pw = new PrintWriter(stringWriter, true);
    when(response.getWriter()).thenReturn(pw);
    return response;
  }

  /**
   * The method to be called in test method to add an appointment with
   * <strong>valid</strong> argument, and valid username and password.
   */
  void testAddAppointmentWithValidArgument(String owner, String description, String begin, String end, String username,
      String password) throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet(storage);

    HttpServletRequest request = createMockedRequest(owner, description, begin, end, username, password);

    // Use a StringWriter to gather the text from multiple calls to println()
    StringWriter stringWriter = new StringWriter();

    HttpServletResponse response = createMockedResponse(stringWriter);

    servlet.doPost(request, response);
    Gson gson = new Gson();
    Appointment appointmentReturned = gson.fromJson(stringWriter.toString(), Appointment.class);

    assertThat(description, equalTo(appointmentReturned.getDescription()));
    assertThat(Helper.validateAndParseDate(begin),
        equalTo(appointmentReturned.getBeginTime()));
    assertThat(Helper.validateAndParseDate(end),
        equalTo(appointmentReturned.getEndTime()));

    if (validAppointments.containsKey(owner)) {
      validAppointments.get(owner).add(appointmentReturned);
    } else {
      validAppointments.put(owner, new ArrayList<Appointment>() {{ add(appointmentReturned); }});
    }

    ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
    verify(response).setStatus(statusCode.capture());
    assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));
  }

  /**
   * The method to be called in test method to add an appointment with
   * <strong>invalid</strong> argument, and valid username and password.
   */
  void testAddAppointmentWithInvalidArgument(String owner, String description, String begin, String end, int sc,
      String errorMessage, String username, String password) throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet(storage);

    HttpServletRequest request = createMockedRequest(owner, description, begin, end, username, password);

    StringWriter stringWriter = new StringWriter();

    HttpServletResponse response = createMockedResponse(stringWriter);

    servlet.doPost(request, response);

    verify(response).setStatus(sc);
  }

  /**
   * The method to be called in test method to get an appointment with
   * <strong>valid</strong> owner and time interval, but valid username and
   * password.
   */
  void testGetExistingAppointments(String owner, String start, String end, String username, String password,
      int... expectedAt) throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet(storage);

    HttpServletRequest request = createMockedRequest(owner, null, start, end, username, password);

    StringWriter stringWriter = new StringWriter();

    HttpServletResponse response = createMockedResponse(stringWriter);

    servlet.doGet(request, response);

    String s = stringWriter.toString();

    AppointmentBook<Appointment> apptbook = new AppointmentBook<>(owner);
    for (int index : expectedAt) {
      apptbook.addAppointment(validAppointments.get(owner).get(index));
    }

    // Type t = new TypeToken<AppointmentBook<Appointment>>() {
    // }.getType();
    Gson gson = new Gson();
    String json = gson.toJson(apptbook);
    // AppointmentBook<Appointment> apptbook = gson.fromJson(s, t);
    // StringWriter sw = new StringWriter();
    // TextDumper td = new TextDumper(sw);
    // td.dump(apptbook);
    assertThat(s, equalTo(json));

    ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
    verify(response).setStatus(statusCode.capture());
    assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));
  }

  /**
   * The method to be called in test method to get an appointment with
   * <strong>invalid</strong> owner or time interval, but valid username and
   * password.
   */
  void testGetNonExistingAppointments(String owner, String start, String end, int sc, String expected, String username,
      String password) throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet(storage);

    HttpServletRequest request = createMockedRequest(owner, null, start, end, username, password);

    StringWriter stringWriter = new StringWriter();

    HttpServletResponse response = createMockedResponse(stringWriter);

    servlet.doGet(request, response);

    verify(response).setStatus(sc);
  }

  static User u1, u2, u3;
  static Appointment a11, a12, a13, a1_overlap1, a1_overlap2;
  static Appointment a21, a22, a23, a2_overlap1, a2_overlap2;
  // Appointment a31, a32, a33;

  void addSomeInstance() {
    u1 = new User("the first user", "the_first_user_password", "first@email.com", "street 1");
    u2 = new User("the 2nd user", "the_2nd_user_password", "second@email.com", "street 2");
    u3 = new User("the 3rd user", "the_3rd_user_password", "third@email.com", "street 3");

    a11 = new Appointment(Helper.validateAndParseDate("7/30/2021 4:00 am"),
        Helper.validateAndParseDate("7/30/2021 5:00 am"), "a11");
    a12 = new Appointment(Helper.validateAndParseDate("7/30/2021 5:01 am"),
        Helper.validateAndParseDate("7/30/2021 5:30 am"), "a12");
    a13 = new Appointment(Helper.validateAndParseDate("7/30/2021 6:00 am"),
        Helper.validateAndParseDate("7/30/2021 7:00 am"), "a13");
    a1_overlap1 = new Appointment(Helper.validateAndParseDate("7/30/2021 3:40 am"),
        Helper.validateAndParseDate("7/30/2021 4:30 am"), "a1_overlap1");
    a1_overlap2 = new Appointment(Helper.validateAndParseDate("7/30/2021 5:20 am"),
        Helper.validateAndParseDate("7/30/2021 6:30 am"), "a1_overlap2");

    a21 = new Appointment(Helper.validateAndParseDate("7/30/2021 4:00 am"),
        Helper.validateAndParseDate("7/30/2021 5:00 am"), "a21");
    a22 = new Appointment(Helper.validateAndParseDate("7/30/2021 5:30 am"),
        Helper.validateAndParseDate("7/30/2021 6:30 am"), "a22");
    a23 = new Appointment(Helper.validateAndParseDate("7/30/2021 8:00 am"),
        Helper.validateAndParseDate("7/30/2021 9:00 am"), "a23");
    a2_overlap1 = new Appointment(Helper.validateAndParseDate("7/30/2021 3:40 am"),
        Helper.validateAndParseDate("7/30/2021 5:29 am"), "a2_overlap1");
    a2_overlap2 = new Appointment(Helper.validateAndParseDate("7/30/2021 5:20 am"),
        Helper.validateAndParseDate("7/30/2021 8:30 am"), "a2_overlap2");
  }

  @Test
  @Order(1)
  void addSomeUserFirst() throws IOException {
    addSomeInstance();
    File f = new File(dir, "db_user.txt");
    Writer writer = new FileWriter(f);
    User user1 = new User("unittest", "unittest_password", "unittest@email.com", "unittest st.");
    User user2 = new User("another unittest", "another unittest_password", "another unittest@email.com",
        "another unittest st.");
    ParseableUserDumper dumper = new ParseableUserDumper(writer);
    dumper.dump(user1);
    dumper.dump(user2);
    dumper.dump(u1);
    dumper.dump(u2);
    dumper.dump(u3);
    writer.flush();
    writer.close();
  }

  /**
   * Add appointments with valid arguments.
   * 
   * a11 = new Appointment(Helper.validateAndParseDate("7/30/2021 4:00 am"),
   * Helper.validateAndParseDate("7/30/2021 5:00 am"), "a11");
   */
  @Test
  @Order(2)
  void addOneWordToDictionary() throws ServletException, IOException {
    String owner = "the first user";
    String description = "a11";
    String begin = "7/30/2021 4:00 am";
    String end = "7/30/2021 5:00 am";

    testAddAppointmentWithValidArgument(owner, description, begin, end, "the first user", "the_first_user_password");
  }

  /**
   * Add appointments with valid arguments.
   * 
   * a12 = new Appointment(Helper.validateAndParseDate("7/30/2021 5:01 am"),
   * Helper.validateAndParseDate("7/30/2021 5:30 am"), "a12");
   */
  @Test
  @Order(3)
  void addAnotherWordToDictionary() throws ServletException, IOException {
    String owner = "the first user";
    String description = "a12";
    String begin = "7/30/2021 5:01 am";
    String end = "7/30/2021 5:30 am";

    testAddAppointmentWithValidArgument(owner, description, begin, end, "the first user", "the_first_user_password");
  }

  /**
   * Add appointments with valid arguments to another owner
   * 
   * a21 = new Appointment(Helper.validateAndParseDate("7/30/2021 4:00 am"),
   * Helper.validateAndParseDate("7/30/2021 5:00 am"), "a21");
   */
  @Test
  @Order(4)
  void addAppointmentWithAnotherOwner() throws ServletException, IOException {
    String owner = "the 2nd user";
    String description = "a21";
    String begin = "7/30/2021 4:00 am";
    String end = "7/30/2021 5:00 am";

    testAddAppointmentWithValidArgument(owner, description, begin, end, "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Add appointments with valid arguments to another owner
   * 
   * a22 = new Appointment(Helper.validateAndParseDate("7/30/2021 5:30 am"),
   * Helper.validateAndParseDate("7/30/2021 6:30 am"), "a22");
   */
  @Test
  @Order(5)
  void addAnotherAppointmentWithAnotherOwner() throws ServletException, IOException {
    String owner = "the 2nd user";
    String description = "a22";
    String begin = "7/30/2021 5:30 am";
    String end = "7/30/2021 6:30 am";

    testAddAppointmentWithValidArgument(owner, description, begin, end, "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Get all appointment with "the first user"
   */
  @Test
  @Order(6)
  void getAllWithFirstOwner() throws ServletException, IOException {
    testGetExistingAppointments("the first user", null, null, "the first user", "the_first_user_password", 0, 1);
  }

  /**
   * Get all appointment with "the 2nd user"
   */
  @Test
  @Order(7)
  void getAllWithSecondOwner() throws ServletException, IOException {
    testGetExistingAppointments("the 2nd user", null, null, "the 2nd user", "the_2nd_user_password", 0, 1);
  }

  /**
   * Create an appointment with invalid arguments -- an empty description
   */
  @Test
  @Order(8)
  void addAppointmentWithInvalidField() throws ServletException, IOException {
    String owner = "the 2nd user";
    String description = "    ";
    String begin = "6/28/2021 3:00 pm";
    String end = "6/28/2021 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Field description should not be empty", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Create an appointment with invalid arguments -- an empty owner name
   */
  @Test
  @Order(9)
  void addAppointmentWithInvalidFieldCase2() throws ServletException, IOException {
    String owner = "  ";
    String description = "ffsdfwe";
    String begin = "6/28/2021 3:00 pm";
    String end = "6/28/2021 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, 401,
        "Invalid credential for user \"" + owner + "\"", "the first user", "the_first_user_password");
  }

  /**
   * Create an appointment with invalid arguments -- an unparseable begin time
   */
  @Test
  @Order(10)
  void addAppointmentWithInvalidFieldCase3() throws ServletException, IOException {
    String owner = "the 2nd user";
    String description = "ffsdfwe";
    String begin = "6-28-2021 3:00 pm";
    String end = "6/28/2021 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Unparseable date: \"6-28-2021 3:00 pm\"", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Create an appointment with invalid arguments -- an unparseable end time
   * 
   * add it to u3
   */
  @Test
  @Order(11)
  void addAppointmentWithInvalidFieldCase4() throws ServletException, IOException {
    String owner = "the 3rd user";
    String description = "ffsdfwe";
    String begin = "6/28/2021 3:00 pm";
    String end = "6/28/XXXX 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Unparseable date: \"6/28/XXXX 5:00 pm\"", "the 3rd user", "the_3rd_user_password");
  }

  /**
   * Create an appointment with invalid arguments -- an unparseable end time
   * 
   * add it to u3
   */
  @Test
  @Order(12)
  void addAppointmentWithInvalidFieldCase5() throws ServletException, IOException {
    String owner = "the 3rd user";
    String description = "ffsdfwe";
    String begin = "6/28/2021 3:00 pm";
    String end = "6/28/2021 5:00pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Unparseable date: \"6/28/2021 5:00pm\"", "the 3rd user", "the_3rd_user_password");
  }

  /**
   * Create an appointment with invalid arguments -- begin time is later than end
   * time
   * 
   * add it to u3
   */
  @Test
  @Order(13)
  void addAppointmentWithInvalidFieldCase6() throws ServletException, IOException {
    String owner = "the 3rd user";
    String description = "ffsdfwe";
    String end = "6/28/2021 3:00 pm";
    String begin = "6/28/2021 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Begin time is not early than end time of appointment, begin at 6/28/2021 5:00 pm, but end at 6/28/2021 3:00 pm",
        "the 3rd user", "the_3rd_user_password");
  }

  /**
   * For "the first user", data should be affected after request with invalid
   * arguments.
   */
  @Test
  @Order(14)
  void dataShouldNotBeAffectedAfterError() throws ServletException, IOException {
    testGetExistingAppointments("the first user", null, null, "the first user", "the_first_user_password", 0, 1);
  }

  /**
   * For "the second user", data should be affected after request with invalid
   * arguments.
   */
  @Test
  @Order(15)
  void dataShouldNotBeAffectedAfterErrorCase2() throws ServletException, IOException {
    testGetExistingAppointments("the 2nd user", null, null, "the 2nd user", "the_2nd_user_password", 0, 1);
  }

  /**
   * After some error handling, the insertion still functions well for
   * well-formatted input. Add appointments with valid arguments to "the first
   * user"
   * 
   * a13 = new Appointment(Helper.validateAndParseDate("7/30/2021 6:00 am"),
   * Helper.validateAndParseDate("7/30/2021 7:00 am"), "a13");
   */
  @Test
  @Order(16)
  void canStillAddAppointmentsAfterError() throws ServletException, IOException {
    String owner = "the first user";
    String description = "a13";
    String begin = "7/30/2021 6:00 am";
    String end = "7/30/2021 7:00 am";

    testAddAppointmentWithValidArgument(owner, description, begin, end, "the first user", "the_first_user_password");
  }

  /**
   * After some error handling, the insertion still functions well for
   * well-formatted input. Add appointments with valid arguments to "the 2nd user"
   * 
   * a23 = new Appointment(Helper.validateAndParseDate("7/30/2021 8:00 am"),
   * Helper.validateAndParseDate("7/30/2021 9:00 am"), "a23");
   */
  @Test
  @Order(17)
  void canStillAddAppointmentsAfterErrorCase2() throws ServletException, IOException {
    String owner = "the 2nd user";
    String description = "a23";
    String begin = "7/30/2021 8:00 am";
    String end = "7/30/2021 9:00 am";

    testAddAppointmentWithValidArgument(owner, description, begin, end, "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Get all appointments for "the first user"
   */
  @Test
  @Order(18)
  void getAllAgain() throws ServletException, IOException {
    testGetExistingAppointments("the first user", null, null, "the first user", "the_first_user_password", 0, 1, 2);
  }

  /**
   * Searching interval covers all appointments added to "the first user" so far.
   */
  @Test
  @Order(19)
  void getSearchBeginWithinIntervalCase1() throws ServletException, IOException {
    testGetExistingAppointments("the first user", "7/29/2021 4:20 pm", "7/31/2021 9:05 pm", "the first user",
        "the_first_user_password",0, 1, 2);
  }

  @Test
  @Order(20)
  void getSearchBeginWithinIntervalCase2() throws ServletException, IOException {
    testGetExistingAppointments("the first user", "7/30/2021 3:50 am", "7/30/2021 5:20 am", "the first user",
        "the_first_user_password", 0, 1);
  }

  @Test
  @Order(21)
  void getSearchBeginWithinIntervalCase3() throws ServletException, IOException {
    testGetExistingAppointments("the first user", "7/30/2021 5:50 am", "7/30/2021 6:20 am", "the first user",
        "the_first_user_password", 2);
  }

  /**
   * An interval with existing owner, but non-existing appts
   */
  @Test
  @Order(22)
  void getSearchNonexistingCase1() throws ServletException, IOException {
    String begin = "6/27/2021 4:20 am";
    String end = "6/27/2021 9:20 am";
    String owner = "the first user";
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_NOT_FOUND,
        "No appointment found with owner " + owner + " that begins between " + begin + " and " + end, "the first user",
        "the_first_user_password");
  }

  /**
   * A non-existing owner with interval
   */
  @Test
  @Order(23)
  void getSearchNonexistingCase2() throws ServletException, IOException {
    String begin = "6/28/2021 4:20 am";
    String end = "6/28/2021 11:00 pm";
    String owner = "unittest1";
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_FORBIDDEN,
        "User \"" + owner + "\" is not a registered user", "unittest1", "unittest1_password");
  }

  /**
   * A non-existing owner without interval
   */
  @Test
  @Order(24)
  void getSearchNonexistingCase3() throws ServletException, IOException {
    String owner = "unittest1";
    testGetNonExistingAppointments(owner, null, null, HttpServletResponse.SC_FORBIDDEN,
        "User \"" + owner + "\" is not a registered user", "unittest1", "unittest1_password");
  }

  /**
   * Invalid argument for searching -- the lower bound is later than higher bound
   * for time interval
   */
  @Test
  @Order(25)
  void getSearchNonexistingCase4() throws ServletException, IOException {
    String end = "6/27/2021 4:20 am";
    String begin = "6/27/2021 9:20 am";
    String owner = "the 2nd user";

    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The lowerbound of the time that appointments begin at to search, " + begin + " is after the upperbound, "
            + end,
        "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Invalid argument for searching -- unparseable date
   */
  @Test
  @Order(26)
  void getSearchNonexistingCase5() throws ServletException, IOException {
    String end = "6-27-2021 4:20 am";
    String begin = "6/27/2021 9:20 am";
    String owner = "the 2nd user";

    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Unparseable date: \"6-27-2021 4:20 am\"", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Missing end
   */
  @Test
  @Order(27)
  void getSearchNonexistingCase6() throws ServletException, IOException {
    String end = "";
    String begin = "6/27/2021 9:20 am";
    String owner = "the 2nd user";

    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"end\" is missing", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Missing begin
   */
  @Test
  @Order(28)
  void getSearchNonexistingCase7() throws ServletException, IOException {
    String begin = "";
    String end = "6/27/2021 9:20 am";
    String owner = "the 2nd user";

    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"start\" is missing", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Missing owner
   */
  @Test
  @Order(29)
  void getSearchNonexistingCase8() throws ServletException, IOException {
    String begin = "";
    String end = "";
    String owner = "";

    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"owner\" is missing", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Missing owner
   */
  @Test
  @Order(30)
  void addNonexistingCase1() throws ServletException, IOException {
    String end = "6/27/2021 4:20 am";
    String begin = "6/27/2021 9:20 am";
    String description = "ddd";
    String owner = null;

    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"owner\" is missing", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Missing description
   */
  @Test
  @Order(31)
  void addNonexistingCase2() throws ServletException, IOException {
    String end = "6/27/2021 4:20 am";
    String begin = "6/27/2021 9:20 am";
    String description = null;
    String owner = "the 2nd user";

    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"description\" is missing", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Missing start
   */
  @Test
  @Order(32)
  void addNonexistingCase3() throws ServletException, IOException {
    String end = "6/27/2021 4:20 am";
    String begin = null;
    String description = "ddd";
    String owner = "the 2nd user";

    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"start\" is missing", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * Missing end
   */
  @Test
  @Order(33)
  void addNonexistingCase4() throws ServletException, IOException {
    String end = null;
    String begin = "6/27/2021 4:20 am";
    String description = "ddd";
    String owner = "the 2nd user";

    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"end\" is missing", "the 2nd user", "the_2nd_user_password");
  }

  /**
   * The username does not match login credential for searching
   */
  @Test
  @Order(34)
  void invalidArgsForSearchingCase1() throws ServletException, IOException {
    String begin = "6-27-2021 4:20 am";
    String end = "6/27/2021 9:20 am";
    String owner = "the 3rd user";

    testGetNonExistingAppointments(owner, begin, end, 401, "Invalid credential for user \"" + owner + "\"",
        "the 2nd user", "the_2nd_user_password");
  }

  /**
   * The username does not match login credential for get all appointments
   */
  @Test
  @Order(35)
  void invalidArgsForSearchingCase2() throws ServletException, IOException {
    String owner = "the 3rd user";

    testGetNonExistingAppointments(owner, null, null, 401, "Invalid credential for user \"" + owner + "\"",
        "the 2nd user", "the_2nd_user_password");
  }

  /**
   * More test on invalid argument for searching
   */
  @Test
  @Disabled
  @Order(36)
  void testWithMalformattedFile() throws ServletException, IOException {
    String owner = "the 3rd user";
    createFileWithText("sdfkljsadhflakshdfsd", owner);

    testGetNonExistingAppointments(owner, null, null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
        "File in storage is malformatted: " + "End of file reached before the field been parsed completely",
        "the 3rd user", "the_3rd_user_password");
  }

}
