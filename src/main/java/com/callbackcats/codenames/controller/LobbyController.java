package com.callbackcats.codenames.controller;

import com.callbackcats.codenames.domain.Lobby;
import com.callbackcats.codenames.dto.LobbyDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lobby")
@Slf4j
public class LobbyController {

    @GetMapping
    public ResponseEntity<LobbyDetails> getNewLobby() {
        Lobby lobby = new Lobby();
        LobbyDetails lobbyDetails = new LobbyDetails(lobby);
        log.info("New lobby generation requested");
        return new ResponseEntity<>(lobbyDetails, HttpStatus.OK);
    }

}
