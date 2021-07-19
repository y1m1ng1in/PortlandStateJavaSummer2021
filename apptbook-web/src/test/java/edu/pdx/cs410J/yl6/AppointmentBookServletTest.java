package edu.pdx.cs410J.yl6;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;

import edu.pdx.cs410J.ParserException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

/**
 * A unit test for the {@link AppointmentBookServlet}. It uses mockito to
 * provide mock http requests and responses.
 */
@TestMethodOrder(OrderAnnotation.class)
public class AppointmentBookServletTest {

  void createFileWithText(String content, String owner) throws IOException {
    File f = new File(owner + ".txt");
    Writer writer = new FileWriter(f);
    writer.write(content);
    writer.flush();
    writer.close();
  }

  void testAddAppointmentWithValidArgument(String owner, String description, String begin, String end)
      throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("owner")).thenReturn(owner);
    when(request.getParameter("description")).thenReturn(description);
    when(request.getParameter("start")).thenReturn(begin);
    when(request.getParameter("end")).thenReturn(end);

    HttpServletResponse response = mock(HttpServletResponse.class);

    // Use a StringWriter to gather the text from multiple calls to println()
    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter, true);

    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);

    assertThat(stringWriter.toString(), containsString("Add appointment "));

    // Use an ArgumentCaptor when you want to make multiple assertions against the
    // value passed to the mock
    ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
    verify(response).setStatus(statusCode.capture());

    assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));
  }

  void testAddAppointmentWithInvalidArgument(String owner, String description, String begin, String end, int sc,
      String errorMessage) throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    if (owner != null)
      when(request.getParameter("owner")).thenReturn(owner);
    if (description != null)
      when(request.getParameter("description")).thenReturn(description);
    if (begin != null)
      when(request.getParameter("start")).thenReturn(begin);
    if (end != null)
      when(request.getParameter("end")).thenReturn(end);

    HttpServletResponse response = mock(HttpServletResponse.class);

    // Use a StringWriter to gather the text from multiple calls to println()
    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter, true);

    when(response.getWriter()).thenReturn(pw);

    servlet.doPost(request, response);

    verify(response).sendError(sc, errorMessage);
  }

  void testGetExistingAppointments(String owner, String start, String end, String expected)
      throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("owner")).thenReturn(owner);
    if (start != null && end != null) {
      when(request.getParameter("start")).thenReturn(start);
      when(request.getParameter("end")).thenReturn(end);
    }

    HttpServletResponse response = mock(HttpServletResponse.class);

    // Use a StringWriter to gather the text from multiple calls to println()
    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter, true);

    when(response.getWriter()).thenReturn(pw);

    servlet.doGet(request, response);
    assertThat(stringWriter.toString(), equalTo(expected));

    // Use an ArgumentCaptor when you want to make multiple assertions against the
    // value passed to the mock
    ArgumentCaptor<Integer> statusCode = ArgumentCaptor.forClass(Integer.class);
    verify(response).setStatus(statusCode.capture());

    assertThat(statusCode.getValue(), equalTo(HttpServletResponse.SC_OK));
  }

  void testGetNonExistingAppointments(String owner, String start, String end, int sc, String expected)
      throws ServletException, IOException {
    AppointmentBookServlet servlet = new AppointmentBookServlet();

    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("owner")).thenReturn(owner);
    if (start != null && end != null) {
      when(request.getParameter("start")).thenReturn(start);
      when(request.getParameter("end")).thenReturn(end);
    }

    HttpServletResponse response = mock(HttpServletResponse.class);

    // Use a StringWriter to gather the text from multiple calls to println()
    StringWriter stringWriter = new StringWriter();
    PrintWriter pw = new PrintWriter(stringWriter, true);

    when(response.getWriter()).thenReturn(pw);

    servlet.doGet(request, response);

    verify(response).sendError(sc, expected);
  }

  /**
   * Add appointments with valid arguments.
   */
  @Test
  @Order(1)
  void addOneWordToDictionary() throws ServletException, IOException {
    String owner = "unittest";
    String description = "test description";
    String begin = "7/18/2021 9:00 pm";
    String end = "7/18/2021 10:00 pm";

    testAddAppointmentWithValidArgument(owner, description, begin, end);
  }

  /**
   * Add appointments with valid arguments.
   */
  @Test
  @Order(2)
  void addAnotherWordToDictionary() throws ServletException, IOException {
    String owner = "unittest";
    String description = "another test description";
    String begin = "7/18/2021 9:00 pm";
    String end = "7/18/2021 11:59 pm";

    testAddAppointmentWithValidArgument(owner, description, begin, end);
  }

  /**
   * Add appointments with valid arguments to another owner
   */
  @Test
  @Order(3)
  void addAppointmentWithAnotherOwner() throws ServletException, IOException {
    String owner = "another unittest";
    String description = "test description for another unittest";
    String begin = "6/18/2021 9:00 pm";
    String end = "6/18/2021 11:59 pm";

    testAddAppointmentWithValidArgument(owner, description, begin, end);
  }

  /**
   * Add appointments with valid arguments to another owner
   */
  @Test
  @Order(4)
  void addAnotherAppointmentWithAnotherOwner() throws ServletException, IOException {
    String owner = "another unittest";
    String description = "another test description for another unittest";
    String begin = "6/28/2021 3:00 pm";
    String end = "6/28/2021 5:00 pm";

    testAddAppointmentWithValidArgument(owner, description, begin, end);
  }

  /**
   * Get all appointment with existing owner
   */
  @Test
  @Order(5)
  void getAllWithFirstOwner() throws ServletException, IOException {
    testGetExistingAppointments("unittest", null, null,
        "unittest&7/18/2021 9:00 pm#7/18/2021 10:00 pm#test description&7/18/2021 9:00 pm#7/18/2021 11:59 pm#another test description&");
  }

  /**
   * Get all appointment with existing owner
   */
  @Test
  @Order(6)
  void getAllWithSecondOwner() throws ServletException, IOException {
    testGetExistingAppointments("another unittest", null, null,
        "another unittest&6/18/2021 9:00 pm#6/18/2021 11:59 pm#test description for another unittest&6/28/2021 3:00 pm#6/28/2021 5:00 pm#another test description for another unittest&");
  }

  /**
   * Create an appointment with invalid arguments
   */
  @Test
  @Order(7)
  void addAppointmentWithInvalidField() throws ServletException, IOException {
    String owner = "another unittest";
    String description = "    ";
    String begin = "6/28/2021 3:00 pm";
    String end = "6/28/2021 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Field description should not be empty");
  }

  @Test
  @Order(8)
  void addAppointmentWithInvalidFieldCase2() throws ServletException, IOException {
    String owner = "  ";
    String description = "ffsdfwe";
    String begin = "6/28/2021 3:00 pm";
    String end = "6/28/2021 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Field owner should not be empty");
  }

  @Test
  @Order(9)
  void addAppointmentWithInvalidFieldCase3() throws ServletException, IOException {
    String owner = "another unittest";
    String description = "ffsdfwe";
    String begin = "6-28-2021 3:00 pm";
    String end = "6/28/2021 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Unparseable date: \"6-28-2021 3:00 pm\"");
  }

  @Test
  @Order(10)
  void addAppointmentWithInvalidFieldCase4() throws ServletException, IOException {
    String owner = "another unittest";
    String description = "ffsdfwe";
    String begin = "6/28/2021 3:00 pm";
    String end = "6/28/XXXX 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Unparseable date: \"6/28/XXXX 5:00 pm\"");
  }

  @Test
  @Order(11)
  void addAppointmentWithInvalidFieldCase5() throws ServletException, IOException {
    String owner = "another unittest";
    String description = "ffsdfwe";
    String begin = "6/28/2021 3:00 pm";
    String end = "6/28/2021 5:00pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Unparseable date: \"6/28/2021 5:00pm\"");
  }

  @Test
  @Order(12)
  void addAppointmentWithInvalidFieldCase6() throws ServletException, IOException {
    String owner = "another unittest";
    String description = "ffsdfwe";
    String end = "6/28/2021 3:00 pm";
    String begin = "6/28/2021 5:00 pm";
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Begin time is not early than end time of appointment, begin at 6/28/2021 5:00 pm, but end at 6/28/2021 3:00 pm");
  }

  /**
   * Data should be affected after request with invalid arguments.
   */
  @Test
  @Order(13)
  void dataShouldNotBeAffectedAfterError() throws ServletException, IOException {
    testGetExistingAppointments("unittest", null, null,
        "unittest&7/18/2021 9:00 pm#7/18/2021 10:00 pm#test description&7/18/2021 9:00 pm#7/18/2021 11:59 pm#another test description&");
  }

  /**
   * Data should be affected after request with invalid arguments.
   */
  @Test
  @Order(14)
  void dataShouldNotBeAffectedAfterErrorCase2() throws ServletException, IOException {
    testGetExistingAppointments("another unittest", null, null,
        "another unittest&6/18/2021 9:00 pm#6/18/2021 11:59 pm#test description for another unittest&6/28/2021 3:00 pm#6/28/2021 5:00 pm#another test description for another unittest&");
  }

  @Test
  @Order(15)
  void canStillAddAppointmentsAfterError() throws ServletException, IOException {
    String owner = "unittest";
    String description = "this is the description for test15";
    String begin = "6/28/2021 4:00 pm";
    String end = "6/28/2021 6:00 pm";

    testAddAppointmentWithValidArgument(owner, description, begin, end);
  }

  @Test
  @Order(16)
  void canStillAddAppointmentsAfterErrorCase2() throws ServletException, IOException {
    String owner = "unittest";
    String description = "this is the description for test16";
    String begin = "6/28/2021 4:30 pm";
    String end = "6/28/2021 8:00 pm";

    testAddAppointmentWithValidArgument(owner, description, begin, end);
  }

  @Test
  @Order(17)
  void getAllAgain() throws ServletException, IOException {
    testGetExistingAppointments("unittest", null, null,
        "unittest&" + "6/28/2021 4:00 pm#6/28/2021 6:00 pm#this is the description for test15&"
            + "6/28/2021 4:30 pm#6/28/2021 8:00 pm#this is the description for test16&"
            + "7/18/2021 9:00 pm#7/18/2021 10:00 pm#test description&"
            + "7/18/2021 9:00 pm#7/18/2021 11:59 pm#another test description&");
  }

  @Test
  @Order(17)
  void getSearchBeginWithinIntervalCase1() throws ServletException, IOException {
    testGetExistingAppointments("unittest", "6/28/2021 4:20 pm", "7/18/2021 9:05 pm",
        "unittest&" + "6/28/2021 4:30 pm#6/28/2021 8:00 pm#this is the description for test16&"
            + "7/18/2021 9:00 pm#7/18/2021 10:00 pm#test description&"
            + "7/18/2021 9:00 pm#7/18/2021 11:59 pm#another test description&");
  }

  @Test
  @Order(18)
  void getSearchBeginWithinIntervalCase2() throws ServletException, IOException {
    testGetExistingAppointments("unittest", "7/18/2021 4:20 am", "7/18/2021 11:00 pm",
        "unittest&" + "7/18/2021 9:00 pm#7/18/2021 10:00 pm#test description&"
            + "7/18/2021 9:00 pm#7/18/2021 11:59 pm#another test description&");
  }

  @Test
  @Order(18)
  void getSearchBeginWithinIntervalCase3() throws ServletException, IOException {
    testGetExistingAppointments("unittest", "6/28/2021 4:20 am", "6/28/2021 11:00 pm",
        "unittest&" + "6/28/2021 4:00 pm#6/28/2021 6:00 pm#this is the description for test15&"
            + "6/28/2021 4:30 pm#6/28/2021 8:00 pm#this is the description for test16&");
  }

  /**
   * An interval with existing owner, but non-existing appts
   */
  @Test
  @Order(19)
  void getSearchNonexistingCase1() throws ServletException, IOException {
    String begin = "6/27/2021 4:20 am";
    String end = "6/27/2021 9:20 am";
    String owner = "unittest";
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_NOT_FOUND,
        "No appointment found with owner " + owner + " that begins between " + begin + " and " + end);
  }

  /**
   * A non-existing owner with interval
   */
  @Test
  @Order(20)
  void getSearchNonexistingCase2() throws ServletException, IOException {
    String begin = "6/28/2021 4:20 am";
    String end = "6/28/2021 11:00 pm";
    String owner = "unittest1";
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_NOT_FOUND,
        "No appointment found with owner " + owner + " that begins between " + begin + " and " + end);
  }

  /**
   * A non-existing owner without interval
   */
  @Test
  @Order(21)
  void getSearchNonexistingCase3() throws ServletException, IOException {
    String owner = "unittest1";
    testGetNonExistingAppointments(owner, null, null, HttpServletResponse.SC_NOT_FOUND,
        "No appointment found with owner " + owner);
  }

  /**
   * Invalid argument for searching
   */
  @Test
  @Order(22)
  void getSearchNonexistingCase4() throws ServletException, IOException {
    String end = "6/27/2021 4:20 am";
    String begin = "6/27/2021 9:20 am";
    String owner = "unittest";
    
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The lowerbound of the time that appointments begin at to search, " + begin + " is after the upperbound, "
            + end);
  }

  /**
   * Invalid argument for searching
   */
  @Test
  @Order(23)
  void getSearchNonexistingCase5() throws ServletException, IOException {
    String end = "6-27-2021 4:20 am";
    String begin = "6/27/2021 9:20 am";
    String owner = "unittest";
    
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Unparseable date: \"6-27-2021 4:20 am\"");
  }

  /**
   * Missing end
   */
  @Test
  @Order(24)
  void getSearchNonexistingCase6() throws ServletException, IOException {
    String end = "";
    String begin = "6/27/2021 9:20 am";
    String owner = "unittest";
    
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"end\" is missing");
  }

  /**
   * Missing begin
   */
  @Test
  @Order(25)
  void getSearchNonexistingCase7() throws ServletException, IOException {
    String begin = "";
    String end = "6/27/2021 9:20 am";
    String owner = "unittest";
    
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"start\" is missing");
  }

  /**
   * Missing owner
   */
  @Test
  @Order(26)
  void getSearchNonexistingCase8() throws ServletException, IOException {
    String begin = "";
    String end = "";
    String owner = "";
    
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"owner\" is missing");
  }
  
  /**
   * Missing owner 
   */
  @Test
  @Order(27)
  void addNonexistingCase1() throws ServletException, IOException {
    String end = "6/27/2021 4:20 am";
    String begin = "6/27/2021 9:20 am";
    String description = "ddd";
    String owner = null;
    
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"owner\" is missing");
  }

  /**
   * Missing description 
   */
  @Test
  @Order(28)
  void addNonexistingCase2() throws ServletException, IOException {
    String end = "6/27/2021 4:20 am";
    String begin = "6/27/2021 9:20 am";
    String description = null;
    String owner = "unittest";
    
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"description\" is missing");
  }

  /**
   * Missing description 
   */
  @Test
  @Order(29)
  void addNonexistingCase3() throws ServletException, IOException {
    String end = "6/27/2021 4:20 am";
    String begin = null;
    String description = "ddd";
    String owner = "unittest";
    
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"start\" is missing");
  }

  /**
   * Missing description 
   */
  @Test
  @Order(30)
  void addNonexistingCase4() throws ServletException, IOException {
    String end = null;
    String begin = "6/27/2021 4:20 am";
    String description = "ddd";
    String owner = "unittest";
    
    testAddAppointmentWithInvalidArgument(owner, description, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "The required parameter \"end\" is missing");
  }

  /**
   * More test on invalid argument for searching
   */
  @Test
  @Order(31)
  void invalidArgsForSearchingCase1() throws ServletException, IOException {
    String begin= "6-27-2021 4:20 am";
    String end = "6/27/2021 9:20 am";
    String owner = "    ";
    
    testGetNonExistingAppointments(owner, begin, end, HttpServletResponse.SC_BAD_REQUEST,
        "Field owner should not be empty");
  }

  /**
   * More test on invalid argument for searching
   */
  @Test
  @Order(32)
  void invalidArgsForSearchingCase2() throws ServletException, IOException {
    String owner = "    ";
    
    testGetNonExistingAppointments(owner, null, null, HttpServletResponse.SC_BAD_REQUEST,
        "Field owner should not be empty");
  }

  /**
   * More test on invalid argument for searching
   */
  @Test
  @Order(33)
  void testWithMalformattedFile() throws ServletException, IOException {
    String owner = "unittest";
    createFileWithText("sdfkljsadhflakshdfsd", owner);
    
    testGetNonExistingAppointments(owner, null, null, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
        "File in storage is malformatted: " + "End of file reached before owner been parsed completely");
  }


}
