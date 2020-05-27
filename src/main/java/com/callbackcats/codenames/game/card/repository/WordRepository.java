package com.callbackcats.codenames.game.card.repository;

import com.callbackcats.codenames.game.domain.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word,Long> {

}
