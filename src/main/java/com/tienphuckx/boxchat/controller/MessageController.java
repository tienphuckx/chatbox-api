package com.tienphuckx.boxchat.controller;

import com.tienphuckx.boxchat.dto.request.SendMessageDto;
import com.tienphuckx.boxchat.dto.response.MessageResponse;
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


    // Get all messages in a group
    @GetMapping("/group/{groupId}")
    public List<MessageResponse> getMessagesByGroupId(@PathVariable Integer groupId) {
        return messageService.findMessagesByGroupId(groupId);
    }

    // Get all messages sent by a user
    @GetMapping("/user/{userId}")
    public List<Message> getMessagesByUserId(@PathVariable Integer userId) {
        return messageService.findMessagesByUserId(userId);
    }
}
