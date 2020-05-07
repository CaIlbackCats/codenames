package com.callbackcats.codenames.player.controller;

import com.callbackcats.codenames.player.dto.PlayerData;
import com.callbackcats.codenames.player.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerData> addPlayer(@RequestBody String name) {
        PlayerData addedPlayer = playerService.savePlayer(name);

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
    public ResponseEntity<List<PlayerData>> getAllPlayersByLobby(String lobbyName) {
        List<PlayerData> players = playerService.getPlayerDataListByLobbyName(lobbyName);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

}
