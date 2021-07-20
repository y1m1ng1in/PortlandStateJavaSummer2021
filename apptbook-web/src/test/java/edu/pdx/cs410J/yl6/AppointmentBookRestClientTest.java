package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.web.HttpRequestHelper;
import edu.pdx.cs410J.web.HttpRequestHelper.Response;
import edu.pdx.cs410J.web.HttpRequestHelper.RestException;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Map;

public class AppointmentBookRestClientTest {

  private static final String WEB_APP = "apptbook";
  private static final String SERVLET = "appointments";

  class MyClassMock extends AppointmentBookRestClient {

    private String mockContent;
    private int mockSc;

    public MyClassMock(String hostName, int port, String mockContent, int mockSc) {
      super(hostName, port);
      this.mockContent = mockContent;
      this.mockSc = mockSc;
    }

    @Override
    public Response get(String urlString, Map<String, String> parameters) {
      Response mockedResponse = mock(Response.class);
      when(mockedResponse.getCode()).thenReturn(this.mockSc);
      when(mockedResponse.getContent()).thenReturn(this.mockContent);
      return mockedResponse;
    }

    @Override
    public Response post(String urlString, Map<String, String> parameters) {
      Response mockedResponse = mock(Response.class);
      when(mockedResponse.getCode()).thenReturn(this.mockSc);
      when(mockedResponse.getContent()).thenReturn(this.mockContent);
      return mockedResponse;
    }
  }

  @Test
  void testGetNormalAll() throws IOException, ParserException {
    String hostName = "localhost";
    int port = 8080;
    String owner = "unittest";

    AppointmentBookRestClient mockedClient = new MyClassMock(hostName, port,
        "unittest&7/18/2021 9:00 pm#7/18/2021 11:59 pm#another test description&", 200);
    AppointmentBook<Appointment> book = mockedClient.getAppointmentBookByOwner(owner);
    assertThat(book.getOwnerName(), equalTo(owner));
  }

  @Test
  void testGetNormalInterval() throws IOException, ParserException {
    String hostName = "localhost";
    int port = 8080;
    String from = "7/10/2021 8:00 am";
    String to = "7/20/2021 8:00 am";
    String owner = "unittest";

    AppointmentBookRestClient mockedClient = new MyClassMock(hostName, port,
        "unittest&7/18/2021 9:00 pm#7/18/2021 11:59 pm#another test description&", 200);
    AppointmentBook<Appointment> book = mockedClient.getAppointmentsByOwnerWithBeginInterval(owner, from, to);
    assertThat(book.getOwnerName(), equalTo(owner));
  }

  @Test
  void testNormalPost() throws IOException, ParserException {
    String hostName = "localhost";
    int port = 8080;
    String begin = "7/10/2021 8:00 am";
    String end = "7/20/2021 8:00 am";
    String owner = "unittest";
    String description = "description";

    AppointmentBookRestClient mockedClient = new MyClassMock(hostName, port, "add appointment", 200);
    assertDoesNotThrow(() -> mockedClient.addAppointment(owner, description, begin, end));
  }

  @Test
  void testThrowExceptionWhenNot200StatusCode() throws IOException, ParserException {
    String hostName = "localhost";
    int port = 8080;
    String owner = "unittest";

    AppointmentBookRestClient mockedClient = new MyClassMock(hostName, port, "Not found", 404);
    assertThrows(RestException.class, () -> mockedClient.getAppointmentBookByOwner(owner));
  }

  @Test
  void testThrowExceptionWhenNot200StatusCodeCase2() throws IOException, ParserException {
    String hostName = "localhost";
    int port = 8080;
    String from = "7/10/2021 8:00 am";
    String to = "7/20/2021 8:00 am";
    String owner = "unittest";

    AppointmentBookRestClient mockedClient = new MyClassMock(hostName, port, "Not found", 404);
    assertThrows(RestException.class, () -> mockedClient.getAppointmentsByOwnerWithBeginInterval(owner, from, to));
  }
}
