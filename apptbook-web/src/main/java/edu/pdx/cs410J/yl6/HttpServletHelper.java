package edu.pdx.cs410J.yl6;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.pdx.cs410J.yl6.database.AppointmentBookStorage;
import edu.pdx.cs410J.yl6.database.PostgresqlDatabase;
import edu.pdx.cs410J.yl6.database.StorageException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

/**
 * HttpServletHelper class collects common methods used in its derived servlets.
 */
public abstract class HttpServletHelper extends HttpServlet {

    protected AppointmentBookStorage storage;

    public HttpServletHelper() {
        this.storage = PostgresqlDatabase.getDatabase();
    }

    /**
     * Check <code>username</code> credential stored via cookie in
     * <code>request</code>. If the username stored in credential is not same as
     * <code>username</code>; or either <code>username</code> or username stored in
     * credential is not a registered user; or in credential the password is wrong,
     * the authentication failed, and the function returns <code>false</code>.
     *
     * @param request  {@link HttpServletRequest} instance of incoming http request
     * @param response {@link HttpServletResponse} instance of http response to be
     *                 sent
     * @param username the username to check credential
     * @return <code>true</code> if authentication is not failed; <code>false</code>
     * otherwise
     * @throws IOException If an input or output exception occurs
     *                     <strong>Note</strong> currently the server uses basic authentication, which is not
     *                     enough. Still looking for more advanced method... then replace the
     *                     implementation in this method
     */
    protected boolean authenticateUser(HttpServletRequest request, HttpServletResponse response, String username)
            throws IOException {
        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length < 1) {
            writeMessageAndSetStatus(response, "Invalid credential for user \"" + username + "\"",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String decodedString = new String(Base64.getDecoder().decode(cookies[0].getValue()));
        String[] toCheck = decodedString.split(":");

        if (!toCheck[0].equals(username)) {
            writeMessageAndSetStatus(response, "Invalid credential for user \"" + username + "\"",
                    HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            User user = this.storage.getUserByUsername(username);
            if (user == null) {
                writeMessageAndSetStatus(response, "User \"" + username + "\" is not a registered user",
                        HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            if (!user.getPassword().equals(toCheck[1])) {
                writeMessageAndSetStatus(response, "Password is wrong", HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        } catch (StorageException e) {
            writeMessageAndSetStatus(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
        return true;
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
    protected void writeMessageAndSetStatus(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("text/json");
        response.setStatus(status);

        JsonObject object = new JsonObject();
        object.addProperty("message", message);
        object.addProperty("status", status);
        Gson gson = new Gson();
        String json = gson.toJson(object);

        PrintWriter pw = response.getWriter();
        pw.println(json);
        pw.flush();
    }

    /**
     * Writes an error message about a missing parameter to the HTTP response, and
     * set HTTP status code to 400 using {@link HttpServletResponse#sendError}
     */
    protected void missingRequiredParameter(HttpServletResponse response, String parameterName) throws IOException {
        String message = String.format("The required parameter \"%s\" is missing", parameterName);
        writeMessageAndSetStatus(response, message, HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @return <code>null</code> if the value of the parameter is <code>null</code>
     * or is the empty string
     */
    protected String getParameter(String name, HttpServletRequest request) {
        String value = request.getParameter(name);
        if (value == null || "".equals(value)) {
            return null;
        } else {
            return value;
        }
    }

}
