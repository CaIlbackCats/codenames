package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.card.domain.Card;
import com.callbackcats.codenames.game.card.domain.CardType;
import com.callbackcats.codenames.game.card.domain.GameLanguage;
import com.callbackcats.codenames.game.card.domain.Word;
import com.callbackcats.codenames.game.card.service.CardService;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Rollback
public class GameServiceTest {

    @Autowired
    private CardService cardService;

    @Test
    public void testGenerateMap_containsOnlyHungarianWords() {

        List<Card> cards = cardService.generateMap(SideType.RED, GameLanguage.HUNGARIAN);

        assertTrue(cards.stream()
                .map(Card::getWord)
                .map(Word::getLanguage)
                .allMatch(gameLanguage -> gameLanguage.equals(GameLanguage.HUNGARIAN)));

    }

    @Test
    public void testGenerateMap_containsOnlyEnglishWords() {
        List<Card> cards = cardService.generateMap(SideType.RED, GameLanguage.ENGLISH);

        assertTrue(cards.stream()
                .map(Card::getWord)
                .map(Word::getLanguage)
                .allMatch(gameLanguage -> gameLanguage.equals(GameLanguage.ENGLISH)));
    }

    @Test
    public void testGenerateMap_returnsCorrectSize() {

        List<Card> cards = cardService.generateMap(SideType.RED, GameLanguage.HUNGARIAN);

        assertEquals(25, cards.size());

    }

    @Test
    public void testGenerateMap() {

        List<Card> cards = cardService.generateMap(SideType.RED, GameLanguage.HUNGARIAN);

        long redCards = cards.stream().map(Card::getType).filter(cardType -> cardType.equals(CardType.RED_SPY)).count();
        assertEquals(9, redCards);

        long blueCards = cards.stream().map(Card::getType).filter(cardType -> cardType.equals(CardType.BLUE_SPY)).count();
        assertEquals(8, blueCards);

        long assassinCard = cards.stream().map(Card::getType).filter(cardType -> cardType.equals(CardType.ASSASSIN)).count();
        assertEquals(1, assassinCard);

        long byStanderCards = cards.stream().map(Card::getType).filter(cardType -> cardType.equals(CardType.BYSTANDER)).count();
        assertEquals(7, byStanderCards);
    }

}
