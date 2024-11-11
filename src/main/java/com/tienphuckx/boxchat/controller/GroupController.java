package com.tienphuckx.boxchat.controller;

import com.tienphuckx.boxchat.dto.request.JoinGroupDto;
import com.tienphuckx.boxchat.dto.request.NewGroupDto;
import com.tienphuckx.boxchat.dto.response.GroupResponse;
import com.tienphuckx.boxchat.mapper.GroupMapper;
import com.tienphuckx.boxchat.model.Group;
import com.tienphuckx.boxchat.service.GroupService;
import com.tienphuckx.boxchat.service.ParticipantService;
import com.tienphuckx.boxchat.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:8080") // CORS for frontend
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;
    private final ParticipantService participantService;

    @Autowired
    public GroupController(GroupService groupService, UserService userService, ParticipantService participantService) {
        this.groupService = groupService;
        this.userService = userService;
        this.participantService = participantService;
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

    @PostMapping("/join")
    public ResponseEntity<?> joinGroup(@RequestBody JoinGroupDto dto) {
        try {
            // Validate DTO (e.g., check for null values)
            if (dto.getGroupCode() == null || dto.getUserId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Group code and user ID must not be null."
                ));
            }

            // Find group by code
            Group gr = groupService.findGroupByCode(dto.getGroupCode());
            if (gr == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Group not found."
                ));
            }

            // Add user to group
            participantService.addUserToGroup(dto.getUserId(), gr.getId());

            // Return success response
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully joined the group."
            ));
        } catch (Exception e) {
            // Handle exceptions and return error response
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "An error occurred while joining the group."
            ));
        }
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

