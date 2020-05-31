package com.callbackcats.codenames.card.dto;

import com.callbackcats.codenames.card.domain.Card;
import com.callbackcats.codenames.card.domain.CardType;
import com.callbackcats.codenames.card.domain.Word;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TypedCardDetailsData {

    private Long id;
    private WordDetails word;
    private String type;
    private boolean isFound;

    public TypedCardDetailsData(Card card) {
        this.id = card.getId();
        this.word = new WordDetails(card.getWord());
        this.type = card.getType().name();
        this.isFound = card.isFound();
    }
}
