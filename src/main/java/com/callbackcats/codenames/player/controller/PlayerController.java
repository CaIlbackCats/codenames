package com.callbackcats.codenames.player.controller;

import com.callbackcats.codenames.player.domain.ActionType;
import com.callbackcats.codenames.player.domain.KickType;
import com.callbackcats.codenames.player.dto.ActionData;
import com.callbackcats.codenames.player.dto.PlayerCreationData;
import com.callbackcats.codenames.player.dto.PlayerData;
import com.callbackcats.codenames.player.dto.PlayerRemovalData;
import com.callbackcats.codenames.player.service.PlayerService;
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

    private void setKickMsg(PlayerRemovalData playerRemovalData, String lobbyName, Boolean isPlayerRemoved) {
        if (isPlayerRemoved) {
            ActionData kickAction = new ActionData(ActionType.GET_KICKED);
            simpMessagingTemplate.convertAndSend("/player/" + lobbyName + "/" + playerRemovalData.getPlayerToRemoveId(), kickAction);
            if (!playerService.isLobbyOwnerInLobby(lobbyName)) {
                PlayerData newOwner = playerService.reassignLobbyOwner(lobbyName);
                ActionData updatePlayer = new ActionData(ActionType.UPDATE_PLAYER, newOwner);
                simpMessagingTemplate.convertAndSend("/player/" + lobbyName + "/" + newOwner.getId(), updatePlayer);
            }
        }
    }


    @MessageMapping("/kickInit")
    public ActionData initKick(@Payload PlayerRemovalData playerRemovalData) {
        log.info("Initiate kicking requested");
        ActionData actionData = new ActionData(ActionType.INIT_KICK, playerRemovalData);
        String lobbyName = playerService.findPlayerDataById(playerRemovalData.getOwnerId()).getLobbyName();
        if (playerRemovalData.getKickType() == KickType.OWNER) {
            simpMessagingTemplate.convertAndSend("/player/" + lobbyName + "/" + playerRemovalData.getOwnerId(), actionData);
        } else {
            playerRemovalData.getVotingPlayers()
                    .forEach(player -> simpMessagingTemplate.convertAndSend("/player/" + lobbyName + "/" + player.getId(), actionData));
        }

        return actionData;
    }

    private ActionData updateList(String lobbyName) {
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyName);
        ActionData actionData = new ActionData(ActionType.UPDATE_LIST, players);
        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyName, actionData);
        return actionData;
    }


}
