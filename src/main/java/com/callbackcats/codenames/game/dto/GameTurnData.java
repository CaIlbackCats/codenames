package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.game.domain.GameTurn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameTurnData {

    private String currentTeam;

    private String currentRole;

    public GameTurnData(GameTurn gameTurn) {
        this.currentTeam = String.valueOf(gameTurn.getCurrentTeam());
        this.currentRole = String.valueOf(gameTurn.getCurrentRole());
    }
}
