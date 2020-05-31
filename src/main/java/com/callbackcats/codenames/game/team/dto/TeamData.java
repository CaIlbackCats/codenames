package com.callbackcats.codenames.game.team.dto;

import com.callbackcats.codenames.game.dto.PuzzleWordData;
import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.player.dto.PlayerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamData {

    private Long id;

    private String side;

    private Integer score;

    private List<PlayerData> players;

    private List<PuzzleWordData> puzzleWords;

    public TeamData(Team team) {
        this.id = team.getId();
        this.side = team.getSide().toString();
        this.score = team.getScore();
        if (team.getPlayers() != null && !team.getPlayers().isEmpty()) {
            this.players = team.getPlayers()
                    .stream()
                    .map(PlayerData::new)
                    .collect(Collectors.toList());
        }
        if (team.getPuzzleWords() != null && !team.getPuzzleWords().isEmpty()) {
            this.puzzleWords = team.getPuzzleWords()
                    .stream()
                    .map(PuzzleWordData::new)
                    .collect(Collectors.toList());
        }
    }
}
