package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.ContactDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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


        String boolString = StringEscapeUtils.escapeJava(request.getParameter("addToContacts"));

        String owner = StringEscapeUtils.escapeJava(request.getParameter("username"));
        String contactUsername = (String) getServletContext().getAttribute("contactUsername");
        Integer contactAccount = (Integer) getServletContext().getAttribute("contactAccount");

        if (owner == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Error in saving contacts");
            return;
        }

        if (boolString.equals("true")) {

            // query db to authenticate for user
            ContactDAO contactDAO = new ContactDAO(connection);
            try {

                if(contactDAO.existingContact(owner, contactUsername, contactAccount)) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("This contact is already registered");
                } else {
                    contactDAO.registerContact(owner, contactUsername, contactAccount);
                    response.setStatus(HttpServletResponse.SC_OK);
                }

            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Internal server error, retry later");
                return;
            }
        }
        // If the user exists, add info to the session and go to home page, otherwise
        // return an error status code and message
        //todo send confirmation
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
