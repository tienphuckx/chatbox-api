package com.tienphuckx.boxchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Message {
    private Integer id;
    private String content;
    private String messageType; // Use ENUM mapping if needed
    private Timestamp createdAt;
    private Integer userId;
    private Integer groupId;
    private String messageUuid;
}
