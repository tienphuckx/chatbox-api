package com.tienphuckx.boxchat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienphuckx.boxchat.config.WebSocketSessionManager;
import com.tienphuckx.boxchat.dto.request.*;
import com.tienphuckx.boxchat.dto.response.*;
import com.tienphuckx.boxchat.mapper.GroupMapper;
import com.tienphuckx.boxchat.model.Group;
import com.tienphuckx.boxchat.model.User;
import com.tienphuckx.boxchat.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
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
    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageService messageService;

    @Autowired
    public GroupController(GroupService groupService, UserService userService, ParticipantService participantService, WaitingListService waitingListService, WebSocketSessionManager webSocketSessionManager, MessageService messageService) {
        this.groupService = groupService;
        this.userService = userService;
        this.participantService = participantService;
        this.waitingListService = waitingListService;
        this.webSocketSessionManager = webSocketSessionManager;
        this.messageService = messageService;
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

    @PostMapping("/member/leave")
    public ResponseWrapper<LeaveGroupResponse> leaveGroup(@RequestBody LeaveGroupRequest request) {
        try{
            User member = userService.findUserByUserCode(request.getUserCode());
            Group group = groupService.findGroupByCode(request.getGroupCode());
            if(member == null || group == null){
                return new ResponseWrapper<>(
                        401,
                        null,
                        "Bad Request! Check the parameters"
                );
            }
            boolean isMemberInGroup = participantService.isUserInGroup(member.getId(), group.getId());
            if (!isMemberInGroup) {
                return new ResponseWrapper<>(
                        404,
                        null,
                        "The member not in the the group!"
                );
            }
            participantService.deleteUserFromGroup(member.getId(), group.getId());
            WebSocketSession session = webSocketSessionManager.getMemberSession(member.getId());
            if(session != null && session.isOpen()){

                LeaveGroupResponse response = new LeaveGroupResponse();
                response.setMemberId(member.getId());
                response.setGroupId(group.getId());

                SocketResponseWrapper<LeaveGroupResponse> wrapper = new SocketResponseWrapper<>();
                wrapper.setType("WS_LEAVE_GR");
                wrapper.setData(response);

                String json = objectMapper.writeValueAsString(wrapper);
                session.sendMessage(new TextMessage(json));
            }

            LeaveGroupResponse response = new LeaveGroupResponse();
            response.setMemberId(member.getId());
            response.setGroupId(group.getId());

            return new ResponseWrapper<>(
                    200,
                    response,
                    "Leave group successfully!"
            );

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseWrapper<>(
                    200,
                    null,
                    "Fail during leave group!"
            );
        }
    }

    @PostMapping("/delete/group")
    public ResponseWrapper<DelGroupResponse> deleteGroup(@RequestBody DelGroupRequest request) {
        try {
            User groupOwner = userService.findUserByUserCode(request.getUserCode());
            Group group = groupService.findGroupByCode(request.getGroupCode());

            if(groupOwner == null || group == null){
                return new ResponseWrapper<>(
                        401,
                        null,
                        "Bad Request! Check the parameters"
                );
            }

            // check if groupOner is onwer of the group
            boolean isGroupOwner = groupOwner.getId().equals(group.getUserId());
            if(!isGroupOwner){
                return new ResponseWrapper<>(
                        401,
                        null,
                        "Permission denied!"
                );
            }

            List<User> usersInGroup = userService.findAllUsersInGroup(group.getId());

            waitingListService.deleteAllWaitingMemberOfGroup(group.getId());
            participantService.deleteAllUserFromGroup(group.getId());
            messageService.deleteAllMessageOfGroup(group.getId());
            groupService.deleteGroup(group.getId());


            for (User u : usersInGroup) {
                WebSocketSession session = webSocketSessionManager.getMemberSession(u.getId());
                if(session != null && session.isOpen()){
                    WsDelGroupResponse response = new WsDelGroupResponse();
                    response.setUserId(u.getId());
                    response.setGroupId(group.getId());

                    SocketResponseWrapper<WsDelGroupResponse> wrapper = new SocketResponseWrapper<>();
                    wrapper.setType("WS_DEL_GR");
                    wrapper.setData(response);

                    String json = objectMapper.writeValueAsString(wrapper);
                    session.sendMessage(new TextMessage(json));
                }
            }




            DelGroupResponse response = new DelGroupResponse();
            response.setGroupId(group.getId());
            response.setUserId(groupOwner.getId());

            return new ResponseWrapper<>(
                    200,
                    response,
                    "Delete group successfully!"
            );


        } catch (Exception e) {
            return new ResponseWrapper<>(
                    500,
                    null,
                    "Failed to delete group!"
            );
        }
    }

    @PostMapping("/member/remove")
    public ResponseWrapper<RmMemberResponse> removeMemberFromGroup(@RequestBody RmMemberRequest request) {
        try {
            User groupOwner = userService.findUserByUserCode(request.getUserCode());
            Group group = groupService.findGroupByCode(request.getGroupCode());
            User removeMember = userService.findUserById(request.getMemberId());

            if(groupOwner == null || group == null || removeMember == null){
                return new ResponseWrapper<>(
                        401,
                        null,
                        "Bad Request! Check the parameters"
                );
            }

            // check if groupOner is onwer of the group
            boolean isGroupOwner = groupOwner.getId().equals(group.getUserId());
            if(!isGroupOwner){
                return new ResponseWrapper<>(
                        401,
                        null,
                        "Permission denied!"
                );
            }

            // check if removeMember is a member in the group
            boolean isMemberInGroup = participantService.isUserInGroup(removeMember.getId(), group.getId());
            if (!isMemberInGroup) {
                return new ResponseWrapper<>(
                        404,
                        null,
                        "The remove member not in the the group!"
                );
            }

            participantService.deleteUserFromGroup(removeMember.getId(), group.getId());

            WebSocketSession ses = webSocketSessionManager.getMemberSession(removeMember.getId());
            if(ses != null && ses.isOpen()){
                SocketResponseWrapper<RmMemberResponse> wrapper = new SocketResponseWrapper<>();
                RmMemberResponse rmMemberResponse = new RmMemberResponse();
                rmMemberResponse.setRemoveMemberId(removeMember.getId());
                wrapper.setType("WS_RM_MEMBER");
                wrapper.setData(rmMemberResponse);
                String msg = objectMapper.writeValueAsString(wrapper);
                ses.sendMessage(new TextMessage(msg));
            }

            RmMemberResponse rmMemberResponse = new RmMemberResponse();
            rmMemberResponse.setRemoveMemberName(removeMember.getUsername());

            return new ResponseWrapper<>(
                    200,
                    rmMemberResponse,
                    "Remove member successfully!"
            );

        } catch (Exception e){
            e.printStackTrace();
            return new ResponseWrapper<>(
                    500,
                    null,
                    "Failed to remove member from the group!"
            );
        }
    }

    @PostMapping("/member/decline")
    public ResponseWrapper<RmMemberResponse> declineMemberFromGroup(@RequestBody RmMemberRequest request) {
        try {
            User groupOwner = userService.findUserByUserCode(request.getUserCode());
            Group group = groupService.findGroupByCode(request.getGroupCode());
            User removeMember = userService.findUserById(request.getMemberId());

            if(groupOwner == null || group == null || removeMember == null){
                return new ResponseWrapper<>(
                        401,
                        null,
                        "Bad Request! Check the parameters"
                );
            }

            // check if groupOner is onwer of the group
            boolean isGroupOwner = groupOwner.getId().equals(group.getUserId());
            if(!isGroupOwner){
                return new ResponseWrapper<>(
                        401,
                        null,
                        "Permission denied!"
                );
            }


            boolean isMemberInWaitingList = waitingListService.isUserInWaitingList(removeMember.getId(), group.getId());
            if(!isMemberInWaitingList){
                return new ResponseWrapper<>(
                        404,
                        null,
                        "The decline member no longer in the the waiting list!"
                );
            }

            waitingListService.deleteFromWaitingList(removeMember.getId(), group.getId());

            //WS: update for waiting member
            //get socket session of the waiting member for response
            WebSocketSession socketSessionOfWaitingMember = webSocketSessionManager.getMemberSession(removeMember.getId());
            if(socketSessionOfWaitingMember != null && socketSessionOfWaitingMember.isOpen()){
                SocketResponseWrapper<DeclineWaitingMemberResponse> res = new SocketResponseWrapper<>();
                DeclineWaitingMemberResponse declineWaitingMemberResponse = new DeclineWaitingMemberResponse();
                declineWaitingMemberResponse.setMemberId(removeMember.getId());
                res.setType("WS_DECLINE");
                res.setData(declineWaitingMemberResponse);
                String msg = objectMapper.writeValueAsString(res);
                socketSessionOfWaitingMember.sendMessage(new TextMessage(msg));
            }


            RmMemberResponse rmMemberResponse = new RmMemberResponse();
            rmMemberResponse.setRemoveMemberName(removeMember.getUsername());

            return new ResponseWrapper<>(
                    200,
                    rmMemberResponse,
                    "Remove member successfully!"
            );

        } catch (Exception e){
            e.printStackTrace();
            return new ResponseWrapper<>(
                    500,
                    null,
                    "Failed to remove member from the group!"
            );
        }
    }


    @PostMapping("/approve")
    public ApproveResponse approveGroup(@RequestBody ApproveRequest approveRequest) {

        // Todo: validate id of owner of the group before approval

        participantService.addUserToGroup(approveRequest.getMemberId(), approveRequest.getGroupId());
        waitingListService.deleteFromWaitingList(approveRequest.getMemberId(), approveRequest.getGroupId());


        // Find the member's WebSocket session from the session manager
        WebSocketSession session = webSocketSessionManager.getMemberSession(approveRequest.getMemberId());
        if (session != null && session.isOpen()) {
            // Send message to the member waiting for approval
            try {
                ApproveResponse approveResponse = new ApproveResponse();
                approveResponse.setStatus(200);
                approveResponse.setMemberId(approveRequest.getMemberId());
                approveResponse.setMessage("You have been approved to join the group!");

                SocketResponseWrapper<ApproveResponse> res = new SocketResponseWrapper<>();
                res.setData(approveResponse);
                res.setType("WS_APPROVED");


                // Serialize the response to JSON
                String message = objectMapper.writeValueAsString(res);

                System.out.println(message);

                // Send the serialized message as a TextMessage
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace(); // Handle the error
            }
        }

        return new ApproveResponse(200, "success", approveRequest.getMemberId());
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

