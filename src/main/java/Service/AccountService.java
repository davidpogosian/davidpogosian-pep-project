package Service;

import java.util.List;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public Account addAccount(Account account) {
        return accountDAO.insertAccount(account);
    }

    public List<Account> getAccountsByUsername(String username) {
        return accountDAO.getAccountsByUsername(username);
    }
}
