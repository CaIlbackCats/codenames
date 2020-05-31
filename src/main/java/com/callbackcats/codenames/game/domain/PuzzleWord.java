package com.callbackcats.codenames.game.domain;

import com.callbackcats.codenames.game.dto.PuzzleWordData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "puzzle_word")
public class PuzzleWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "puzzle_word")
    private String puzzleWord;

    @Column(name = "max_guess_count")
    private Integer maxGuessCount;

    @Column(name = "used_guesses")
    private Integer usedGuesses;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public PuzzleWord(PuzzleWordData puzzleWordData) {
        this.puzzleWord = puzzleWordData.getPuzzleWord();
        this.maxGuessCount = puzzleWordData.getMaxGuessCount();
        this.usedGuesses = puzzleWordData.getUsedGuesses();
    }
}
