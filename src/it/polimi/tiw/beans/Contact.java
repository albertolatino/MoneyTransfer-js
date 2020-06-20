package it.polimi.tiw.beans;

import java.util.ArrayList;

public class Contact {

    private String ownerUsername;
    private String contactUsername;
    private final ArrayList<Integer> contactAccounts;

    public Contact() {
        contactAccounts = new ArrayList<>();
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getContactUsername() {
        return contactUsername;
    }

    public void setContactUsername(String contactUsername) {
        this.contactUsername = contactUsername;
    }

    public ArrayList<Integer> getContactAccounts() {
        return contactAccounts;
    }

    public void addContactAccounts(int contactAccounts) {
        this.contactAccounts.add(contactAccounts);
    }
}
