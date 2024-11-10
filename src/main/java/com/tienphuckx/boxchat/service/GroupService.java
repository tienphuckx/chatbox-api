package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.mapper.GroupMapper;
import com.tienphuckx.boxchat.model.Group;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class GroupService {

    private final GroupMapper groupMapper;
    private final ParticipantService participantService;

    @Autowired
    public GroupService(GroupMapper groupMapper, ParticipantService participantService) {
        this.groupMapper = groupMapper;
        this.participantService = participantService;
    }

    public Group createGroup(Group group) {
        groupMapper.insertGroup(group);
        participantService.addUserToGroup(group.getUserId(), group.getId());
        return group;
    }

    // Find group by ID
    public Group findGroupById(Integer id) {
        return groupMapper.findGroupById(id);
    }

    // Find group by group code
    public Group findGroupByCode(String groupCode) {
        return groupMapper.findGroupByCode(groupCode);
    }

    // Find all groups created by a user
    public List<Group> findGroupsByUserId(Integer userId) {
        return groupMapper.findGroupsByUserId(userId);
    }
}
