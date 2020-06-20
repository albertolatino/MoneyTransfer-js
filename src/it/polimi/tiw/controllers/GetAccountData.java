package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.tiw.beans.Account;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AccountDAO;
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

@WebServlet("/GetAccountData")
public class GetAccountData extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public GetAccountData() {
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

        User user = (User) session.getAttribute("user");
        AccountDAO accountDAO = new AccountDAO(connection);
        List<Account> accounts;
        //todo account.user != user, security bug
        try {
            accounts = accountDAO.findAccountsByUser(user.getUserId());
        } catch (SQLException e) {
            // for debugging only e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Couldn't recover accounts");
            return;
        }

        // Redirect to the Home page and add accounts to the parameters

        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(accounts);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
