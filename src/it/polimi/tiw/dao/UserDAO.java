package it.polimi.tiw.dao;

import it.polimi.tiw.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User checkCredentials(String username, String pwd) throws SQLException {
        String query = "SELECT  * FROM user  WHERE username = ? AND password =?";
        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setString(1, username);
            pstatement.setString(2, pwd);
            try (ResultSet result = pstatement.executeQuery();) {
                if (!result.isBeforeFirst()) // no results, credential check failed
                    return null;
                else {
                    result.next();
                    User user = new User();
                    user.setUserId(result.getInt("userId"));
                    user.setUsername(result.getString("username"));
                    user.setEmail(result.getString("email"));
                    user.setName(result.getString("name"));
                    user.setSurname(result.getString("surname"));
                    return user;
                }
            }
        }
    }


    public void registerUser(String username, String email, String password) throws SQLException {

        String existingUsernameQuery = "SELECT * FROM user WHERE username = ?";
        String existingEmailQuery = "SELECT * FROM user WHERE email = ?";


        String query = "INSERT into user (userId, username, email, password) VALUES(NULL, ?, ?, ?)";
        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setString(1, username);
            pstatement.setString(2, email);
            pstatement.setString(3, password);
            pstatement.executeUpdate();
        }
    }

}
