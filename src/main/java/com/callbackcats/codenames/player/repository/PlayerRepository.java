package com.callbackcats.codenames.player.repository;

import com.callbackcats.codenames.player.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("select p from Player p")
    List<Player> getAllPlayers();

    @Query("select p from Player p where p.lobby.id= :lobbyName")
    List<Player> getPlayersByLobbyName(@Param("lobbyName") String lobbyName);
}
