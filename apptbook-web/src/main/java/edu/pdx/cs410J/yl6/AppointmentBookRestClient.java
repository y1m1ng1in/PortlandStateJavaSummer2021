package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * A helper class for accessing the rest client.
 */
public class AppointmentBookRestClient extends HttpRequestHelper {
  private static final String WEB_APP = "apptbook";
  private static final String SERVLET = "appointments";

  private final String url;

  /**
   * Creates a client to the appointment book REST service running on the given
   * host and port
   *
   * @param hostName The name of the host
   * @param port     The port
   */
  public AppointmentBookRestClient(String hostName, int port) {
    this.url = String.format("http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET);
  }

  /**
   * Parse the content of a {@link Response} instance as an
   * {@link AppointmentBook}
   * 
   * @param response a <code>Response</code> instance
   * @return a json string contains appointments
   * @throws ParserException If content of <code>response</code> cannot be parsed
   *                         completely and successfully
   * @throws IOException     If an input or output exception occurs
   */
  private String parseAppointmentBookFromResponse(Response response)
      throws ParserException, IOException {
    return response.getContent();
  }

  /**
   * Return an {@link AppointmentBook} instance from the server that contains all
   * the appointments with <code>owner</code>
   * 
   * @param owner the owner name of the appointment book
   * @return a json string contains appointments
   * @throws IOException     HTTP request exception occurs during communicating
   *                         with server
   * @throws ParserException Cannot successfully parse an appointment book from
   *                         what is returned from the server
   */
  public String getAppointmentBookByOwner(String owner) throws IOException, ParserException {
    Response response = get(this.url, Map.of("owner", owner));
    throwExceptionIfNotOkayHttpStatus(response);
    return parseAppointmentBookFromResponse(response);
  }

  /**
   * Return an {@link AppointmentBook} instance from the server that contains all
   * the appointments with <code>owner</code>, each appointment has begin time
   * within <code>from</code> to <code>to</code>, which are lowerbound and
   * upperbound of time interval for appointment's begin time.
   * 
   * @param owner the name of owner
   * @param from  the lowerbound of time interval for appointment's begin time
   * @param to    the upperbound of time interval for appointment's begin time
   * @return a json string contains appointments
   * @throws IOException     If an input or output exception occurs
   * @throws ParserException If content of <code>response</code> cannot be parsed
   *                         completely and successfully
   */
  public String getAppointmentsByOwnerWithBeginInterval(String owner, String from, String to)
      throws IOException, ParserException {
    Response response = get(this.url, Map.of("owner", owner, "start", from, "end", to));
    throwExceptionIfNotOkayHttpStatus(response);
    return parseAppointmentBookFromResponse(response);
  }

  /**
   * Add a new appointment to server
   * 
   * @param owner       the name of owner
   * @param description the description of appointment
   * @param begin       the begin time of appointment
   * @param end         the end time of appointment
   * @throws IOException If an input or output exception occurs
   */
  public void addAppointment(String owner, String description, String begin, String end) throws IOException {
    String postUrl = this.url + "?owner=" + URLEncoder.encode(owner, StandardCharsets.UTF_8);
    Response response = post(postUrl, Map.of("description", description, "start", begin, "end", end));
    throwExceptionIfNotOkayHttpStatus(response);
  }

  /**
   * Check status code of {@link Response} instance, if it is not 200, then throw
   * a {@link RestException}
   * 
   * @param response the <code>Response</code> instance to check status code
   * @return <code>response</code> itself
   */
  private Response throwExceptionIfNotOkayHttpStatus(Response response) {
    int code = response.getCode();
    if (code != HTTP_OK) {
      String message = response.getContent();
      throw new RestException(code, message);
    }
    return response;
  }

}
