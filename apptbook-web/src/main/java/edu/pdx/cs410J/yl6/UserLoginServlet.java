package edu.pdx.cs410J.yl6;

import edu.pdx.cs410J.yl6.database.StorageException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

public class UserLoginServlet extends HttpServletHelper {

    static final String USERNAME_PARAMETER = "username";
    static final String PASSWORD_PARAMETER = "password";

    public UserLoginServlet() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("text/plain");
        String[] requiredFields = {USERNAME_PARAMETER, PASSWORD_PARAMETER};
        String[] fields = new String[2];

        for (int i = 0; i < 2; ++i) {
            String value = getParameter(requiredFields[i], request);
            if (value == null) {
                missingRequiredParameter(response, requiredFields[i]);
                return;
            }
            fields[i] = value;
        }

        loginUser(response, fields[0], fields[1]);
    }

    private void loginUser(HttpServletResponse response, String username, String password) throws IOException {
        try {
            User user = this.storage.getUserByUsername(username);
            if (user == null) {
                // cannot find user
                response.sendError(HttpServletResponse.SC_FORBIDDEN, username + " is not an registered user");
            } else if (!user.getPassword().equals(password)) {
                // password is not correct
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "password is wrong");
            } else {
                // registered user with correct password
                String toEncode = username + ":" + password;
                Cookie cookie = new Cookie("Authentication", Base64.getEncoder().encodeToString(toEncode.getBytes()));
                cookie.setMaxAge(-1);
                response.addCookie(cookie);
                writeMessageAndSetStatus(response, "Login successfully", HttpServletResponse.SC_OK);
            }
        } catch (StorageException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
