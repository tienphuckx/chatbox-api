package com.tienphuckx.boxchat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
