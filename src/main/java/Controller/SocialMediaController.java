package Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Service.AccountService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    ObjectMapper mapper;

    AccountService accountService;


    public Javalin startAPI() {
        mapper = new ObjectMapper();
        accountService = new AccountService();

        Javalin app = Javalin.create();
        app.post("/register", this::handleRegister);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private boolean newAccountIsValid(Account account) {
        if (account.getUsername().equals("")) {
            return false;
        }

        if (account.getPassword().length() < 5) {
            return false;
        }

        if (accountService.getAccountsByUsername(account.getUsername()).size() != 0) {
            return false;
        }

        return true;
    }

    private void handleRegister(Context context) {
        Account newAccount;

        try {
            newAccount = mapper.readValue(context.body(), Account.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(400);
            return;
        }

        if (!newAccountIsValid(newAccount)) {
            context.status(400);
            return;
        }

        Account createdAccount = accountService.addAccount(newAccount);

        if (createdAccount == null) {
            context.status(500);
            return;
        }

        String createdAccountString;

        try {
            createdAccountString = mapper.writeValueAsString(createdAccount);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(500);
            return;
        }

        context.status(200).json(createdAccountString);
    }


}