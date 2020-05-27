package com.callbackcats.codenames.game.card.dto;

import com.callbackcats.codenames.game.domain.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WordDetails {

    private Long id;

    private String word;

    public WordDetails(Word word) {
        this.id = word.getId();
        this.word = word.getWord();
    }
}
