package com.callbackcats.codenames.lobby.player.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RdyStateData {
    private Long playerId;
    private Boolean rdyState;
}
