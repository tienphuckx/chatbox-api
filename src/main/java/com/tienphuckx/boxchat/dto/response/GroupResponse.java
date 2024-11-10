package com.tienphuckx.boxchat.dto.response;

public class GroupResponse {
    private Integer id;
    private String name;
    private String groupCode;
    private Boolean approvalRequire;
    private Timestamp createdAt;
    private Timestamp expiredAt;
    private Integer maximumMembers;
    private String status; // "joined" or "waiting"

}
