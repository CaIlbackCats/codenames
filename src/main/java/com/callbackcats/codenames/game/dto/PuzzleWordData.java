package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.game.domain.PuzzleWord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleWordData {

    private String puzzleWord;
    private Integer maxGuessCount;

    public PuzzleWordData(PuzzleWord puzzleWord) {
        this.puzzleWord = puzzleWord.getPuzzleWord();
        this.maxGuessCount = puzzleWord.getMaxGuessCount();
    }
}
