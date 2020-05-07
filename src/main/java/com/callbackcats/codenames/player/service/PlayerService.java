package com.callbackcats.codenames.player.service;

import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.RoleType;
import com.callbackcats.codenames.player.domain.SideType;
import com.callbackcats.codenames.player.dto.PlayerData;
import com.callbackcats.codenames.player.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlayerService {

    private static final int MAX_SPYMASTER = 2;
    private final PlayerRepository playerRepository;


    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerData savePlayer(String name) {
        Player player = new Player(name);
        playerRepository.save(player);
        return new PlayerData(player);
    }

    public List<PlayerData> setPlayerRole(String lobbyName) {
        List<Player> allPlayers = getPlayersByLobbyName(lobbyName);
        if (!allPlayers.isEmpty()) {
            clearRoles(allPlayers);
            long sidelessPlayersNumber = allPlayers
                    .stream()
                    .filter(player -> player.getSide() == SideType.NOT_SELECTED)
                    .count();
            if (sidelessPlayersNumber > 1) {
                setSpymasterToSidelessPlayer(allPlayers);

            } else {
                setSpymasterRoleToSidedPlayers(allPlayers);
            }
            setSpyRolesToPlayers(allPlayers);
            playerRepository.saveAll(allPlayers);
        }
        return allPlayers.stream().map(PlayerData::new).collect(Collectors.toList());
    }

    public List<PlayerData> randomizeTeamSetup(String lobbyName) {
        List<Player> allPlayers = getPlayersByLobbyName(lobbyName);
        int originPlayerSize = allPlayers.size();
        List<Player> assignedPlayers = new ArrayList<>();

        if (!allPlayers.isEmpty()) {
            clearSides(allPlayers);
            clearRoles(allPlayers);
            //   assignedPlayers.addAll(assignSideToSpymaster(allPlayers));

            int teamCapacity = (originPlayerSize - assignedPlayers.size()) / 2;

            for (SideType side : SideType.values()) {
                if (side != SideType.NOT_SELECTED) {
                    assignedPlayers.addAll(setSideToPlayerList(allPlayers, side, teamCapacity));
                }
            }
            if (!allPlayers.isEmpty()) {
                handleLeftoverSideSelection(allPlayers, assignedPlayers);
            }
            setSpymasterRoleToSidedPlayers(assignedPlayers);
            setSpyRolesToPlayers(assignedPlayers);
            playerRepository.saveAll(assignedPlayers);
        }

        return assignedPlayers
                .stream()
                .map(PlayerData::new)
                .collect(Collectors.toList());
    }

    private void handleLeftoverSideSelection(List<Player> allPlayers, List<Player> assignedPlayers) {
        long blueTeamCount = assignedPlayers
                .stream()
                .filter(player -> player.getSide() == SideType.BLUE)
                .count();
        long redTeamCount = assignedPlayers
                .stream()
                .filter(player -> player.getSide() == SideType.RED)
                .count();
        Player leftover = allPlayers.get(0);
        if (blueTeamCount > redTeamCount) {
            assignedPlayers.add(setPlayerSide(leftover, allPlayers, SideType.RED));
        } else {
            assignedPlayers.add(setPlayerSide(leftover, allPlayers, SideType.BLUE));
        }
    }

    private Player setPlayerSide(Player player, List<Player> allPlayers, SideType side) {
        player.setSide(side);
        allPlayers.remove(player);
        return player;
    }

    private List<Player> assignSideToSpymaster(List<Player> players) {
        List<Player> spymasters = players
                .stream()
                .filter(player -> player.getRole() == RoleType.SPYMASTER)
                .collect(Collectors.toList());
        List<Player> assignedSpymaster = new ArrayList<>();
        if (spymasters.size() == 1) {
            Player spymaster = spymasters.get(0);
            assignedSpymaster.add(setPlayerSide(spymaster, players, SideType.BLUE));
        }
        if (spymasters.size() == 2) {
            Player spymaster = spymasters.get(0);
            assignedSpymaster.add(setPlayerSide(spymaster, players, SideType.BLUE));

            spymaster = spymasters.get(1);
            assignedSpymaster.add(setPlayerSide(spymaster, players, SideType.RED));
        }

        return assignedSpymaster;
    }

    private List<Player> setSideToPlayerList(List<Player> players, SideType side, int teamCapacity) {
        List<Player> assignedPlayers = new ArrayList<>();
        while (assignedPlayers.size() < teamCapacity) {
            int rndIndex = getRandomIndex(players);
            Player rndPlayer = players.get(rndIndex);
            assignedPlayers.add(setPlayerSide(rndPlayer, players, side));
        }
        return assignedPlayers;
    }

    private boolean isSpymasterInTeam(List<Player> players) {
        return players
                .stream()
                .anyMatch(player -> player.getRole() == RoleType.SPYMASTER);
    }

    private int getRandomIndex(List<Player> players) {
        return (int) Math.floor(Math.random() * players.size());
    }

    public List<PlayerData> getPlayerDataListByLobbyName(String lobbyName) {
        return getPlayersByLobbyName(lobbyName)
                .stream()
                .map(PlayerData::new)
                .collect(Collectors.toList());
    }

    //todo when lobby entity implemented modify this to getplayers by given lobby
    private List<Player> getPlayersByLobbyName(String lobbyName) {
        return playerRepository.getPlayersByLobbyName(lobbyName);
    }

    private void setSpymasterToSidelessPlayer(List<Player> players) {
        while (getTotalSpymasters(players) != MAX_SPYMASTER) {
            Player randomPlayer = getRandomPlayer(players);
            if (randomPlayer.getRole() == RoleType.NOT_SELECTED && randomPlayer.getSide() == SideType.NOT_SELECTED) {
                randomPlayer.setRole(RoleType.SPYMASTER);
            }
        }
    }

    private void setSpymasterRoleToSidedPlayers(List<Player> players) {
        while (getTotalSpymasters(players) != MAX_SPYMASTER) {
            Player randomPlayer = getRandomPlayer(players);
            if (randomPlayer.getRole() == RoleType.NOT_SELECTED) {
                randomPlayer.setRole(RoleType.SPYMASTER);
            }
        }
    }

    private void setSpyRolesToPlayers(List<Player> players) {
        players
                .stream()
                .filter(player -> player.getRole() == RoleType.NOT_SELECTED)
                .forEach(player -> player.setRole(RoleType.SPY));
    }

    private void clearRoles(List<Player> players) {
        players.forEach(player -> player.setRole(RoleType.NOT_SELECTED));
    }

    private void clearSides(List<Player> players) {
        players.forEach(player -> player.setSide(SideType.NOT_SELECTED));
    }

    private Player getRandomPlayer(List<Player> players) {
        int randomIndex = (int) Math.floor(Math.random() * players.size());
        return players.get(randomIndex);
    }

    private Integer getTotalSpymasters(List<Player> players) {
        return (int) players
                .stream()
                .filter(player -> player.getRole() == RoleType.SPYMASTER)
                .count();
    }
}
