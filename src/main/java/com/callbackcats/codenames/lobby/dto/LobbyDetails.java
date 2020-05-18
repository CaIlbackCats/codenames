package com.callbackcats.codenames.lobby.dto;

import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.player.dto.PlayerData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Setter
public class LobbyDetails {
    private String id;

    private List<PlayerData> players;

    private Boolean everyOneRdy;

    private Boolean blueSpymaster;

    private Boolean blueSpy;

    private Boolean redSpymaster;

    private Boolean redSpy;

    public LobbyDetails(Lobby lobby) {
        this.id = lobby.getId();
    }

    public LobbyDetails(Boolean everyOneRdy) {
        this.everyOneRdy = everyOneRdy;
    }
}
