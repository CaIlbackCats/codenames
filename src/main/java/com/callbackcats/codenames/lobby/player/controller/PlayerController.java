package com.callbackcats.codenames.lobby.player.controller;

import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.lobby.player.domain.ActionType;
import com.callbackcats.codenames.lobby.player.domain.KickType;
import com.callbackcats.codenames.lobby.player.dto.*;
import com.callbackcats.codenames.lobby.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
public class PlayerController {

    private final PlayerService playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public PlayerController(PlayerService playerService, SimpMessagingTemplate simpMessagingTemplate) {
        this.playerService = playerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/create")
    public ActionData registerNewPlayer(@Payload PlayerCreationData playerCreationData) {
        log.info("Player creation requested");
        PlayerData playerData = playerService.savePlayer(playerCreationData);
        String lobbyName = playerCreationData.getLobbyName();
        ActionData actionData = new ActionData(ActionType.CREATE_PLAYER, playerData);
        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyName, actionData);

//        Boolean everyOneRdy = playerService.isEveryOneRdy(lobbyName);
//        LobbyDetails lobbyDetails = new LobbyDetails(everyOneRdy);
//        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyName, new ActionData(ActionType.UPDATE_LOBBY, lobbyDetails));

        return updateList(lobbyName);
    }

    @MessageMapping("/role")
    public ActionData setPlayerRole(@Payload String lobbyName) {
        log.info("Player random role setting requested");
        playerService.setPlayerRole(lobbyName);
        return updateList(lobbyName);
    }

    @MessageMapping("/side")
    public ActionData setPlayerSide(@Payload String lobbyName) {
        log.info("Player randomize role and side requested");
        playerService.randomizeTeamSetup(lobbyName);
        return updateList(lobbyName);
    }

    @MessageMapping("/kickCount")
    public void countKickVotes(@Payload PlayerRemovalData playerRemovalData) {
        log.info("Player kick count modification requested");
        playerService.setPlayerKickScore(playerRemovalData);
    }

    @MessageMapping("/kick")
    public ActionData kickPlayer(@Payload PlayerRemovalData playerRemovalData) {
        log.info("Kicking player requested");
        String lobbyName = playerService.findPlayerDataById(playerRemovalData.getOwnerId()).getLobbyName();
        if (playerRemovalData.getKickType() == KickType.OWNER) {
            setKickMsg(playerRemovalData, lobbyName, playerService.removePlayerByOwner(playerRemovalData));
        } else {
            setKickMsg(playerRemovalData, lobbyName, playerService.isPlayerRemovedByVote(playerRemovalData));
        }

        return updateList(lobbyName);
    }

    @MessageMapping("/kickInit")
    public ActionData initKick(@Payload PlayerRemovalData playerRemovalData) {
        log.info("Initiate kicking requested");
        ActionData initializeKick = new ActionData(ActionType.INIT_KICK, playerRemovalData);
        String lobbyName = playerService.findPlayerDataById(playerRemovalData.getOwnerId()).getLobbyName();
        if (playerRemovalData.getKickType() == KickType.OWNER) {
            setPlayerChangeMsg(lobbyName, playerRemovalData.getOwnerId(), initializeKick);
        } else {
            playerRemovalData.getVotingPlayers()
                    .forEach(player -> setPlayerChangeMsg(lobbyName, player.getId(), initializeKick));
        }

        return initializeKick;
    }

    @MessageMapping("/rdy")
    public ActionData setRdyState(@Payload RdyStateData rdyStateData) {
        log.info("Ready state change is requested");
        PlayerData playerData = playerService.setRdyState(rdyStateData);
        ActionData setRdyAction = new ActionData(ActionType.UPDATE_PLAYER, playerData);
        String lobbyName = playerData.getLobbyName();
        setPlayerChangeMsg(lobbyName, playerData.getId(), setRdyAction);

        //todo refactor
//        Boolean everyOneRdy = playerService.isEveryOneRdy(lobbyName);
//        LobbyDetails lobbyDetails = new LobbyDetails(everyOneRdy);
//        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyName, new ActionData(ActionType.UPDATE_LOBBY, lobbyDetails));


        return updateList(lobbyName);
    }

    @MessageMapping("/selection")
    public ActionData setPlayerSideAndRole(@Payload SelectionData selectionData) {
        log.info("Player selection requested");
        PlayerData modifiedPlayer = playerService.setPlayerSideAndRole(selectionData);
        String lobbyName = modifiedPlayer.getLobbyName();
        ActionData updatePlayer = new ActionData(ActionType.UPDATE_PLAYER, modifiedPlayer);
        setPlayerChangeMsg(lobbyName, modifiedPlayer.getId(), updatePlayer);

        return updateList(lobbyName);
    }

    private ActionData updateList(String lobbyName) {
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyName);
        LobbyDetails lobbyDetails = new LobbyDetails();

        playerService.getTeamsByRoles(lobbyName, lobbyDetails);
        lobbyDetails.setEveryOneRdy(playerService.isEveryOneRdy(lobbyName));
        lobbyDetails.setPlayers(players);

        ActionData actionData = new ActionData(ActionType.UPDATE_LOBBY, lobbyDetails);
        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyName, actionData);
        return actionData;
    }

    private void setPlayerChangeMsg(String lobbyName, Long id, ActionData actionData) {
        simpMessagingTemplate.convertAndSend("/player/" + lobbyName + "/" + id, actionData);
    }

    private void setKickMsg(PlayerRemovalData playerRemovalData, String lobbyName, Boolean isPlayerRemoved) {
        if (isPlayerRemoved) {
            ActionData kickAction = new ActionData(ActionType.GET_KICKED);
            setPlayerChangeMsg(lobbyName, playerRemovalData.getPlayerToRemoveId(), kickAction);
            if (!playerService.isLobbyOwnerInLobby(lobbyName)) {
                PlayerData newOwner = playerService.reassignLobbyOwner(lobbyName);
                ActionData updatePlayer = new ActionData(ActionType.UPDATE_PLAYER, newOwner);
                setPlayerChangeMsg(lobbyName, newOwner.getId(), updatePlayer);
            }
        }
    }


}
