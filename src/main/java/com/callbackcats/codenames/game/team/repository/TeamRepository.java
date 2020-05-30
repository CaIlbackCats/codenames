package com.callbackcats.codenames.game.team.repository;

import com.callbackcats.codenames.game.team.domain.Team;
import com.callbackcats.codenames.player.domain.SideType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("select t from Team t join t.game g where g.active=true and t.side= :side and g.id= :gameId")
    Optional<Team> findCurrentTeamByGameIdBySide(@Param("gameId") Long id, @Param("side") SideType side);

    @Query("select t from Team t where t.game.id= :gameId")
    List<Team> findTeamsByGameId(@Param("gameId") Long gameId);
}
