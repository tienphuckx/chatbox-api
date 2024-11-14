package com.tienphuckx.boxchat.service;

import com.tienphuckx.boxchat.dto.response.*;
import com.tienphuckx.boxchat.mapper.GroupMapper;
import com.tienphuckx.boxchat.mapper.ParticipantMapper;
import com.tienphuckx.boxchat.mapper.UserMapper;
import com.tienphuckx.boxchat.mapper.WaitingListMapper;
import com.tienphuckx.boxchat.model.Group;
import com.tienphuckx.boxchat.model.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GroupService {

    private final GroupMapper groupMapper;
    private final ParticipantService participantService;
    private final ParticipantMapper participantMapper;
    private final WaitingListMapper waitingMapper;
    private final UserMapper userMapper;


    @Autowired
    public GroupService(GroupMapper groupMapper, ParticipantService participantService, ParticipantMapper participantMapper, WaitingListMapper waitingListMapper, WaitingListMapper waitingMapper, UserMapper userMapper) {
        this.groupMapper = groupMapper;
        this.participantService = participantService;
        this.participantMapper = participantMapper;
        this.waitingMapper = waitingMapper;
        this.userMapper = userMapper;
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

    public GroupSettingResponse getGroupSetting(String groupCode) {
        GroupSettingResponse response = new GroupSettingResponse();
        // Fetch group information
        Group group = groupMapper.findGroupByCode(groupCode);
        if (group == null) {
            throw new IllegalArgumentException("Group with code " + groupCode + " not found");
        }

        User user = userMapper.findUserById(group.getUserId());

        // Set basic group information
        response.setGroupId(group.getId());
        response.setGroupName(group.getName());
        response.setGroupCode(group.getGroupCode());
        response.setOwnerId(group.getUserId());
        response.setOwnerName(user.getUsername());
        response.setApprovalRequire(group.getApprovalRequire());
        response.setCreatedAt(group.getCreatedAt());
        response.setExpiredAt(group.getExpiredAt());
        response.setMaximumMembers(group.getMaximumMembers());
        // Fetch list of joined members
        List<JoinedMemberDto> joinedMembers = participantMapper.findJoinedMembersByGroupId(group.getId());
        response.setListJoinedMember(joinedMembers);

        // Fetch list of waiting members
        List<WaitingMemberDto> waitingMembers = waitingMapper.findWaitingMembersByGroupId(group.getId());
        response.setListWaitingMember(waitingMembers);

        // Add fake data for links, files, and media
        List<LinkDto> links = List.of(
                new LinkDto("https://example.com", "example.com", "/images/example.png"),
                new LinkDto("https://another.com", "another.com", "/images/another.png")
        );

        List<FileDto> files = List.of(
                new FileDto("Document.pdf", "PDF", "/files/document.pdf"),
                new FileDto("Presentation.pptx", "PPTX", "/files/presentation.pptx")
        );

        List<MediaDto> medias = List.of(
                new MediaDto("1", "image", "/media/photo1.jpg", "1MB"),
                new MediaDto("2", "video", "/media/video1.mp4", "20MB")
        );

        response.setLinks(links);
        response.setFiles(files);
        response.setMedias(medias);

        return response;
    }

    public void deleteGroup(Integer id) {
        groupMapper.deleteGroup(id);
    }
}
