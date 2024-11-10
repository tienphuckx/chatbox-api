package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.mapper.MessageMapper;
import com.tienphuckx.boxchat.model.Message;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageMapper messageMapper;

    @Autowired
    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    // Send a message
    public Message sendMessage(Message message) {
        message.setMessageUuid(UUID.randomUUID()); // Generate unique UUID for the message
        messageMapper.insertMessage(message);
        return message; // Return the message object with generated ID
    }

    // Find all messages in a group
    public List<Message> findMessagesByGroupId(Integer groupId) {
        return messageMapper.findMessagesByGroupId(groupId);
    }

    // Find all messages sent by a user
    public List<Message> findMessagesByUserId(Integer userId) {
        return messageMapper.findMessagesByUserId(userId);
    }
}
