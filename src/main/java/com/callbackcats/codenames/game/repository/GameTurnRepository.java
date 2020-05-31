package com.callbackcats.codenames.game.repository;

import com.callbackcats.codenames.game.domain.GameTurn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameTurnRepository extends JpaRepository<GameTurn, Long> {
}
