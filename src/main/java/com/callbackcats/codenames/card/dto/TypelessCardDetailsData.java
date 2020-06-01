package com.callbackcats.codenames.card.dto;

import com.callbackcats.codenames.card.domain.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TypelessCardDetailsData {

    private Long id;
    private WordDetails word;
    private boolean found;
    private String type;
    private Boolean voted;
    private Integer voteCounter;

    public TypelessCardDetailsData(Card card) {
        this.id = card.getId();
        this.word = new WordDetails(card.getWord());
        this.found = card.isFound();
        if (this.found) {
            this.type = String.valueOf(card.getType());
        }
        this.voted = card.getVoted();
        this.voteCounter = card.getVoteCounter();
    }
}
