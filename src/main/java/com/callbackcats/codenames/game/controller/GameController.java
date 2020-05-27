package com.callbackcats.codenames.game.controller;

import com.callbackcats.codenames.game.dto.GameDetails;
import com.callbackcats.codenames.game.dto.GameStateData;
import com.callbackcats.codenames.game.dto.PayloadData;
import com.callbackcats.codenames.game.dto.TeamVoteData;
import com.callbackcats.codenames.game.service.GameService;
import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.lobby.service.LobbyService;
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

@Controller
@Slf4j
public class GameController {

    private GameService gameService;
    private final LobbyService lobbyService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameController(GameService gameService, LobbyService lobbyService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.lobbyService = lobbyService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @ResponseBody
    @PostMapping("/api/game")
    public ResponseEntity<GameDetails> createGame(@RequestBody PayloadData payloadData) {
        log.info("Game creation requested");
        GameDetails gameDetails = gameService.createGame(payloadData.getLobbyId());

        return new ResponseEntity<>(gameDetails, HttpStatus.CREATED);
    }

    @MessageMapping("/fetchGame")
    public void createGames(@Payload LobbyDetails lobbyDetails) {

        LobbyDetails modifiedLobby = lobbyService.getLobbyDetailsById(lobbyDetails.getId());

        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyDetails.getId(), modifiedLobby);
    }

    @MessageMapping("/processVote")
    public void processVote(@Payload TeamVoteData teamVoteData) {
        log.info("Process vote requested");
        GameStateData gameStateData = gameService.processVotes(teamVoteData);

        //todo send gamestate to lobby/gameId
        simpMessagingTemplate.convertAndSend("/lobby/" + gameStateData.getLobbyId() + "/" + gameStateData.getId());
    }
}
