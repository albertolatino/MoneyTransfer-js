package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Transaction;
import it.polimi.tiw.dao.TransactionDAO;
import it.polimi.tiw.utils.ConnectionHandler;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/GetAccountDetails")
public class GetAccountDetails extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public GetAccountDetails() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // If the user is not logged in (not present in session) redirect to the login
        String loginpath = getServletContext().getContextPath() + "/index.html";
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            response.sendRedirect(loginpath);
            return;
        }

        // get and check params
        int accountId;
        try {
            accountId = Integer.parseInt(request.getParameter("accountid"));
        } catch (NumberFormatException | NullPointerException e) {
            // only for debugging e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
        }


        //todo passare account a questa servlet in modo che venga passato a template account details
        //User user = (User) session.getAttribute("user");
        TransactionDAO transactionDAO = new TransactionDAO(connection);
        List<Transaction> transactions;
        try {
            transactions = transactionDAO.findTransactionsByAccount(accountId);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover transactions");
            return;
        }

        request.getSession().setAttribute("accountId", accountId);

        // Redirect to the Home page and add missions to the parameters
        String path = "/WEB-INF/AccountDetails.html";
        ServletContext servletContext = getServletContext();
        //final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        //ctx.setVariable("transactions", transactions);
        //templateEngine.process(path, ctx, response.getWriter());
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
