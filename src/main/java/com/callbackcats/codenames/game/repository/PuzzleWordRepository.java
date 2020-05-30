package com.callbackcats.codenames.game.repository;

import com.callbackcats.codenames.game.domain.PuzzleWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuzzleWordRepository extends JpaRepository<PuzzleWord, Long> {
}
