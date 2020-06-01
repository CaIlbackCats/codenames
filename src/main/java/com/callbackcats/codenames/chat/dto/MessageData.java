package com.callbackcats.codenames.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class MessageData {
    private Integer id;
    private Long playerId;
    private String name;
    private String teamColor;
    private String message;
}
