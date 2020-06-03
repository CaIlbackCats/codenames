package com.callbackcats.codenames.player.controller;

import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.player.dto.*;
import com.callbackcats.codenames.player.service.PlayerService;
import com.callbackcats.codenames.lobby.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

@Controller
@Slf4j
public class PlayerController {

    private final PlayerService playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LobbyService lobbyService;

    public PlayerController(PlayerService playerService, SimpMessagingTemplate simpMessagingTemplate, LobbyService lobbyService) {
        this.playerService = playerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.lobbyService = lobbyService;
    }


    @ResponseBody
    @PostMapping("/api/createPlayer")
    public ResponseEntity<PlayerData> createPlayer(@RequestBody PlayerCreationData playerCreationData) {
        log.info("Player Creation requested");
        PlayerData savedPlayer = playerService.savePlayer(playerCreationData);
        ResponseEntity<PlayerData> responseEntity;
        if (savedPlayer != null) {
            responseEntity = new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return responseEntity;
    }

    @ResponseBody
    @PostMapping("/api/updateVisiblePlayer")
    public ResponseEntity<PlayerData> updatePlayerVisibility(@RequestBody PlayerDetailsData playerDetailsData) {
        log.info("Player visibility change requested");
        PlayerData playerData = playerService.setPlayerVisible(playerDetailsData);

        return new ResponseEntity<>(playerData, HttpStatus.OK);
    }

    @MessageMapping("/role")
    public void setPlayerRole(@Payload RoleSelectionData roleSelectionData) {
        log.info("Player random role setting requested");
        String lobbyId = roleSelectionData.getLobbyId();
        playerService.setPlayerRole(lobbyId);
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyId);
        players.forEach(player -> {
            updatePlayer(lobbyId, player.getId(), player);
        });
        updateLobbyState(lobbyId);
    }

    @MessageMapping("/side")
    public void setPlayerSide(@Payload SideSelectionData sideSelectionData) {
        log.info("Player randomize role and side requested");
        String lobbyId = sideSelectionData.getLobbyId();
        playerService.randomizeTeamSetup(lobbyId);
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyId);
        players.forEach(player -> {
            updatePlayer(lobbyId, player.getId(), player);
        });
        updateLobbyState(lobbyId);
    }

    @MessageMapping("/{lobbyId}/kickCount")
    @SendTo("/lobby/{lobbyId}")
    public LobbyDetails countKickVotes(@DestinationVariable String lobbyId, @Payload PlayerRemovalData playerRemovalData) {
        log.info("Player kick count modification requested");
        playerService.processKickBeforeCountDown(playerRemovalData);

        return lobbyService.getLobbyDetailsById(lobbyId);
    }

    @MessageMapping("/{lobbyId}/kickInit")
    public void initKick(@DestinationVariable String lobbyId, @Payload PlayerRemovalData playerRemovalData) {
        log.info("Initiate kicking requested");

        playerService.setPlayerRemoval(playerRemovalData);
        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyId + "/kick", playerRemovalData);
        updateLobbyState(lobbyId);

        Boolean initPlayerLobbyOwner = playerService.isInitPlayerLobbyOwner(playerRemovalData.getPlayerInitId());
        if (!initPlayerLobbyOwner) {
            startKickPhase(lobbyId, playerRemovalData);
        }

    }

    private void startKickPhase(@DestinationVariable String lobbyId, @Payload PlayerRemovalData playerRemovalData) {
        try {
            lobbyService.setKickPhase(lobbyId, true);
            ScheduledFuture<?> votingFinished = playerService.initVotingPhase(playerRemovalData);
            votingFinished.get();

            if (!playerService.isLobbyOwnerInLobby(lobbyId)) {
                PlayerData newLobbyOwner = playerService.reassignLobbyOwner(lobbyId);
                updatePlayer(lobbyId, newLobbyOwner.getId(), newLobbyOwner);
            }
            lobbyService.setKickPhase(lobbyId, false);
            updateLobbyState(lobbyId);
        } catch (InterruptedException | ExecutionException e) {
            log.warn(e.getMessage());
        }
    }

    @MessageMapping("/{lobbyId}/ready")
    public void setRdyState(@DestinationVariable String lobbyId, @Payload RdyStateData rdyStateData) {
        log.info("Ready state change is requested");
        PlayerData playerData = playerService.setRdyState(rdyStateData);

        updatePlayer(lobbyId, playerData.getId(), playerData);
        updateLobbyState(lobbyId);
    }

    @MessageMapping("/selection")
    public void setPlayerSideAndRole(@Payload SelectionData selectionData) {
        log.info("Player selection requested");
        PlayerData modifiedPlayer = playerService.setPlayerSideAndRole(selectionData);
        String lobbyName = modifiedPlayer.getLobbyId();

        updatePlayer(lobbyName, modifiedPlayer.getId(), modifiedPlayer);
        updateLobbyState(lobbyName);
    }

    @MessageMapping("/{lobbyId}/{playerId}/hidePlayer")
    @SendTo("/lobby/{lobbyId}")
    public LobbyDetails hidePlayer(@DestinationVariable String lobbyId, @DestinationVariable Long playerId) {
        playerService.hidePlayer(playerId);

        return lobbyService.getLobbyDetailsById(lobbyId);
    }

    private void updateLobbyState(String lobbyId) {

        LobbyDetails lobbyDetails = lobbyService.getLobbyDetailsById(lobbyId);
        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyId, lobbyDetails);
    }

    private void updatePlayer(String lobbyId, Long playerId, PlayerData updatedPlayer) {
        simpMessagingTemplate.convertAndSend("/player/" + lobbyId + "/" + playerId, updatedPlayer);
    }

}
