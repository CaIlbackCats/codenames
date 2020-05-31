package com.callbackcats.codenames.game.team.service;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.service.PuzzleWordService;
import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.game.team.dto.TeamData;
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

    public void increaseTeamScore(Team team) {
        int increasedScore = team.getScore() + 1;
        team.setScore(increasedScore);
        teamRepository.save(team);
        puzzleWordService.increaseLatestPuzzleWordGuessCounter(team);
    }

    public Boolean isCurrentTeamReachMaxGuesses(Team team) {
        return puzzleWordService.isPuzzleWordGuessLimitReached(team);
    }

    public List<TeamData> findTeamsByGameId(Long id) {
        return teamRepository.findTeamsByGameId(id).stream().map(TeamData::new).collect(Collectors.toList());
    }


    private Team createTeam(String lobbyId, SideType side, Game game) {
        List<Player> players = playerService.findVisiblePlayersByLobbyIdBySide(lobbyId, side);

        Team team = new Team(players, side, game);
        teamRepository.save(team);

        players.forEach(player -> playerService.saveTeamToPlayer(team, player));
        return team;
    }
}
