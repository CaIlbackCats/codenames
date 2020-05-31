package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.domain.PuzzleWord;
import com.callbackcats.codenames.game.dto.PuzzleWordData;
import com.callbackcats.codenames.game.repository.PuzzleWordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class PuzzleWordService {

    private final PuzzleWordRepository puzzleWordRepository;

    public PuzzleWordService(PuzzleWordRepository puzzleWordRepository) {
        this.puzzleWordRepository = puzzleWordRepository;
    }

    public void savePuzzleWorldToGame(Game game, PuzzleWordData puzzleWordData) {
        PuzzleWord puzzleWord = new PuzzleWord(puzzleWordData);
        puzzleWord.setGame(game);
        puzzleWordRepository.save(puzzleWord);
        log.info("PuzzleWord has been saved to game id:\t" + game.getId());
    }

    public void increaseGuessCounter(PuzzleWord puzzleWord) {
        int guess = puzzleWord.getUsedGuesses() + 1;
        puzzleWord.setUsedGuesses(guess);
        puzzleWordRepository.save(puzzleWord);
    }
}
