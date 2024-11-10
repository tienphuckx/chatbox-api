package com.tienphuckx.boxchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Group {
    private Integer id;
    private String name;
    private String groupCode;
    private Integer userId;
    private Boolean approvalRequire;
    private Timestamp createdAt;
    private Timestamp expiredAt;
    private Integer maximumMembers;
}
