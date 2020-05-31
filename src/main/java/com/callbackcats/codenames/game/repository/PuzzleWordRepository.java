package com.callbackcats.codenames.game.repository;

import com.callbackcats.codenames.game.domain.PuzzleWord;
import com.callbackcats.codenames.game.team.domain.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PuzzleWordRepository extends JpaRepository<PuzzleWord, Long> {

    @Query("select p from PuzzleWord p where p.team= :team order by p.wordRegisterTime desc")
    PuzzleWord findLatestPuzzleWordByTeam(@Param("team")Team team, Pageable limit);

}
