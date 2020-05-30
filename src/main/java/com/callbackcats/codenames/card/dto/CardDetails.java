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
public class CardDetails {

    private Long id;
    private WordDetails word;
    private String type;
    private boolean isFound;
    private Integer vote;

    public CardDetails(Card card) {
        this.id = card.getId();
        this.word = new WordDetails(card.getWord());
        this.type = card.getType().name();
        this.isFound = card.isFound();
        this.vote = card.getVote();
    }

    public CardDetails(Word word, CardType type) {
        this.word = new WordDetails(word);
        this.type = type.toString();
    }
}
