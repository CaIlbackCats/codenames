package com.callbackcats.codenames.lobby.lobby_chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class MessageData {
    private Integer id;
    private String name;
    private String message;
    private String lobbyName;
}
