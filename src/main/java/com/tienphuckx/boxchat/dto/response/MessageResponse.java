package com.tienphuckx.boxchat.dto.response;

import com.tienphuckx.boxchat.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MessageResponse {
    private Integer id;
    private String content;
    private String messageType;
    private Timestamp createdAt;
    private Integer userId;
    private Integer groupId;
    private String messageUuid;
    private String senderName;
    private boolean seen;

    public MessageResponse(Message savedMessage) {
        this.id = savedMessage.getId();
        this.content = savedMessage.getContent();
        this.messageType = savedMessage.getMessageType();
        this.createdAt = savedMessage.getCreatedAt();
        this.userId = savedMessage.getUserId();
        this.groupId = savedMessage.getGroupId();
        this.messageUuid = savedMessage.getMessageUuid();
        this.seen = savedMessage.isSeen();
    }
}
