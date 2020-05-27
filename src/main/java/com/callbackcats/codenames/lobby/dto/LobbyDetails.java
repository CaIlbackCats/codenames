package com.callbackcats.codenames.lobby.dto;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.player.dto.PlayerData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EntityNotFoundException;
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

    public LobbyDetails(Lobby lobby) {
        this.id = lobby.getId();
        if (lobby.getPlayerList()!=null && !lobby.getPlayerList().isEmpty()){
            this.players = lobby.getPlayerList()
                    .stream()
                    .map(PlayerData::new)
                    .collect(Collectors.toList());
        }
        if (lobby.getGames()!=null && !lobby.getGames().isEmpty()){
            this.currentGameId = lobby.getGames()
                    .stream()
                    .filter(Game::getActive)
                    .findFirst().orElse(new Game())
                    .getId();
        }
    }
}
