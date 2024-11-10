package com.tienphuckx.boxchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Attachment {
    private Integer id;
    private String url;
    private String attachmentType; // Use ENUM mapping if needed
    private Integer messageId;
}
