package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Account;
import Util.ConnectionUtil;

public class AccountDAO {
    Connection connection;

    public AccountDAO() {
        connection = ConnectionUtil.getConnection();
    }

    public Account insertAccount(Account account) {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?);";
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.executeUpdate();
            ResultSet PrimaryKeyResultSet = preparedStatement.getGeneratedKeys();
            
            if (PrimaryKeyResultSet.next()) {
                int accountId = (int) PrimaryKeyResultSet.getLong(1);
                return new Account(accountId, account.getUsername(), account.getPassword());
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    public List<Account> getAccountsByUsername(String username) {
        String sql = "SELECT * FROM account WHERE username=?;";
        List<Account> accounts = new ArrayList<Account>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Account account = new Account(
                    resultSet.getInt("account_id"), 
                    resultSet.getString("username"),
                    resultSet.getString("password"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return accounts;
    }
}
