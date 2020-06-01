package com.callbackcats.codenames.game.controller;

import com.callbackcats.codenames.card.dto.CardVoteData;
import com.callbackcats.codenames.card.dto.TypedCardDetailsData;
import com.callbackcats.codenames.card.dto.TypelessCardDetailsData;
import com.callbackcats.codenames.card.service.CardService;
import com.callbackcats.codenames.game.dto.*;
import com.callbackcats.codenames.game.service.GameService;
import com.callbackcats.codenames.player.service.PlayerService;
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
public class GameController {

    private final GameService gameService;
    private final PlayerService playerService;
    private final CardService cardService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameController(GameService gameService, PlayerService playerService, CardService cardService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.cardService = cardService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @ResponseBody
    @PostMapping("/api/game")
    public ResponseEntity<GameDetails> createGame(@RequestBody PayloadData payloadData) {
        log.info("Game creation requested");
        GameDetails game = gameService.createGame(payloadData.getLobbyId());

        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }

    @MessageMapping("/fetchGame/{gameId}")
    @SendTo("/game/{gameId}")
    public GameStateData fetchGame(@DestinationVariable Long gameId) {

        log.info("Request gamestate by game id:\t" + gameId);
        sendMapMessage(gameId);

        return gameService.getGameStateData(gameId);
    }

    @MessageMapping("/cardVote/{gameId}")
    @SendTo("/game/{gameId}")
    public GameStateData voteForCard(@DestinationVariable Long gameId, @Payload CardVoteData cardVoteData) {

        log.info("Player vote requested");
        playerService.setCardVote(cardVoteData);
        sendMapMessage(gameId);
        if (!gameService.isGameInCardVotingPhase(gameId)){
            ScheduledFuture<?> future = gameService.startVotingPhase(gameId);
            try {
                future.get();
                gameService.changeGameVotingPhase(false, gameId);
                if (gameService.isEndTurn(gameId)) {
                    gameService.changeTurn(gameId);
                }
                log.info("Player card vote finished");
            } catch (InterruptedException | ExecutionException e) {
                log.info(e.getMessage());
            }
        }

        sendMapMessage(gameId);
        return gameService.getGameStateData(gameId);
    }

    @MessageMapping("/passTurn/{gameId}")
    @SendTo("/game/{gameId}")
    public GameStateData votePassTurn(@DestinationVariable Long gameId, PassVoteData passVoteData) {

        playerService.setPlayerPassVote(passVoteData);
        gameService.processPassTurnVote(gameId);

        return gameService.getGameStateData(gameId);
    }

    @MessageMapping("/setPuzzleWord/{gameId}")
    @SendTo("/game/{gameId}")
    public GameStateData setPuzzleWord(@DestinationVariable Long gameId, @Payload PuzzleWordData puzzleWordData) {

        log.info("Puzzle world saving requested");
        gameService.setPuzzleWord(gameId, puzzleWordData);
        return gameService.getGameStateData(gameId);
    }

    private void sendMapMessage(Long gameId) {
        List<TypelessCardDetailsData> spyMap = cardService.getSpyMap(gameId);
        simpMessagingTemplate.convertAndSend("/game/" + gameId + "/spy", spyMap);

        List<TypedCardDetailsData> spymasterMap = cardService.getSpymasterMap(gameId);
        simpMessagingTemplate.convertAndSend("/game/" + gameId + "/spymaster", spymasterMap);
    }
}
