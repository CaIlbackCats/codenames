package com.callbackcats.codenames.lobby.dto;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.player.dto.PlayerData;
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

    private Boolean everyoneRdy;

    private Long currentGameId;

    private Boolean kickingPhase;

    private String gameLanguage;

    public LobbyDetails(Lobby lobby) {
        this.id = lobby.getId();
        if (lobby.getPlayerList() != null && !lobby.getPlayerList().isEmpty()) {
            this.players = lobby.getPlayerList()
                    .stream()
                    .map(PlayerData::new)
                    .collect(Collectors.toList());
        }
        if (lobby.getGames() != null && !lobby.getGames().isEmpty()) {
            this.currentGameId = lobby.getGames()
                    .stream()
                    .filter(Game::getActive)
                    .findFirst().orElseThrow(() -> new RuntimeException("No active game exists"))
                    .getId();
        }
        this.kickingPhase = lobby.getKickingPhase();
        this.gameLanguage = String.valueOf(lobby.getGameLanguage());
    }
}
