package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.card.dto.CardDetails;
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
import com.callbackcats.codenames.game.card.dto.CardVoteData;
import com.callbackcats.codenames.lobby.player.dto.PlayerData;
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
    private final PlayerService playerService;
    private final CardService cardService;
    private final WordRepository wordRepository;

    public GameService(GameRepository gameRepository, LobbyService lobbyService, PlayerService playerService, CardService cardService, WordRepository wordRepository) {
        this.gameRepository = gameRepository;
        this.lobbyService = lobbyService;
        this.playerService = playerService;
        this.cardService = cardService;
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


    public GameStateData processVotes(TeamVoteData teamVoteData) {
        Map<CardVoteData, Integer> cardVoteMap = getCardVoteScores(teamVoteData);
        Integer maxScore = Collections.max(cardVoteMap.values());
        List<CardVoteData> mostVotedCards = cardVoteMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(maxScore))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        log.info("Find most voted cards");
        Game game = findGameById(teamVoteData.getGameId());

        boolean mostVoteOnOneCard = mostVotedCards.size() == 1;
        if (mostVoteOnOneCard) {
            handleGameByGoodVote(teamVoteData, maxScore, mostVotedCards, game);
        } else if (mostVotedCards.size() > 1) {
            game.setEndTurn(true);
        }

        checkWinningCondition(game, teamVoteData);
        gameRepository.save(game);

        return new GameStateData(game);
    }

    public void checkWinningCondition(Game game, TeamVoteData teamVoteData) {
        SideType currentSide = CardType.valueOf(teamVoteData.getVotingTeam()).getTeamColorValue();
        int foundCardsBySide = (int) game.getBoard()
                .stream()
                .filter(card -> card.getType().getTeamColorValue().equals(currentSide))
                .filter(Card::isFound)
                .count();
        if (game.getStartingTeamColor().equals(currentSide) && foundCardsBySide == MAX_STARTING_TEAM_CARD) {
            game.setWinner(currentSide);
        } else if (foundCardsBySide == MAX_SECOND_TEAM_CARD) {
            game.setWinner(currentSide);
        }
    }

    private void handleGameByGoodVote(TeamVoteData teamVoteData, Integer maxScore, List<CardVoteData> maxCardVote, Game game) {
        CardType votedCardType = CardType.valueOf(maxCardVote.get(0).getVotedCard().getType());
        if (votedCardType == CardType.ASSASSIN) {
            setupGameEndByAssassin(teamVoteData, game);
            log.info("Assassin found, and set");
        } else {
            updateGameByVotes(maxCardVote, maxScore, teamVoteData, game);
        }
    }

    private void setupGameEndByAssassin(TeamVoteData teamVoteData, Game game) {
        game.setEndGameByAssassin(true);
        SideType currentTeam = SideType.valueOf(teamVoteData.getVotingTeam());
        if ((currentTeam == SideType.BLUE)) {
            game.setWinner(SideType.RED);
        } else {
            game.setWinner(SideType.BLUE);
        }
    }

    private void updateGameByVotes(List<CardVoteData> maxCardVote, Integer maxScore, TeamVoteData teamVoteData, Game game) {
        String votingTeam = teamVoteData.getVotingTeam();
        CardDetails mostVotedCard = maxCardVote.get(0).getVotedCard();
        String votedCardColor = mostVotedCard.getType();

        setGameByCardPick(mostVotedCard, maxScore, game);

        cardService.setCardFound(mostVotedCard.getId(), true);

        setGameEndTurnOnWrongPick(votingTeam, game, votedCardColor);

    }

    private void setGameEndTurnOnWrongPick(String votingTeam, Game game, String cardColor) {
        SideType votingTeamColor = SideType.valueOf(votingTeam);
        SideType votedCardColor = CardType.valueOf(cardColor).getTeamColorValue();
        if (votingTeamColor != votedCardColor) {
            game.setEndTurn(true);
        }
    }

    private void setGameByCardPick(CardDetails mostVotedCard, Integer maxScore, Game game) {
        int currentScore;
        switch (CardType.valueOf(mostVotedCard.getType())) {
            case BLUE_SPY:
                currentScore = game.getBlueScore() + maxScore;
                game.setBlueScore(currentScore);
                break;
            case RED_SPY:
                currentScore = game.getRedSCore() + maxScore;
                game.setRedSCore(currentScore);
                break;
//            case ASSASSIN:
//                game.setEndGameByAssassin(true);
//                break;
            case BYSTANDER:
                game.setEndTurn(true);
                break;
        }
    }


    private Map<CardVoteData, Integer> getCardVoteScores(TeamVoteData teamVoteData) {
        List<CardVoteData> cardVotes = teamVoteData.getCardVotes();
        Map<CardVoteData, Integer> cardVoteMap = new HashMap<>();
        for (CardVoteData currentCardVote : cardVotes) {
            Integer score = countScore(teamVoteData.getGameId(), currentCardVote);
            cardVoteMap.put(currentCardVote, score);
        }
        log.info("Finished counting votes for all voted cards");
        return cardVoteMap;
    }

    private Integer countScore(Long gameId, CardVoteData cardVoteData) {

        Game game = findGameById(gameId);
        List<PlayerData> playersInGame = playerService.findPlayersByGame(game);

        Integer voteNumber = (int) playersInGame
                .stream()
                .map(PlayerData::getId)
                .filter(cardVoteData.getVotedPlayersId()::contains)
                .count();

        log.info("Count votes for given card:\t" + cardVoteData.getVotedCard().getId());

        return voteNumber;
    }

    private Game findGameById(Long id) {
        return gameRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Game not found by id:\t" + id));
    }


}
