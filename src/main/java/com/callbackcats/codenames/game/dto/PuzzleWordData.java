package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.game.domain.PuzzleWord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleWordData {

    private Long id;
    private String puzzleWord;
    private Integer maxGuessCount;
    private Integer usedGuesses;
    private LocalDateTime wordRegisterTime;

    public PuzzleWordData(PuzzleWord puzzleWord) {
        this.id = puzzleWord.getId();
        this.puzzleWord = puzzleWord.getPuzzleWord();
        this.maxGuessCount = puzzleWord.getMaxGuessCount();
        this.usedGuesses = puzzleWord.getUsedGuesses();
        this.wordRegisterTime = puzzleWord.getWordRegisterTime();
    }
}
