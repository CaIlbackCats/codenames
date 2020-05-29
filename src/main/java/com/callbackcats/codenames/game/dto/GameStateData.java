package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.game.domain.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameStateData {

    //todo add teamData

    private Long id;

    private Integer blueScore;

    private Integer redScore;

    private Boolean endGame;

    private Boolean endTurn;

    private String winnerTeam;

    private Boolean gameEndByAssassin;

    private String startingTeamColor;

    private String lobbyId;

    public GameStateData(Game game) {
        this.id = game.getId();
        this.endGame = game.getEndGame();
        this.endTurn = game.getEndTurn();
        this.winnerTeam = game.getWinner().toString();
        this.gameEndByAssassin = game.getEndGameByAssassin();
        this.startingTeamColor = game.getStartingTeamColor().toString();
        this.lobbyId = game.getLobby().getId();
    }
}
