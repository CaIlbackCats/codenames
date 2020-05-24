package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.card.service.CardService;
import com.callbackcats.codenames.game.domain.Card;
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

    private static final Integer MAX_STARTING_TEAM_CARD = 9;
    private static final Integer MAX_SECOND_TEAM_CARD = 8;

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

    public void processVotes(TeamVoteData teamVoteData) {
        Map<CardVoteData, Integer> cardVoteMap = getCardVoteScores(teamVoteData);
        Integer maxScore = Collections.max(cardVoteMap.values());
        List<CardVoteData> maxCardVote = cardVoteMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(maxScore))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        Game game = findGameById(teamVoteData.getGameId());

        boolean maxVotesNotShared = maxCardVote.size() == 1;
        if (maxVotesNotShared) {
            handleGameByGoodVote(teamVoteData, maxScore, maxCardVote, game);
        } else if (maxCardVote.size() > 1) {
            game.setEndTurn(true);
        }

        gameRepository.save(game);
    }

    public void checkWinningCondition(Game game, TeamVoteData teamVoteData) {
        SideType currentSide = CardType.valueOf(teamVoteData.getVotingTeam()).getTeamColorValue();
        int foundCardsBySide = (int) game.getBoard().stream().filter(card -> card.getType().getTeamColorValue().equals(currentSide)).map(Card::isFound).count();
        if (game.getStartingTeamColor().equals(currentSide) && foundCardsBySide == MAX_STARTING_TEAM_CARD) {
            game.setWinner(currentSide);
        } else if (foundCardsBySide == MAX_SECOND_TEAM_CARD) {
            game.setWinner(currentSide);
        }
    }

    private void handleGameByGoodVote(TeamVoteData teamVoteData, Integer maxScore, List<CardVoteData> maxCardVote, Game game) {
        CardType votedCardType = CardType.valueOf(maxCardVote.get(0).getVotedCard().getType());
        if (votedCardType == CardType.ASSASSIN) {
            setupEndGameByAssassin(teamVoteData, game);
        } else {
            updateGameByVotes(maxCardVote, maxScore, teamVoteData, game);
        }
    }

    private void setupEndGameByAssassin(TeamVoteData teamVoteData, Game game) {
        game.setEndGameByAssassin(true);
        SideType currentTeam = SideType.valueOf(teamVoteData.getVotingTeam());
        if ((currentTeam == SideType.BLUE)) {
            game.setWinner(SideType.RED);
        } else {
            game.setWinner(SideType.BLUE);
        }
    }

    private void updateGameByVotes(List<CardVoteData> maxCardVote, Integer maxScore, TeamVoteData teamVoteData, Game game) {
        String votingTeam = teamVoteData.getVotingTeam();
        CardDetails mostVotedCard = maxCardVote.get(0).getVotedCard();
        String votedCardColor = mostVotedCard.getType();

        setGameByCardPick(mostVotedCard, maxScore, game);

        cardService.setCardFound(mostVotedCard.getId(), true);

        setGameEndTurnOnWrongPick(votingTeam, game, votedCardColor);

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
//            case ASSASSIN:
//                game.setEndGameByAssassin(true);
//                break;
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
