package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.beans.Transaction;
import it.polimi.tiw.dao.TransactionDAO;
import it.polimi.tiw.utils.ConnectionHandler;

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

@WebServlet("/GetAccountDetailsData")
public class GetAccountDetailsData extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public GetAccountDetailsData() {
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
        request.getSession().setAttribute("accountId", accountId);


        //todo passare account a questa servlet in modo che venga passato a template account details
        //User user = (User) session.getAttribute("user");
        TransactionDAO transactionDAO = new TransactionDAO(connection);
        List<Transaction> transactions;
        try {
            transactions = transactionDAO.findTransactionsByAccount(accountId);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Couldn't recover transactions");

            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover transactions");
            return;
        }

        //add accounts to the parameters

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(transactions);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);

    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
