package com.callbackcats.codenames.lobby.service;

import com.callbackcats.codenames.card.domain.GameLanguage;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.lobby.repository.LobbyRepository;
import com.callbackcats.codenames.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
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

        return new LobbyDetails(lobby, everyoneReady);
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

    private LobbyDetails getLobbyDetailsByLobby(Lobby lobby) {
        boolean everyoneReady = lobby.getPlayerList()
                .stream()
                .allMatch(Player::getRdyState);

        return new LobbyDetails(lobby, everyoneReady);
    }


}
