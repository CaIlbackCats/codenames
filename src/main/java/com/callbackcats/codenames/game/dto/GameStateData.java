package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.game.card.dto.CardDetails;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.team.dto.TeamData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameStateData {

    //todo add teamData

    private Long id;

    private List<CardDetails> board;

    private Boolean endGame;

    private Boolean endTurn;

    private String winnerTeam;

    private Boolean gameEndByAssassin;

    private String startingTeamColor;

    private List<TeamData> teams;

    private String currentTeam;

    private Boolean active;

    private Boolean votingPhase;


    public GameStateData(Game game) {
        this.id = game.getId();
        this.board = game.getBoard().stream().map(CardDetails::new).collect(Collectors.toList());
        this.endGame = game.getEndGame();
        this.endTurn = game.getEndTurn();
        this.winnerTeam = game.getWinner().toString();
        this.gameEndByAssassin = game.getEndGameByAssassin();
        this.startingTeamColor = game.getStartingTeamColor().toString();
        this.teams = game.getTeams().stream().map(TeamData::new).collect(Collectors.toList());
        this.currentTeam = game.getCurrentTeam().toString();
        this.active = game.getActive();
        this.votingPhase = game.getVotingPhase();
    }
}
