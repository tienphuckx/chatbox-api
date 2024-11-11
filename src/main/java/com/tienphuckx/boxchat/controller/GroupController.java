package com.tienphuckx.boxchat.controller;

import com.tienphuckx.boxchat.dto.request.JoinGroupDto;
import com.tienphuckx.boxchat.dto.request.NewGroupDto;
import com.tienphuckx.boxchat.dto.response.GroupResponse;
import com.tienphuckx.boxchat.dto.response.GroupSettingResponse;
import com.tienphuckx.boxchat.mapper.GroupMapper;
import com.tienphuckx.boxchat.model.Group;
import com.tienphuckx.boxchat.service.GroupService;
import com.tienphuckx.boxchat.service.ParticipantService;
import com.tienphuckx.boxchat.service.UserService;
import com.tienphuckx.boxchat.service.WaitingListService;
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
    private final WaitingListService waitingListService;

    @Autowired
    public GroupController(GroupService groupService, UserService userService, ParticipantService participantService, WaitingListService waitingListService) {
        this.groupService = groupService;
        this.userService = userService;
        this.participantService = participantService;
        this.waitingListService = waitingListService;
    }

    @GetMapping("/setting/{groupCode}")
    public ResponseEntity<GroupSettingResponse> getGroupSetting(@PathVariable String groupCode) {
        try {
            if(groupCode == null){
                System.out.println("Group code must not be null");
                return ResponseEntity.status(500).build();
            }
            GroupSettingResponse res = groupService.getGroupSetting(groupCode);
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            System.out.println("Error while getting group setting");
            return ResponseEntity.status(500).build();
        }
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

            if (dto.getGroupCode() == null || dto.getUserId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Group code and user ID must not be null."
                ));
            }

            Group gr = groupService.findGroupByCode(dto.getGroupCode());
            if (gr == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Group not found."
                ));
            }

            if(gr.getApprovalRequire()){
                waitingListService.addToWaitingList(dto.getUserId(), gr.getId(), dto.getMessage());
            }else{
                participantService.addUserToGroup(dto.getUserId(), gr.getId());
            }

            // Add user to participant


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

