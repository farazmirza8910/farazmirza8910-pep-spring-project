package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;
import com.example.repository.AccountRepository; // Assuming an AccountRepository exists
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository; // Dependency for checking users

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository) {
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository; 
    }

    /**
     * Save a new message after validation.
     */
    public Message createMessage(Message newMessage) {
        // Validate message text
        if (newMessage.getMessageText() == null || newMessage.getMessageText().isBlank()) {
            throw new IllegalArgumentException("Message text cannot be blank.");
        }
        if (newMessage.getMessageText().length() > 255) {
            throw new IllegalArgumentException("Message text cannot exceed 255 characters.");
        }

        // Validate postedBy user exists
        if (newMessage.getPostedBy() == null || !accountRepository.existsById(newMessage.getPostedBy())) {
            throw new EntityNotFoundException("User with id " + newMessage.getPostedBy() + " does not exist.");
        }

        try {
            return messageRepository.save(newMessage);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Failed to save message due to integrity constraints.");
        }
    }

    /**
     * Retrieve a message by its ID.
     */
    public Message getMessageById(Integer messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));
    }

    /**
     * Retrieve all messages.
     */
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    /**
     * Retrieve messages by the user who posted them.
     */
    public List<Message> getMessagesByPostedBy(Integer postedBy) {
        return messageRepository.findByPostedBy(postedBy);
    }

    /**
     * Retrieve messages posted after a specific epoch time.
     */
    public List<Message> getMessagesPostedAfter(Long epochTime) {
        return messageRepository.findByTimePostedEpochGreaterThanEqual(epochTime);
    }

    /**
     * Delete a message by its ID.
     */
    public int deleteMessage(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return 1; // Indicates one row (message) was deleted
        }
        return 0; // Indicates no rows were deleted because the message was not found
    }

     //Update an existing message.
     
    public int updateMessage(Integer messageId, Message updatedMessageData) {
        // Retrieve the existing message
        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id: " + messageId));
    
        // Validate the new message text
        String newMessageText = updatedMessageData.getMessageText();
        if (newMessageText == null || newMessageText.isBlank()) {
            throw new IllegalArgumentException("Message text cannot be blank.");
        }
        if (newMessageText.length() > 255) {
            throw new IllegalArgumentException("Message text cannot exceed 255 characters.");
        }
    
        // Update the existing messages text
        existingMessage.setMessageText(newMessageText);
        
        // Save the updated message back to the database
        messageRepository.save(existingMessage);
    
        return 1; // Return the number of rows updated, which is 1 if successful
    }

}
