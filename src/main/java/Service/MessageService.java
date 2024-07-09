package Service;

import java.util.List;

import DAO.MessageDAO;
import Model.Message;

public class MessageService {
    MessageDAO messageDAO;

    public MessageService() {
        messageDAO = new MessageDAO();
    }

    public Message addMessage(Message message) {
        return messageDAO.insertMessage(message);
    }

    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    public Message getMessageById(int messageId) {
        return messageDAO.getMessageById(messageId);
    }

    public Message deleteMessageById(int messageId) {
        Message message = messageDAO.getMessageById(messageId);
        messageDAO.deleteMessageById(messageId);
        return message;
    }

    public Message updateMessageById(int messageId, String messageText) {
        messageDAO.updateMessageById(messageId, messageText);
        return messageDAO.getMessageById(messageId);
    }

    public List<Message> getMessagesByPostedBy(int accountId) {
        return messageDAO.getMessagesByPostedBy(accountId);
    }
}
