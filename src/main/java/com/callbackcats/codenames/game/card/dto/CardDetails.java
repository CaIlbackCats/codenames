package com.callbackcats.codenames.game.card.dto;

import com.callbackcats.codenames.game.card.domain.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CardDetails {

    private Long id;
    private String word;
    private String type;
    private boolean isFound;

    public CardDetails(Card card) {
        this.id = card.getId();
        this.word = card.getWord();
        this.type = card.getType().name();
        this.isFound = card.isFound();
    }
}
