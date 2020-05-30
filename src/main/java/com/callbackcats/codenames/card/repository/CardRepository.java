package com.callbackcats.codenames.card.repository;

import com.callbackcats.codenames.card.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("select c from Card c where c.game.id= :gameId")
    List<Card> findCardsByGameId(@Param("gameId") Long gameId);
}
