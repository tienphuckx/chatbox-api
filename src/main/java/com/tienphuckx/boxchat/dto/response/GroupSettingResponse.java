package com.tienphuckx.boxchat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupSettingResponse {
    private Integer groupId;
    private String groupName;
    private String groupCode;
    private Integer ownerId;
    private String ownerName;
    private Boolean approvalRequire;
    private Timestamp createdAt;
    private Timestamp expiredAt;
    private Integer maximumMembers;

    List<JoinedMemberDto> listJoinedMember;
    List<WaitingMemberDto> listWaitingMember;
    List<LinkDto> links;
    List<FileDto> files;
    List<MediaDto> medias;
}

