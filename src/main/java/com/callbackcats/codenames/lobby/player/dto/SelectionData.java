package com.callbackcats.codenames.lobby.player.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SelectionData {
    private String role;
    private String side;
    private Long playerId;
}
