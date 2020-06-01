package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.card.domain.Card;
import com.callbackcats.codenames.card.domain.CardType;
import com.callbackcats.codenames.card.domain.GameLanguage;
import com.callbackcats.codenames.card.service.CardService;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.dto.GameDetails;
import com.callbackcats.codenames.game.dto.GameStateData;
import com.callbackcats.codenames.game.dto.PuzzleWordData;
import com.callbackcats.codenames.game.repository.GameRepository;
import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.game.team.service.TeamService;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.RoleType;
import com.callbackcats.codenames.player.domain.SideType;
import com.callbackcats.codenames.lobby.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class GameService {

    private static final Integer CARD_VOTING_PHASE_DURATION = 5;

    private final GameRepository gameRepository;
    private final LobbyService lobbyService;
    private final TeamService teamService;
    private final CardService cardService;
    private final PuzzleWordService puzzleWordService;
    private final GameTurnService gameTurnService;
    private final ScheduledExecutorService scheduler;


    public GameService(GameRepository gameRepository, LobbyService lobbyService, TeamService teamService, CardService cardService, PuzzleWordService puzzleWordService, GameTurnService gameTurnService, ScheduledExecutorService scheduler) {
        this.gameRepository = gameRepository;
        this.lobbyService = lobbyService;
        this.teamService = teamService;
        this.cardService = cardService;
        this.puzzleWordService = puzzleWordService;
        this.gameTurnService = gameTurnService;
        this.scheduler = scheduler;
    }

    public GameStateData getGameStateData(Long gameId) {
        Game game = findGameById(gameId);
        return new GameStateData(game);
    }

    public GameDetails createGame(String lobbyId) {
        Lobby lobby = this.lobbyService.findLobbyById(lobbyId);
        GameLanguage language = lobby.getGameLanguage();
        Game game = new Game(lobby);
        this.gameRepository.save(game);

        gameTurnService.createGameTurnToGame(game);
        teamService.createTeamsByLobbyId(lobbyId, game);
        SideType startingSide = game.getStartingTeam();
        cardService.generateMap(startingSide, language, game);

        log.info("Game created");
        return new GameDetails(game);
    }

    public ScheduledFuture<?> startVotingPhase(Long gameId) {
        ScheduledFuture<?> schedule = null;
        Game game = findGameById(gameId);
        if (!game.getVotingPhase()) {
            changeGameVotingPhase(true, gameId);
            schedule = scheduler.schedule(() -> countScore(game), CARD_VOTING_PHASE_DURATION, TimeUnit.SECONDS);
        }
        return schedule;
    }

    public Boolean isEndTurn(Long gameId) {
        Game game = findGameById(gameId);
        return game.getEndTurn();
    }

    public void changeGameVotingPhase(Boolean votingPhase, Long gameId) {
        Game game = findGameById(gameId);
        game.setVotingPhase(votingPhase);
        gameRepository.save(game);
        log.info("Game voting phase changed id:\t" + gameId + "\t to:\t" + votingPhase);
    }

    public void countScore(Game game) {
        SideType currentTeamSide = game.getGameTurn().getCurrentTeam();
        Team currentTeam = teamService.findTeamByGameIdBySide(game.getId(), currentTeamSide);
        List<Card> votedCards = currentTeam.getPlayers()
                .stream()
                .filter(player -> player.getRole() == RoleType.SPY)
                .map(Player::getVotedCard)
                .collect(Collectors.toList());

        List<Card> mostVotedCards = getMostVotedCards(votedCards);

        processMostVotedCardScore(game, currentTeam, mostVotedCards);

        gameRepository.save(game);
    }

    public void setPuzzleWord(Long gameId, PuzzleWordData puzzleWord) {
        Game game = findGameById(gameId);
        SideType currentTeamSide = game.getGameTurn().getCurrentTeam();

        Team currentTeam = game.getTeams()
                .stream()
                .filter(team -> team.getSide() == currentTeamSide)
                .findFirst().orElseThrow(NoSuchElementException::new);
        puzzleWordService.savePuzzleWorldToGame(currentTeam, puzzleWord);
        gameTurnService.advanceToSpyTurn(game.getGameTurn());
    }

    public void processPassTurnVote(Long gameId) {
        Game game = findGameById(gameId);
        SideType currentTeamSide = game.getGameTurn().getCurrentTeam();
        Team currentTeam = game.getTeams().stream().filter(team -> team.getSide() == currentTeamSide).findFirst().orElseThrow(NoSuchElementException::new);
        boolean everyOnePassed = currentTeam.getPlayers()
                .stream()
                .filter(player -> player.getRole() == RoleType.SPY)
                .allMatch(Player::getPassed);
        if (everyOnePassed) {
            changeTurn(game);
        }
    }

    public void changeTurn(Long gameId) {
        Game game = findGameById(gameId);

        gameTurnService.changeTurn(game.getGameTurn());
        game.setEndTurn(false);
        gameRepository.save(game);
    }

    private void changeTurn(Game game) {

        gameTurnService.changeTurn(game.getGameTurn());
        game.setEndTurn(false);
        gameRepository.save(game);
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
        SideType oppositeSide = SideType.getOppositeSide(game.getGameTurn().getCurrentTeam());
        Team otherTeam = teamService.findTeamByGameIdBySide(game.getId(), oppositeSide);
        Card mostVotedCard = mostVotedCards.get(0);

        if (mostVotedCards.size() > 1) {
            game.setEndTurn(true);
            log.info("Same card has the max amount of votes");
        } else if (!mostVotedCard.isFound()) {
            if (mostVotedCard.getType() == CardType.ASSASSIN) {
                game.setEndGameByAssassin(true);
                game.setEndGame(true);
                log.info("Assassin has been found");
            } else if (mostVotedCard.getType().equals(CardType.BYSTANDER)) {
                game.setEndTurn(true);
                log.info("Civilian found");
            } else {
                if (mostVotedCard.getType().getTeamColorValue() == currentTeam.getSide()) {
                    teamService.increaseTeamScore(currentTeam);
                    log.info("current team scored");
                    if (teamService.isCurrentTeamReachMaxGuesses(currentTeam)) {
                        gameTurnService.advanceToSpyTurn(game.getGameTurn());
                        game.setEndTurn(true);
                        log.info("reached maximum guesses");
                    }

                } else {
                    teamService.increaseTeamScore(otherTeam);
                    game.setEndTurn(true);
                    log.info("enemy team scored");
                }
            }
            cardService.setCardFound(mostVotedCard);
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
