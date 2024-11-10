package com.tienphuckx.boxchat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    private Integer id;
    private Integer userId;
    private Integer groupId;
}
