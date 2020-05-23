package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.domain.Card;
import com.callbackcats.codenames.game.domain.CardType;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.dto.GameDetails;
import com.callbackcats.codenames.game.dto.TeamVoteData;
import com.callbackcats.codenames.game.repository.GameRepository;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.player.domain.Player;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import com.callbackcats.codenames.lobby.player.dto.CardVoteData;
import com.callbackcats.codenames.lobby.player.dto.PlayerData;
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

    public GameService(GameRepository gameRepository, LobbyService lobbyService, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.lobbyService = lobbyService;
        this.playerService = playerService;
    }


    public GameDetails createGame(String lobbyId) {
        Lobby lobby = this.lobbyService.getLobbyById(lobbyId).orElseThrow(() -> new EntityNotFoundException("Lobby not found"));
        Game game = new Game();
        this.gameRepository.save(game);
        lobbyService.addGame(lobby, game);
        log.info("Game created");
        return new GameDetails(game);
    }

    private void setCardScore(TeamVoteData teamVoteData) {
        Game game = findGameById(teamVoteData.getGameId());
        List<CardVoteData> cardVotes = teamVoteData.getCardVotes();
        Map<CardVoteData, Integer> cardVoteMap = new HashMap<>();
        for (CardVoteData currentCardVote : cardVotes) {
            Integer score = countScore(teamVoteData.getGameId(), currentCardVote);
            cardVoteMap.put(currentCardVote, score);
        }
        Integer maxScore = Collections.max(cardVoteMap.values());
        List<CardVoteData> maxCardVote = cardVoteMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(maxScore))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        if (maxCardVote.size() == 1) {
            String mostVotedCardType = maxCardVote.get(0).getVotedCard().getType();
            int currentScore;
            switch (CardType.valueOf(mostVotedCardType)) {
                case BLUE_SPY:
                    currentScore = game.getBlueScore() + maxScore;
                    game.setBlueScore(currentScore);
                    break;
                case RED_SPY:
                    currentScore = game.getRedSCore() + maxScore;
                    game.setRedSCore(currentScore);
                    break;
                case ASSASSIN:
                    break;
                case BYSTANDER:
                    break;
            }
        } else {

        }

    }

    private Integer countScore(Long gameId, CardVoteData cardVoteData) {

        Game game = findGameById(gameId);
        List<PlayerData> playersInGame = playerService.findPlayersByGame(game);

        return (int) playersInGame.stream().map(PlayerData::getId).filter(cardVoteData.getVotedPlayersId()::contains).count();
    }

    private Game findGameById(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Game not found by id:\t" + id));
    }


}
