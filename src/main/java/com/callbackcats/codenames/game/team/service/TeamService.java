package com.callbackcats.codenames.game.team.service;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.service.PuzzleWordService;
import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.game.team.domain.TurnStat;
import com.callbackcats.codenames.game.team.repository.TeamRepository;
import com.callbackcats.codenames.game.team.repository.TurnStatRepository;
import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.SideType;
import com.callbackcats.codenames.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerService playerService;
    private final PuzzleWordService puzzleWordService;
    private final TurnStatRepository turnStatRepository;

    public TeamService(TeamRepository teamRepository, PlayerService playerService, PuzzleWordService puzzleWordService, TurnStatRepository turnStatRepository) {
        this.teamRepository = teamRepository;
        this.playerService = playerService;
        this.puzzleWordService = puzzleWordService;
        this.turnStatRepository = turnStatRepository;
    }

    public List<Team> createTeamsByLobbyId(String lobbyId, Game game) {
        List<Team> teams = Arrays.stream(SideType.values())
                .filter(side -> !side.equals(SideType.NOT_SELECTED))
                .map(side -> createTeam(lobbyId, side, game))
                .collect(Collectors.toList());

        Team startingTeam = teams.stream()
                .filter(team -> team.getSide().equals(game.getStartingTeam()))
                .findFirst().orElseThrow();
        saveSpyMasterTurnStartTime(startingTeam);

        return teams;
    }

    public Team findTeamByGameIdBySide(Long gameId, SideType sideType) {
        return teamRepository.findCurrentTeamByGameIdBySide(gameId, sideType).orElseThrow(() -> new EntityNotFoundException("Team not found by given gameId:\t" + gameId));
    }

    public void increaseTeamScore(Team team) {
        int increasedScore = team.getScore() + 1;
        team.setScore(increasedScore);
        teamRepository.save(team);
        log.info("Team :\t" + team.getId() + "\t current score increased to:\t" + team.getScore());
        puzzleWordService.increaseLatestPuzzleWordGuessCounter(team);
    }

    public Boolean isCurrentTeamReachMaxGuesses(Team team) {
        return puzzleWordService.isPuzzleWordGuessLimitReached(team);
    }

    public void increaseNumOfEnemySpies(Team team) {
        int increasedValue = team.getStatistics().getNumOfEnemySpies() + 1;
        team.getStatistics().setNumOfEnemySpies(increasedValue);
        teamRepository.save(team);
    }

    public void increaseNumOfCivilians(Team team) {
        int increasedValue = team.getStatistics().getNumOfCivilians() + 1;
        team.getStatistics().setNumOfCivilians(increasedValue);
        teamRepository.save(team);
    }

    public void increaseNumOfInvalidVotes(Team team) {
        int increasedValue = team.getStatistics().getNumOfInvalidVotes() + 1;
        team.getStatistics().setNumOfInvalidVotes(increasedValue);
        teamRepository.save(team);
    }

    public void increaseTeamRounds(Team team) {
        int increasedValue = team.getStatistics().getTeamRounds() + 1;
        team.getStatistics().setTeamRounds(increasedValue);
        teamRepository.save(team);
    }

    public void increaseTeamPasses(Team team) {
        int increasedValue = team.getStatistics().getNumOfPasses() + 1;
        team.getStatistics().setNumOfPasses(increasedValue);
        teamRepository.save(team);
    }

    private Team createTeam(String lobbyId, SideType side, Game game) {
        List<Player> players = playerService.findVisiblePlayersByLobbyIdBySide(lobbyId, side);

        Team team = new Team(players, side, game);
        teamRepository.save(team);

        players.forEach(player -> playerService.saveTeamToPlayer(team, player));
        return team;
    }

    public void saveSpyMasterTurnStartTime(Team currentTeam) {
        TurnStat turnStat = new TurnStat(currentTeam.getStatistics());
        turnStatRepository.save(turnStat);
    }

    public void saveSpyMasterTurnEndTime(Team currentTeam) {
        TurnStat currentTurnStat = getCurrentTurnStat(currentTeam);
        currentTurnStat.setSpyMasterEndTime(LocalDateTime.now());
        turnStatRepository.save(currentTurnStat);
    }

    public void saveSpyTurnStartTime(Team currentTeam) {
        TurnStat currentTurnStat = getCurrentTurnStat(currentTeam);
        currentTurnStat.setSpyStartTime(LocalDateTime.now());
        turnStatRepository.save(currentTurnStat);
    }

    public void saveSpyTurnEndTime(Team currentTeam) {
        TurnStat currentTurnStat = getCurrentTurnStat(currentTeam);
        currentTurnStat.setSpyEndTime(LocalDateTime.now());
        turnStatRepository.save(currentTurnStat);
    }

    private TurnStat getCurrentTurnStat(Team currentTeam) {
        int turnStatsSize = currentTeam.getStatistics().getTurnStats().size();
        return currentTeam.getStatistics().getTurnStats().get(turnStatsSize - 1);
    }

    public void increaseSpiesInARow(Team currentTeam) {
        TurnStat currentTurnStat = getCurrentTurnStat(currentTeam);
        int currentNumOfSpies = currentTurnStat.getNumOfSpies();
        currentTurnStat.setNumOfSpies(currentNumOfSpies + 1);
        turnStatRepository.save(currentTurnStat);
    }

    public void saveNumOfGuesses(Team currentTeam, Integer maxGuessCount) {
        TurnStat currentTurnStat = getCurrentTurnStat(currentTeam);
        currentTurnStat.setNumOfGuesses(maxGuessCount);
        turnStatRepository.save(currentTurnStat);
    }
}
