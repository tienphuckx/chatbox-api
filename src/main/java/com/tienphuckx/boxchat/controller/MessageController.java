package com.tienphuckx.boxchat.controller;

import com.tienphuckx.boxchat.dto.request.SendMessageDto;
import com.tienphuckx.boxchat.dto.response.GroupDetailResponse;
import com.tienphuckx.boxchat.dto.response.MessageResponse;
import com.tienphuckx.boxchat.model.Message;
import com.tienphuckx.boxchat.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/group/{groupId}/{page}/{limit}")
    public ResponseEntity<GroupDetailResponse> getMessagesByGroupId(
            @PathVariable Integer groupId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            GroupDetailResponse groupDetail = messageService.findMessagesByGroupId(groupId, page, limit);
            return ResponseEntity.ok(groupDetail);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Get all messages sent by a user
    @GetMapping("/user/{userId}")
    public List<Message> getMessagesByUserId(@PathVariable Integer userId) {
        return messageService.findMessagesByUserId(userId);
    }
}
