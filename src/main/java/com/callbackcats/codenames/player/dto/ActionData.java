package com.callbackcats.codenames.player.dto;

import com.callbackcats.codenames.player.domain.ActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActionData {
    private ActionType actionToDo;
    private PlayerData currentPlayer;

    public ActionData(ActionType initKick) {
        this.actionToDo = initKick;
    }
}
