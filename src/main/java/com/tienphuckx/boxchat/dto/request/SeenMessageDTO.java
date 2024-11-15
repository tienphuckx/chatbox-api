package com.tienphuckx.boxchat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeenMessageDTO {
    private Integer userId;
    private Integer messageId;
}
