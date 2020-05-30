package com.callbackcats.codenames.card.dto;

import com.callbackcats.codenames.card.domain.Word;
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
