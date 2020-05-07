package com.callbackcats.codenames.dto;

import com.callbackcats.codenames.domain.Lobby;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LobbyDetails {
    private String id;

    public LobbyDetails(Lobby lobby) {
        this.id = lobby.getId();
    }
}
