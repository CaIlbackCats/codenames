package com.callbackcats.codenames.player.service;

import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.service.LobbyService;
import com.callbackcats.codenames.player.dto.PlayerCreationData;
import com.callbackcats.codenames.player.dto.PlayerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional
@Rollback
public class PlayerServiceTest {

    @Autowired
    private PlayerService playerService;

    @Mock
    private LobbyService lobbyService;

    private Lobby lobby;

    private PlayerCreationData playerCreationData;

    @BeforeEach
    public void init() {
        this.lobby = new Lobby();
        lobby.setId("1");
        lobby.setPlayerList(new ArrayList<>());
        this.lobbyService = mock(LobbyService.class);
        doReturn(this.lobby).when(lobbyService).findLobbyById(any());
        this.playerService.setLobbyService(this.lobbyService);
        this.playerCreationData = new PlayerCreationData("1", "test1");
    }

    @Test
    public void testSavePlayerAsOwner_whenLobbyIsEmpty() {
        PlayerData playerData = playerService.savePlayer(this.playerCreationData);
        assertTrue(playerService.findPlayerDataById(playerData.getId()).getLobbyOwner());
    }

    @Test
    public void testSavePlayer_whenPlayersNameIsTaken() {
        playerService.savePlayer(new PlayerCreationData("1", "test1"));
        PlayerData playerData = playerService.savePlayer(this.playerCreationData);
        assertNull(playerData);
    }

    @Test
    public void testSavePlayer_whenPlayersNameIsNotTaken() {
        playerService.savePlayer(this.playerCreationData);
        PlayerData playerData = playerService.savePlayer(new PlayerCreationData(lobby.getId(), "test2"));
        assertNotNull(playerData);
    }
}
