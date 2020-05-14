package com.callbackcats.codenames.player.service;

import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.repository.LobbyRepository;
import com.callbackcats.codenames.player.domain.ActionType;
import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.RoleType;
import com.callbackcats.codenames.player.domain.SideType;
import com.callbackcats.codenames.player.dto.ActionData;
import com.callbackcats.codenames.player.dto.PlayerCreationData;
import com.callbackcats.codenames.player.dto.PlayerData;
import com.callbackcats.codenames.player.dto.PlayerRemovalData;
import com.callbackcats.codenames.player.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PlayerService {

    private static final int MAX_SPYMASTER = 2;
    private final PlayerRepository playerRepository;
    private final LobbyRepository lobbyRepository;


    public PlayerService(PlayerRepository playerRepository, LobbyRepository lobbyRepository) {
        this.playerRepository = playerRepository;
        this.lobbyRepository = lobbyRepository;
    }

    public PlayerData savePlayer(PlayerCreationData playerCreationData) {
        Player player = new Player(playerCreationData);
        Lobby lobby = lobbyRepository.findLobbyByName(playerCreationData.getLobbyName());
        if (lobby.getPlayerList().isEmpty()) {
            player.setLobbyOwner(true);
        }
        player.setLobby(lobby);
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

    public List<PlayerData> getPlayerDataListByLobbyName(String lobbyName) {
        log.info("Get players in the given lobby:\t" + lobbyName);
        return getPlayersByLobbyName(lobbyName)
                .stream()
                .map(PlayerData::new)
                .collect(Collectors.toList());
    }

    public Boolean isPlayerRemovedByVote(PlayerRemovalData playerRemovalData) {
        Player player = findPlayerById(playerRemovalData.getPlayerToRemoveId());
        Lobby lobby = player.getLobby();
        boolean removed = false;
        int playersInLobby = lobby.getPlayerList().size();
        if (player.getKickVoteCount() > playersInLobby / 2) {
            removePlayer(player);
            // actionData = new ActionData(ActionType.GET_KICKED, new PlayerData(player));
            log.info("Remove player:\t" + player.getId() + "\tby vote:\t" + player.getKickVoteCount());
            removed = true;
        } else {
            player.setKickVoteCount(0);
            playerRepository.save(player);
        }
        return removed;
    }

    public Boolean removePlayerByOwner(PlayerRemovalData playerRemovalData) {
        Player owner = findPlayerById(playerRemovalData.getOwnerId());
        Player playerToRemove = findPlayerById(playerRemovalData.getPlayerToRemoveId());
        boolean removed = false;
        if (owner.getLobbyOwner()) {
            log.info("Remove player:\t" + playerToRemove.getId() + "\tby owner:\t" + owner.getId());
            removePlayer(playerToRemove);
            removed = true;
        }
        return removed;
    }

    public void setPlayerKickScore(PlayerRemovalData playerRemovalData) {
        if (playerRemovalData.getVote()) {
            Player foundPlayer = findPlayerById(playerRemovalData.getPlayerToRemoveId());
            Integer kickVoteCount = foundPlayer.getKickVoteCount();
            foundPlayer.setKickVoteCount(++kickVoteCount);
            playerRepository.save(foundPlayer);
            log.info("Set player kick score:\t" + kickVoteCount);
        }
    }

    public PlayerData findPlayerDataById(Long id) {
        log.info("Find playerdata by id:\t" + id);
        return new PlayerData(findPlayerById(id));
    }

    private void removePlayer(Player player) {
        player.setLobby(null);
        playerRepository.delete(player);
    }

    private Player findPlayerById(Long id) {
        return playerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Given Player not found"));
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

    private List<Player> setSideToPlayerList(List<Player> players, SideType side, int teamCapacity) {
        List<Player> assignedPlayers = new ArrayList<>();
        while (assignedPlayers.size() < teamCapacity) {
            int rndIndex = getRandomIndex(players);
            Player rndPlayer = players.get(rndIndex);
            assignedPlayers.add(setPlayerSide(rndPlayer, players, side));
        }
        return assignedPlayers;
    }

    private int getRandomIndex(List<Player> players) {
        return (int) Math.floor(Math.random() * players.size());
    }

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
