package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {
    private Connection connection;

    public ContactDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves all saved contacts of a user
     * @param owner the owner of the contact list
     * @return list of all his saved contacts
     */
    public List<Contact> getContactList(String owner) throws SQLException {

        List<Contact> contacts = new ArrayList<>();
        Contact contact = null;
        String contactUsername;

        String query = "SELECT contactUsername, contactAccount FROM contact  WHERE ownerUsername = ? ORDER BY contactUsername";

        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setString(1, owner);

            try (ResultSet result = pstatement.executeQuery()) {

                while (result.next()) {
                     contactUsername = result.getString("contactUsername");

                    if(contact != null && contactUsername.equals(contact.getContactUsername())) {
                        //only add his account
                        contact.addContactAccounts(result.getInt("contactAccount"));
                    } else {
                        //if contactUsername != previous one

                        contact = new Contact(); //Create java Bean
                        contact.setOwnerUsername(owner);
                        contact.setContactUsername(result.getString("contactUsername"));
                        contact.addContactAccounts(result.getInt("contactAccount"));

                        contacts.add(contact);

                    }
                }
            }
        }
        return contacts;
    }


    public void registerContact(String ownerUsername, String contactUsername, int contactAccount) throws SQLException {

        String registerQuery = "INSERT into contact (ownerUsername, contactUsername, contactAccount) VALUES(?, ?, ?)";

        try (PreparedStatement pstatement = connection.prepareStatement(registerQuery)) {
            pstatement.setString(1, ownerUsername);
            pstatement.setString(2, contactUsername);
            pstatement.setInt(3, contactAccount);

            pstatement.executeUpdate();
        }
    }


    public boolean existingContact(String ownerUsername, String contactUsername, int contactAccount) throws SQLException {
        String existingContactQuery = "SELECT * FROM contact WHERE ownerUsername = ? AND contactUsername = ? AND contactAccount = ?";

        try (PreparedStatement pstatement = connection.prepareStatement(existingContactQuery)) {

            pstatement.setString(1, ownerUsername);
            pstatement.setString(2, contactUsername);
            pstatement.setInt(3, contactAccount);

            try (ResultSet result = pstatement.executeQuery()) {
                return result.next(); //returns true if contact exists
            }
        }
    }

}
