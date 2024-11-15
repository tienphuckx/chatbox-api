package com.tienphuckx.boxchat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienphuckx.boxchat.config.WebSocketSessionManager;
import com.tienphuckx.boxchat.dto.request.SeenMessageDTO;
import com.tienphuckx.boxchat.dto.request.SendMessageDto;
import com.tienphuckx.boxchat.dto.response.GroupDetailResponse;
import com.tienphuckx.boxchat.dto.response.MessageResponse;
import com.tienphuckx.boxchat.dto.response.SeenMessageResponse;
import com.tienphuckx.boxchat.dto.response.SocketResponseWrapper;
import com.tienphuckx.boxchat.model.Message;
import com.tienphuckx.boxchat.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:8080") // CORS for frontend
public class MessageController {

    private final MessageService messageService;
    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public MessageController(MessageService messageService, WebSocketSessionManager webSocketSessionManager) {
        this.messageService = messageService;
        this.webSocketSessionManager = webSocketSessionManager;
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

    @PutMapping("/seen")
    public ResponseEntity<?> seenMessages(@RequestBody List<SeenMessageDTO> userMessageList) throws IOException {
        if (userMessageList == null || userMessageList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No message data provided.");
        }

        List<MessageResponse> updatedMessages = new ArrayList<>();

        for (SeenMessageDTO userMessage : userMessageList) {
            Integer messageId = userMessage.getMessageId();
            Integer userId = userMessage.getUserId(); // Sender's userId

            Message message = messageService.findMessagesById(messageId);
            if (message != null) {
                messageService.markSeenMessage(messageId); // Mark the message as seen
                Message messageAfter = messageService.findMessagesById(messageId);
                updatedMessages.add(new MessageResponse(messageAfter));

                WebSocketSession session = webSocketSessionManager.getMemberSession(userId);
                if (session != null && session.isOpen()) {
                    SeenMessageResponse seenMessageResponse = new SeenMessageResponse();
                    seenMessageResponse.setMessageId(messageId);
                    seenMessageResponse.setUserId(userId);
                    SocketResponseWrapper<SeenMessageResponse> wrapper = new SocketResponseWrapper<>();
                    wrapper.setData(seenMessageResponse);
                    wrapper.setType("WS_SEEN");
                    String json = objectMapper.writeValueAsString(wrapper);
                    session.sendMessage(new TextMessage(json));
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Message ID " + messageId + " not found.");
            }
        }

        return ResponseEntity.ok(updatedMessages);
    }



    // Get all messages sent by a user
    @GetMapping("/user/{userId}")
    public List<Message> getMessagesByUserId(@PathVariable Integer userId) {
        return messageService.findMessagesByUserId(userId);
    }
}
