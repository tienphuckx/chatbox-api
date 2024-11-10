package com.tienphuckx.boxchat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewGroupDto {
    private String groupName;
    private Integer userId;
    private Boolean approvalRequire;
    private Integer remainSeconds;
    private Integer maximumMembers;
}
