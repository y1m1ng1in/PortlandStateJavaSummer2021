package edu.pdx.cs410J.yl6;

import com.google.common.annotations.VisibleForTesting;

import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

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

  private AppointmentBook<Appointment> parseAppointmentBookFromResponse(Response response)
      throws ParserException, IOException {
    String toParse = response.getContent();
    Reader reader = new StringReader(toParse);
    TextAppointmentBookParser bookParser = new TextAppointmentBookParser(reader);
    TextAppointmentParser appointmentParser = new TextAppointmentParser(reader,
        new AppointmentValidator("M/d/yyyy h:m a"));
    TextParser parser = new TextParser(bookParser, appointmentParser);
    AppointmentBook<Appointment> parsed = parser.parse();
    reader.close();
    return parsed;
  }

  /**
   * Return an appointment book from the server
   * 
   * @param owner the owner name of the appointment book
   * @return an appointment book contains all the appointments <code>owner</code>
   * @throws IOException     HTTP request exception occurs during communicating
   *                         with server
   * @throws ParserException Cannot successfully parse an appointment book from
   *                         what is returned from the server
   */
  public AppointmentBook<Appointment> getAppointmentBookByOwner(String owner) throws IOException, ParserException {
    Response response = get(this.url, Map.of("owner", owner));
    throwExceptionIfNotOkayHttpStatus(response);
    return parseAppointmentBookFromResponse(response);
  }

  public AppointmentBook<Appointment> getAppointmentsByOwnerWithBeginInterval(String owner, String from, String to)
      throws IOException, ParserException {
    Response response = get(this.url, Map.of("owner", owner, "start", from, "end", to));
    throwExceptionIfNotOkayHttpStatus(response);
    return parseAppointmentBookFromResponse(response);
  }

  public void addAppointment(String owner, String description, String begin, String end) throws IOException {
    Response response = post(this.url + "?owner=" + owner,
        Map.of("description", description, "start", begin, "end", end));
    throwExceptionIfNotOkayHttpStatus(response);
  }

  @VisibleForTesting
  Response postToMyURL(Map<String, String> dictionaryEntries) throws IOException {
    return post(this.url, dictionaryEntries);
  }

  private Response throwExceptionIfNotOkayHttpStatus(Response response) {
    int code = response.getCode();
    System.out.println(code);
    if (code != HTTP_OK) {
      String message = response.getContent();
      throw new RestException(code, message);
    }
    return response;
  }

}
