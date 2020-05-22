package com.callbackcats.codenames.game.controller;

import com.callbackcats.codenames.game.dto.GameDetails;
import com.callbackcats.codenames.game.service.GameService;
import com.callbackcats.codenames.lobby.player.domain.ActionType;
import com.callbackcats.codenames.lobby.player.dto.ActionData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class GameController {

    private GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameController(GameService gameService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/createGame")
    public void createGame(@Payload String lobbyId) {
        GameDetails gameDetails = gameService.createGame(lobbyId);
        ActionData actionData = new ActionData(ActionType.CREATE_GAME, gameDetails);
        simpMessagingTemplate.convertAndSend("/lobby/" + lobbyId, actionData);
    }
}
