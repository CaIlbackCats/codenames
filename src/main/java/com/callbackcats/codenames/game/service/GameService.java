package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.card.repository.WordRepository;
import com.callbackcats.codenames.game.card.service.CardService;
import com.callbackcats.codenames.game.card.domain.Card;
import com.callbackcats.codenames.game.card.domain.CardType;
import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.domain.Word;
import com.callbackcats.codenames.game.dto.*;
import com.callbackcats.codenames.game.repository.GameRepository;
import com.callbackcats.codenames.lobby.domain.Lobby;
import com.callbackcats.codenames.lobby.player.domain.SideType;
import com.callbackcats.codenames.lobby.player.service.PlayerService;
import com.callbackcats.codenames.lobby.service.LobbyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class GameService {

    private static final Integer MAX_STARTING_TEAM_CARD = 9;
    private static final Integer MAX_SECOND_TEAM_CARD = 8;
    private static final Integer MAX_ASSASSIN_CARD = 1;
    private static final Integer BOARD_SIZE = 25;

    private final GameRepository gameRepository;
    private final LobbyService lobbyService;
    private final WordRepository wordRepository;

    public GameService(GameRepository gameRepository, LobbyService lobbyService, WordRepository wordRepository) {
        this.gameRepository = gameRepository;
        this.lobbyService = lobbyService;
        this.wordRepository = wordRepository;
    }


    public GameDetails createGame(String lobbyId) {
        Lobby lobby = this.lobbyService.findLobbyById(lobbyId);
        SideType randomSide = SideType.getRandomSide();
        List<Card> cards = generateMap(randomSide);
        Game game = new Game();

        game.setBoard(cards);

        this.gameRepository.save(game);
        lobbyService.addGame(lobby, game);
        log.info("Game created");
        return new GameDetails(game);
    }


    List<Card> generateMap(SideType startingTeamColor) {
        Set<Word> words = new HashSet<>();
        List<Word> allWords = wordRepository.findAll();
        while (words.size() < BOARD_SIZE) {
            Word randomWord = findRandomWord(allWords);
            words.add(randomWord);
        }
        List<Card> cards = new ArrayList<>();
        CardType startingCardType = CardType.findCardTypeBySide(startingTeamColor);

        //todo multi-thread?!

        CardType secondCardType = CardType.selectOpposite(startingTeamColor);
        cards.addAll(getCardsByCardType(words, MAX_STARTING_TEAM_CARD, startingCardType));
        cards.addAll(getCardsByCardType(words, MAX_SECOND_TEAM_CARD, secondCardType));
        cards.addAll(getCardsByCardType(words, MAX_ASSASSIN_CARD, CardType.ASSASSIN));
        cards.addAll(words.stream().map(word -> createCard(word, CardType.BYSTANDER)).collect(Collectors.toList()));

        Collections.shuffle(cards);

        return cards;

    }

    private List<Card> getCardsByCardType(Set<Word> words, int size, CardType cardType) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int randomIndex = (int) Math.floor(Math.random() * words.size());
            Word foundWord = words
                    .stream()
                    .skip(randomIndex)
                    .findFirst().orElseThrow(() -> new RuntimeException("Word not found"));
            cards.add(createCard(foundWord, cardType));
            words.remove(foundWord);
        }
        return cards;
    }

    private Card createCard(Word word, CardType cardType) {
        return new Card(word, cardType);
    }

    private Word findRandomWord(List<Word> words) {
        int randomIndex = (int) Math.floor(Math.random() * words.size());
        return words.get(randomIndex);
    }

    public void countScore() {

    }

    private Game findGameById(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Game not found by id:\t" + id));
    }


}
