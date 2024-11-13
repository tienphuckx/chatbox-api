package com.tienphuckx.boxchat.config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienphuckx.boxchat.dto.request.SendMessageDto;
import com.tienphuckx.boxchat.dto.response.MessageResponse;
import com.tienphuckx.boxchat.dto.response.SocketResponseWrapper;
import com.tienphuckx.boxchat.model.Message;
import com.tienphuckx.boxchat.model.User;
import com.tienphuckx.boxchat.service.MessageService;
import com.tienphuckx.boxchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;

    public ChatWebSocketHandler(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Autowired
    private WebSocketSessionManager webSocketSessionManager;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri != null) {
            String query = uri.getQuery();
            Map<String, String> params = parseQueryParams(query);
            String token = params.get("token"); // token is user code
            if (token != null) {
                User member = userService.findUserByUserCode(token);

                if (member != null) {
                    // Add session to both WebSocketSessionManager and the local sessions map
                    webSocketSessionManager.addMemberSession(member.getId(), session);
                    sessions.put(session.getId(), session);  // Add to the sessions map for broadcasting

                    session.sendMessage(new TextMessage("{\"type\":\"info\",\"content\":\"Connected to chat!\"}"));
                } else {
                    session.sendMessage(new TextMessage("{\"type\":\"error\",\"content\":\"Authentication failed!\"}"));
                    session.close();
                }
            }
        }
    }


    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {



        // Cast buffer to Object
        SendMessageDto msg = objectMapper.readValue(textMessage.getPayload(), SendMessageDto.class);

        // Save message to DB
        Message savedMessage = messageService.sendMessage(msg);

        // get sender info to return sender username
        User senderInfo = userService.findUserById(savedMessage.getUserId());

        MessageResponse data = new MessageResponse(savedMessage);
        data.setSenderName(senderInfo.getUsername());

        SocketResponseWrapper<MessageResponse> res = new SocketResponseWrapper<>();
        res.setType("WS_MSG");
        res.setData(data);

        // Todo: Only broadcast for member in the group that have message

        // Broadcast message to all sessions
        for (WebSocketSession webSocketSession : sessions.values()) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(
                        new TextMessage(objectMapper.writeValueAsString(res))
                );
            }
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }
}

