package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.dto.response.GroupResponse;
import com.tienphuckx.boxchat.mapper.GroupMapper;
import com.tienphuckx.boxchat.model.Group;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<GroupResponse> getUserGroups(Integer userId) {
        List<Group> joinedGroups = groupMapper.findJoinedGroupsByUserId(userId);
        List<Group> waitingGroups = groupMapper.findWaitingGroupsByUserId(userId);

        return Stream.concat(
                joinedGroups.stream().map(group -> mapToGroupResponse(group, "joined")),
                waitingGroups.stream().map(group -> mapToGroupResponse(group, "waiting"))
        ).collect(Collectors.toList());
    }

    private GroupResponse mapToGroupResponse(Group group, String status) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setGroupCode(group.getGroupCode());
        response.setApprovalRequire(group.getApprovalRequire());
        response.setCreatedAt(group.getCreatedAt());
        response.setExpiredAt(group.getExpiredAt());
        response.setMaximumMembers(group.getMaximumMembers());
        response.setStatus(status);
        response.setOwnerId(group.getUserId());
        return response;
    }

}
