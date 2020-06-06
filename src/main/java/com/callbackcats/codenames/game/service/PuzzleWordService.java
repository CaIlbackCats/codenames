package com.callbackcats.codenames.game.service;

import com.callbackcats.codenames.game.domain.Game;
import com.callbackcats.codenames.game.domain.PuzzleWord;
import com.callbackcats.codenames.game.dto.PuzzleWordData;
import com.callbackcats.codenames.game.repository.PuzzleWordRepository;
import com.callbackcats.codenames.game.team.domain.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
public class PuzzleWordService {

    private final PuzzleWordRepository puzzleWordRepository;
  //  private static final PageRequest LATEST_PUZZLE_WORD = PageRequest.of(0, 1);

    public PuzzleWordService(PuzzleWordRepository puzzleWordRepository) {
        this.puzzleWordRepository = puzzleWordRepository;
    }

    public void savePuzzleWorldToGame(Team team, PuzzleWordData puzzleWordData) {
        PuzzleWord puzzleWord = new PuzzleWord(puzzleWordData);
        puzzleWord.setTeam(team);
        puzzleWordRepository.save(puzzleWord);
        log.info("PuzzleWord has been saved to game id:\t" + team.getId());
    }

    public void increaseLatestPuzzleWordGuessCounter(Team team) {
        PuzzleWord puzzleWord = findLastWordByTeam(team);
        int guess = puzzleWord.getUsedGuesses() + 1;
        puzzleWord.setUsedGuesses(guess);
        puzzleWordRepository.save(puzzleWord);
        log.info("Used guesses increased for puzzleword id:\t" + puzzleWord.getId() + "\t to:\t" + puzzleWord.getUsedGuesses() + "\tof\t" + puzzleWord.getMaxGuessCount());
    }

    public Boolean isPuzzleWordGuessLimitReached(Team team) {
        boolean limitReached = false;
        PuzzleWord puzzleWord = findLastWordByTeam(team);
        int maxGuess = puzzleWord.getMaxGuessCount() + 1;
        if (maxGuess == puzzleWord.getUsedGuesses()) {
            limitReached = true;
        }
        return limitReached;
    }

    private PuzzleWord findLastWordByTeam(Team team) {
        return puzzleWordRepository.findLatestPuzzleWordByTeam(team).stream().findFirst().orElseThrow();
    }
}
