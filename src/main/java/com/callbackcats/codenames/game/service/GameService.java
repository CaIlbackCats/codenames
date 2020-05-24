package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.card.service.CardService;
import com.callbackcats.codenames.game.domain.CardType;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.dto.*;
import com.callbackcats.codenames.game.repository.GameRepository;
import com.callbackcats.codenames.lobby.domain.Lobby;
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
    private final CardService cardService;

    public GameService(GameRepository gameRepository, LobbyService lobbyService, PlayerService playerService, CardService cardService) {
        this.gameRepository = gameRepository;
        this.lobbyService = lobbyService;
        this.playerService = playerService;
        this.cardService = cardService;
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
        Map<CardVoteData, Integer> cardVoteMap = getCardVoteScores(teamVoteData);
        Integer maxScore = Collections.max(cardVoteMap.values());
        List<CardVoteData> maxCardVote = cardVoteMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(maxScore))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Game updatedGame = updateGameByVotes(maxCardVote, maxScore, teamVoteData);



        if (updatedGame.getEndGameByAssassin()) {
            SideType currentTeam = SideType.valueOf(teamVoteData.getVotingTeam());
            if ((currentTeam == SideType.BLUE)) {
                updatedGame.setWinner(SideType.RED);
            } else {
                updatedGame.setWinner(SideType.RED);
            }
        }

    }

    private Game updateGameByVotes(List<CardVoteData> maxCardVote, Integer maxScore, TeamVoteData teamVoteData) {
        Game game = findGameById(teamVoteData.getGameId());
        boolean maxVotesNotShared = maxCardVote.size() == 1;
        if (maxVotesNotShared) {
            String votingTeam = teamVoteData.getVotingTeam();
            CardDetails mostVotedCard = maxCardVote.get(0).getVotedCard();
            String votedCardColor = mostVotedCard.getType();

            setGameByCardPick(mostVotedCard, maxScore, game);

            cardService.setCardFound(mostVotedCard.getId(), true);

            setGameEndTurnOnWrongPick(votingTeam, game, votedCardColor);
        } else {
            game.setEndTurn(true);
        }
        gameRepository.save(game);
        return game;
    }

    private void setGameEndTurnOnWrongPick(String votingTeam, Game game, String cardColor) {
        SideType votingTeamColor = SideType.valueOf(votingTeam);
        SideType votedCardColor = CardType.valueOf(cardColor).getTeamColorValue();
        if (votingTeamColor != votedCardColor) {
            game.setEndTurn(true);
        }
    }

    private void setGameByCardPick(CardDetails mostVotedCard, Integer maxScore, Game game) {
        int currentScore;
        switch (CardType.valueOf(mostVotedCard.getType())) {
            case BLUE_SPY:
                currentScore = game.getBlueScore() + maxScore;
                game.setBlueScore(currentScore);
                break;
            case RED_SPY:
                currentScore = game.getRedSCore() + maxScore;
                game.setRedSCore(currentScore);
                break;
            case ASSASSIN:
                game.setEndGameByAssassin(true);
                break;
            case BYSTANDER:
                game.setEndTurn(true);
                break;
        }
    }


    private Map<CardVoteData, Integer> getCardVoteScores(TeamVoteData teamVoteData) {
        List<CardVoteData> cardVotes = teamVoteData.getCardVotes();
        Map<CardVoteData, Integer> cardVoteMap = new HashMap<>();
        for (CardVoteData currentCardVote : cardVotes) {
            Integer score = countScore(teamVoteData.getGameId(), currentCardVote);
            cardVoteMap.put(currentCardVote, score);
        }
        return cardVoteMap;
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
