package com.callbackcats.codenames.lobby.player.dto;

import com.callbackcats.codenames.game.dto.GameDetails;
import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.lobby.player.domain.ActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActionData {
    private ActionType actionToDo;
    private PlayerData currentPlayer;
    private PlayerRemovalData playerRemoval;
    private LobbyDetails lobbyDetails;
    private GameDetails gameDetails;

    public ActionData(ActionType initKick) {
        this.actionToDo = initKick;
    }

    public ActionData(ActionType actionToDo, PlayerData playerData) {
        this.actionToDo = actionToDo;
        this.currentPlayer = playerData;
    }

    public ActionData(ActionType actionToDo, GameDetails gameDetails) {
        this.actionToDo = actionToDo;
        this.gameDetails = gameDetails;
    }

    public ActionData(ActionType actionToDo, PlayerRemovalData playerRemovalData) {
        this.actionToDo = actionToDo;
        this.playerRemoval = playerRemovalData;
    }

    public ActionData(ActionType actionToDo, LobbyDetails lobbyDetails) {
        this.actionToDo = actionToDo;
        this.lobbyDetails = lobbyDetails;
    }
}
