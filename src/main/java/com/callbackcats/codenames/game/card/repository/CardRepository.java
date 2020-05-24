package com.callbackcats.codenames.game.card.repository;

import com.callbackcats.codenames.game.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {

}
