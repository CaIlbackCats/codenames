package com.callbackcats.codenames.lobby.player.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerCreationData {

    private String lobbyName;

    private String name;
}
