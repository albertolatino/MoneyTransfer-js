package it.polimi.tiw.controllers;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/RegisterUser")
@MultipartConfig
public class RegisterUser extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public RegisterUser() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // obtain and escape params
        String username = null;
        String name = null;
        String surname = null;
        String email = null;
        String pwd = null;

        username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        name = StringEscapeUtils.escapeJava(request.getParameter("name"));
        surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
        pwd = StringEscapeUtils.escapeJava(request.getParameter("pwd"));
        email = StringEscapeUtils.escapeJava(request.getParameter("email"));
        if (username == null || pwd == null || username.isEmpty() || pwd.isEmpty() || email.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Credentials must be not null");
            return;
        }

        // query db to authenticate for user
        UserDAO userDao = new UserDAO(connection);
        try {

            if (userDao.existingEmail(email)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("This email is already associated to an account");
            } else if (userDao.existingUsername(username)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("This username is already taken");
            } else {
                userDao.registerUser(username, email, pwd, name, surname);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().print(username);
                response.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
        }

    }


    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
