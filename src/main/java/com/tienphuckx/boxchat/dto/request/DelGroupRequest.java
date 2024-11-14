package com.tienphuckx.boxchat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DelGroupRequest {
    private String userCode;
    private String groupCode;
}
