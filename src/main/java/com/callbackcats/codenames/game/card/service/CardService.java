package com.callbackcats.codenames.game.card.service;

import com.callbackcats.codenames.game.card.domain.Card;
import com.callbackcats.codenames.game.card.domain.CardType;
import com.callbackcats.codenames.game.card.domain.GameLanguage;
import com.callbackcats.codenames.game.card.domain.Word;
import com.callbackcats.codenames.game.card.dto.CardDetails;
import com.callbackcats.codenames.game.card.repository.CardRepository;
import com.callbackcats.codenames.game.card.repository.WordRepository;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.domain.Word;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardService {

    private static final Integer MAX_STARTING_TEAM_CARD = 9;
    private static final Integer MAX_SECOND_TEAM_CARD = 8;
    private static final Integer MAX_ASSASSIN_CARD = 1;
    private static final Integer BOARD_SIZE = 25;

    private final CardRepository cardRepository;
    private final WordRepository wordRepository;

    public CardService(CardRepository cardRepository, WordRepository wordRepository) {
        this.cardRepository = cardRepository;
        this.wordRepository = wordRepository;
    }

    public void setCardFound(Long id, Boolean found) {
        Card card = findCardById(id);
        card.setFound(found);
        cardRepository.save(card);
    }

    public List<Card> generateMap(SideType startingTeamColor, GameLanguage language, Game game) {
        Set<Word> words = new HashSet<>();
        List<Word> allWords = wordRepository.findWordsByLanguage(language);
        while (words.size() < BOARD_SIZE) {
            Word randomWord = findRandomWord(allWords);
            words.add(randomWord);
        }
        List<Card> cards = new ArrayList<>();
        CardType startingCardType = CardType.findCardTypeBySide(startingTeamColor);

        //todo multi-thread?!

        CardType secondCardType = CardType.selectOpposite(startingTeamColor);
        cards.addAll(getCardsByCardType(words, MAX_STARTING_TEAM_CARD, startingCardType, game));
        cards.addAll(getCardsByCardType(words, MAX_SECOND_TEAM_CARD, secondCardType, game));
        cards.addAll(getCardsByCardType(words, MAX_ASSASSIN_CARD, CardType.ASSASSIN, game));
        cards.addAll(words.stream().map(word -> createCard(word, CardType.BYSTANDER, game)).collect(Collectors.toList()));

        Collections.shuffle(cards);

        cardRepository.saveAll(cards);

        return cards;
    }

    public Card findCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Card not found by given ID:\t" + id));
    }

    public List<CardDetails> findCardsByGameId(Long gameId) {
        return cardRepository.findCardsByGameId(gameId).stream().map(CardDetails::new).collect(Collectors.toList());
    }


    private List<Card> getCardsByCardType(Set<Word> words, int size, CardType cardType, Game game) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int randomIndex = (int) Math.floor(Math.random() * words.size());
            Word foundWord = words
                    .stream()
                    .skip(randomIndex)
                    .findFirst().orElseThrow(() -> new RuntimeException("Word not found"));
            cards.add(createCard(foundWord, cardType, game));
            words.remove(foundWord);
        }
        return cards;
    }

    private Card createCard(Word word, CardType cardType, Game game) {
        return new Card(word, cardType, game);
    }

    private Word findRandomWord(List<Word> words) {
        int randomIndex = (int) Math.floor(Math.random() * words.size());
        return words.get(randomIndex);
    }
}
