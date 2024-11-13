package com.tienphuckx.boxchat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveRequest {
    private Integer groupId;
    private Integer groupOwnerId;
    private Integer memberId;
}
