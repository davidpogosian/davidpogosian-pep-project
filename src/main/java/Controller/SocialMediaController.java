package Controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
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
    MessageService messageService;


    public Javalin startAPI() {
        mapper = new ObjectMapper();
        accountService = new AccountService();
        messageService = new MessageService();

        Javalin app = Javalin.create();
        app.post("/register", this::handleRegister);
        app.post("/login", this::handleLogin);
        app.post("/messages", this::handlePostMessages);
        app.get("/messages", this::handleGetMessages);
        app.get("/messages/{messageId}", this::handleGetMessageById);
        app.delete("/messages/{messageId}", this::handleDeleteMessageById);
        app.patch("/messages/{messageId}", this::handlePatchMessageById);
        app.get("/accounts/{accountId}/messages", this::handleGetMessagesByAccountId);

        return app;
    }

    private boolean newAccountIsValid(Account account) {
        if (account.getUsername().equals("")) {
            return false;
        }

        if (account.getPassword().length() < 5) {
            return false;
        }

        if (accountService.getAccountByUsername(account.getUsername()) != null) {
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

    private void handleLogin(Context context) {
        Account account;

        try {
            account = mapper.readValue(context.body(), Account.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(401);
            return;
        }

        Account existingAccount = accountService.getAccountByUsername(account.getUsername());

        if (existingAccount == null) {
            context.status(401);
            return;
        }

        if (!existingAccount.getPassword().equals(account.getPassword())) {
            context.status(401);
            return;
        }

        context.status(200).json(existingAccount);
        return;
    }

    private boolean messageTextIsValid(String messageText) {
        if (messageText.length() == 0) {
            return false;
        }

        if (messageText.length() >= 255) {
            return false;
        }

        return true;
    }

    private boolean newMessageIsValid(Message message) {
        Account account = accountService.getAccountById(message.getPosted_by());

        if (account == null) {
            return false;
        }

        return messageTextIsValid(message.getMessage_text());
    }

    private void handlePostMessages(Context context) {
        Message newMessage;

        try {
            newMessage = mapper.readValue(context.body(), Message.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(400);
            return;
        }

        if (!newMessageIsValid(newMessage)) {
            context.status(400);
            return;
        }

        Message createdMessage = messageService.addMessage(newMessage);

        if (createdMessage == null) {
            context.status(500);
            return;
        }

        String createdMessageString;

        try {
            createdMessageString = mapper.writeValueAsString(createdMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(500);
            return;
        }

        context.status(200).json(createdMessageString);
    }

    private void handleGetMessages(Context context) {
        List<Message> messages;

        messages = messageService.getAllMessages();

        String messagesString;

        try {
            messagesString = mapper.writeValueAsString(messages);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(500);
            return;
        }

        context.status(200).json(messagesString);
    }

    private void handleGetMessageById(Context context) {
        int messageId = Integer.parseInt(context.pathParam("messageId"));

        Message message = messageService.getMessageById(messageId);

        String messageString;

        try {
            messageString = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(500);
            return;
        }

        if (message == null) {
            messageString = "";
        }

        context.status(200).json(messageString);
    }

    private void handleDeleteMessageById(Context context) {
        int messageId = Integer.parseInt(context.pathParam("messageId"));

        Message message = messageService.deleteMessageById(messageId);

        String messageString;

        try {
            messageString = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(500);
            return;
        }

        if (message == null) {
            messageString = "";
        }

        context.status(200).json(messageString);
    }

    private void handlePatchMessageById(Context context) {
        int messageId = Integer.parseInt(context.pathParam("messageId"));

        Message newMessage;

        try {
            newMessage = mapper.readValue(context.body(), Message.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(400);
            return;
        }

        String messageText = newMessage.getMessage_text();

        if (!messageTextIsValid(messageText)) {
            context.status(400);
            return;
        }

        Message message = messageService.getMessageById(messageId);

        if (message == null) {
            context.status(400);
            return;
        }

        message = messageService.updateMessageById(messageId, messageText);

        String messageString;

        try {
            messageString = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(500);
            return;
        }

        context.status(200).json(messageString);
    }

    private void handleGetMessagesByAccountId(Context context) {
        int accountId = Integer.parseInt(context.pathParam("accountId"));

        List<Message> messages = messageService.getMessagesByPostedBy(accountId);

        String messagesString;

        try {
            messagesString = mapper.writeValueAsString(messages);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            context.status(500);
            return;
        }

        context.status(200).json(messagesString);
    }
}