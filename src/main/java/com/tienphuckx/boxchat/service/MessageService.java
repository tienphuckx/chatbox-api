package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.dto.request.SendMessageDto;
import com.tienphuckx.boxchat.dto.response.GroupDetailResponse;
import com.tienphuckx.boxchat.dto.response.MessageResponse;
import com.tienphuckx.boxchat.mapper.*;
import com.tienphuckx.boxchat.model.Group;
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
    private final GroupMapper groupMapper;
    private final ParticipantMapper participantMapper;
    private final WaitingListMapper waitingMapper;

    @Autowired
    public MessageService(MessageMapper messageMapper, UserMapper userMapper, GroupMapper groupMapper, ParticipantMapper participantMapper, WaitingListMapper waitingMapper) {
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
        this.groupMapper = groupMapper;
        this.participantMapper = participantMapper;
        this.waitingMapper = waitingMapper;
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

    public GroupDetailResponse findMessagesByGroupId(Integer groupId, Integer page, Integer limit) {

        GroupDetailResponse groupDetailResponse = new GroupDetailResponse();

        Group group = groupMapper.findGroupById(groupId);

        if (group == null) {
            throw new IllegalArgumentException("Group not found with ID: " + groupId);
        }

        // Map group information to GroupDetailResponse
        groupDetailResponse.setGroupId(group.getId());
        groupDetailResponse.setGroupName(group.getName());

        // Fetch owner details
        User owner = userMapper.findUserById(group.getUserId());
        groupDetailResponse.setOwnerId(group.getUserId().toString());
        groupDetailResponse.setOwnerName(owner != null ? owner.getUsername() : "Unknown Owner");

        // Count joined members
        Integer joinedMemberCount = participantMapper.countJoinedMembersByGroupId(groupId);
        groupDetailResponse.setJoinedMember(joinedMemberCount);

        // Count waiting members
        Integer waitingMemberCount = waitingMapper.countWaitingMembersByGroupId(groupId);
        groupDetailResponse.setWaitingMember(waitingMemberCount);

        // Fetch messages with pagination
        Integer offset = (page - 1) * limit;
        List<Message> messageList = messageMapper.findMessagesByGroupIdWithPagination(groupId, offset, limit);

        // Map messages to MessageResponse
        List<MessageResponse> messageResponses = messageList.stream()
                .map(message -> {
                    User senderInfo = userMapper.findUserById(message.getUserId());
                    MessageResponse messageResponse = new MessageResponse(message);
                    messageResponse.setSenderName(senderInfo != null ? senderInfo.getUsername() : "Unknown User");
                    return messageResponse;
                })
                .collect(Collectors.toList());

        // Set pagination details and messages
        groupDetailResponse.setMessages(messageResponses);
        groupDetailResponse.setLimitMessage(limit);
        groupDetailResponse.setPageMessage(page);

        return groupDetailResponse;

    }


    // Find all messages sent by a user
    public List<Message> findMessagesByUserId(Integer userId) {
        return messageMapper.findMessagesByUserId(userId);
    }
}
