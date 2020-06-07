package com.callbackcats.codenames.game.team.repository;

import com.callbackcats.codenames.game.team.domain.TurnStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TurnStatRepository extends JpaRepository<TurnStat, Long> {


}
