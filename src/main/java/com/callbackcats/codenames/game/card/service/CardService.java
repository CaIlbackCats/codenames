package com.callbackcats.codenames.game.card.service;

import com.callbackcats.codenames.game.card.repository.CardRepository;
import com.callbackcats.codenames.game.card.domain.Card;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void setCardFound(Long id, Boolean found) {
        Card card = findCardById(id);
        card.setFound(found);
        cardRepository.save(card);
    }

    public Card findCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Card not found by given ID:\t" + id));
    }
}
