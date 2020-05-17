package com.callbackcats.codenames.lobby.service;

import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.dto.LobbyDetails;
import com.callbackcats.codenames.lobby.repository.LobbyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

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

    public Optional<Lobby> getLobbyById(String id) {
        return this.lobbyRepository.findById(id);
    }
}
