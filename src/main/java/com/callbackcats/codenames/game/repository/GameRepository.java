package com.callbackcats.codenames.game.repository;

import com.callbackcats.codenames.game.domain.Board;
import com.callbackcats.codenames.lobby.player.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Board, Long> {

}
