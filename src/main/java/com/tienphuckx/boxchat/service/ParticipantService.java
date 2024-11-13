package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.mapper.ParticipantMapper;
import com.tienphuckx.boxchat.model.Participant;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantMapper participantMapper;

    @Autowired
    public ParticipantService(ParticipantMapper participantMapper) {
        this.participantMapper = participantMapper;
    }

    // Add a user to a group
    public void addUserToGroup(Integer userId, Integer groupId) {
        if (!isUserInGroup(userId, groupId)) {
            participantMapper.addParticipant(userId, groupId);
        } else {
            throw new IllegalArgumentException("User is already in the group");
        }
    }

    // Check if a user is in a group
    public boolean isUserInGroup(Integer userId, Integer groupId) {
        return participantMapper.isUserInGroup(userId, groupId);
    }

    // Find all participants in a group
    public List<Participant> findParticipantsByGroupId(Integer groupId) {
        return participantMapper.findParticipantsByGroupId(groupId);
    }

    public void deleteUserFromGroup(Integer memberId, Integer groupId) {
        participantMapper.deleteUserFromGroup(memberId, groupId);
    }
}
