package com.tienphuckx.boxchat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponse {
    private Integer groupId;
    private String groupName;
    private Integer joinedMember;
    private Integer waitingMember;
    private String ownerId;
    private String ownerName;
    private Integer limitMessage;
    private Integer pageMessage;
    private boolean seen;
    private List<MessageResponse> messages;
}
