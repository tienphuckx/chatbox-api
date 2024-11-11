package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.dto.request.SendMessageDto;
import com.tienphuckx.boxchat.dto.response.MessageResponse;
import com.tienphuckx.boxchat.mapper.MessageMapper;
import com.tienphuckx.boxchat.mapper.UserMapper;
import com.tienphuckx.boxchat.model.Message;
import com.tienphuckx.boxchat.model.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Autowired
    public MessageService(MessageMapper messageMapper, UserMapper userMapper) {
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
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

    public List<MessageResponse> findMessagesByGroupId(Integer groupId) {
        List<Message> messageList = messageMapper.findMessagesByGroupId(groupId);
        return messageList.stream()
                .map(message -> {
                    User senderInfo = userMapper.findUserById(message.getUserId());
                    MessageResponse messageResponse = new MessageResponse(message);
                    messageResponse.setSenderName(senderInfo != null ? senderInfo.getUsername() : "Unknown User");
                    return messageResponse;
                })
                .collect(Collectors.toList());
    }


    // Find all messages sent by a user
    public List<Message> findMessagesByUserId(Integer userId) {
        return messageMapper.findMessagesByUserId(userId);
    }
}
