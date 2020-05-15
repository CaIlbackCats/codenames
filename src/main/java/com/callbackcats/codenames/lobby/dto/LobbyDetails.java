package com.callbackcats.codenames.lobby.dto;

import com.callbackcats.codenames.lobby.domain.Lobby;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LobbyDetails {
    private String id;

    private Boolean everyOneRdy;

    public LobbyDetails(Lobby lobby) {
        this.id = lobby.getId();
    }

    public LobbyDetails(Boolean everyOneRdy) {
        this.everyOneRdy = everyOneRdy;
    }
}
