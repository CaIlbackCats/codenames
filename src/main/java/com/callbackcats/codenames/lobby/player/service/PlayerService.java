package com.callbackcats.codenames.lobby.player.service;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.player.domain.Player;
import com.callbackcats.codenames.lobby.player.domain.RoleType;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import com.callbackcats.codenames.lobby.player.dto.*;
import com.callbackcats.codenames.lobby.player.repository.PlayerRepository;
import com.callbackcats.codenames.lobby.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PlayerService {

    private static final int MAX_SPYMASTER = 2;
    private static final int MIN_PLAYERS = 4;
    private static final int VOTING_PHASE_TIME = 15;

    private final PlayerRepository playerRepository;
    private final LobbyService lobbyService;
    private final ScheduledExecutorService scheduler;

    public PlayerService(PlayerRepository playerRepository, LobbyService lobbyService, ScheduledExecutorService scheduler) {
        this.playerRepository = playerRepository;
        this.lobbyService = lobbyService;
        this.scheduler = scheduler;
    }

    public PlayerData savePlayer(PlayerCreationData playerCreationData) {
        Player player = new Player(playerCreationData);
        Lobby lobby = lobbyService.findLobbyById(playerCreationData.getLobbyName());
        if (lobby.getPlayerList().isEmpty()) {
            player.setLobbyOwner(true);
        } else {
            player.setLobbyOwner(false);
        }
        player.setLobby(lobby);
        playerRepository.save(player);
        log.info("New player created by id:\t" + player.getId());
        return new PlayerData(player);
    }

    public void setPlayerRole(String lobbyName) {
        List<Player> allPlayers = getVisiblePlayersByLobbyName(lobbyName);
        if (allPlayers.size() >= MIN_PLAYERS) {
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
        log.info("Player roles randomized");
    }

    public void randomizeTeamSetup(String lobbyName) {
        List<Player> allPlayers = getVisiblePlayersByLobbyName(lobbyName);
        int originPlayerSize = allPlayers.size();
        List<Player> assignedPlayers = new ArrayList<>();

        if (allPlayers.size() >= MIN_PLAYERS) {
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
        log.info("Player sides and roles randomized");
    }

    public List<PlayerData> getPlayerDataListByLobbyName(String lobbyName) {
        log.info("Get players in the given lobby:\t" + lobbyName);
        return getVisiblePlayersByLobbyName(lobbyName)
                .stream()
                .map(PlayerData::new)
                .collect(Collectors.toList());
    }

    public PlayerData reassignLobbyOwner(String lobbyName) {
        List<Player> players = getVisiblePlayersByLobbyName(lobbyName);
        Player newOwnerPlayer = getRandomPlayer(players);
        newOwnerPlayer.setLobbyOwner(true);
        playerRepository.save(newOwnerPlayer);
        log.info("New lobby owner assgined to player:\t" + newOwnerPlayer.getId());
        return new PlayerData(newOwnerPlayer);
    }

    public Boolean isLobbyOwnerInLobby(String lobbyName) {
        log.info("Find lobby owner in lobby:\t" + lobbyName);
        List<Player> players = getVisiblePlayersByLobbyName(lobbyName);
        return players.stream().anyMatch(Player::getLobbyOwner);
    }

    public void processKickBeforeCountDown(PlayerRemovalData playerRemovalData) {
        if (playerRemovalData.getVote()) {
            Player kickInitPlayer = findPlayerById(playerRemovalData.getOwnerId());
            Player playerToRemove = findPlayerById(playerRemovalData.getPlayerToRemoveId());
            if (kickInitPlayer.getLobbyOwner()) {
                removePlayer(playerToRemove);
            } else {
                Integer kickVoteCount = playerToRemove.getKickVoteCount();
                playerToRemove.setKickVoteCount(++kickVoteCount);
                playerRepository.save(playerToRemove);
                log.info("Set player kick score:\t" + kickVoteCount);
            }
        }
    }

    public Boolean isGivenPlayerInLobby(PlayerDetailsData playerDetailsData) {
        List<Player> playersInLobby = findAllPlayersInLobby(playerDetailsData.getLobbyName());
        return playersInLobby.stream().map(Player::getId).anyMatch(id -> id.equals(playerDetailsData.getId()));
    }

    public PlayerData findPlayerDataById(Long id) {
        log.info("Find playerdata by id:\t" + id);
        return new PlayerData(findPlayerById(id));
    }

    public PlayerData showPlayer(Long id) {
        Player player = findPlayerById(id);
        player.setVisible(true);
        playerRepository.save(player);
        return new PlayerData(player);
    }

    public void hidePlayer(Long id) {
        Player player = findPlayerById(id);
        player.setVisible(false);
        playerRepository.save(player);
    }

    public PlayerData setRdyState(RdyStateData rdyStateData) {
        Player foundPlayer = findPlayerById(rdyStateData.getPlayerId());
        foundPlayer.setRdyState(rdyStateData.getRdyState());
        playerRepository.save(foundPlayer);
        log.info("Ready state changed for player:\t" + foundPlayer.getId() + "\t to:\t" + foundPlayer.getRdyState());
        return new PlayerData(foundPlayer);
    }

    public Boolean isEveryOneRdy(String lobbyName) {
        log.info("Check if everyone is rdy!");
        List<Player> players = getVisiblePlayersByLobbyName(lobbyName);
        return players.stream().allMatch(Player::getRdyState);
    }

    public PlayerData setPlayerSideAndRole(SelectionData selectionData) {
        Player player = findPlayerById(selectionData.getPlayerId());
        player.setRole(RoleType.valueOf(selectionData.getRole()));
        player.setSide(SideType.valueOf(selectionData.getSide()));
        playerRepository.save(player);
        return new PlayerData(player);
    }

    public List<PlayerData> findPlayersByGame(Game game) {
        return playerRepository.findAllPlayersByGame(game).stream().map(PlayerData::new).collect(Collectors.toList());
    }

    public RemainingRoleData getRemainingRoleData(String lobbyName) {
        RemainingRoleData remainingRoleData = new RemainingRoleData();
        List<Player> playersInLobby = getVisiblePlayersByLobbyName(lobbyName);

        long blueSpymaster = playersInLobby
                .stream()
                .filter(player -> player.getSide() == SideType.BLUE && player.getRole() == RoleType.SPYMASTER)
                .count();
        long blueSpy = playersInLobby
                .stream()
                .filter(player -> player.getSide() == SideType.BLUE && player.getRole() == RoleType.SPY)
                .count();
        long redSpymaster = playersInLobby
                .stream()
                .filter(player -> player.getSide() == SideType.RED && player.getRole() == RoleType.SPYMASTER)
                .count();
        long redSpy = playersInLobby
                .stream()
                .filter(player -> player.getSide() == SideType.RED && player.getRole() == RoleType.SPY)
                .count();

        boolean blueSpymasterFull = blueSpymaster == 1;
        boolean blueSpyFull = playersInLobby.size() / 2 - blueSpy == 0;
        boolean redSpymasterFull = redSpymaster == 1;
        boolean redSpyFull = playersInLobby.size() / 2 - redSpy == 0;

        remainingRoleData.setBlueSpymaster(blueSpymasterFull);
        remainingRoleData.setBlueSpy(blueSpyFull);
        remainingRoleData.setRedSpymaster(redSpymasterFull);
        remainingRoleData.setRedSpy(redSpyFull);

        return remainingRoleData;
    }

    public void setPlayerRemoval(PlayerRemovalData playerRemovalData) {
        PlayerData playerToKick = findPlayerDataById(playerRemovalData.getPlayerToRemoveId());
        playerRemovalData.setPlayerToRemove(playerToKick);
    }

    public ScheduledFuture<?> initVotingPhase(PlayerRemovalData playerRemovalData) {
        PlayerData playerToKick = findPlayerDataById(playerRemovalData.getPlayerToRemoveId());
        Player kickInitPlayer = findPlayerById(playerRemovalData.getOwnerId());
        ScheduledFuture<?> schedule = null;
        if (!kickInitPlayer.getLobbyOwner()) {
            schedule = scheduler.schedule(() -> processVotes(playerToKick.getId()), VOTING_PHASE_TIME, TimeUnit.SECONDS);
        }
        return schedule;
    }

    // @Async()
    public void processVotes(Long playerToKickId) {
        log.info("Process votes for player id:\t" + playerToKickId);
        Player playerToKick = findPlayerById(playerToKickId);
        int playersInLobby = (int) playerToKick.getLobby().getPlayerList().stream().filter(Player::getVisible).count();
        if (playerToKick.getKickVoteCount() > playersInLobby / 2) {
            removePlayer(playerToKick);
        } else {
            playerToKick.setKickVoteCount(0);
            playerRepository.save(playerToKick);
        }
    }

    public PlayerData setPlayerVisible(PlayerDetailsData playerDetailsData) {
        Player player = findPlayerById(playerDetailsData.getId());
        player.setVisible(true);
        playerRepository.save(player);
        log.info("Visibility changed on player id:\t" + player.getId());
        return new PlayerData(player);
    }

    private void removePlayer(Player player) {
        player.setLobby(null);
        playerRepository.delete(player);
        log.info("Player removed by id:\t" + player.getId());
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

    private List<Player> getVisiblePlayersByLobbyName(String lobbyName) {
        return playerRepository.getVisiblePlayersByLobbyName(lobbyName);
    }

    private List<Player> findAllPlayersInLobby(String lobbyName) {
        return playerRepository.findAllPlayersInLobby(lobbyName);
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
        List<Player> blueTeam = players.stream().filter(player -> player.getSide() == SideType.BLUE).collect(Collectors.toList());
        List<Player> redTeam = players.stream().filter(player -> player.getSide() == SideType.RED).collect(Collectors.toList());
        Player blueSpymaster = getRandomPlayer(blueTeam);
        Player redSpymaster = getRandomPlayer(redTeam);
        blueSpymaster.setRole(RoleType.SPYMASTER);
        redSpymaster.setRole(RoleType.SPYMASTER);
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
