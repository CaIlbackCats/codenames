package com.callbackcats.codenames.game.team.service;

import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.game.team.repository.TeamRepository;
import com.callbackcats.codenames.lobby.player.domain.Player;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import com.callbackcats.codenames.lobby.player.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerService playerService;

    public TeamService(TeamRepository teamRepository, PlayerService playerService) {
        this.teamRepository = teamRepository;
        this.playerService = playerService;
    }

    public List<Team> createTeamsByLobbyId(String lobbyId) {
        List<Team> teams = Arrays.stream(SideType.values())
                .filter(side -> !side.equals(SideType.NOT_SELECTED))
                .map(side -> createTeam(lobbyId, side))
                .collect(Collectors.toList());

        teamRepository.saveAll(teams);
        return teams;
    }

    public Team findTeamByGameIdBySide(Long id, SideType sideType) {
        return teamRepository.findCurrentTeamByGameIdBySide(id, sideType);
    }


    private Team createTeam(String lobbyId, SideType side) {
        List<Player> players = playerService.findVisiblePlayersByLobbyIdBySide(lobbyId, side);
        return new Team(players, side);
    }
}
