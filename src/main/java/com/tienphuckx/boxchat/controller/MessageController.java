package com.tienphuckx.boxchat.controller;

import com.tienphuckx.boxchat.model.Message;
import com.tienphuckx.boxchat.service.MessageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:8080") // CORS for frontend
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // Send a message
    @PostMapping
    public Message sendMessage(@RequestBody Message message) {
        if (message.getContent() == null || message.getGroupId() == null || message.getUserId() == null) {
            throw new IllegalArgumentException("Message content, group ID, and user ID must not be null");
        }
        return messageService.sendMessage(message);
    }

    // Get all messages in a group
    @GetMapping("/group/{groupId}")
    public List<Message> getMessagesByGroupId(@PathVariable Integer groupId) {
        return messageService.findMessagesByGroupId(groupId);
    }

    // Get all messages sent by a user
    @GetMapping("/user/{userId}")
    public List<Message> getMessagesByUserId(@PathVariable Integer userId) {
        return messageService.findMessagesByUserId(userId);
    }
}
