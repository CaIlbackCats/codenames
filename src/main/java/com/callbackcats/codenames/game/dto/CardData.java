package com.callbackcats.codenames.game.dto;

import com.callbackcats.codenames.game.domain.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardData {

    private String word;
    private String type;
    private boolean isFound;

    public CardData(Card card) {
        this.word = card.getWord();
        this.type = card.getType().name();
        this.isFound = card.isFound();
    }
}
