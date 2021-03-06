package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Account;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AccountDAO;
import it.polimi.tiw.dao.TransactionDAO;
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

@MultipartConfig
@WebServlet("/CreateTransaction")
public class CreateTransaction extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Connection connection = null;

    public CreateTransaction() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // If the user is not logged in (not present in session) redirect to the login
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (session.isNew() || user == null) {
            String loginpath = getServletContext().getContextPath() + "/index.html";
            response.sendRedirect(loginpath);
            return;
        }

        // Get and parse all parameters from request
        boolean isBadRequest = false;

        String destinationUsername = null;
        Integer destinationAccountId = null;
        Double amount = null;
        String description = null;
        Integer originAccountId = null;

        try {

            destinationUsername = StringEscapeUtils.escapeJava(request.getParameter("recipient-username"));
            originAccountId = Integer.parseInt(request.getParameter("accountid"));
            destinationAccountId = Integer.parseInt(request.getParameter("recipient-accountid"));
            amount = Double.parseDouble(request.getParameter("amount"));
            description = StringEscapeUtils.escapeJava(request.getParameter("description"));

            isBadRequest = amount <= 0 || destinationUsername.isEmpty() || description.isEmpty();
        } catch (NumberFormatException | NullPointerException e) {
            isBadRequest = true;
            e.printStackTrace();
        }
        if (isBadRequest) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Incorrect or missing param values");
            return;
        }


        //account id origin (mine) + balance
        //account id destination + balance
        AccountDAO accountDAO = new AccountDAO(connection);
        TransactionDAO transactionDAO = new TransactionDAO(connection);
        Account origin;
        Account destination;
        boolean originUserOwnsAccount,destinationUserOwnsAccount;
        try {

            originUserOwnsAccount = accountDAO.userOwnsAccount(user.getUsername(), originAccountId);
            destinationUserOwnsAccount = accountDAO.userOwnsAccount(destinationUsername, destinationAccountId);

            origin = accountDAO.findAccountById(originAccountId);
            //destination returns null if recipient account doesn't exist
            destination = accountDAO.findAccountById(destinationAccountId);

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Couldn't retrieve accounts data");
            return;
        }

        /*
        transaction checks:
        existence destination (account)
        origin != destination (account)
        amount <= origin.balance (account)
        username owns destination account (user, account)
        */


        String errorMsg = "";

        if (destination == null) {
            errorMsg = "Destination account doesn't exist";
        } else if (!originUserOwnsAccount) {
            errorMsg = "You don't own the origin account";
        } else if (!destinationUserOwnsAccount) {
            errorMsg = "Username doesn't match the selected account";
        } else if (origin.getAccountId() == destination.getAccountId()) {
            errorMsg = "Origin and destination accounts must be different";
        } else if (amount > origin.getBalance()) {
            errorMsg = "Insufficient funds for this transaction";
        }


        //no errors in transaction -> create transaction
        if (errorMsg.equals("")) {

            // Create transaction in DB
            try {
                transactionDAO.createTransaction(originAccountId, destinationAccountId, amount, description);
                response.getWriter().print(originAccountId);
                response.setStatus(HttpServletResponse.SC_OK);

            } catch (SQLException e) {
                //e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Couldn't create transaction");
            }

        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println(errorMsg);
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
