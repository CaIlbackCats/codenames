package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.dto.GameDetails;
import com.callbackcats.codenames.game.repository.GameRepository;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@Slf4j
public class GameService {

    private GameRepository gameRepository;
    private LobbyService lobbyService;

    public GameService(GameRepository gameRepository, LobbyService lobbyService) {
        this.gameRepository = gameRepository;
        this.lobbyService = lobbyService;
    }


    public GameDetails createGame(String lobbyId) {
        Lobby lobby = this.lobbyService.getLobbyById(lobbyId).orElseThrow(() -> new EntityNotFoundException("Lobby not found"));
        Game game = new Game();
        this.gameRepository.save(game);
        lobbyService.addGame(lobby, game);
        log.info("Game created");
        return new GameDetails(game);
    }

}
