package com.callbackcats.codenames.player.dto;

import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.player.domain.ActionType;
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
    private List<PlayerData> playerList;
    private PlayerRemovalData playerRemoval;
    private LobbyDetails lobbyDetails;

    public ActionData(ActionType initKick) {
        this.actionToDo = initKick;
    }

    public ActionData(ActionType actionToDo, PlayerData playerData) {
        this.actionToDo = actionToDo;
        this.currentPlayer = playerData;
    }

    public ActionData(ActionType actionToDo, List<PlayerData> modifiedPlayers) {
        this.actionToDo = actionToDo;
        this.playerList = modifiedPlayers;
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
