package com.callbackcats.codenames.game.repository;

import com.callbackcats.codenames.game.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("select g from Game g where g.active=true and g.lobby.id= :lobbyId")
    Game findActiveGameByLobby(@Param("lobbyId") String lobbyId);


}
