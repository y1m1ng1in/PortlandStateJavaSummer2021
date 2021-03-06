package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.yl6.database.AppointmentBookStorage;
import edu.pdx.cs410J.yl6.database.StorageException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * UserRegistrationServlet is the class that processes incoming HTTP requests for user new registration.
 */
public class UserRegistrationServlet extends HttpServletHelper {

    static final String USERNAME_PARAMETER = "username";
    static final String PASSWORD_PARAMETER = "password";
    static final String EMAIL_PARAMETER = "email";
    static final String ADDRESS_PARAMETER = "address";

    public UserRegistrationServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain");
        String[] requiredFields = {USERNAME_PARAMETER, PASSWORD_PARAMETER, EMAIL_PARAMETER, ADDRESS_PARAMETER};
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
            int result = this.storage.insertUser(user);
            if (result == AppointmentBookStorage.USERNAME_CONFLICT) {
                writeMessageAndSetStatus(response, username + " has already been registered",
                        HttpServletResponse.SC_BAD_REQUEST);
            } else if (result == AppointmentBookStorage.EMAIL_CONFLICT) {
                writeMessageAndSetStatus(response, email + " has already been registered",
                        HttpServletResponse.SC_BAD_REQUEST);
            } else {
                writeMessageAndSetStatus(response, "Add appointment " + user, HttpServletResponse.SC_OK);
            }
        } catch (StorageException e) {
            writeMessageAndSetStatus(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
