package com.callbackcats.codenames.game.team.dto;

import com.callbackcats.codenames.game.team.domain.Stat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatData {

    private Long id;

    private Integer numOfCivilians;
    private Integer numOfEnemySpies;

    private Integer teamRounds;

    private Integer maxFoundInARow;
    private Integer minFoundInARow;

    private Integer numOfPasses;
    private Integer numOfInvalidVotes;

    public StatData(Stat stat) {
        this.id = stat.getId();
        this.numOfCivilians = stat.getNumOfCivilians();
        this.numOfEnemySpies = stat.getNumOfEnemySpies();
        this.maxFoundInARow = stat.getMaxFoundInARow();
        this.minFoundInARow = stat.getMinFoundInARow();
        this.numOfPasses = stat.getNumOfPasses();
        this.teamRounds = stat.getTeamRounds();
        this.numOfInvalidVotes = stat.getNumOfInvalidVotes();
    }
}
