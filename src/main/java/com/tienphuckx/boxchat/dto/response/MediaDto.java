package com.tienphuckx.boxchat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto {
    private String mediaId;
    private String mediaType;
    private String mediaPath;
    private String mediaSize;
}