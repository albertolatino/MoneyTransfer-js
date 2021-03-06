package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.beans.Contact;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.ContactDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@MultipartConfig
@WebServlet("/AddToContacts")
public class AddToContacts extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public AddToContacts() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // obtain and escape params
        String loginpath = getServletContext().getContextPath() + "/index.html";
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (session.isNew() || user == null) {
            response.sendRedirect(loginpath);
            return;
        }

        String owner = user.getUsername();

        if (owner == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Error in saving contacts");
            return;
        }

        String contactUsername = null;
        int contactAccount = -1;
        boolean isBadRequest;
        try {
            contactUsername = StringEscapeUtils.escapeJava(request.getParameter("contactUsername"));
            contactAccount = Integer.parseInt(request.getParameter("contactAccount"));

            isBadRequest = contactUsername.isEmpty() || contactAccount == -1;
        } catch (NumberFormatException | NullPointerException e) {
            isBadRequest = true;
            e.printStackTrace();
        }
        if (isBadRequest) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Incorrect or missing param values");
            return;
        }


        // query db to authenticate for user
        ContactDAO contactDAO = new ContactDAO(connection);
        try {

            if (owner.equals(contactUsername)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("You cannot register yourself");
            } else if (contactDAO.existingContact(owner, contactAccount)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("This contact is already registered");
            } else {
                contactDAO.registerContact(owner, contactAccount);
                response.getWriter().println("Contact successfully added");
                response.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Internal server error, retry later");
        }

    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // If the user is not logged in (not present in session) redirect to the login
        String loginpath = getServletContext().getContextPath() + "/index.html";
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (session.isNew() || user == null) {
            response.sendRedirect(loginpath);
            return;
        }

        String owner = user.getUsername();

        ContactDAO contactDAO = new ContactDAO(connection);
        List<Contact> contacts;
        try {
            contacts = contactDAO.getContactList(owner);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Couldn't retrieve contacts");
            return;
        }

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(contacts);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
        response.setStatus(HttpServletResponse.SC_OK);

    }


    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
