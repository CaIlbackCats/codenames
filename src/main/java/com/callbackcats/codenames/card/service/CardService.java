package com.callbackcats.codenames.card.service;

import com.callbackcats.codenames.card.domain.Card;
import com.callbackcats.codenames.card.domain.CardType;
import com.callbackcats.codenames.card.domain.GameLanguage;
import com.callbackcats.codenames.card.domain.Word;
import com.callbackcats.codenames.card.dto.TypedCardDetailsData;
import com.callbackcats.codenames.card.dto.TypelessCardDetailsData;
import com.callbackcats.codenames.card.repository.CardRepository;
import com.callbackcats.codenames.card.repository.WordRepository;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.player.domain.SideType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
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

    public void setCardFound(Card card) {
        card.setFound(true);
        card.setVoted(false);
        card.setVoteCounter(0);
        cardRepository.save(card);
        log.info("Card by id:\t" + card.getId() + "\t is found");
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
        log.info("Map successfully generated for game id:\t" + game.getId());
        return cards;
    }

    public Card findCardById(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Card not found by given ID:\t" + id));
    }

    public List<TypelessCardDetailsData> getSpyMap(Long gameId) {
        log.info("Map requested for spies");
        return findCardsByGameId(gameId).stream().map(TypelessCardDetailsData::new).collect(Collectors.toList());
    }

    public List<TypedCardDetailsData> getSpymasterMap(Long gameId) {
        log.info("Map requested for spymasters");
        return findCardsByGameId(gameId).stream().map(TypedCardDetailsData::new).collect(Collectors.toList());
    }

    public void deselectCard(Card card) {
        card.setVoted(false);
        int updatedNumberOfVotes = card.getVoteCounter() - 1;
        card.setVoteCounter(updatedNumberOfVotes);
        log.info("Card by id:\t" + card.getId() + "\t is deselected");
        cardRepository.save(card);
    }

    public void selectCard(Card card) {
        card.setVoted(true);
        int updatedNumberOfVotes = card.getVoteCounter() + 1;
        card.setVoteCounter(updatedNumberOfVotes);
        log.info("Card by id:\t" + card.getId() + "\t is selected");
        cardRepository.save(card);
    }

    private List<Card> findCardsByGameId(Long gameId) {
        return cardRepository.findCardsByGameId(gameId);
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
