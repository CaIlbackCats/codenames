package com.callbackcats.codenames.lobby_chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageData {
    private Integer id;
    private String name;
    private String message;
}
