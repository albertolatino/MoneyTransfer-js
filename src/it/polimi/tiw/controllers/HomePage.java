package it.polimi.tiw.controllers;

import it.polimi.tiw.beans.Account;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AccountDAO;
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

@WebServlet("/Home")
public class HomePage extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public HomePage() {
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
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover accounts");
            return;
        }

        // Redirect to the Home page and add accounts to the parameters
        String path = "/WEB-INF/Home.html";
        ServletContext servletContext = getServletContext();
        //final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        //ctx.setVariable("accounts", accounts);
        //templateEngine.process(path, ctx, response.getWriter());
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
