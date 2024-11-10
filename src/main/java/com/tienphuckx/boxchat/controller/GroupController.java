package com.tienphuckx.boxchat.controller;

import com.tienphuckx.boxchat.dto.request.NewGroupDto;
import com.tienphuckx.boxchat.dto.response.GroupResponse;
import com.tienphuckx.boxchat.model.Group;
import com.tienphuckx.boxchat.service.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:8080") // CORS for frontend
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/add")
    public Group createGroup(@RequestBody NewGroupDto groupDto) {
        Group group = new Group();
        group.setGroupCode(generateGroupCode());
        group.setUserId(groupDto.getUserId());
        group.setName(groupDto.getGroupName());
        group.setMaximumMembers(groupDto.getMaximumMembers());
        group.setApprovalRequire(Boolean.TRUE.equals(groupDto.getApprovalRequire()));
        group.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        group.setExpiredAt(Timestamp.valueOf(LocalDateTime.now().plusSeconds(groupDto.getRemainSeconds())));
        return groupService.createGroup(group);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupResponse>> getUserGroups(@PathVariable Integer userId) {
        List<GroupResponse> groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(groups);
    }

    // Get a group by ID
    @GetMapping("/{id}")
    public Group getGroupById(@PathVariable Integer id) {
        return groupService.findGroupById(id);
    }

    // Get a group by group code
    @GetMapping("/by-code/{groupCode}")
    public Group getGroupByCode(@PathVariable String groupCode) {
        return groupService.findGroupByCode(groupCode);
    }

    // Get all groups created by a user
    @GetMapping("/by-user/{userId}")
    public List<Group> getGroupsByUserId(@PathVariable Integer userId) {
        return groupService.findGroupsByUserId(userId);
    }

    private String generateGroupCode() {
        return "GR" + System.currentTimeMillis();
    }
}

