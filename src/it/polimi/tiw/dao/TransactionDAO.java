package it.polimi.tiw.dao;

import it.polimi.tiw.beans.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private Connection connection;

    public TransactionDAO(Connection connection) {
        this.connection = connection;
    }


    public List<Transaction> findTransactionsByAccount(int accountId) throws SQLException {

        List<Transaction> transactions = new ArrayList<>();

        String query = "SELECT * from transaction where originId = ? OR destinationId = ? ORDER BY date DESC";
        try (PreparedStatement pstatement = connection.prepareStatement(query)) {
            pstatement.setInt(1, accountId);
            pstatement.setInt(2, accountId);
            try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    Transaction transaction = new Transaction();//Create java Bean
                    transaction.setTransactionId(result.getInt("transactionId"));
                    transaction.setDate(result.getDate("date"));
                    transaction.setAmount((result.getDouble("amount")));
                    transaction.setOriginId(result.getInt("originId"));
                    transaction.setDestinationId(result.getInt("destinationId"));
                    transaction.setDescription(result.getString("description"));
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }


    public void createTransaction(int originId, int destinationId, double amount, String description)
            throws SQLException {

        String transactionQuery = "INSERT into transaction (transactionId, date, amount, originId, destinationId, description) VALUES(NULL, NOW(), ?, ?, ?, ?)";
        String originBalanceQuery = "UPDATE account SET balance = balance - ? WHERE accountId = ? AND balance >= 0";
        String destinationBalanceQuery = "UPDATE account SET balance = balance + ? WHERE accountId = ? AND balance >= 0";


        try (PreparedStatement insertTransaction = connection.prepareStatement(transactionQuery);
             PreparedStatement updateOriginBalance = connection.prepareStatement(originBalanceQuery);
             PreparedStatement updateDestinationBalance = connection.prepareStatement(destinationBalanceQuery)) {

            connection.setAutoCommit(false);

            insertTransaction.setDouble(1, amount);
            insertTransaction.setInt(2, originId);
            insertTransaction.setInt(3, destinationId);
            insertTransaction.setString(4, description);

            updateOriginBalance.setDouble(1, amount);
            updateOriginBalance.setInt(2, originId);

            updateDestinationBalance.setDouble(1, amount);
            updateDestinationBalance.setInt(2, destinationId);

            updateOriginBalance.executeUpdate();
            updateDestinationBalance.executeUpdate();
            insertTransaction.executeUpdate();

            connection.commit();
        }
    }


}
