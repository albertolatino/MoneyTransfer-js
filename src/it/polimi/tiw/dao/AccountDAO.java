package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AccountDAO {
    private Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }


    public List<Account> findAccountsByUser(String username) throws SQLException {

        List<Account> accounts = new ArrayList<>();

        String query = "SELECT * from account where username = ? ORDER BY balance DESC";
        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setString(1, username);
            try (ResultSet result = pstatement.executeQuery();) {
                while (result.next()) {
                    Account account = new Account();//Create java Bean
                    account.setUsername(result.getString("username"));
                    account.setAccountId(result.getInt("accountId"));
                    account.setBalance((result.getDouble("balance")));
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }


    public Account findAccountById(int accountId) throws SQLException {

        Account account; //Create java Bean

        String query = "SELECT * from account where accountId = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setInt(1, accountId);

            try (ResultSet result = pstatement.executeQuery()) {
                result.first();
                account = new Account();
                account.setUsername(result.getString("username"));
                account.setAccountId(result.getInt("accountId"));
                account.setBalance((result.getDouble("balance")));
            } catch (SQLException e) {
                System.out.println("invalid accountId");
                return null;
            }
        }
        return account;

    }

}
