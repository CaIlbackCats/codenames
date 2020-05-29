package com.callbackcats.codenames.game.controller;

import com.callbackcats.codenames.game.card.dto.CardVoteData;
import com.callbackcats.codenames.game.dto.GameStateData;
import com.callbackcats.codenames.game.dto.PayloadData;
import com.callbackcats.codenames.game.service.GameService;
import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.lobby.player.service.PlayerService;
import com.callbackcats.codenames.lobby.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

@Controller
@Slf4j
public class GameController {

    private final GameService gameService;
    private final LobbyService lobbyService;
    private final PlayerService playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameController(GameService gameService, LobbyService lobbyService, PlayerService playerService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.lobbyService = lobbyService;
        this.playerService = playerService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @ResponseBody
    @PostMapping("/api/game")
    public ResponseEntity<GameStateData> createGame(@RequestBody PayloadData payloadData) {
        log.info("Game creation requested");
        GameStateData game = gameService.createGame(payloadData.getLobbyId());

        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }

    @MessageMapping("/fetchGame")
    public void createGames(@Payload LobbyDetails lobbyDetails) {

        LobbyDetails modifiedLobby = lobbyService.getLobbyDetailsById(lobbyDetails.getId());

        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyDetails.getId(), modifiedLobby);
    }

    @MessageMapping("/cardVote/{gameId}")
    public void voteForCard(@DestinationVariable Long gameId, @Payload CardVoteData cardVoteData) {

        log.info("Player vote requested");
        playerService.setCardVote(cardVoteData);

        ScheduledFuture<?> future = gameService.startVotingPhase(gameId);
        try {
            future.get();
            gameService.changeGameVotingPhase(false, gameId);
            log.info("Player card vote finished");
            updateGameMessage(gameId);
        } catch (InterruptedException | ExecutionException e) {
            log.info(e.getMessage());
        }
    }


    private void updateGameMessage(Long gameId) {
        GameStateData game = gameService.getGameStateData(gameId);
        simpMessagingTemplate.convertAndSend("/game/" + gameId, game);
    }
}
