package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.card.dto.CardDetails;
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

    private List<PuzzleWordData> puzzleWords;

    public GameStateData(Game game, List<CardDetails> board, List<TeamData> teams) {
        this.id = game.getId();
        if (game.getBoard() != null && !game.getBoard().isEmpty()) {
            this.board = game.getBoard()
                    .stream()
                    .map(CardDetails::new)
                    .collect(Collectors.toList());
        }
        this.endGame = game.getEndGame();
        this.endTurn = game.getEndTurn();
        this.winnerTeam = String.valueOf(game.getWinner());
        this.gameEndByAssassin = game.getEndGameByAssassin();
        this.startingTeamColor = String.valueOf(game.getStartingTeam());
        if (game.getTeams() != null && !game.getTeams().isEmpty()) {
            this.teams = game.getTeams()
                    .stream()
                    .map(TeamData::new)
                    .collect(Collectors.toList());
        }
        this.currentTeam = String.valueOf(game.getCurrentTeam());
        this.active = game.getActive();
        this.votingPhase = game.getVotingPhase();
        if (game.getPuzzleWords() != null && !game.getPuzzleWords().isEmpty()) {
            this.puzzleWords = game.getPuzzleWords()
                    .stream()
                    .map(PuzzleWordData::new)
                    .collect(Collectors.toList());
        }

    }
}
