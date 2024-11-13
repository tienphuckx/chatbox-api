package com.tienphuckx.boxchat.config;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<Integer, WebSocketSession> memberSessions = new ConcurrentHashMap<>();

    public void addSession(String sessionId, WebSocketSession session) {
        sessions.put(sessionId, session);
    }

    public void addMemberSession(Integer memberId, WebSocketSession session) {
        memberSessions.put(memberId, session);
    }

    public WebSocketSession getMemberSession(Integer memberId) {
        return memberSessions.get(memberId);
    }

    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public void removeWaitingMember(Integer memberId) {
        memberSessions.remove(memberId);
    }
}
