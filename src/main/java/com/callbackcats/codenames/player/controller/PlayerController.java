package com.callbackcats.codenames.player.controller;

import com.callbackcats.codenames.player.dto.PlayerCreationData;
import com.callbackcats.codenames.player.dto.PlayerData;
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

//@RestController
//@RequestMapping("/api/players")
@Controller
public class PlayerController {

    private final PlayerService playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public PlayerController(PlayerService playerService, SimpMessagingTemplate simpMessagingTemplate) {
        this.playerService = playerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

//    @MessageMapping("/create")
//    public List<PlayerData> addPlayer(@Payload PlayerCreationData playerCreationData) {
//        playerService.savePlayer(playerCreationData);
//        List<PlayerData> playerList = playerService.getPlayerDataListByLobbyName(playerCreationData.getLobbyName());
//        simpMessagingTemplate.convertAndSend("/topic/options/" + playerCreationData.getLobbyName(), playerList);
//        return playerList;
//    }

    @MessageMapping("/refresh")
    public List<PlayerData> getAllPlayersByLobby2(@Payload String lobbyName) {
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyName);
        simpMessagingTemplate.convertAndSend("/topic/options/" + lobbyName, players);
        return players;
    }

    @MessageMapping("/create")
    public List<PlayerData> registerNewPlayer(@Payload PlayerCreationData playerCreationData) {
        playerService.savePlayer(playerCreationData);
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

/*    @PostMapping
    public ResponseEntity<PlayerData> addPlayer(@RequestBody PlayerCreationData playerCreationData) {
        PlayerData addedPlayer = playerService.savePlayer(playerCreationData);

        return new ResponseEntity<>(addedPlayer, HttpStatus.CREATED);
    }

    @PutMapping("/role")
    public ResponseEntity<List<PlayerData>> setPlayerRole(@RequestBody String lobbyName) {
        List<PlayerData> modifiedPlayers = playerService.setPlayerRole(lobbyName);
        return new ResponseEntity<>(modifiedPlayers, HttpStatus.OK);
    }

    @PutMapping("/side")
    public ResponseEntity<List<PlayerData>> setPlayerSide(@RequestBody String lobbyName) {
        List<PlayerData> modifiedPlayers = playerService.randomizeTeamSetup(lobbyName);
        return new ResponseEntity<>(modifiedPlayers, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PlayerData>> getAllPlayersByLobby(@RequestParam("lobbyName") String lobbyName) {
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyName);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }*/

}
