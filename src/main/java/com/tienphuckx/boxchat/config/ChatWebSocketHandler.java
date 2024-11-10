package com.tienphuckx.boxchat.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienphuckx.boxchat.dto.request.SendMessageDto;
import com.tienphuckx.boxchat.model.Message;
import com.tienphuckx.boxchat.service.MessageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatWebSocketHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        session.sendMessage(new TextMessage("{\"type\":\"info\",\"content\":\"Connected to chat!\"}"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {

        // Cast buffer to Object
        SendMessageDto msg = objectMapper.readValue(textMessage.getPayload(), SendMessageDto.class);

        // Save message to DB
        Message savedMessage = messageService.sendMessage(msg);

        // Broadcast message to all sessions
        for (WebSocketSession webSocketSession : sessions.values()) {
            if (webSocketSession.isOpen()) {

                webSocketSession.sendMessage(
                        new TextMessage(objectMapper.writeValueAsString(savedMessage))
                );
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }
}

