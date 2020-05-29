package com.callbackcats.codenames.lobby.controller;

import com.callbackcats.codenames.game.card.domain.GameLanguage;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.dto.LanguageDetails;
import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.lobby.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lobby")
@Slf4j
public class LobbyController {

    private LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<LobbyDetails> getLobbyById(@PathVariable String id) {
        LobbyDetails lobbyDetails = this.lobbyService.getLobbyDetailsById(id);
        return new ResponseEntity<>(lobbyDetails, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<HttpStatus> setGameLanguage(@PathVariable String id, @RequestBody LanguageDetails gameLanguage) {
        GameLanguage language = GameLanguage.valueOf(gameLanguage.getLanguage());
        this.lobbyService.updateLobbyGameLanguage(id, language);
        log.info("Game language updated to " + gameLanguage.getLanguage() + " in lobby with id: " + id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<LobbyDetails> createLobby() {
        Lobby lobby = new Lobby();
        this.lobbyService.saveNewLobby(lobby);
        LobbyDetails lobbyDetails = new LobbyDetails(lobby);
        log.info("New lobby generation requested");
        return new ResponseEntity<>(lobbyDetails, HttpStatus.CREATED);
    }
}
