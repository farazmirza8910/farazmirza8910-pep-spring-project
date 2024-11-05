package com.example.controller;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/")
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    // Endpoint for account registration
    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        try {
            Account createdAccount = accountService.createAccount(account);
            return ResponseEntity.status(HttpStatus.OK).body(createdAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // 409 Conflict for account issues
        }
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account loginAccount) {
        try {
            Account authenticatedAccount = accountService.authenticate(loginAccount.getUsername(), loginAccount.getPassword());
            return ResponseEntity.ok(authenticatedAccount); // 200 OK with authenticated account
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 401 Unauthorized
        }
    }

    // Endpoint for submitting a new message
    @PostMapping("/messages")
    public ResponseEntity<Message> submitMessage(@RequestBody Message newMessage) {
        try {
            Message createdMessage = messageService.createMessage(newMessage); // Updated to call createMessage
            return ResponseEntity.status(HttpStatus.OK).body(createdMessage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request for validation errors
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 404 if user doesn't exist
        }
    }

    // Endpoint for retrieving all messages
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages); // 200 OK with list of messages
    }

    // Endpoint for retrieving a message by ID
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        try {
            Message message = messageService.getMessageById(messageId);
            return ResponseEntity.ok(message); // 200 OK with message if found
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }

    // Endpoint for deleting a message by ID
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable Integer messageId) {
        int rowsDeleted = messageService.deleteMessage(messageId);
        
        if (rowsDeleted == 1) {
            return ResponseEntity.ok(rowsDeleted);
        } else {
            return ResponseEntity.ok().build();
        }
    }

    // Endpoint for updating a message
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(
            @PathVariable Integer messageId,
            @RequestBody Message updatedMessageData) {
        try {
            // Call the service to update the message
            int rowsUpdated = messageService.updateMessage(messageId, updatedMessageData);
            return ResponseEntity.ok(rowsUpdated); // 200 OK with the number of rows updated
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request for validation errors
        }
    }

    // Endpoint for retrieving all messages by account ID
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByPostedBy(accountId);
        if (messages.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(messages);
        }
        return ResponseEntity.ok(messages); // 200 OK with list of messages
    }
}
