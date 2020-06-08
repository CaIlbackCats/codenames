package com.callbackcats.codenames.game.team.service;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.service.PuzzleWordService;
import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.game.team.repository.TeamRepository;
import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.SideType;
import com.callbackcats.codenames.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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

    public TeamService(TeamRepository teamRepository, PlayerService playerService, PuzzleWordService puzzleWordService) {
        this.teamRepository = teamRepository;
        this.playerService = playerService;
        this.puzzleWordService = puzzleWordService;
    }

    public List<Team> createTeamsByLobbyId(String lobbyId, Game game) {
        return Arrays.stream(SideType.values())
                .filter(side -> !side.equals(SideType.NOT_SELECTED))
                .map(side -> createTeam(lobbyId, side, game))
                .collect(Collectors.toList());
    }

    public Team findTeamByGameIdBySide(Long gameId, SideType sideType) {
        return teamRepository.findCurrentTeamByGameIdBySide(gameId, sideType).orElseThrow(() -> new EntityNotFoundException("Team not found by given gameId:\t" + gameId));
    }

    public void increaseTeamScore(Team team, SideType currentSide) {

        int increasedScore = team.getScore() + 1;
        team.setScore(increasedScore);
        teamRepository.save(team);
        log.info("Team :\t" + team.getId() + "\t current score increased to:\t" + team.getScore());
        if (currentSide==team.getSide()){
            puzzleWordService.increaseLatestPuzzleWordGuessCounter(team);
        }
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
}
