package com.callbackcats.codenames.player.controller;

import com.callbackcats.codenames.player.dto.PlayerCreationData;
import com.callbackcats.codenames.player.dto.PlayerData;
import com.callbackcats.codenames.player.dto.PlayerRemovalData;
import com.callbackcats.codenames.player.service.PlayerService;
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
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, players);
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

    @MessageMapping("/removeByVote")
    public List<PlayerData> removePlayerByVote(@Payload PlayerRemovalData playerRemovalData) {
        String lobbyName = playerService.removePlayerByVote(playerRemovalData);
        List<PlayerData> modifiedPlayers = playerService.getPlayerDataListByLobbyName(lobbyName);
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, modifiedPlayers);
        return modifiedPlayers;
    }

    @MessageMapping("/removeByOwner")
    public List<PlayerData> removePlayerByOwner(@Payload PlayerRemovalData playerRemovalData) {
        String lobbyName = playerService.removePlayerByOwner(playerRemovalData);
        List<PlayerData> modifiedPlayers = playerService.getPlayerDataListByLobbyName("");
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, modifiedPlayers);
        return modifiedPlayers;
    }
}
