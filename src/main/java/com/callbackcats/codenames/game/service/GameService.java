package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.card.domain.Card;
import com.callbackcats.codenames.card.domain.CardType;
import com.callbackcats.codenames.card.domain.GameLanguage;
import com.callbackcats.codenames.card.dto.CardDetails;
import com.callbackcats.codenames.card.service.CardService;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.dto.GameDetails;
import com.callbackcats.codenames.game.dto.GameStateData;
import com.callbackcats.codenames.game.dto.PuzzleWordData;
import com.callbackcats.codenames.game.repository.GameRepository;
import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.game.team.dto.TeamData;
import com.callbackcats.codenames.game.team.service.TeamService;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.SideType;
import com.callbackcats.codenames.lobby.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class GameService {

    private static final Integer CARD_VOTING_PHASE_DURATION = 10;

    private final GameRepository gameRepository;
    private final LobbyService lobbyService;
    private final TeamService teamService;
    private final CardService cardService;
    private final PuzzleWordService puzzleWordService;
    private final ScheduledExecutorService scheduler;


    public GameService(GameRepository gameRepository, LobbyService lobbyService, TeamService teamService, CardService cardService, PuzzleWordService puzzleWordService, ScheduledExecutorService scheduler) {
        this.gameRepository = gameRepository;
        this.lobbyService = lobbyService;
        this.teamService = teamService;
        this.cardService = cardService;
        this.puzzleWordService = puzzleWordService;
        this.scheduler = scheduler;
    }

    public GameStateData getGameStateData(Long gameId) {
        Game game = findGameById(gameId);
        List<TeamData> teamsInGame = teamService.findTeamsByGameId(gameId);
        List<CardDetails> boardInGame = cardService.findCardsByGameId(gameId);
        return new GameStateData(game, boardInGame, teamsInGame);
    }

    public GameDetails createGame(String lobbyId) {
        Lobby lobby = this.lobbyService.findLobbyById(lobbyId);
        GameLanguage language = lobby.getGameLanguage();
        Game game = new Game(lobby);
        this.gameRepository.save(game);
        teamService.createTeamsByLobbyId(lobbyId, game);
        SideType startingSide = game.getStartingTeam();
        cardService.generateMap(startingSide,language, game);

        log.info("Game created");
        return new GameDetails(game);
    }

    public ScheduledFuture<?> startVotingPhase(Long gameId) {
        ScheduledFuture<?> schedule = null;
        Game game = findGameById(gameId);
        if (!game.getVotingPhase()) {
            changeGameVotingPhase(true,gameId);
            schedule = scheduler.schedule(() -> countScore(game), CARD_VOTING_PHASE_DURATION, TimeUnit.SECONDS);
        }
        return schedule;
    }

    public void changeGameVotingPhase(Boolean votingPhase, Long gameId) {
        Game game = findGameById(gameId);
        game.setVotingPhase(votingPhase);
        gameRepository.save(game);
        log.info("Game voting phase changed id:\t"+ gameId+"\t to:\t"+votingPhase);
    }

    public void countScore(Game game) {
        Team currentTeam = teamService.findTeamByGameIdBySide(game.getId(), game.getCurrentTeam());
        List<Card> votedCards = currentTeam.getPlayers()
                .stream()
                .map(Player::getVotedCard)
                .collect(Collectors.toList());

        List<Card> mostVotedCards = getMostVotedCards(votedCards);

        processMostVotedCardScore(game, currentTeam, mostVotedCards);

        gameRepository.save(game);
    }

    public void setPuzzleWord(Long gameId, PuzzleWordData puzzleWord){
        Game game = findGameById(gameId);
        puzzleWordService.savePuzzleWorldToGame(game,puzzleWord);
    }

    private List<Card> getMostVotedCards(List<Card> votedCards) {
        Map<Card, Integer> cardScores = fillCardVotesMap(votedCards);
        Integer maxVote = Collections.max(cardScores.values());
        return cardScores.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(maxVote))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void processMostVotedCardScore(Game game, Team currentTeam, List<Card> mostVotedCards) {
        SideType oppositeSide = SideType.getOppositeSide(game.getCurrentTeam());
        Team otherTeam = teamService.findTeamByGameIdBySide(game.getId(), oppositeSide);

        if (mostVotedCards.size() > 1) {
            game.setEndTurn(true);
        } else {
            Card mostVotedCard = mostVotedCards.get(0);
            if (mostVotedCard.getType() == CardType.ASSASSIN) {
                game.setEndGameByAssassin(true);
            } else if (mostVotedCard.getType().equals(CardType.BYSTANDER)) {
                game.setEndTurn(true);
            } else {
                if (mostVotedCard.getType().getTeamColorValue() == currentTeam.getSide()) {
                    teamService.increaseTeamScore(currentTeam);
                } else {
                    teamService.increaseTeamScore(otherTeam);
                    game.setEndTurn(true);
                }
            }
        }
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
