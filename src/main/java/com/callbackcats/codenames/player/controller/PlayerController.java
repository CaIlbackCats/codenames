package com.callbackcats.codenames.player.controller;

import com.callbackcats.codenames.player.domain.ActionType;
import com.callbackcats.codenames.player.dto.ActionData;
import com.callbackcats.codenames.player.dto.PlayerCreationData;
import com.callbackcats.codenames.player.dto.PlayerData;
import com.callbackcats.codenames.player.dto.PlayerRemovalData;
import com.callbackcats.codenames.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
public class PlayerController {

    private final PlayerService playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public PlayerController(PlayerService playerService, SimpMessagingTemplate simpMessagingTemplate) {
        this.playerService = playerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/refresh")
    public List<PlayerData> getAllPlayersByLobby(@Payload String lobbyName) {
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyName);
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, players);
        return players;
    }

    @MessageMapping("/create")
    public List<PlayerData> registerNewPlayer(@Payload PlayerCreationData playerCreationData) {
        PlayerData playerData = playerService.savePlayer(playerCreationData);
        String lobbyName = playerCreationData.getLobbyName();
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyName);
        ActionData actionData = new ActionData(ActionType.SET_CURRENT_PLAYER, playerData);
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, players);
        simpMessagingTemplate.convertAndSend("/player/change/" + playerData.getName(), actionData);
        return players;
    }

    @MessageMapping("/role")
    public List<PlayerData> setPlayerRole(@Payload String lobbyName) {
        List<PlayerData> modifiedPlayers = playerService.setPlayerRole(lobbyName);
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, modifiedPlayers);
        return modifiedPlayers;
    }

    @MessageMapping("/side")
    public List<PlayerData> setPlayerSide(@Payload String lobbyName) {
        List<PlayerData> modifiedPlayers = playerService.randomizeTeamSetup(lobbyName);
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, modifiedPlayers);
        return modifiedPlayers;
    }


    @MessageMapping("/kickCount")
    public void countKickVotes(@Payload PlayerRemovalData playerRemovalData) {
        playerService.setPlayerKickScore(playerRemovalData);
    }

    @MessageMapping("/kickByVote")
    public List<PlayerData> removePlayerByVote(@Payload PlayerRemovalData playerRemovalData) {
        ActionData actionData = playerService.removePlayerByVote(playerRemovalData);
        if (actionData != null) {
            PlayerData playerData = actionData.getCurrentPlayer();
            simpMessagingTemplate.convertAndSend("/player/change/" + playerData.getName(), actionData);
        }
        String lobbyName = playerService.findPlayerDataById(playerRemovalData.getOwnerId()).getLobbyName();
        List<PlayerData> modifiedPlayers = playerService.getPlayerDataListByLobbyName(lobbyName);
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, modifiedPlayers);
        return modifiedPlayers;
    }

    @MessageMapping("/kickByOwner")
    public List<PlayerData> removePlayerByOwner(@Payload PlayerRemovalData playerRemovalData) {
        PlayerData removedPlayer = playerService.removePlayerByOwner(playerRemovalData);
        ActionData actionData = new ActionData(ActionType.GET_KICKED);
        String lobbyName = playerService.findPlayerDataById(playerRemovalData.getOwnerId()).getLobbyName();
        List<PlayerData> modifiedPlayers = playerService.getPlayerDataListByLobbyName(lobbyName);
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, modifiedPlayers);
        simpMessagingTemplate.convertAndSend("/player/change/" + removedPlayer.getName(), actionData);
        return modifiedPlayers;
    }


    @MessageMapping("/kickInit")
    public ActionData initKick(@Payload String playerName) {
        ActionData actionData = new ActionData(ActionType.INIT_KICK);
        simpMessagingTemplate.convertAndSend("/player/change/" + playerName, actionData);
        return actionData;
    }

}
