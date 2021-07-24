package edu.pdx.cs410J.yl6;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class UserRegistrationServlet extends HttpServlet {

  static final String USERNAME_PARAMETER = "username";
  static final String PASSWORD_PARAMETER = "password";
  static final String EMAIL_PARAMETER = "email";
  static final String ADDRESS_PARAMETER = "address";

  private PlainTextAsStorage storage = new PlainTextAsStorage(".");

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("text/plain");
    String[] requiredFields = { USERNAME_PARAMETER, PASSWORD_PARAMETER, EMAIL_PARAMETER, ADDRESS_PARAMETER };
    String[] fields = new String[4];

    for (int i = 0; i < 4; ++i) {
      String value = getParameter(requiredFields[i], request);
      if (value == null) {
        missingRequiredParameter(response, requiredFields[i]);
        return;
      }
      fields[i] = value;
    }

    insertUser(response, fields[0], fields[1], fields[2], fields[3]);
  }
  
  private void insertUser(HttpServletResponse response, String username, String password, String email,
      String address) throws IOException {

    User user = new User(username, password, email, address);

    try {
      this.storage.insertUser(user);
      writeMessageAndSetStatus(response, "Add appointment " + user.toString(), HttpServletResponse.SC_OK);
    } catch (StorageException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  /**
   * Writes an error message about a missing parameter to the HTTP response, and
   * set HTTP status code to 400 using {@link HttpServletResponse#sendError}
   */
  private void missingRequiredParameter(HttpServletResponse response, String parameterName) throws IOException {
    String message = String.format("The required parameter \"%s\" is missing", parameterName);
    response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
  }

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
    PrintWriter pw = response.getWriter();
    pw.println(message);
    pw.flush();
    response.setStatus(status);
  }
}
