package com.callbackcats.codenames.player.repository;

import com.callbackcats.codenames.player.domain.Player;
import com.callbackcats.codenames.player.domain.SideType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("select p from Player p where p.visible=true and p.lobby.id= :lobbyName")
    List<Player> getVisiblePlayersByLobbyName(@Param("lobbyName") String lobbyName);


    @Query("select p from Player p where p.lobby.id= :lobbyId and p.side= :side and p.visible=true")
    List<Player> findAllVisiblePlayersByLobbyIdBySide(@Param("lobbyId") String lobbyId, @Param("side") SideType side);

    @Query("select p from Player p where p.name= :name and p.lobby.id= :lobbyId")
    List<Player> findPlayersInLobbyByName(@Param("name") String name, @Param("lobbyId") String lobbyId);
}
