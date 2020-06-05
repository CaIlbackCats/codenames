package com.callbackcats.codenames.lobby.service;

import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.RoleType;
import com.callbackcats.codenames.player.domain.SideType;
import com.callbackcats.codenames.player.dto.PlayerCreationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback
public class LobbyServiceTest {

    @Autowired
    private LobbyService lobbyService;

    private Lobby lobby;
    private List<Player> players;

    @BeforeEach
    public void init() {
        this.lobby = new Lobby();
        this.players = List.of(
                new Player(new PlayerCreationData(lobby.getId(), "test1")),
                new Player(new PlayerCreationData(lobby.getId(), "test2")),
                new Player(new PlayerCreationData(lobby.getId(), "test3")),
                new Player(new PlayerCreationData(lobby.getId(), "test4"))
        );
    }

    @Test
    public void testGetRemainingRolesDataByLobby_whenLobbyIsEmpty() {
        this.lobby.setPlayerList(new ArrayList<>());
        this.lobbyService.saveNewLobby(lobby);
        /*RemainingRoleData remainingRoleData = lobbyService.getRemainingRolesByLobby(this.lobby);

        assertTrue(remainingRoleData.getBlueSpy());
        assertFalse(remainingRoleData.getBlueSpymaster());
        assertTrue(remainingRoleData.getRedSpy());
        assertFalse(remainingRoleData.getRedSpymaster());*/
    }

    @Test
    public void testGetRemainingRolesDataByLobby_whenLobbyIsNotEmptyAndRedSpiesTaken() {
        this.lobby.setPlayerList(this.players);
        lobby.getPlayerList().get(0).setRole(RoleType.SPYMASTER);
        lobby.getPlayerList().get(0).setSide(SideType.RED);
        lobby.getPlayerList().get(1).setRole(RoleType.SPY);
        lobby.getPlayerList().get(1).setSide(SideType.RED);
        lobby.getPlayerList().get(2).setRole(RoleType.SPY);
        lobby.getPlayerList().get(2).setSide(SideType.RED);
        this.lobbyService.saveNewLobby(lobby);
        /*RemainingRoleData remainingRoleData = lobbyService.getRemainingRolesByLobby(this.lobby);

        assertFalse(remainingRoleData.getBlueSpy());
        assertFalse(remainingRoleData.getBlueSpymaster());
        assertTrue(remainingRoleData.getRedSpy());
        assertTrue(remainingRoleData.getRedSpymaster())*/
        ;
    }
}
