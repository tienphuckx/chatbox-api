package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.dto.request.SendMessageDto;
import com.tienphuckx.boxchat.mapper.MessageMapper;
import com.tienphuckx.boxchat.model.Message;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    public Message sendMessage(SendMessageDto dto) {
        Message msg = new Message();

        msg.setMessageUuid(UUID.randomUUID().toString());
        msg.setGroupId(dto.getGroupId());
        msg.setUserId(dto.getUserId());
        msg.setContent(dto.getContent());
        msg.setMessageType("TEXT");

        messageMapper.insertMessage(msg);
        return msg;
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
