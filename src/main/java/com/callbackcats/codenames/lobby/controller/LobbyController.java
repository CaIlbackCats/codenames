package com.callbackcats.codenames.lobby.controller;

import com.callbackcats.codenames.card.domain.GameLanguage;
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

    @PostMapping("/{lobbyId}")
    public ResponseEntity<LobbyDetails> setGameLanguage(@PathVariable String lobbyId, @RequestParam(value="lang") String lang, @RequestBody LanguageDetails gameLanguage) {
        GameLanguage language = GameLanguage.valueOf(gameLanguage.getLanguage());
        LobbyDetails updatedLobby = this.lobbyService.updateLobbyGameLanguage(lobbyId, language);
        log.info("Game language updated to " + gameLanguage.getLanguage() + " in lobby with id: " + lobbyId);
        return new ResponseEntity<>(updatedLobby, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<LobbyDetails> createLobby(@RequestBody LanguageDetails gameLanguage) {
//        GameLanguage language = GameLanguage.valueOf(gameLanguage.getLanguage());
        GameLanguage language;
        if(gameLanguage.getLanguage() == null) {
            language = GameLanguage.ENGLISH;
        } else if (gameLanguage.getLanguage().equals("hu")) {
            language = GameLanguage.HUNGARIAN;
        } else {
            language = GameLanguage.ENGLISH;
        }
        Lobby lobby = new Lobby();
        lobby.setGameLanguage(language);
        this.lobbyService.saveNewLobby(lobby);
        LobbyDetails lobbyDetails = new LobbyDetails(lobby);
        System.out.println(lobbyDetails.getGameLanguage());
        log.info("New lobby generation requested");
        return new ResponseEntity<>(lobbyDetails, HttpStatus.CREATED);
    }
}
