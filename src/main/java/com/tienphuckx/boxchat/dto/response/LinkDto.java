package com.tienphuckx.boxchat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkDto {
    private String href;
    private String domain;
    private String imagePath;
}
