package com.tienphuckx.boxchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MessagesText {
    private Integer id;
    private String content;
    private String messageType;
    private Timestamp createdAt;
    private Integer userId;
    private Integer groupId;
}
