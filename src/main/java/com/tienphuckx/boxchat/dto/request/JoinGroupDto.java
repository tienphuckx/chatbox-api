package com.tienphuckx.boxchat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinGroupDto {
    private String groupCode;
    private Integer userId;
}
