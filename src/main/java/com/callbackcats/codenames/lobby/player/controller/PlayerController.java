package com.callbackcats.codenames.lobby.player.controller;

import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.lobby.player.dto.*;
import com.callbackcats.codenames.lobby.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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

    public PlayerController(PlayerService playerService, SimpMessagingTemplate simpMessagingTemplate) {
        this.playerService = playerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @ResponseBody
    @PostMapping("/api/createPlayer")
    public ResponseEntity<PlayerData> createPlayer(@RequestBody PlayerCreationData playerCreationData) {
        log.info("Player Creation requested");
        PlayerData savedPlayer = playerService.savePlayer(playerCreationData);

        return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
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
        updateList(lobbyId);
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
        updateList(lobbyId);
    }

    @MessageMapping("/kickCount")
    public void countKickVotes(@Payload PlayerRemovalData playerRemovalData) {
        log.info("Player kick count modification requested");
        playerService.processKickBeforeCountDown(playerRemovalData);
        String lobbyName = playerService.findPlayerDataById(playerRemovalData.getOwnerId()).getLobbyId();

        updateList(lobbyName);
    }

    @MessageMapping("/getPlayers")
    public void kickPlayer(@Payload PlayerRemovalData playerRemovalData) {
        log.info("Kicking player requested");
        String lobbyName = playerService.findPlayerDataById(playerRemovalData.getOwnerId()).getLobbyId();

        updateList(lobbyName);
    }

    @MessageMapping("/kickInit")
    public void initKick(@Payload PlayerRemovalData playerRemovalData) {
        log.info("Initiate kicking requested");

        playerService.setPlayerRemoval(playerRemovalData);
        //todo set kicking phase and send refreshed lobby back

        String lobbyName = playerService.findPlayerDataById(playerRemovalData.getOwnerId()).getLobbyId();

        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyName + "/kick", playerRemovalData);

        try {
            ScheduledFuture<?> votingFinished = playerService.isVotingFinished(playerRemovalData);
            votingFinished.get();

            if (!playerService.isLobbyOwnerInLobby(lobbyName)) {
                PlayerData newLobbyOwner = playerService.reassignLobbyOwner(lobbyName);
                updatePlayer(lobbyName, newLobbyOwner.getId(), newLobbyOwner);
            }

            updateList(lobbyName);
        } catch (InterruptedException | ExecutionException e) {
            log.info(e.getMessage());
        }
    }

    @MessageMapping("/rdy")
    public void setRdyState(@Payload RdyStateData rdyStateData) {
        log.info("Ready state change is requested");
        PlayerData playerData = playerService.setRdyState(rdyStateData);
        String lobbyName = playerData.getLobbyId();
        updatePlayer(lobbyName, playerData.getId(), playerData);

        updateList(lobbyName);
    }

    @MessageMapping("/selection")
    public void setPlayerSideAndRole(@Payload SelectionData selectionData) {
        log.info("Player selection requested");
        PlayerData modifiedPlayer = playerService.setPlayerSideAndRole(selectionData);
        String lobbyName = modifiedPlayer.getLobbyId();
        updatePlayer(lobbyName, modifiedPlayer.getId(), modifiedPlayer);

        updateList(lobbyName);
    }

    @MessageMapping("/getPlayer")
    public void getPlayer(@Payload PlayerDetailsData playerDetailsData) {
        log.info("Get player requested");
        String lobbyName = playerDetailsData.getLobbyName();
        if (playerService.isGivenPlayerInLobby(playerDetailsData)) {
            PlayerData player = playerService.showPlayer(playerDetailsData.getId());
            updatePlayer(lobbyName, player.getId(), player);

            updateList(playerDetailsData.getLobbyName());
        }
    }

    @MessageMapping("/fetchLobby")
    public void fetchLobby(@Payload LobbyDetails lobbyDetails) {
        log.info("Lobby fetch requested");

        updateList(lobbyDetails.getId());
    }

    @MessageMapping("/hidePlayer")
    public void hidePlayer(@Payload PlayerDetailsData playerDetailsData) {
        playerService.hidePlayer(playerDetailsData.getId());

        updateList(playerDetailsData.getLobbyName());
    }

    private void updateList(String lobbyName) {
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyName);
        LobbyDetails lobbyDetails = new LobbyDetails();

        lobbyDetails.setEveryoneRdy(playerService.isEveryOneRdy(lobbyName));
        lobbyDetails.setPlayers(players);
        lobbyDetails.setId(lobbyName);
        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyName, lobbyDetails);

        RemainingRoleData remainingRoleData = playerService.getRemainingRoleData(lobbyName);
        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyName + "/roleData", remainingRoleData);


    }

    private void updatePlayer(String lobbyId, Long playerId, PlayerData updatedPlayer) {
        simpMessagingTemplate.convertAndSend("/player/" + lobbyId + "/" + playerId, updatedPlayer);
    }

}
