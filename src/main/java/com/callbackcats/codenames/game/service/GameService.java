package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.card.domain.Card;
import com.callbackcats.codenames.game.card.domain.CardType;
import com.callbackcats.codenames.game.card.service.CardService;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.dto.*;
import com.callbackcats.codenames.game.repository.GameRepository;
import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.game.team.service.TeamService;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.player.domain.Player;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import com.callbackcats.codenames.lobby.player.service.PlayerService;
import com.callbackcats.codenames.lobby.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class GameService {


    private final GameRepository gameRepository;
    private final LobbyService lobbyService;
    private final PlayerService playerService;
    private final TeamService teamService;
    private final CardService cardService;

    public GameService(GameRepository gameRepository, LobbyService lobbyService, PlayerService playerService, TeamService teamService, CardService cardService) {
        this.gameRepository = gameRepository;
        this.lobbyService = lobbyService;
        this.playerService = playerService;
        this.teamService = teamService;
        this.cardService = cardService;
    }

    public GameDetails createGame(String lobbyId) {
        Lobby lobby = this.lobbyService.findLobbyById(lobbyId);
        List<Team> teams = teamService.createTeamsByLobbyId(lobbyId);

        SideType randomSide = SideType.getRandomSide();
        List<Card> cards = cardService.generateMap(randomSide);
        Game game = new Game(cards, teams);

        this.gameRepository.save(game);
        lobbyService.addGame(lobby, game);
        log.info("Game created");
        return new GameDetails(game);
    }


    public void countScore(Long gameId) {
        Game game = findGameById(gameId);
        Team currentTeam = teamService.findTeamByGameIdBySide(gameId,game.getCurrentTeam());
        List<Card> votedCards = currentTeam.getPlayers()
                .stream()
                .map(Player::getVotedCard)
                .collect(Collectors.toList());

        Map<Card, Integer> cardScores = fillCardVotesMap(votedCards);
        Integer maxVote = Collections.max(cardScores.values());
        List<Card> mostVotedCards = cardScores.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(maxVote))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (mostVotedCards.size() > 1) {
            game.setEndTurn(true);
        } else {
            Card mostVotedCard = mostVotedCards.get(0);
            if (mostVotedCard.getType() == CardType.ASSASSIN) {
                game.setEndGameByAssassin(true);
            } else {
                switch (mostVotedCard.getType()) {
                    case BLUE_SPY:

                        break;
                }
            }
        }

        gameRepository.save(game);
    }

    private Map<Card, Integer> fillCardVotesMap(List<Card> votedCards) {
        Map<Card, Integer> votesMap = new HashMap<>();
        for (Card votedCard : votedCards) {
            if (votesMap.containsKey(votedCard)) {
                Integer newScore = votesMap.get(votedCard) + 1;
                votesMap.put(votedCard, newScore);
            } else {
                votesMap.put(votedCard, 0);
            }
        }
        return votesMap;
    }

    private Game findGameById(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Game not found by id:\t" + id));
    }


}
