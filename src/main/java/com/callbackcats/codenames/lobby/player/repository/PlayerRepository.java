package com.callbackcats.codenames.lobby.player.repository;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.lobby.player.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("select p from Player p")
    List<Player> getAllPlayers();

    @Query("select p from Player p where p.visible=true and p.lobby.id= :lobbyName")
    List<Player> getVisiblePlayersByLobbyName(@Param("lobbyName") String lobbyName);

    @Query("select p from Player p where p.lobby.id= :lobbyName")
    List<Player> getAllPlayersByLobbyName(@Param("lobbyName") String lobbyName);

    @Query("select p from Player p join p.lobby l where :game member of l.games")
    List<Player> findAllPlayersByGame(@Param("game") Game game);
}
