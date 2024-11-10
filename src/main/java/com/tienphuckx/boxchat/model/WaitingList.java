package com.tienphuckx.boxchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaitingList {
    private Integer id;
    private Integer userId;
    private Integer groupId;
    private String message;
    private Timestamp createdAt;
}
