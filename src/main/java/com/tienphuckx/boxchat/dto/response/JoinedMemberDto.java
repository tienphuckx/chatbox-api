package com.tienphuckx.boxchat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinedMemberDto {
    private Integer memberId;
    private String memberName;
}
