package com.callbackcats.codenames.lobby.service;

import com.callbackcats.codenames.card.domain.GameLanguage;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.lobby.repository.LobbyRepository;
import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.RoleType;
import com.callbackcats.codenames.player.domain.SideType;
import com.callbackcats.codenames.player.dto.RemainingRoleData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class LobbyService {

    private LobbyRepository lobbyRepository;

    public LobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public void saveNewLobby(Lobby lobby) {
        this.lobbyRepository.save(lobby);
    }

    public LobbyDetails getLobbyDetailsById(String lobbyId) {
        Lobby lobby = findLobbyById(lobbyId);
        boolean everyoneReady = lobby.getPlayerList()
                .stream()
                .filter(Player::getVisible)
                .allMatch(Player::getRdyState);

        RemainingRoleData remainingRoleData = getRemainingRolesByLobbyId(lobbyId);
        log.info("Lobby details fetched by id:\t" + lobbyId);
        return new LobbyDetails(lobby, everyoneReady, remainingRoleData);
    }

    public Lobby findLobbyById(String id) {
        return this.lobbyRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lobby not found by given id:\t" + id));
    }

    public void setKickPhase(String lobbyId, boolean kickPhase) {
        Lobby lobby = findLobbyById(lobbyId);
        lobby.setKickingPhase(kickPhase);
        lobbyRepository.save(lobby);
    }

    public LobbyDetails updateLobbyGameLanguage(String id, GameLanguage gameLanguage) {
        Lobby lobby = lobbyRepository.findLobbyByName(id);
        lobby.setGameLanguage(gameLanguage);
        this.lobbyRepository.save(lobby);

        return getLobbyDetailsByLobby(lobby);
    }

    public RemainingRoleData getRemainingRolesByLobbyId(String lobbyId) {
        Lobby lobby = findLobbyById(lobbyId);
        return createRemainingRolesData(lobby);
    }

    private RemainingRoleData getRemainingRolesByLobby(Lobby lobby) {
        return createRemainingRolesData(lobby);
    }

    private RemainingRoleData createRemainingRolesData(Lobby lobby) {
        RemainingRoleData remainingRoleData = new RemainingRoleData();
        List<Player> visiblePlayersInLobby = lobby.getPlayerList()
                .stream()
                .filter(Player::getVisible)
                .collect(Collectors.toList());

        long blueSpymaster = visiblePlayersInLobby
                .stream()
                .filter(player -> player.getSide() == SideType.BLUE && player.getRole() == RoleType.SPYMASTER)
                .count();
        long blueSpy = visiblePlayersInLobby
                .stream()
                .filter(player -> player.getSide() == SideType.BLUE && player.getRole() == RoleType.SPY)
                .count();
        long redSpymaster = visiblePlayersInLobby
                .stream()
                .filter(player -> player.getSide() == SideType.RED && player.getRole() == RoleType.SPYMASTER)
                .count();
        long redSpy = visiblePlayersInLobby
                .stream()
                .filter(player -> player.getSide() == SideType.RED && player.getRole() == RoleType.SPY)
                .count();

        boolean blueSpymasterFull = blueSpymaster == 1;
        boolean blueSpyFull = visiblePlayersInLobby.size() / 2 - blueSpy == 0;
        boolean redSpymasterFull = redSpymaster == 1;
        boolean redSpyFull = visiblePlayersInLobby.size() / 2 - redSpy == 0;

        remainingRoleData.setBlueSpymaster(blueSpymasterFull);
        remainingRoleData.setBlueSpy(blueSpyFull);
        remainingRoleData.setRedSpymaster(redSpymasterFull);
        remainingRoleData.setRedSpy(redSpyFull);

        return remainingRoleData;
    }

    private LobbyDetails getLobbyDetailsByLobby(Lobby lobby) {
        boolean everyoneReady = lobby.getPlayerList()
                .stream()
                .allMatch(Player::getRdyState);

        RemainingRoleData remainingRoleData = getRemainingRolesByLobby(lobby);
        return new LobbyDetails(lobby, everyoneReady, remainingRoleData);
    }


}
