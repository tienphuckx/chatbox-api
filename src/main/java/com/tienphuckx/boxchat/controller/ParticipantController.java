package com.tienphuckx.boxchat.controller;

import com.tienphuckx.boxchat.model.Group;
import com.tienphuckx.boxchat.model.Participant;
import com.tienphuckx.boxchat.service.GroupService;
import com.tienphuckx.boxchat.service.ParticipantService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/participants")
@CrossOrigin(origins = "http://localhost:8080") // CORS for frontend
public class ParticipantController {

    private final ParticipantService participantService;
    private final GroupService groupService;

    @Autowired
    public ParticipantController(ParticipantService participantService, GroupService groupService) {
        this.participantService = participantService;
        this.groupService = groupService;
    }

    // Add a user to a group
    @PostMapping("/join")
    public String joinGroup(@RequestParam Integer userId, @RequestParam String groupCode) {
        Group group = groupService.findGroupByCode(groupCode);
        if (group == null) {
            throw new IllegalArgumentException("Group not found with code: " + groupCode);
        }
        participantService.addUserToGroup(userId, group.getId());
        return "User successfully joined the group: " + group.getName();
    }

    // Get all participants in a group
    @GetMapping("/group/{groupId}")
    public List<Participant> getParticipantsByGroupId(@PathVariable Integer groupId) {
        return participantService.findParticipantsByGroupId(groupId);
    }
}

